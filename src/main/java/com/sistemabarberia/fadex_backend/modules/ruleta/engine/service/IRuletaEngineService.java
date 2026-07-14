package com.sistemabarberia.fadex_backend.modules.ruleta.engine.service;

import com.sistemabarberia.fadex_backend.modules.fidelizacion.tarjeta.entity.FidelizacionTarjeta;
import com.sistemabarberia.fadex_backend.modules.ruleta.recompensa.entity.RecompensaObtenida;
import com.sistemabarberia.fadex_backend.modules.ruleta.ruleta.entity.Ruleta;

public interface IRuletaEngineService {
    RecompensaObtenida ejecutarGiro(FidelizacionTarjeta tarjeta);
    Ruleta obtenerRuletaDisponible(FidelizacionTarjeta tarjeta);
}
