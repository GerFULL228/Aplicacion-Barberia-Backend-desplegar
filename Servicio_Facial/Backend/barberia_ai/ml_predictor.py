
import logging
import math
from pathlib import Path
from typing import Dict, Tuple

import joblib
import numpy as np

log = logging.getLogger(__name__)

# ─────────────────────────────────────────────
# RUTAS
# ─────────────────────────────────────────────

MODELS_DIR         = Path("models")
FACE_MODEL_PATH    = MODELS_DIR / "face_shape_classifier.pkl"
HAIRCUT_MODEL_PATH = MODELS_DIR / "haircut_scorer.pkl"

# ─────────────────────────────────────────────
# UMBRALES Y PARÁMETROS DE FUSIÓN
# ─────────────────────────────────────────────

# Si la probabilidad top del ML es menor a esto, usamos heurístico puro.
MIN_ML_CONFIDENCE = 0.40

# Si la diferencia top1-top2 es menor a esto, activamos desempate heurístico.
PROB_GAP_THRESHOLD = 0.15

# Confianza máxima reportable al usuario.
# "100%" solo indica sobreajuste en datos sintéticos, no certeza real.
MAX_REPORTED_CONFIDENCE = 87.0

# Peso del heurístico en la fusión de probabilidades.
# 0.0 = solo ML, 1.0 = solo heurístico.
# Con dataset casi todo sintético, el heurístico aporta regularización esencial.
# Cuando haya >100 registros reales, bajar este valor a 0.20.
HEURISTIC_BLEND_WEIGHT = 0.45

# ─────────────────────────────────────────────
# CARGA LAZY
# ─────────────────────────────────────────────

_face_artifact    = None
_haircut_artifact = None


def _load_face_model():
    global _face_artifact
    if _face_artifact is None and FACE_MODEL_PATH.exists():
        try:
            _face_artifact = joblib.load(FACE_MODEL_PATH)
            ver = _face_artifact.get("version", "1.0")
            log.info(f"[ML] Face classifier v{ver} cargado desde {FACE_MODEL_PATH}")
        except Exception as e:
            log.warning(f"[ML] No se pudo cargar face model: {e}")
    return _face_artifact


def _load_haircut_model():
    global _haircut_artifact
    if _haircut_artifact is None and HAIRCUT_MODEL_PATH.exists():
        try:
            _haircut_artifact = joblib.load(HAIRCUT_MODEL_PATH)
            log.info(f"[ML] Haircut scorer cargado desde {HAIRCUT_MODEL_PATH}")
        except Exception as e:
            log.warning(f"[ML] No se pudo cargar haircut model: {e}")
    return _haircut_artifact


# ─────────────────────────────────────────────
# FALLBACK HEURÍSTICO
# ─────────────────────────────────────────────

_MOLDES_IDEALES = {
    "Ovalada":  {"ratio_largo_ancho": 1.35, "ratio_frente_mandibula": 1.10,
                 "ratio_pomulos_mandibula": 1.12, "ratio_frente_pomulos": 0.94,
                 "jaw_angle": 158, "face_roundness": 0.74,
                 "jaw_curvature": 0.16, "lower_face_ratio": 0.35},
    "Redonda":  {"ratio_largo_ancho": 1.08, "ratio_frente_mandibula": 1.02,
                 "ratio_pomulos_mandibula": 1.15, "ratio_frente_pomulos": 0.96,
                 "jaw_angle": 165, "face_roundness": 0.84,
                 "jaw_curvature": 0.08, "lower_face_ratio": 0.31},
    "Cuadrada": {"ratio_largo_ancho": 1.18, "ratio_frente_mandibula": 1.00,
                 "ratio_pomulos_mandibula": 1.03, "ratio_frente_pomulos": 0.98,
                 "jaw_angle": 138, "face_roundness": 0.72,
                 "jaw_curvature": 0.26, "lower_face_ratio": 0.33},
    "Corazón":  {"ratio_largo_ancho": 1.28, "ratio_frente_mandibula": 1.28,
                 "ratio_pomulos_mandibula": 1.18, "ratio_frente_pomulos": 1.02,
                 "jaw_angle": 160, "face_roundness": 0.76,
                 "jaw_curvature": 0.20, "lower_face_ratio": 0.29},
    "Alargada": {"ratio_largo_ancho": 1.62, "ratio_frente_mandibula": 1.08,
                 "ratio_pomulos_mandibula": 1.05, "ratio_frente_pomulos": 0.90,
                 "jaw_angle": 157, "face_roundness": 0.64,
                 "jaw_curvature": 0.12, "lower_face_ratio": 0.39},
}

