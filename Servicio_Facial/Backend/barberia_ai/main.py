from fastapi import FastAPI, UploadFile, File, HTTPException, Form
from fastapi.middleware.cors import CORSMiddleware
from ml_auto_retrain import AutoRetrainer
from contextlib import asynccontextmanager
import logging
import uvicorn
import os        # ← agregar
import base64    # ← agregar
import httpx     # ← agregar
from dotenv import load_dotenv
from pathlib import Path
load_dotenv(dotenv_path=Path(__file__).parent / ".env")
logger_key = os.getenv("OPENAI_API_KEY")
print(f"KEY CARGADA: {logger_key[:10] if logger_key else 'NO ENCONTRADA'}")

from database import get_connection
from face_analyzer import analizar_cara
from ml_predictor import predict_face_shape

# ============================================
# CONFIGURAR LOGGING
# ============================================
retrainer = AutoRetrainer()


logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s - %(levelname)s - %(message)s"
)

logger = logging.getLogger(__name__)

# ============================================
# FASTAPI APP
# ============================================

@asynccontextmanager
async def lifespan(app: FastAPI):
    logger.info("Iniciando API Barberia AI...")
    retrainer.start()
    yield
    retrainer.stop()

    logger.info("Cerrando API Barberia AI...")


app = FastAPI(
    title="Barberia La Ocasion - API",
    description="Sistema inteligente de recomendacion de cortes",
    version="3.0.0",
    lifespan=lifespan
)

# ============================================
# CORS
# ============================================

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)
# ============================================
# FUNCIONES AUXILIARES
# ============================================

def construir_molde_facial(
    forma_detectada: str,
    confianza_pct: float,
    resultado_ml: dict,
    metodo: str = "heuristic",
) -> dict:
    """
    Determina si el molde facial es confiable y construye el resultado.

    Umbrales dinámicos según el método de predicción:
      - "ml_blend"    : 58%
      - "ml_desempate": 55%
      - "heuristic"   : 62%
    """

    _UMBRALES = {
        "ml_blend":     58.0,
        "ml_desempate": 55.0,
        "heuristic":    62.0,
    }
    umbral_confianza = _UMBRALES.get(metodo, 60.0)

    apto = (
        confianza_pct >= umbral_confianza
        and forma_detectada not in ("Desconocida", None, "")
    )

    if apto:
        if confianza_pct >= 78:
            mensaje = "Forma facial detectada con alta certeza."
        elif confianza_pct >= 65:
            mensaje = "Forma facial detectada con buena certeza."
        else:
            mensaje = "Forma facial detectada. Puede haber rasgos de otra forma secundaria."
    else:
        if forma_detectada in ("Desconocida", None, ""):
            mensaje = "No se pudo determinar la forma facial. Intenta con mejor iluminación."
        else:
            mensaje = (
                f"La cara muestra rasgos de '{forma_detectada}' "
                "pero con margen de incertidumbre. El barbero puede confirmar."
            )

    return {
        "apto": apto,
        "estado": "Sí da" if apto else "Revisar",
        "mensaje": mensaje,
        "forma": forma_detectada,
        "confianza_pct": round(confianza_pct, 1),
        "umbral_confianza": umbral_confianza,
        "metodo": metodo,

        "ratios": {
            "ratio_largo_ancho":
                round(float(resultado_ml.get("ratio_largo_ancho", 0)), 3),
            "ratio_frente_mandibula":
                round(float(resultado_ml.get("ratio_frente_mandibula", 0)), 3),
            "ratio_pomulos_mandibula":
                round(float(resultado_ml.get("ratio_pomulos_mandibula", 0)), 3),
        },

        "mediciones_px": {
            "ancho_pomulos":
                round(float(resultado_ml.get("ancho_pomulos_px", 0)), 1),
            "ancho_frente":
                round(float(resultado_ml.get("ancho_frente_px", 0)), 1),
            "ancho_mandibula":
                round(float(resultado_ml.get("ancho_mandibula_px", 0)), 1),
            "largo_cara":
                round(float(resultado_ml.get("largo_cara_px", 0)), 1),
        }
    }


# ============================================
# PESOS POR FORMA FACIAL
# ============================================

