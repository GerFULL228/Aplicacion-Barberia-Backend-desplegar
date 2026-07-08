package com.sistemabarberia.fadex_backend.modules.ruleta.validator;

import com.sistemabarberia.fadex_backend.commons.exception.BusinessException;
import com.sistemabarberia.fadex_backend.modules.ruleta.item.entity.RuletaItem;
import com.sistemabarberia.fadex_backend.modules.ruleta.ruleta.entity.Ruleta;
import com.sistemabarberia.fadex_backend.modules.ruleta.validator.dto.ResultadoSeleccionRuleta;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class RuletaValidator {

    public void validarRuleta(Ruleta ruleta) {
        if (ruleta == null) {
            throw new BusinessException("No existe una ruleta configurada.", HttpStatus.BAD_REQUEST);
        }
        if (!Boolean.TRUE.equals(ruleta.getActiva())) {
            throw new BusinessException("La ruleta está inactiva.", HttpStatus.BAD_REQUEST);
        }
    }

    public void validarItems(List<RuletaItem> items) {
        if (items.isEmpty()) {
            throw new BusinessException("La ruleta no posee premios.", HttpStatus.BAD_REQUEST);
        }
    }

    public void validarProbabilidades(List<RuletaItem> items) {
        BigDecimal total = items.stream().map(RuletaItem::getProbabilidad).reduce(BigDecimal.ZERO, BigDecimal::add);
        if (total.compareTo(BigDecimal.valueOf(100)) != 0) {
            throw new BusinessException("La suma de probabilidades debe ser exactamente 100%.", HttpStatus.BAD_REQUEST);
        }
    }

    public void validarPremioMayor(List<RuletaItem> items) {
        long premiosMayores = items.stream().filter(i -> Boolean.TRUE.equals(i.getEsPremioMayor())).count();
        if (premiosMayores != 1) {
            throw new BusinessException("La ruleta debe tener exactamente un premio mayor.", HttpStatus.BAD_REQUEST);
        }
        BigDecimal menorProbabilidad = items.stream().map(RuletaItem::getProbabilidad).min(BigDecimal::compareTo).orElseThrow();
        RuletaItem premioMayor = items.stream().filter(i -> Boolean.TRUE.equals(i.getEsPremioMayor())).findFirst().orElseThrow();
        if (premioMayor.getProbabilidad().compareTo(menorProbabilidad) != 0) {
            throw new BusinessException("El premio mayor debe tener la menor probabilidad.", HttpStatus.BAD_REQUEST);
        }
    }

    public void validarRuletaListaParaGirar(List<RuletaItem> items) {
        validarItems(items);
        validarProbabilidades(items);
        validarPremioMayor(items);
    }

//    public RuletaItem seleccionarPorAngulo(List<RuletaItem> items) {
//        List<RuletaItem> itemsOrdenados = items.stream().sorted(Comparator.comparing(RuletaItem::getOrdenDisplay)).toList();
//        double angulo = ThreadLocalRandom.current().nextDouble(0, 360);
//        double inicio = 0;
//        for (RuletaItem item : itemsOrdenados) {
//            double fin = inicio + item.getProbabilidad().doubleValue() * 3.6;
//            if (angulo >= inicio && angulo < fin) {return item;}
//            inicio = fin;
//        }
//        return itemsOrdenados.get(itemsOrdenados.size() - 1);
//    }

    public ResultadoSeleccionRuleta seleccionarPorAngulo(List<RuletaItem> items) {
        List<RuletaItem> itemsOrdenados = items.stream().sorted(Comparator.comparing(RuletaItem::getOrdenDisplay)).toList();
        double angulo = ThreadLocalRandom.current().nextDouble(0, 360);
        double inicio = 0;
        for (RuletaItem item : itemsOrdenados) {
            double fin = inicio + item.getProbabilidad().doubleValue() * 3.6;
            if (angulo >= inicio && angulo < fin) {
                return new ResultadoSeleccionRuleta(angulo, item);
            }
            inicio = fin;
        }
        return new ResultadoSeleccionRuleta(angulo, itemsOrdenados.get(itemsOrdenados.size() - 1));
    }
}