_PESOS = {
    "ratio_largo_ancho": 4.0, "ratio_frente_mandibula": 3.0,
    "ratio_pomulos_mandibula": 2.8, "ratio_frente_pomulos": 2.0,
    "jaw_angle": 0.8, "face_roundness": 0.6,
    "jaw_curvature": 0.8, "lower_face_ratio": 1.5,
}

_TOLERANCIAS = {
    "Ovalada":  {"ratio_largo_ancho": 0.18, "ratio_frente_mandibula": 0.12,
                 "ratio_pomulos_mandibula": 0.10, "ratio_frente_pomulos": 0.08,
                 "jaw_angle": 12, "face_roundness": 0.12, "jaw_curvature": 0.08, "lower_face_ratio": 0.05},
    "Redonda":  {"ratio_largo_ancho": 0.10, "ratio_frente_mandibula": 0.10,
                 "ratio_pomulos_mandibula": 0.12, "ratio_frente_pomulos": 0.08,
                 "jaw_angle": 10, "face_roundness": 0.14, "jaw_curvature": 0.06, "lower_face_ratio": 0.05},
    "Cuadrada": {"ratio_largo_ancho": 0.15, "ratio_frente_mandibula": 0.08,
                 "ratio_pomulos_mandibula": 0.10, "ratio_frente_pomulos": 0.08,
                 "jaw_angle": 14, "face_roundness": 0.12, "jaw_curvature": 0.10, "lower_face_ratio": 0.05},
    "Corazón":  {"ratio_largo_ancho": 0.14, "ratio_frente_mandibula": 0.18,
                 "ratio_pomulos_mandibula": 0.12, "ratio_frente_pomulos": 0.10,
                 "jaw_angle": 12, "face_roundness": 0.12, "jaw_curvature": 0.08, "lower_face_ratio": 0.06},
    "Alargada": {"ratio_largo_ancho": 0.20, "ratio_frente_mandibula": 0.12,
                 "ratio_pomulos_mandibula": 0.10, "ratio_frente_pomulos": 0.10,
                 "jaw_angle": 12, "face_roundness": 0.10, "jaw_curvature": 0.06, "lower_face_ratio": 0.06},
}

_SHAPE_WEIGHTS = {
    "Ovalada":  {"adds_height": 0, "reduces_width": 0, "softens_jaw": 0,
                 "adds_volume_sides": 0, "adds_volume_top": 0, "reduces_forehead": 0},
    "Redonda":  {"adds_height": 3, "reduces_width": 2, "softens_jaw": 1,
                 "adds_volume_sides": 0, "adds_volume_top": 2, "reduces_forehead": 0},
    "Cuadrada": {"adds_height": 1, "reduces_width": 2, "softens_jaw": 3,
                 "adds_volume_sides": 1, "adds_volume_top": 1, "reduces_forehead": 0},
    "Alargada": {"adds_height": 0, "reduces_width": 1, "softens_jaw": 0,
                 "adds_volume_sides": 3, "adds_volume_top": 0, "reduces_forehead": 1},
    "Corazón":  {"adds_height": 0, "reduces_width": 1, "softens_jaw": 2,
                 "adds_volume_sides": 2, "adds_volume_top": 0, "reduces_forehead": 2},
}

