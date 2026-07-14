package com.sistemabarberia.fadex_backend.modules.fidelizacion.helper;

import com.sistemabarberia.fadex_backend.modules.categoria.entity.Categoria;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.configuracion.entity.FidelizacionConfiguracion;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.configuracion.repository.FidelizacionConfiguracionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CategoriaFidelizacionResolver {
    private final FidelizacionConfiguracionRepository configuracionRepository;
    public Categoria resolver(Categoria categoria) {
        Categoria actual = categoria;
        while (actual != null) {
            FidelizacionConfiguracion configuracion = configuracionRepository.findByCategoriaIdAndActivaTrue(actual.getId()).orElse(null);
            if (configuracion != null) {return actual;}
            actual = actual.getPadre();
        }
        return null;
    }
}