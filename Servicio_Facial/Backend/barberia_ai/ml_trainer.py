"""
ml_trainer.py  v2.0 — corregido
================================
CORRECCIONES aplicadas (alineación al schema SQL):
  - _load_face_data:    FROM ia.face_measurements JOIN ia.face_shapes  (prefijo ia.)
                        id_cliente (no client_id)
  - _load_haircut_data: FROM ia.haircut_scores
                        JOIN cliente c  ON c.id_cliente = hs.id_cliente  (no clients / client_id)
                        JOIN cortes h   ON h.id_corte   = hs.id_corte    (no haircuts / haircut_id)
                        JOIN ia.haircut_features hf ON hf.id_corte = hs.id_corte
                        JOIN ia.face_measurements fm ON fm.id_cliente = c.id_cliente
                        LEFT JOIN ia.haircut_feedback hfb ON hfb.id_cliente/id_corte
                        c.id_persona → JOIN persona p para obtener genero
"""

import argparse
import logging
import os
import json
from pathlib import Path

import joblib
import numpy as np
import pandas as pd
from sklearn.ensemble import GradientBoostingClassifier, GradientBoostingRegressor
from sklearn.model_selection import train_test_split, cross_val_score, StratifiedKFold
from sklearn.preprocessing import LabelEncoder, StandardScaler
from sklearn.metrics import (
    classification_report,
    confusion_matrix,
    mean_absolute_error,
    r2_score,
)
from sklearn.pipeline import Pipeline

from database import get_connection

# ─────────────────────────────────────────────
# CONFIG
# ─────────────────────────────────────────────

logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s  %(levelname)-8s  %(message)s",
)
log = logging.getLogger(__name__)

MODELS_DIR = Path("models")
MODELS_DIR.mkdir(exist_ok=True)

FACE_MODEL_PATH    = MODELS_DIR / "face_shape_classifier.pkl"
HAIRCUT_MODEL_PATH = MODELS_DIR / "haircut_scorer.pkl"
META_PATH          = MODELS_DIR / "meta.json"

FACE_FEATURES = [
    "ratio_largo_ancho",
    "ratio_frente_mandibula",
    "ratio_pomulos_mandibula",
    "ratio_frente_pomulos",
    "jaw_angle",
    "face_roundness",
    "jaw_curvature",
    "lower_face_ratio",
    "upper_face_ratio",
    "middle_face_ratio",
    "facial_symmetry",
    "face_tilt",
]

HAIRCUT_FEATURES_COLS = [
    "adds_height",
    "reduces_width",
    "softens_jaw",
    "adds_volume_sides",
    "adds_volume_top",
    "reduces_forehead",
    "maintenance_level",
]

# ─────────────────────────────────────────────
# HELPERS DE BD
# ─────────────────────────────────────────────

def _load_face_data() -> pd.DataFrame:
    """
    Carga ia.face_measurements con su face_shape confirmado.
    FIX: prefijo ia. en ambas tablas, columna id_cliente.
    """
    conn   = get_connection()
    cursor = conn.cursor()
    try:
        cursor.execute(
            f"""
            SELECT
                fm.ratio_largo_ancho,
                fm.ratio_frente_mandibula,
                fm.ratio_pomulos_mandibula,
                fm.ratio_frente_pomulos,
                fm.jaw_angle,
                fm.face_roundness,
                fm.jaw_curvature,
                fm.lower_face_ratio,
                fm.upper_face_ratio,
                fm.middle_face_ratio,
                fm.facial_symmetry,
                fm.face_tilt,
                fs.nombre   AS forma_cara
            FROM ia.face_measurements fm
            JOIN ia.face_shapes fs ON fs.id = fm.face_shape_id
            WHERE fm.face_shape_id IS NOT NULL
              AND fm.ratio_largo_ancho IS NOT NULL
            ORDER BY fm.fecha_analisis DESC
            """
        )
        rows = cursor.fetchall()
        cols = FACE_FEATURES + ["forma_cara"]
        df   = pd.DataFrame(rows, columns=cols)
        log.info(f"[face_data] {len(df)} registros reales cargados.")
        return df
    finally:
        cursor.close()
        conn.close()


