package com.sistemabarberia.fadex_backend.modules.producto.dto.request;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductoRequest {
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private Integer stock;
    private boolean estado;
    private boolean publicado;
    private Long idCategoria;

}