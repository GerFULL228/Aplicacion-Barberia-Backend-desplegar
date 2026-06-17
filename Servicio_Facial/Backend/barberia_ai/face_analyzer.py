"""
face_analyzer.py  v4.0
======================
Extractor PURO de features faciales usando MediaPipe Face Mesh.

Responsabilidades:
  ✅ Detectar landmarks faciales
  ✅ Calcular medidas, ratios y features geométricas
  ✅ Devolver puntos para graficar el contorno

Responsabilidades ELIMINADAS (ahora en ml_predictor.py):
  ❌ Clasificar forma facial      → predict_face_shape()
  ❌ Calcular confianza           → predict_face_shape()
  ❌ Comparar con MOLDES_IDEALES  → ml_predictor._heuristic_face()

La función pública es:

    resultado = analizar_cara(imagen_bytes: bytes) -> dict

    Devuelve un dict con:
        - features_avanzadas  : dict completo de features (ratios, ángulos, etc.)
        - puntos_grafico      : landmarks normalizados [0,1] para el frontend
        - contorno_grafico    : lista ordenada de keys para trazar el contorno
        - medidas_px          : medidas brutas en píxeles
        - error               : str (solo si falla la detección)
"""

import cv2
import mediapipe as mp
import numpy as np
import math
from typing import Dict, Tuple

mp_face_mesh = mp.solutions.face_mesh

# ─────────────────────────────────────────────────────────────
# ÍNDICES DE LANDMARKS
# ─────────────────────────────────────────────────────────────

PUNTOS = {
    "frente_izq":    70,
    "frente_der":    300,
    "ojo_izq":       468,
    "ojo_der":       473,
    "nariz":         1,
    "pomulo_izq":    234,
    "pomulo_der":    454,
    "mandibula_izq": 172,
    "mandibula_der": 397,
    "jaw_mid_izq":   200,
    "jaw_mid_der":   430,
    "frente_top":    10,
    "frente_centro": 9,
    "labio_centro":  13,
    "menton":        152,
}

CONTORNO_GRAFICO = [
    "frente_top", "frente_izq", "ojo_izq", "pomulo_izq",
    "jaw_mid_izq", "mandibula_izq", "menton", "mandibula_der",
    "jaw_mid_der", "pomulo_der", "ojo_der", "frente_der", "frente_top",
]

# ─────────────────────────────────────────────────────────────
# UTILIDADES GEOMÉTRICAS
# ─────────────────────────────────────────────────────────────

def _distancia(p1: tuple, p2: tuple) -> float:
    return math.sqrt((p1[0] - p2[0]) ** 2 + (p1[1] - p2[1]) ** 2)


def _calcular_angulo(a: tuple, b: tuple, c: tuple) -> float:
    """Ángulo en el vértice b formado por los segmentos ba y bc."""
    ab = (a[0] - b[0], a[1] - b[1])
    cb = (c[0] - b[0], c[1] - b[1])
    dot    = ab[0] * cb[0] + ab[1] * cb[1]
    norm_a = math.sqrt(ab[0] ** 2 + ab[1] ** 2)
    norm_c = math.sqrt(cb[0] ** 2 + cb[1] ** 2)
    if norm_a == 0 or norm_c == 0:
        return 0.0
    coseno = max(-1.0, min(1.0, dot / (norm_a * norm_c)))
    return math.degrees(math.acos(coseno))


def _calcular_simetria(
    ojo_izq, ojo_der, nariz, labio_centro, mandibula_izq, mandibula_der
) -> float:
    def similitud(a, b):
        avg = (a + b) / 2 if (a + b) else 1.0
        return max(0.0, 1.0 - abs(a - b) / avg)

    sim_ojos  = similitud(ojo_izq[1], ojo_der[1])
    sim_nariz = similitud(_distancia(ojo_izq, nariz), _distancia(ojo_der, nariz))
    sim_labio = similitud(
        _distancia(mandibula_izq, labio_centro),
        _distancia(mandibula_der, labio_centro),
    )
    sim_jaw   = similitud(
        _distancia(ojo_izq, mandibula_izq),
        _distancia(ojo_der, mandibula_der),
    )
    return round((sim_ojos + sim_nariz + sim_labio + sim_jaw) / 4, 3)