def _load_haircut_data() -> pd.DataFrame:
    """
    Construye dataset de entrenamiento para el scorer.

    FIX Bug 1: cliente  (no clients),  id_cliente  (no client_id)
    FIX Bug 2: cortes   (no haircuts),  id_corte    (no haircut_id)
    FIX Bug 3: columnas FK id_cliente / id_corte en todas las tablas ia.*
    FIX Bug 4: JOIN persona p para obtener p.genero (no c.genero)
    FIX Bug 5: prefijo ia. en haircut_scores, haircut_features,
                face_measurements, haircut_feedback
    """
    conn   = get_connection()
    cursor = conn.cursor()
    try:
        cursor.execute(
            """
            SELECT
                fm.ratio_largo_ancho,
                fm.ratio_frente_mandibula,
                fm.ratio_pomulos_mandibula,
                fm.ratio_frente_pomulos,
                fm.jaw_angle,
                fm.face_roundness,
                fm.jaw_curvature,
                fm.lower_face_ratio,
                hf.adds_height,
                hf.reduces_width,
                hf.softens_jaw,
                hf.adds_volume_sides,
                hf.adds_volume_top,
                hf.reduces_forehead,
                hf.maintenance_level,
                h.genero_objetivo,
                p.genero          AS genero_cliente,
                COALESCE(
                    AVG(hfb.rating) * 20.0,
                    hs.score
                )                 AS target_score
            FROM ia.haircut_scores hs
            JOIN cliente        c   ON c.id_cliente  = hs.id_cliente
            JOIN persona        p   ON p.id_persona  = c.id_persona
            JOIN cortes         h   ON h.id_corte    = hs.id_corte
            JOIN ia.haircut_features hf ON hf.id_corte   = hs.id_corte
            JOIN ia.face_measurements fm ON fm.id_cliente = c.id_cliente
            LEFT JOIN ia.haircut_feedback hfb
                   ON hfb.id_cliente = hs.id_cliente
                  AND hfb.id_corte   = hs.id_corte
            GROUP BY
                fm.ratio_largo_ancho, fm.ratio_frente_mandibula,
                fm.ratio_pomulos_mandibula, fm.ratio_frente_pomulos,
                fm.jaw_angle, fm.face_roundness, fm.jaw_curvature,
                fm.lower_face_ratio,
                hf.adds_height, hf.reduces_width, hf.softens_jaw,
                hf.adds_volume_sides, hf.adds_volume_top,
                hf.reduces_forehead, hf.maintenance_level,
                h.genero_objetivo, p.genero, hs.score
            """
        )
        rows = cursor.fetchall()
        cols = (
            FACE_FEATURES[:8]
            + HAIRCUT_FEATURES_COLS
            + ["genero_objetivo", "genero_cliente", "target_score"]
        )
        df = pd.DataFrame(rows, columns=cols)
        log.info(f"[haircut_data] {len(df)} registros cargados.")
        return df
    finally:
        cursor.close()
        conn.close()


# ─────────────────────────────────────────────
# PRE-PROCESAMIENTO
# ─────────────────────────────────────────────

def _encode_genero(df: pd.DataFrame) -> pd.DataFrame:
    df = df.copy()
    for col in ["genero_objetivo", "genero_cliente"]:
        if col in df.columns:
            dummies = pd.get_dummies(df[col], prefix=col, drop_first=False)
            df      = pd.concat([df.drop(columns=[col]), dummies], axis=1)
    return df


def _clean_numeric(df: pd.DataFrame, target_col: str) -> pd.DataFrame:
    df = df.copy()
    num_cols = [c for c in df.columns if c != target_col]
    for c in num_cols:
        df[c] = pd.to_numeric(df[c], errors="coerce")
    df.dropna(subset=[target_col], inplace=True)
    df.fillna(df.median(numeric_only=True), inplace=True)
    return df


# ─────────────────────────────────────────────
# DATOS SINTÉTICOS
# ─────────────────────────────────────────────

_MOLDES_PRINCIPALES = {
    "Ovalada":  [1.35, 1.10, 1.12, 0.94, 158.0, 0.74, 0.16, 0.35, 0.38, 0.27, 0.92, 2.0],
    "Redonda":  [1.08, 1.02, 1.15, 0.96, 165.0, 0.84, 0.08, 0.31, 0.36, 0.33, 0.89, 1.5],
    "Cuadrada": [1.18, 1.00, 1.03, 0.98, 138.0, 0.72, 0.26, 0.33, 0.37, 0.30, 0.91, 3.0],
    "Corazón":  [1.28, 1.28, 1.18, 1.02, 160.0, 0.76, 0.20, 0.29, 0.40, 0.31, 0.88, 2.5],
    "Alargada": [1.62, 1.08, 1.05, 0.90, 157.0, 0.64, 0.12, 0.39, 0.42, 0.22, 0.90, 1.0],
}