# Orden canónico de formas (debe coincidir con el LabelEncoder del modelo)
_FORMAS_ORDEN = ["Alargada", "Corazón", "Cuadrada", "Ovalada", "Redonda"]


def _heuristic_face(features: dict) -> Tuple[str, float, dict]:
    """
    Clasifica la forma facial por similitud gaussiana con moldes ideales.
    Devuelve (forma, confianza_0_100, scores_dict_0_100).
    Los scores están en escala 0-100 y SÍ suman más de 100 (no son probabilidades).
    """
    scores = {}
    for molde, ideal in _MOLDES_IDEALES.items():
        tols = _TOLERANCIAS[molde]
        pts, cuenta = 0.0, 0.0
        for feat, peso in _PESOS.items():
            if feat not in features or feat not in ideal:
                continue
            diff  = abs(features[feat] - ideal[feat])
            tol   = tols.get(feat, 0.10)
            pts   += math.exp(-(diff**2) / (2 * tol**2)) * peso
            cuenta += peso
        scores[molde] = round((pts / cuenta * 100) if cuenta else 0, 1)

    sorted_items = sorted(scores.items(), key=lambda x: x[1], reverse=True)
    top1, s1     = sorted_items[0]
    diferencia   = s1 - sorted_items[1][1]
    sym          = features.get("facial_symmetry", 0.85)
    confianza    = max(30.0, min(MAX_REPORTED_CONFIDENCE,
                                 42 + s1 * 0.30 + diferencia * 1.7 + sym * 8))
    return top1, round(confianza, 1), scores


def _heuristic_scores_as_proba(features: dict) -> dict:
    """
    Devuelve los scores heurísticos normalizados a [0, 1] como pseudo-probabilidades.
    Útil para fusionar con las probabilidades del ML.
    """
    _, _, scores = _heuristic_face(features)
    total = sum(scores.values()) or 1.0
    return {forma: s / total for forma, s in scores.items()}


def _heuristic_haircut_score(face_features: dict, haircut_row: dict, forma: str) -> float:
    pesos       = _SHAPE_WEIGHTS.get(forma, _SHAPE_WEIGHTS["Ovalada"])
    score_bruto = sum(
        float(haircut_row.get(feat, 0)) * w for feat, w in pesos.items()
    )
    max_posible = sum(5 * w for w in pesos.values()) or 1.0
    score       = 30.0 + (score_bruto / max_posible) * 70.0
    return round(min(score, 100.0), 2)


# ─────────────────────────────────────────────
# API PÚBLICA
# ─────────────────────────────────────────────

FACE_FEATURE_ORDER = [
    "ratio_largo_ancho", "ratio_frente_mandibula",
    "ratio_pomulos_mandibula", "ratio_frente_pomulos",
    "jaw_angle", "face_roundness", "jaw_curvature",
    "lower_face_ratio", "upper_face_ratio", "middle_face_ratio",
    "facial_symmetry", "face_tilt",
]


