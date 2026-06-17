"""
ml_auto_retrain.py
==================
Servicio de reentrenamiento automático en background.

FIX: ia.haircut_feedback (prefijo ia. faltaba en la query de chequeo)
"""

import logging
import threading
import time
from datetime import datetime, timezone
from pathlib import Path

log = logging.getLogger(__name__)

RETRAIN_INTERVAL_HOURS = 6
MIN_NEW_FEEDBACK       = 10
LOCK_FILE              = Path("models/.retrain_lock")


class AutoRetrainer:

    def __init__(self):
        self._stop_event    = threading.Event()
        self._thread        = threading.Thread(
            target=self._loop,
            name="AutoRetrainer",
            daemon=True,
        )
        self._last_retrain: datetime | None = None

    def start(self):
        log.info("[AutoRetrain] Iniciando hilo de reentrenamiento automático.")
        self._thread.start()

    def stop(self):
        log.info("[AutoRetrain] Deteniendo hilo.")
        self._stop_event.set()

    def _loop(self):
        self._stop_event.wait(timeout=300)
        while not self._stop_event.is_set():
            try:
                self._check_and_retrain()
            except Exception as e:
                log.exception(f"[AutoRetrain] Error en ciclo: {e}")
            self._stop_event.wait(timeout=RETRAIN_INTERVAL_HOURS * 3600)

    def _check_and_retrain(self):
        from database import get_connection

        conn   = get_connection()
        cursor = conn.cursor()
        try:
            # FIX Bug 5: ia.haircut_feedback (prefijo ia.)
            if self._last_retrain:
                cursor.execute(
                    """
                    SELECT COUNT(*) FROM ia.haircut_feedback
                    WHERE created_at > %s
                    """,
                    (self._last_retrain,),
                )
            else:
                cursor.execute("SELECT COUNT(*) FROM ia.haircut_feedback")

            count = cursor.fetchone()[0]
            log.info(f"[AutoRetrain] Feedback nuevo desde último entreno: {count}")

            if count >= MIN_NEW_FEEDBACK:
                self._retrain()
            else:
                log.info(
                    f"[AutoRetrain] No es suficiente ({count} < {MIN_NEW_FEEDBACK}). "
                    "Esperando más feedback..."
                )
        finally:
            cursor.close()
            conn.close()

    def _retrain(self):
        if LOCK_FILE.exists():
            log.warning("[AutoRetrain] Lock activo — reentrenamiento ya en curso.")
            return

        LOCK_FILE.touch()
        log.info("[AutoRetrain] ▶ Iniciando reentrenamiento...")

        try:
            from ml_trainer import train_face_classifier, train_haircut_scorer, _save_meta

            train_face_classifier(evaluate=False)
            train_haircut_scorer(evaluate=False)
            _save_meta()

            from ml_predictor import reload_models
            reload_models()

            self._last_retrain = datetime.now(timezone.utc)
            log.info(
                f"[AutoRetrain] ✅ Reentrenamiento completado "
                f"({self._last_retrain.isoformat()})"
            )

        except Exception as e:
            log.exception(f"[AutoRetrain] ❌ Error durante reentrenamiento: {e}")

        finally:
            LOCK_FILE.unlink(missing_ok=True)

    def status(self) -> dict:
        return {
            "last_retrain":           self._last_retrain.isoformat() if self._last_retrain else None,
            "retrain_interval_hours": RETRAIN_INTERVAL_HOURS,
            "min_new_feedback":       MIN_NEW_FEEDBACK,
            "lock_active":            LOCK_FILE.exists(),
        }