_MOLDES_SECUNDARIOS = {
    "Ovalada": [
        [1.30, 1.08, 1.10, 0.93, 155.0, 0.72, 0.14, 0.34, 0.36, 0.30, 0.91, 1.5],
        [1.42, 1.12, 1.14, 0.95, 161.0, 0.76, 0.18, 0.37, 0.40, 0.23, 0.93, 2.5],
    ],
    "Redonda": [
        [1.05, 1.00, 1.18, 0.97, 168.0, 0.87, 0.06, 0.30, 0.34, 0.36, 0.88, 1.0],
        [1.12, 1.04, 1.12, 0.95, 162.0, 0.80, 0.10, 0.32, 0.38, 0.30, 0.90, 2.0],
    ],
    "Cuadrada": [
        [1.15, 0.98, 1.01, 0.96, 132.0, 0.70, 0.30, 0.32, 0.36, 0.32, 0.90, 3.5],
        [1.22, 1.02, 1.05, 1.00, 142.0, 0.74, 0.22, 0.34, 0.38, 0.28, 0.92, 2.5],
    ],
    "Corazón": [
        [1.24, 1.32, 1.20, 1.05, 163.0, 0.78, 0.18, 0.27, 0.42, 0.31, 0.87, 2.0],
        [1.32, 1.24, 1.15, 0.99, 157.0, 0.74, 0.22, 0.31, 0.38, 0.31, 0.89, 3.0],
    ],
    "Alargada": [
        [1.70, 1.10, 1.07, 0.88, 154.0, 0.61, 0.10, 0.41, 0.44, 0.20, 0.89, 0.5],
        [1.55, 1.06, 1.03, 0.92, 160.0, 0.67, 0.14, 0.37, 0.40, 0.23, 0.91, 1.5],
    ],
}

_SIGMAS_BASE = [0.12, 0.09, 0.08, 0.07, 10.0, 0.07, 0.06, 0.04, 0.04, 0.04, 0.04, 1.2]

_N_POR_FORMA = {
    "Ovalada":  160,
    "Redonda":  140,
    "Cuadrada": 130,
    "Corazón":  120,
    "Alargada": 110,
}


def _generate_synthetic_face_data() -> pd.DataFrame:
    rows = []
    rng  = np.random.default_rng(42)

    mins = [0.85, 0.80, 0.85, 0.75, 110.0, 0.50, 0.00, 0.20, 0.25, 0.15, 0.70, 0.0]
    maxs = [2.00, 1.60, 1.50, 1.20, 180.0, 0.98, 0.50, 0.55, 0.60, 0.50, 1.00, 8.0]

    for forma, n_total in _N_POR_FORMA.items():
        medias_principal = _MOLDES_PRINCIPALES[forma]
        variantes_sec    = _MOLDES_SECUNDARIOS[forma]

        n_principal = int(n_total * 0.60)
        n_sec_each  = int(n_total * 0.20)

        samples_p = rng.normal(
            loc=medias_principal, scale=_SIGMAS_BASE, size=(n_principal, len(medias_principal))
        )
        for s in samples_p:
            clipped = [max(mn, min(mx, v)) for v, mn, mx in zip(s, mins, maxs)]
            rows.append(clipped + [forma])

        for variante in variantes_sec:
            samples_v = rng.normal(
                loc=variante, scale=_SIGMAS_BASE, size=(n_sec_each, len(variante))
            )
            for s in samples_v:
                clipped = [max(mn, min(mx, v)) for v, mn, mx in zip(s, mins, maxs)]
                rows.append(clipped + [forma])

    df = pd.DataFrame(rows, columns=FACE_FEATURES + ["forma_cara"])
    log.info(
        f"[synthetic_face] {len(df)} muestras generadas "
        f"({df['forma_cara'].value_counts().to_dict()})"
    )
    return df


# ─────────────────────────────────────────────
# MODELO 1 — CLASIFICADOR FORMA FACIAL
# ─────────────────────────────────────────────