def _area_poligono(puntos: list) -> float:
    if len(puntos) < 3:
        return 0.0
    area = 0.0
    for i in range(len(puntos)):
        x1, y1 = puntos[i]
        x2, y2 = puntos[(i + 1) % len(puntos)]
        area += x1 * y2 - x2 * y1
    return abs(area) / 2.0


def _perimetro_poligono(puntos: list) -> float:
    if len(puntos) < 2:
        return 0.0
    return sum(
        _distancia(puntos[i], puntos[(i + 1) % len(puntos)])
        for i in range(len(puntos))
    )


def _calcular_redondez(area: float, perimetro: float) -> float:
    if perimetro <= 0:
        return 0.0
    return round((4 * math.pi * area) / (perimetro ** 2), 3)


def _calcular_curvatura(jaw_mid_izq, menton, jaw_mid_der) -> float:
    angulo = _calcular_angulo(jaw_mid_izq, menton, jaw_mid_der)
    return round(max(0.0, 180.0 - angulo) / 180.0, 3)


# ─────────────────────────────────────────────────────────────
# EXTRACCIÓN DE FEATURES
# ─────────────────────────────────────────────────────────────

def _extraer_features(
    frente_izq, frente_der, ojo_izq, ojo_der, nariz,
    pomulo_izq, pomulo_der, mandibula_izq, mandibula_der,
    jaw_mid_izq, jaw_mid_der, frente_top, frente_centro,
    labio_centro, menton,
) -> dict:
    """
    Calcula todas las métricas geométricas a partir de los landmarks px.
    NO clasifica la forma — eso es trabajo de ml_predictor.
    """

    # ── medidas brutas (px) ──────────────────────────────────
    ancho_pomulos_px   = _distancia(pomulo_izq,    pomulo_der)
    ancho_frente_px    = _distancia(frente_izq,    frente_der)
    ancho_mandibula_px = _distancia(mandibula_izq, mandibula_der)
    largo_cara_px      = _distancia(frente_top,    menton)

    # ── normalización interna (para comparación relativa) ────
    ref = (ancho_frente_px + ancho_pomulos_px + ancho_mandibula_px) / 3.0
    ref = ref if ref > 0 else 1.0

    frente_n    = ancho_frente_px    / ref
    mandibula_n = ancho_mandibula_px / ref
    pomulos_n   = ancho_pomulos_px   / ref
    largo_n     = largo_cara_px      / ref

    # ── ratios reales (px) — los que usan los modelos ML ─────
    r_la = round(largo_cara_px      / ancho_pomulos_px,   3) if ancho_pomulos_px   else 0.0
    r_fm = round(ancho_frente_px    / ancho_mandibula_px, 3) if ancho_mandibula_px else 0.0
    r_pm = round(ancho_pomulos_px   / ancho_mandibula_px, 3) if ancho_mandibula_px else 0.0
    r_fp = round(ancho_frente_px    / ancho_pomulos_px,   3) if ancho_pomulos_px   else 0.0

    # ── features complementarios ─────────────────────────────
    jaw_angle  = round(_calcular_angulo(jaw_mid_izq, menton, jaw_mid_der), 2)
    symmetry   = _calcular_simetria(
        ojo_izq, ojo_der, nariz, labio_centro, mandibula_izq, mandibula_der
    )
    curvature  = _calcular_curvatura(jaw_mid_izq, menton, jaw_mid_der)
    face_tilt  = round(abs(math.degrees(math.atan2(
        ojo_der[1] - ojo_izq[1], ojo_der[0] - ojo_izq[0]
    ))), 2)

    contorno = [
        frente_top, frente_izq, pomulo_izq, mandibula_izq,
        menton, mandibula_der, pomulo_der, frente_der,
    ]
    area      = _area_poligono(contorno)
    perimetro = _perimetro_poligono(contorno)
    roundness = _calcular_redondez(area, perimetro)

    # ── ratios de tercios faciales ───────────────────────────
    total_h = largo_cara_px if largo_cara_px > 0 else 1.0
    upper_face_ratio  = round(_distancia(frente_top,    nariz)        / total_h, 3)
    middle_face_ratio = round(_distancia(nariz,          labio_centro) / total_h, 3)
    lower_face_ratio  = round(_distancia(labio_centro,   menton)       / total_h, 3)

    return {
        # ── ratios reales (usados por ML y BD) ──────────────
        "ratio_largo_ancho":       r_la,
        "ratio_frente_mandibula":  r_fm,
        "ratio_pomulos_mandibula": r_pm,
        "ratio_frente_pomulos":    r_fp,

        # ── features de forma ────────────────────────────────
        "jaw_angle":         jaw_angle,
        "facial_symmetry":   symmetry,
        "face_roundness":    roundness,
        "jaw_curvature":     curvature,
        "face_tilt":         face_tilt,

        # ── tercios faciales ─────────────────────────────────
        "upper_face_ratio":  upper_face_ratio,
        "middle_face_ratio": middle_face_ratio,
        "lower_face_ratio":  lower_face_ratio,

        # ── área y perímetro ─────────────────────────────────
        "face_area":      round(area,      2),
        "face_perimeter": round(perimetro, 2),

        # ── normalizados (compatibilidad BD) ─────────────────
        "largo":     round(largo_n,     3),
        "frente":    round(frente_n,    3),
        "mandibula": round(mandibula_n, 3),
        "pomulos":   round(pomulos_n,   3),

        # ── landmarks (para graficar o debug) ────────────────
        "frente_izq":    frente_izq,
        "frente_der":    frente_der,
        "ojo_izq":       ojo_izq,
        "ojo_der":       ojo_der,
        "nariz":         nariz,
        "pomulo_izq":    pomulo_izq,
        "pomulo_der":    pomulo_der,
        "mandibula_izq": mandibula_izq,
        "mandibula_der": mandibula_der,
        "jaw_mid_izq":   jaw_mid_izq,
        "jaw_mid_der":   jaw_mid_der,
        "frente_top":    frente_top,
        "frente_centro": frente_centro,
        "labio_centro":  labio_centro,
        "menton":        menton,
    }