SHAPE_WEIGHTS = {
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


def obtener_cortes(cursor, face_shape_id: int, genero_cliente: str, id_cliente: int,
                    pagina: int = 1, por_pagina: int = 10):
    """
    Devuelve los cortes recomendados para una forma facial, ordenados de
    mayor a menor score (los más recomendados primero), paginados.

    Retorna: (lista_de_cortes, total_cortes)
    """

    cursor.execute(
        "SELECT nombre FROM ia.face_shapes WHERE id = %s",
        (face_shape_id,)
    )
    row = cursor.fetchone()
    forma_nombre = row[0] if row else "Ovalada"
    pesos = SHAPE_WEIGHTS.get(forma_nombre, SHAPE_WEIGHTS["Ovalada"])

    if genero_cliente in ("Hombre", "Mujer"):
        genero_filter = (genero_cliente, "Unisex")
    else:
        genero_filter = ("Hombre", "Mujer", "Unisex")

    placeholders = ",".join(["%s"] * len(genero_filter))
    offset = (pagina - 1) * por_pagina

    if forma_nombre == "Ovalada":
        score_expr = "(6 - hf.maintenance_level)"
        orden = "score DESC"
    else:
        score_expr = " + ".join([
            f"COALESCE(hf.{feature}, 0) * {peso}"
            for feature, peso in pesos.items()
            if peso > 0
        ]) or "0"
        orden = "score DESC, hf.maintenance_level ASC"

    # COUNT(*) OVER() trae el total de resultados sin filtrar por LIMIT,
    # en la misma consulta — evita un segundo round-trip a la BD.
    cursor.execute(
        f"""
        SELECT
            c.id_corte,
            c.nombre,
            c.descripcion,
            c.imagen_url,
            c.dificultad,
            ({score_expr}) AS score,
            COUNT(*) OVER() AS total_cortes
        FROM ia.haircut_features hf
        JOIN cortes c ON c.id_corte = hf.id_corte
        WHERE c.genero_objetivo IN ({placeholders})
        ORDER BY {orden}
        LIMIT %s OFFSET %s
        """,
        genero_filter + (por_pagina, offset)
    )

    filas = cursor.fetchall()
    total = filas[0][6] if filas else 0

    # Solo registramos el score de los cortes que realmente se muestran
    # en esta página (igual que el comportamiento original con el top 5)
    for f in filas:
        score_value = round(float(f[5]), 2) if f[5] is not None else 0.0
        cursor.execute(
            """
            INSERT INTO ia.haircut_scores (id_cliente, id_corte, score, algorithm, model_version)
            VALUES (%s, %s, %s, %s, %s)
            """,
            (id_cliente, f[0], score_value, "shape_weights_v1", "1.0.0")
        )

    cortes = [
        {
            "id":          f[0],
            "nombre":      f[1],
            "descripcion": f[2],
            "imagen_url":  f[3],
            "dificultad":  f[4],
            "score":       round(float(f[5]), 2) if f[5] is not None else 0.0,
        }
        for f in filas
    ]

    return cortes, total
# ============================================
# ROOT
# ============================================

@app.get("/")
def inicio():
    return {
        "status": "ok",
        "mensaje": "API Barberia AI funcionando"
    }


# ============================================
# ANALIZAR ROSTRO
# ============================================

@app.post("/analizar")
async def analizar_y_recomendar(
    foto: UploadFile = File(...),
    id_cliente: int = Form(...)
):

    # ── Validar imagen ───────────────────────────────────────
    if not foto.content_type.startswith("image/"):
        raise HTTPException(
            status_code=400,
            detail="El archivo debe ser una imagen"
        )

    imagen_bytes = await foto.read()

    if len(imagen_bytes) == 0:
        raise HTTPException(
            status_code=400,
            detail="La imagen está vacía"
        )

    # ── Análisis IA ──────────────────────────────────────────
    resultado_ml = analizar_cara(imagen_bytes)

    if "error" in resultado_ml:
        raise HTTPException(
            status_code=400,
            detail=resultado_ml["error"]
        )

    features = resultado_ml.get("features_avanzadas", {})

    forma_detectada, confianza_pct, scores_debug, metodo_clf = (
        predict_face_shape(features)
    )

    # ── Molde facial ─────────────────────────────────────────
    molde_facial = construir_molde_facial(
        forma_detectada,
        confianza_pct,
        resultado_ml,
        metodo=metodo_clf,
    )

    conn = get_connection()
    cursor = conn.cursor()

    try:

        # ──────────────────────────────────────────────
        # Obtener datos del cliente existente
        # ──────────────────────────────────────────────

        cursor.execute(
            """
            SELECT
                p.nombre,
                p.genero
            FROM cliente c
            JOIN persona p
                ON p.id_persona = c.id_persona
            WHERE c.id_cliente = %s
            """,
            (id_cliente,)
        )

        cliente = cursor.fetchone()

        if not cliente:
            raise HTTPException(
                status_code=404,
                detail="Cliente no encontrado"
            )

        nombre_cliente = cliente[0]
        genero_cliente = cliente[1]

        logger.info(
            f"Nuevo análisis para: {nombre_cliente} ({genero_cliente})"
        )

        # ──────────────────────────────────────────────
        # Buscar forma facial
        # ──────────────────────────────────────────────

        cursor.execute(
            """
            SELECT id
            FROM ia.face_shapes
            WHERE nombre = %s
            """,
            (forma_detectada,)
        )

        face_shape = cursor.fetchone()

        if not face_shape:
            raise HTTPException(
                status_code=404,
                detail=f"No existe la forma facial: {forma_detectada}"
            )

        face_shape_id = face_shape[0]

        # ──────────────────────────────────────────────
        # Guardar mediciones
        # ──────────────────────────────────────────────

        cursor.execute(
            """
            INSERT INTO ia.face_measurements (
                id_cliente,
                face_shape_id,
                predicted_face_shape_id,
                ancho_cara,
                largo_cara,
                ancho_frente,
                ancho_mandibula,
                ancho_pomulos,
                ratio_largo_ancho,
                ratio_frente_mandibula,
                ratio_pomulos_mandibula,
                ratio_frente_pomulos,
                jaw_angle,
                facial_symmetry,
                jaw_curvature,
                face_roundness,
                face_tilt,
                upper_face_ratio,
                middle_face_ratio,
                lower_face_ratio,
                face_area,
                face_perimeter,
                confianza_pct
            )
            VALUES (
                %s, %s, %s, %s, %s,
                %s, %s, %s, %s, %s,
                %s, %s, %s, %s, %s,
                %s, %s, %s, %s, %s,
                %s, %s, %s
            )
            """,
            (
                id_cliente,
                face_shape_id,
                face_shape_id,

                round(float(resultado_ml.get("ancho_cara", 0)), 2),
                round(float(resultado_ml.get("largo_cara", 0)), 3),
                round(float(resultado_ml.get("ancho_frente", 0)), 3),
                round(float(resultado_ml.get("ancho_mandibula", 0)), 3),
                round(float(resultado_ml.get("ancho_pomulos", 0)), 3),

                round(float(resultado_ml.get("ratio_largo_ancho", 0)), 3),
                round(float(features.get("ratio_frente_mandibula", 0)), 3),
                round(float(features.get("ratio_pomulos_mandibula", 0)), 3),
                round(float(features.get("ratio_frente_pomulos", 0)), 3),
                round(float(features.get("jaw_angle", 0)), 2),
                round(float(features.get("facial_symmetry", 0)), 3),
                round(float(features.get("jaw_curvature", 0)), 3),
                round(float(features.get("face_roundness", 0)), 3),
                round(float(features.get("face_tilt", 0)), 2),
                round(float(features.get("upper_face_ratio", 0)), 3),
                round(float(features.get("middle_face_ratio", 0)), 3),
                round(float(features.get("lower_face_ratio", 0)), 3),
                round(float(features.get("face_area", 0)), 2),
                round(float(features.get("face_perimeter", 0)), 2),
                round(float(confianza_pct), 2),
            )
        )

        # ──────────────────────────────────────────────
        # Guardar foto
        # ──────────────────────────────────────────────

        image_url = resultado_ml.get("image_url")
        thumbnail = resultado_ml.get("thumbnail_url")
        landmarks = resultado_ml.get("landmarks_json")
        embedding = resultado_ml.get("embedding_vector")

        if any([image_url, thumbnail, landmarks, embedding]):

            import json

            cursor.execute(
                """
                INSERT INTO ia.client_photos (
                    id_cliente,
                    image_url,
                    thumbnail_url,
                    landmarks_json,
                    embedding_vector
                )
                VALUES (%s, %s, %s, %s, %s)
                """,
                (
                    id_cliente,
                    image_url,
                    thumbnail,
                    json.dumps(landmarks) if landmarks else None,
                    json.dumps(embedding) if embedding else None,
                )
            )

        # ──────────────────────────────────────────────
        # Obtener recomendaciones
        # ──────────────────────────────────────────────

        cortes,total_cortes = obtener_cortes(
            cursor,
            face_shape_id,
            genero_cliente,
            id_cliente,
            pagina=1,
            por_pagina=10
        )

        conn.commit()

        logger.info(
            f"Análisis completado correctamente para cliente {id_cliente}"
        )

        return {
            "success": True,
            "cliente_id": id_cliente,
            "nombre": nombre_cliente,
            "genero": genero_cliente,
            "forma_cara": forma_detectada,
            "confianza_pct": round(confianza_pct, 1),
            "scores_debug": scores_debug,
            "molde_facial": molde_facial,
            "features_avanzadas": features,
            "puntos_grafico": resultado_ml.get("puntos_grafico", {}),
            "contorno_grafico": resultado_ml.get("contorno_grafico", []),
            "cortes_recomendados": cortes,
            "total_cortes": total_cortes,
            "pagina": 1,
            "por_pagina": 10,
        }

    except HTTPException:
        conn.rollback()
        raise

    except Exception as e:
        conn.rollback()
        logger.exception("Error interno del servidor")
        raise HTTPException(
            status_code=500,
            detail=str(e)
        )

    finally:
        cursor.close()
        conn.close()

# ============================================
# Endpoint nuevo para pedir más páginas sin re-analizar
# ============================================

@app.get("/cortes/recomendados/{id_cliente}")
def obtener_cortes_paginados(id_cliente: int, pagina: int = 1, por_pagina: int = 10):

    if pagina < 1 or por_pagina < 1:
        raise HTTPException(status_code=400, detail="pagina y por_pagina deben ser mayores a 0")

    conn = get_connection()
    cursor = conn.cursor()

    try:
        cursor.execute(
            """
            SELECT p.genero, fm.predicted_face_shape_id
            FROM cliente c
            JOIN persona p ON p.id_persona = c.id_persona
            LEFT JOIN ia.face_measurements fm ON fm.id_cliente = c.id_cliente
            WHERE c.id_cliente = %s
            ORDER BY fm.id DESC
            LIMIT 1
            """,
            (id_cliente,)
        )
        row = cursor.fetchone()

        if not row or row[1] is None:
            raise HTTPException(
                status_code=404,
                detail="El cliente no tiene un análisis facial registrado"
            )

        genero_cliente, face_shape_id = row

        cortes, total = obtener_cortes(
            cursor, face_shape_id, genero_cliente, id_cliente, pagina, por_pagina
        )
        conn.commit()

        total_paginas = (total + por_pagina - 1) // por_pagina

        return {
            "cliente_id": id_cliente,
            "pagina": pagina,
            "por_pagina": por_pagina,
            "total_cortes": total,
            "total_paginas": total_paginas,
            "cortes_recomendados": cortes,
        }

    except HTTPException:
        conn.rollback()
        raise
    except Exception as e:
        conn.rollback()
        logger.exception("Error al obtener cortes paginados")
        raise HTTPException(status_code=500, detail=str(e))
    finally:
        cursor.close()
        conn.close()
# ============================================
# FEEDBACK
# ============================================

@app.post("/feedback")
def guardar_feedback(
    client_id: int = Form(...),
    haircut_id: int = Form(...),
    liked: bool = Form(...),
    rating: int = Form(...)
):
    """
    Guarda el feedback del usuario sobre un corte recomendado.
    FIX Bug 3 + 5 + 6: ia.haircut_feedback con id_cliente e id_corte.
    """
    logger.info(f"Feedback: Cliente {client_id}, Corte {haircut_id}, Liked {liked}, Rating {rating}")

    if not (1 <= rating <= 5):
        raise HTTPException(status_code=400, detail="Rating debe estar entre 1 y 5")

    conn   = get_connection()
    cursor = conn.cursor()

    try:
        # FIX Bug 5 + 6: ia.haircut_feedback, columnas id_cliente e id_corte
        cursor.execute(
            """
            INSERT INTO ia.haircut_feedback (id_cliente, id_corte, liked, rating)
            VALUES (%s, %s, %s, %s)
            """,
            (client_id, haircut_id, liked, rating)
        )
        conn.commit()
        logger.info("Feedback guardado exitosamente")

        return {
            "success":    True,
            "mensaje":    "Feedback guardado correctamente",
            "client_id":  client_id,
            "haircut_id": haircut_id
        }

    except Exception as e:
        conn.rollback()
        logger.exception("Error al guardar feedback")
        raise HTTPException(status_code=500, detail=str(e))

    finally:
        cursor.close()
        conn.close()


# ============================================
# LISTAR CORTES
# ============================================

@app.get("/cortes")
def listar_cortes():
    conn   = get_connection()
    cursor = conn.cursor()

    try:
        # FIX Bug 2: tabla cortes (no haircuts), columna id_corte
        cursor.execute(
            """
            SELECT id_corte, nombre, descripcion, dificultad, imagen_url, genero_objetivo
            FROM cortes
            ORDER BY id_corte
            """
        )
        cortes = cursor.fetchall()

        return [
            {
                "id":              c[0],
                "nombre":          c[1],
                "descripcion":     c[2],
                "dificultad":      c[3],
                "imagen_url":      c[4],
                "genero_objetivo": c[5],
            }
            for c in cortes
        ]

    finally:
        cursor.close()
        conn.close()


# ============================================
# HISTORIAL CLIENTES
# ============================================

@app.get("/clientes")
def listar_clientes():
    conn   = get_connection()
    cursor = conn.cursor()

    try:
        # FIX Bug 1 + 4: JOIN persona + cliente, prefijo ia., columnas correctas
        cursor.execute(
            """
            SELECT
                c.id_cliente,
                p.nombre,
                p.genero,
                fs.nombre         AS forma_cara,
                fm.confianza_pct,
                c.fecha_registro
            FROM cliente c
            JOIN persona p              ON p.id_persona    = c.id_persona
            LEFT JOIN ia.face_measurements fm ON fm.id_cliente = c.id_cliente
            LEFT JOIN ia.face_shapes fs       ON fs.id         = fm.face_shape_id
            ORDER BY c.fecha_registro DESC
            """
        )
        clientes = cursor.fetchall()

        return [
            {
                "id":           c[0],
                "nombre":       c[1],
                "genero":       c[2],
                "forma_cara":   c[3],
                "confianza":    c[4],
                "fecha_visita": str(c[5]),
            }
            for c in clientes
        ]

    finally:
        cursor.close()
        conn.close()

@app.post("/ia/preview-corte")
async def preview_corte(
    foto: UploadFile = File(...),
    nombre_corte: str = Form(...)
):
    if not foto.content_type.startswith("image/"):
        raise HTTPException(status_code=400, detail="El archivo debe ser una imagen")

    foto_bytes = await foto.read()
    foto_b64 = base64.b64encode(foto_bytes).decode("utf-8")

    async with httpx.AsyncClient(timeout=120) as client:
        response = await client.post(
            "https://api.openai.com/v1/images/edits",
            headers={
                "Authorization": f"Bearer {os.getenv('OPENAI_API_KEY')}",
            },
            data={
                "model": "gpt-image-1",
                "prompt": f"Modifica el cabello de esta persona aplicándole el corte de barbería '{nombre_corte}'. Mantén exactamente los mismos rasgos faciales, tono de piel y fondo. Solo cambia el cabello.",
                "n": "1",
                "size": "1024x1024"
            },
            files={
                "image": ("foto.jpg", foto_bytes, "image/jpeg")
            }
        )

    if response.status_code != 200:
        raise HTTPException(status_code=500, detail=f"Error OpenAI: {response.text}")

    data = response.json()
    imagen_b64 = data["data"][0]["b64_json"]

    return {"imagen_b64": imagen_b64}

# ============================================
# START SERVER
# ============================================

if __name__ == "__main__":
    uvicorn.run(
        "main:app",
        host="0.0.0.0",
        port=8000,
        reload=True
    )