def predict_face_shape(
    features: dict,
) -> Tuple[str, float, dict, str]:
    """
    Predice la forma facial fusionando ML + heurístico.

    ESTRATEGIA DE FUSIÓN (novedad v2.1):
    ─────────────────────────────────────
    En lugar de elegir entre ML O heurístico, fusionamos sus distribuciones
    de probabilidad con peso HEURISTIC_BLEND_WEIGHT. Esto evita que el ML
    colapse todo a 100/0 cuando está sobreajustado a datos sintéticos.

      prob_final(forma) = (1 - w) * prob_ML(forma) + w * prob_heurístico(forma)

    La forma ganadora se elige sobre prob_final, y la confianza se deriva
    de la probabilidad fusionada, capeada a MAX_REPORTED_CONFIDENCE.

    Lógica de decisión adicional:
      1. Sin modelo → heurístico puro.
      2. prob_top1_ML < MIN_ML_CONFIDENCE → heurístico puro
         (el ML está completamente perdido).
      3. gap_ML < PROB_GAP_THRESHOLD → desempate: la forma la elige el
         heurístico, la confianza viene de la fusión.
      4. ML seguro → resultado de la fusión ML + heurístico.

    Returns
    -------
    (forma_cara, confianza_pct, scores_debug, metodo)
      forma_cara    : str
      confianza_pct : float en [30, MAX_REPORTED_CONFIDENCE]
      scores_debug  : dict {forma: pct_fusionado} — SIEMPRE con margen
      metodo        : "ml_blend" | "ml_desempate" | "heuristic"
    """
    artifact = _load_face_model()

    # ── Calcular scores heurísticos (siempre los necesitamos) ──
    forma_h, conf_h, scores_h_raw = _heuristic_face(features)
    proba_h = _heuristic_scores_as_proba(features)   # normalizado a [0,1]

    if artifact is None:
        return forma_h, conf_h, scores_h_raw, "heuristic"

    try:
        pipeline   = artifact["pipeline"]
        le         = artifact["label_encoder"]
        feat_order = artifact.get("features", FACE_FEATURE_ORDER)

        X     = np.array([[features.get(f, 0.0) for f in feat_order]])
        proba = pipeline.predict_proba(X)[0]   # shape: (n_classes,)

        # Mapear probabilidades ML a dict por nombre de forma
        clases_ml = list(le.classes_)
        proba_ml  = {clases_ml[i]: float(proba[i]) for i in range(len(clases_ml))}

        # ── Ordenar top ML para detectar casos inseguros ────────
        sorted_ml  = sorted(proba_ml.items(), key=lambda x: x[1], reverse=True)
        forma_ml_1 = sorted_ml[0][0]
        prob_ml_1  = sorted_ml[0][1]
        prob_ml_2  = sorted_ml[1][1]
        gap_ml     = prob_ml_1 - prob_ml_2

        log.debug(
            f"[ML face] top1={forma_ml_1}({prob_ml_1*100:.1f}%) "
            f"top2={sorted_ml[1][0]}({prob_ml_2*100:.1f}%) "
            f"gap={gap_ml*100:.1f}pp"
        )

        # ── Caso 1: ML completamente inseguro → heurístico puro ─
        if prob_ml_1 < MIN_ML_CONFIDENCE:
            log.info(
                f"[ML face] Confianza ML baja ({prob_ml_1*100:.1f}%) → heurístico."
            )
            return forma_h, conf_h, scores_h_raw, "heuristic"

        # ── Fusión ML + heurístico ───────────────────────────────
        # Combinar todas las formas posibles
        todas_formas = set(list(proba_ml.keys()) + list(proba_h.keys()))
        w = HEURISTIC_BLEND_WEIGHT
        proba_blend = {}
        for forma in todas_formas:
            p_ml = proba_ml.get(forma, 0.0)
            p_h  = proba_h.get(forma, 0.0)
            proba_blend[forma] = (1.0 - w) * p_ml + w * p_h

        # Renormalizar (por si acaso)
        total_blend = sum(proba_blend.values()) or 1.0
        proba_blend = {f: v / total_blend for f, v in proba_blend.items()}

        # Scores debug en porcentaje (lo que ve el frontend)
        scores_debug = {
            forma: round(prob * 100, 1)
            for forma, prob in sorted(proba_blend.items(),
                                      key=lambda x: x[1], reverse=True)
        }

        sorted_blend = sorted(proba_blend.items(), key=lambda x: x[1], reverse=True)
        forma_final  = sorted_blend[0][0]
        prob_final_1 = sorted_blend[0][1]

        # Confianza: probabilidad fusionada escalada, nunca > MAX
        confianza = round(
            min(MAX_REPORTED_CONFIDENCE, prob_final_1 * 100), 1
        )
        # Garantizar mínimo razonable
        confianza = max(32.0, confianza)

        # ── Caso 2: zona ambigua post-fusión → forma la elige el heurístico
        gap_blend = prob_final_1 - sorted_blend[1][1]
        if gap_blend < PROB_GAP_THRESHOLD:
            log.info(
                f"[ML face] Gap blend pequeño ({gap_blend*100:.1f}pp) — "
                f"blend dice '{forma_final}', heurístico dice '{forma_h}' → "
                f"usando '{forma_h}'."
            )
            return forma_h, max(32.0, min(MAX_REPORTED_CONFIDENCE, conf_h)), scores_h_raw, "ml_desempate"

        log.info(
            f"[ML face] blend → {forma_final} ({confianza:.1f}%) | "
            f"scores: { {k: f'{v:.1f}%' for k, v in scores_debug.items()} }"
        )
        return forma_final, confianza, scores_debug, "ml_blend"

    except Exception as e:
        log.warning(f"[ML face] Error en predicción, usando heurístico: {e}")
        return forma_h, conf_h, scores_h_raw, "heuristic"