# ─────────────────────────────────────────────────────────────
# FUNCIÓN PÚBLICA
# ─────────────────────────────────────────────────────────────

def _obtener_face_mesh():
    return mp_face_mesh.FaceMesh(
        static_image_mode        = True,
        max_num_faces            = 1,
        refine_landmarks         = True,
        min_detection_confidence = 0.5,
        min_tracking_confidence  = 0.5,
    )


def analizar_cara(imagen_bytes: bytes) -> dict:
    """
    Detecta landmarks y extrae features geométricas. NO clasifica.

    Returns
    -------
    dict con las claves:
        features_avanzadas  : dict completo de features
        puntos_grafico      : {key: {x, y}} normalizados [0,1]
        contorno_grafico    : lista ordenada de keys
        medidas_px          : {ancho_pomulos, ancho_frente, ancho_mandibula, largo_cara}
        ancho_cara          : float (px, = ancho_pomulos)
        ancho_pomulos_px    : float
        ancho_frente_px     : float
        ancho_mandibula_px  : float
        largo_cara_px       : float
        (+ versiones normalizadas para compatibilidad con la BD)
        error               : str  (solo si falla)
    """
    nparr  = np.frombuffer(imagen_bytes, np.uint8)
    imagen = cv2.imdecode(nparr, cv2.IMREAD_COLOR)

    if imagen is None:
        return {"error": "No se pudo leer la imagen"}

    imagen_rgb          = cv2.cvtColor(imagen, cv2.COLOR_BGR2RGB)
    alto_img, ancho_img = imagen.shape[:2]

    face_mesh  = _obtener_face_mesh()
    resultados = face_mesh.process(imagen_rgb)

    if not resultados.multi_face_landmarks:
        return {"error": "No se detectó ninguna cara en la imagen"}

    landmarks = resultados.multi_face_landmarks[0].landmark

    def px(idx: int) -> tuple:
        lm = landmarks[idx]
        return (lm.x * ancho_img, lm.y * alto_img)

    # ── extraer todos los landmarks ──────────────────────────
    frente_izq    = px(PUNTOS["frente_izq"])
    frente_der    = px(PUNTOS["frente_der"])
    ojo_izq       = px(PUNTOS["ojo_izq"])
    ojo_der       = px(PUNTOS["ojo_der"])
    nariz         = px(PUNTOS["nariz"])
    pomulo_izq    = px(PUNTOS["pomulo_izq"])
    pomulo_der    = px(PUNTOS["pomulo_der"])
    mandibula_izq = px(PUNTOS["mandibula_izq"])
    mandibula_der = px(PUNTOS["mandibula_der"])
    jaw_mid_izq   = px(PUNTOS["jaw_mid_izq"])
    jaw_mid_der   = px(PUNTOS["jaw_mid_der"])
    frente_top    = px(PUNTOS["frente_top"])
    frente_centro = px(PUNTOS["frente_centro"])
    labio_centro  = px(PUNTOS["labio_centro"])
    menton        = px(PUNTOS["menton"])

    # ── puntos normalizados para el frontend ─────────────────
    puntos_grafico = {
        k: {
            "x": round(px(PUNTOS[k])[0] / ancho_img, 4),
            "y": round(px(PUNTOS[k])[1] / alto_img,  4),
        }
        for k in PUNTOS
    }

    # ── calcular features (sin clasificar) ───────────────────
    features = _extraer_features(
        frente_izq, frente_der, ojo_izq, ojo_der, nariz,
        pomulo_izq, pomulo_der, mandibula_izq, mandibula_der,
        jaw_mid_izq, jaw_mid_der, frente_top, frente_centro,
        labio_centro, menton,
    )

    # ── medidas brutas px (para el frontend y la BD) ─────────
    ancho_pomulos_px   = _distancia(pomulo_izq,    pomulo_der)
    ancho_frente_px    = _distancia(frente_izq,    frente_der)
    ancho_mandibula_px = _distancia(mandibula_izq, mandibula_der)
    largo_cara_px      = _distancia(frente_top,    menton)

    return {
        # ── features completos ───────────────────────────────
        "features_avanzadas": features,

        # ── gráficos ─────────────────────────────────────────
        "puntos_grafico":   puntos_grafico,
        "contorno_grafico": CONTORNO_GRAFICO,

        # ── medidas px (usadas por construir_molde_facial) ───
        "ancho_cara":         round(ancho_pomulos_px,   2),
        "ancho_pomulos_px":   round(ancho_pomulos_px,   1),
        "ancho_frente_px":    round(ancho_frente_px,    1),
        "ancho_mandibula_px": round(ancho_mandibula_px, 1),
        "largo_cara_px":      round(largo_cara_px,      1),

        # ── versiones normalizadas (compatibilidad BD) ───────
        "largo_cara":      round(features["largo"],     3),
        "ancho_frente":    round(features["frente"],    3),
        "ancho_mandibula": round(features["mandibula"], 3),
        "ancho_pomulos":   round(features["pomulos"],   3),

        # ── ratios (acceso directo desde main.py) ────────────
        "ratio_largo_ancho":       features["ratio_largo_ancho"],
        "ratio_frente_mandibula":  features["ratio_frente_mandibula"],
        "ratio_pomulos_mandibula": features["ratio_pomulos_mandibula"],
        "ratio_frente_pomulos":    features["ratio_frente_pomulos"],
    }

    # NOTA: forma_cara, confianza_pct y scores_debug
    # ya NO se devuelven aquí — los calcula predict_face_shape()
    # en main.py después de recibir este dict.