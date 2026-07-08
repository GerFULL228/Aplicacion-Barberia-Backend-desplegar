package com.sistemabarberia.fadex_backend.modules.ruleta.engine.service.impl;

import com.sistemabarberia.fadex_backend.commons.exception.BusinessException;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.configuracion.entity.FidelizacionConfiguracion;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.configuracion.service.IFidelizacionConfiguracionService;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.tarjeta.entity.FidelizacionTarjeta;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.tarjeta.service.IFidelizacionTarjetaService;
import com.sistemabarberia.fadex_backend.modules.ruleta.engine.service.IRuletaEngineService;
import com.sistemabarberia.fadex_backend.modules.ruleta.giro.entity.RuletaGiro;
import com.sistemabarberia.fadex_backend.modules.ruleta.giro.service.IRuletaGiroService;
import com.sistemabarberia.fadex_backend.modules.ruleta.item.entity.RuletaItem;
import com.sistemabarberia.fadex_backend.modules.ruleta.item.entity.enums.TipoPremio;
import com.sistemabarberia.fadex_backend.modules.ruleta.item.service.IRuletaItemService;
import com.sistemabarberia.fadex_backend.modules.ruleta.recompensa.entity.RecompensaObtenida;
import com.sistemabarberia.fadex_backend.modules.ruleta.recompensa.service.IRecompensaObtenidaService;
import com.sistemabarberia.fadex_backend.modules.ruleta.ruleta.entity.Ruleta;
import com.sistemabarberia.fadex_backend.modules.ruleta.validator.RuletaValidator;
import com.sistemabarberia.fadex_backend.modules.ruleta.validator.dto.ResultadoSeleccionRuleta;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RuletaEngineServiceImpl implements IRuletaEngineService {

    private final IFidelizacionTarjetaService tarjetaService;
    private final IFidelizacionConfiguracionService configuracionService;
    private final IRuletaGiroService giroService;
    private final IRecompensaObtenidaService recompensaService;
    private final IRuletaItemService ruletaItemService;
    private final RuletaValidator ruletaValidator;

    @Override
    @Transactional
    public RecompensaObtenida ejecutarGiro(FidelizacionTarjeta tarjeta) {
        if (tarjeta.getGirosDisponibles() <= 0) {
            throw new BusinessException("No posee giros disponibles.", HttpStatus.BAD_REQUEST);
        }
        if (!Boolean.TRUE.equals(tarjeta.getCicloActivo())) {
            throw new BusinessException("La tarjeta se encuentra inactiva.", HttpStatus.BAD_REQUEST);
        }
        FidelizacionConfiguracion configuracion = configuracionService.obtenerConfiguracionActiva(tarjeta.getCategoria().getId());
        if (configuracion.getRuleta() == null) {
            throw new BusinessException("La categoría no tiene una ruleta configurada.", HttpStatus.BAD_REQUEST);
        }
        Ruleta ruleta = configuracion.getRuleta();
        ruletaValidator.validarRuleta(ruleta);
        List<RuletaItem> items = ruletaItemService.obtenerItemsActivos(ruleta.getRuletaId());
        ruletaValidator.validarRuletaListaParaGirar(items);
        ResultadoSeleccionRuleta resultado = ruletaValidator.seleccionarPorAngulo(items);
        RuletaItem premio = resultado.getItem();
        double angulo = resultado.getAngulo();
        RuletaGiro giro = giroService.guardarGiro(tarjeta, tarjeta.getCliente(), ruleta, premio);
        RecompensaObtenida recompensa = null;
        if (premio.getTipoPremio() != TipoPremio.SIN_PREMIO) {
            recompensa = recompensaService.crearDesdeGiro(giro, tarjeta.getCliente(), premio);
        }

        tarjetaService.consumirGiro(tarjeta);

        return recompensa;
    }

    @Override
    @Transactional(readOnly = true)
    public Ruleta obtenerRuletaDisponible(FidelizacionTarjeta tarjeta) {
        FidelizacionConfiguracion configuracion = configuracionService.obtenerConfiguracionActiva(tarjeta.getCategoria().getId());
        ruletaValidator.validarRuleta(configuracion.getRuleta());
        return configuracion.getRuleta();
    }
}