def predict_haircut_score(
    face_features: dict,
    haircut_row: dict,
    genero_cliente: str,
    forma_cara: str,
) -> float:
    """
    Predice el score de afinidad (0-100) entre un cliente y un corte.
    Sin cambios funcionales respecto a v1.
    """
    artifact = _load_haircut_model()

    if artifact is not None:
        try:
            pipeline     = artifact["pipeline"]
            feature_cols = artifact["feature_cols"]

            face_8 = [
                face_features.get("ratio_largo_ancho",       0.0),
                face_features.get("ratio_frente_mandibula",  0.0),
                face_features.get("ratio_pomulos_mandibula", 0.0),
                face_features.get("ratio_frente_pomulos",    0.0),
                face_features.get("jaw_angle",               0.0),
                face_features.get("face_roundness",          0.0),
                face_features.get("jaw_curvature",           0.0),
                face_features.get("lower_face_ratio",        0.0),
            ]
            haircut_7 = [
                float(haircut_row.get("adds_height",       0)),
                float(haircut_row.get("reduces_width",     0)),
                float(haircut_row.get("softens_jaw",       0)),
                float(haircut_row.get("adds_volume_sides", 0)),
                float(haircut_row.get("adds_volume_top",   0)),
                float(haircut_row.get("reduces_forehead",  0)),
                float(haircut_row.get("maintenance_level", 3)),
            ]

            generos_obj = ["Hombre", "Mujer", "Unisex"]
            generos_cli = ["Hombre", "Mujer", "Unisex"]
            go = haircut_row.get("genero_objetivo", "Unisex")
            gc = genero_cliente

            ohe_go = [1.0 if go == g else 0.0 for g in generos_obj]
            ohe_gc = [1.0 if gc == g else 0.0 for g in generos_cli]

            raw = face_8 + haircut_7 + ohe_go + ohe_gc

            if len(raw) < len(feature_cols):
                raw += [0.0] * (len(feature_cols) - len(raw))
            elif len(raw) > len(feature_cols):
                raw = raw[:len(feature_cols)]

            X     = np.array([raw])
            score = float(pipeline.predict(X)[0])
            score = round(max(0.0, min(100.0, score)), 2)
            log.debug(f"[ML haircut] score={score}")
            return score

        except Exception as e:
            log.warning(f"[ML haircut] Fallo, usando heurístico: {e}")

    return _heuristic_haircut_score(face_features, haircut_row, forma_cara)


def reload_models():
    """Fuerza recarga de los modelos desde disco (usado tras reentrenamiento)."""
    global _face_artifact, _haircut_artifact
    _face_artifact    = None
    _haircut_artifact = None
    _load_face_model()
    _load_haircut_model()
    log.info("[ML] Modelos recargados.")