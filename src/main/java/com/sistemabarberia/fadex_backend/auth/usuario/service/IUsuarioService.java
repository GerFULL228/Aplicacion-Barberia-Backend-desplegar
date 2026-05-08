package com.sistemabarberia.fadex_backend.auth.usuario.service;

import com.sistemabarberia.fadex_backend.auth.usuario.dto.request.RegisterRequest;
import com.sistemabarberia.fadex_backend.auth.usuario.dto.response.UsuarioResponse;

import java.util.List;

public interface IUsuarioService {

    List<UsuarioResponse> getAll();
    UsuarioResponse getById(Integer id);
    UsuarioResponse update(Integer id, RegisterRequest request);
}
