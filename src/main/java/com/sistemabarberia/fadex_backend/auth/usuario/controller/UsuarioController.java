package com.sistemabarberia.fadex_backend.auth.usuario.controller;


import com.sistemabarberia.fadex_backend.auth.usuario.dto.request.RegisterRequest;
import com.sistemabarberia.fadex_backend.auth.usuario.dto.response.UsuarioResponse;
import com.sistemabarberia.fadex_backend.auth.usuario.service.IUsuarioService;
import com.sistemabarberia.fadex_backend.commons.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final IUsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<UsuarioResponse>>> listar() {
        return ResponseEntity.ok( ApiResponse.ok("Usuarios obtenidos correctamente", usuarioService.getAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UsuarioResponse>> buscarPorId(
            @PathVariable Integer id) {
        return ResponseEntity.ok( ApiResponse.ok("Usuario encontrado", usuarioService.getById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UsuarioResponse>> update( @PathVariable Integer id, @RequestBody RegisterRequest request) {
        return ResponseEntity.ok( ApiResponse.ok( "Usuario actualizado correctamente", usuarioService.update(id, request)));
    }

}
