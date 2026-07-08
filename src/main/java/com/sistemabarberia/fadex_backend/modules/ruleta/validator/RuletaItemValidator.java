package com.sistemabarberia.fadex_backend.modules.ruleta.validator;

import com.sistemabarberia.fadex_backend.commons.exception.BusinessException;
import com.sistemabarberia.fadex_backend.modules.ruleta.item.dto.request.RuletaItemRequestDTO;
import com.sistemabarberia.fadex_backend.modules.ruleta.item.entity.RuletaItem;
import com.sistemabarberia.fadex_backend.modules.ruleta.item.repository.RuletaItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RuletaItemValidator {

    private final RuletaItemRepository ruletaItemRepository;

    public void validarPremioMayor(RuletaItemRequestDTO dto, Long itemId) {
        List<RuletaItem> items = ruletaItemRepository.findByRuletaRuletaIdAndActivoTrue(dto.getRuletaId());
        if (itemId != null) {
            items.removeIf(i -> i.getItemId().equals(itemId));
        }
        if (Boolean.TRUE.equals(dto.getEsPremioMayor())) {
            boolean existe = items.stream().anyMatch(i -> Boolean.TRUE.equals(i.getEsPremioMayor()));
            if (existe) {
                throw new BusinessException("La ruleta ya tiene un premio mayor.", HttpStatus.BAD_REQUEST);
            }
            for (RuletaItem item : items) {
                if (dto.getProbabilidad().compareTo(item.getProbabilidad()) > 0) {
                    throw new BusinessException("El premio mayor debe tener la menor probabilidad.", HttpStatus.BAD_REQUEST);
                }
            }
            return;
        }
        RuletaItem premioMayor = items.stream().filter(i -> Boolean.TRUE.equals(i.getEsPremioMayor())).findFirst().orElse(null);
        if (premioMayor != null && dto.getProbabilidad().compareTo(premioMayor.getProbabilidad()) < 0) {
            throw new BusinessException("Ningún premio puede tener una probabilidad menor que el premio mayor.", HttpStatus.BAD_REQUEST);
        }
    }

    public void validarProbabilidadesRuleta(Long ruletaId) {
        List<RuletaItem> items = ruletaItemRepository.findByRuletaRuletaIdAndActivoTrue(ruletaId);
        BigDecimal total = items.stream().map(RuletaItem::getProbabilidad).reduce(BigDecimal.ZERO, BigDecimal::add);
        if (total.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new BusinessException("La suma de las probabilidades no puede superar el 100%.", HttpStatus.BAD_REQUEST);
        }
    }
}