package com.sistemabarberia.fadex_backend.auth.usuario.dto.response;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class UsuarioResponse {

    private Integer idUsuario;
    private String username;
    private String rol;
}
