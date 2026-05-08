package com.sistemabarberia.fadex_backend.auth.usuario.service.impl;

import com.sistemabarberia.fadex_backend.auth.usuario.Entity.Usuario;
import com.sistemabarberia.fadex_backend.auth.usuario.Repository.UsuarioRepository;
import com.sistemabarberia.fadex_backend.auth.usuario.dto.request.RegisterRequest;
import com.sistemabarberia.fadex_backend.auth.usuario.dto.response.UsuarioResponse;
import com.sistemabarberia.fadex_backend.auth.usuario.service.IUsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.sistemabarberia.fadex_backend.commons.exception.BusinessException;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements IUsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    public List<UsuarioResponse> getAll() {
        return usuarioRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UsuarioResponse getById(Integer id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        "Usuario no encontrado con ID: " + id,
                        HttpStatus.NOT_FOUND));
        return toResponse(usuario);
    }

    @Override
    public UsuarioResponse update(Integer id, RegisterRequest request) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        "Usuario no encontrado con ID: " + id,
                        HttpStatus.NOT_FOUND));

        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuarioRepository.save(usuario);
        return toResponse(usuario);
    }

    private UsuarioResponse toResponse(Usuario usuario) {
        String rolNombre = usuario.getRoles().isEmpty() ? "SIN ROL" :
                usuario.getRoles().iterator().next().getNombre();

        return UsuarioResponse.builder()
                .idUsuario(usuario.getIdUsuario())
                .username(usuario.getUser())
                .rol(rolNombre)
                .build();
    }
}