def train_face_classifier(evaluate: bool = False) -> Pipeline:
    df_real = _load_face_data()

    MIN_REAL = 30
    if len(df_real) < MIN_REAL:
        log.warning(
            f"Solo {len(df_real)} registros reales — "
            "usando datos sintéticos para bootstrap."
        )
        df_syn = _generate_synthetic_face_data()
        df     = pd.concat([df_real, df_syn], ignore_index=True)
    else:
        df_syn_small = _generate_synthetic_face_data()
        df_syn_small = df_syn_small.sample(frac=0.30, random_state=42).reset_index(drop=True)
        df = pd.concat([df_real, df_syn_small], ignore_index=True)
        log.info(
            f"[face_clf] Combinando {len(df_real)} reales + "
            f"{len(df_syn_small)} sintéticos de regularización."
        )

    df = _clean_numeric(df, "forma_cara")

    X = df[FACE_FEATURES].values
    y = df["forma_cara"].values

    le    = LabelEncoder()
    y_enc = le.fit_transform(y)

    pipeline = Pipeline([
        ("scaler", StandardScaler()),
        ("clf",    GradientBoostingClassifier(
            n_estimators      = 400,
            learning_rate     = 0.05,
            max_depth         = 4,
            min_samples_split = 6,
            min_samples_leaf  = 3,
            subsample         = 0.85,
            random_state      = 42,
        )),
    ])

    if evaluate and len(np.unique(y_enc)) >= 2:
        skf    = StratifiedKFold(n_splits=5, shuffle=True, random_state=42)
        scores = cross_val_score(pipeline, X, y_enc, cv=skf, scoring="accuracy")
        log.info(f"[face_clf] CV Accuracy: {scores.mean():.3f} ± {scores.std():.3f}")

        X_tr, X_te, y_tr, y_te = train_test_split(
            X, y_enc, test_size=0.2, random_state=42, stratify=y_enc
        )
        pipeline.fit(X_tr, y_tr)
        y_pred = pipeline.predict(X_te)
        log.info("\n" + classification_report(y_te, y_pred, target_names=le.classes_))
        log.info(f"Confusion matrix:\n{confusion_matrix(y_te, y_pred)}")
    else:
        pipeline.fit(X, y_enc)

    artifact = {
        "pipeline":      pipeline,
        "label_encoder": le,
        "features":      FACE_FEATURES,
        "version":       "2.0",
    }
    joblib.dump(artifact, FACE_MODEL_PATH)
    log.info(f"[face_clf] Modelo v2 guardado → {FACE_MODEL_PATH}")
    return pipeline


# ─────────────────────────────────────────────
# MODELO 2 — SCORER DE CORTES
# ─────────────────────────────────────────────

def train_haircut_scorer(evaluate: bool = False) -> Pipeline:
    df = _load_haircut_data()

    if len(df) < 20:
        log.warning(
            f"Solo {len(df)} registros para el scorer — "
            "acumula más feedback para mejorar el modelo."
        )

    df = _encode_genero(df)

    feature_cols = [c for c in df.columns if c != "target_score"]
    df = _clean_numeric(df, "target_score")
    feature_cols = [c for c in feature_cols if c in df.columns]

    X = df[feature_cols].values.astype(float)
    y = df["target_score"].values.astype(float)
    y = np.clip(y, 0, 100)

    pipeline = Pipeline([
        ("scaler", StandardScaler()),
        ("reg",    GradientBoostingRegressor(
            n_estimators      = 300,
            learning_rate     = 0.05,
            max_depth         = 4,
            min_samples_split = 4,
            min_samples_leaf  = 2,
            subsample         = 0.8,
            random_state      = 42,
        )),
    ])

    if evaluate and len(X) >= 10:
        X_tr, X_te, y_tr, y_te = train_test_split(X, y, test_size=0.2, random_state=42)
        pipeline.fit(X_tr, y_tr)
        y_pred = pipeline.predict(X_te)
        mae = mean_absolute_error(y_te, y_pred)
        r2  = r2_score(y_te, y_pred)
        log.info(f"[haircut_scorer] MAE={mae:.2f}  R²={r2:.3f}")
    else:
        pipeline.fit(X, y)

    artifact = {
        "pipeline":     pipeline,
        "feature_cols": feature_cols,
    }
    joblib.dump(artifact, HAIRCUT_MODEL_PATH)
    log.info(f"[haircut_scorer] Modelo guardado → {HAIRCUT_MODEL_PATH}")
    return pipeline


# ─────────────────────────────────────────────
# METADATA
# ─────────────────────────────────────────────

def _save_meta():
    import datetime
    meta = {
        "trained_at":         datetime.datetime.utcnow().isoformat(),
        "face_model_path":    str(FACE_MODEL_PATH),
        "haircut_model_path": str(HAIRCUT_MODEL_PATH),
        "face_features":      FACE_FEATURES,
        "haircut_features":   HAIRCUT_FEATURES_COLS,
        "trainer_version":    "2.0",
    }
    META_PATH.write_text(json.dumps(meta, indent=2, ensure_ascii=False))
    log.info(f"Meta guardada → {META_PATH}")


# ─────────────────────────────────────────────
# ENTRY POINT
# ─────────────────────────────────────────────

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Barberia AI — ML Trainer v2")
    parser.add_argument(
        "--model",
        choices=["face", "haircut", "both"],
        default="both",
        help="Qué modelo entrenar",
    )
    parser.add_argument(
        "--eval",
        action="store_true",
        help="Evaluar con métricas después de entrenar",
    )
    args = parser.parse_args()

    if args.model in ("face", "both"):
        train_face_classifier(evaluate=args.eval)

    if args.model in ("haircut", "both"):
        train_haircut_scorer(evaluate=args.eval)

    _save_meta()
    log.info("✅  Entrenamiento v2 completado.")