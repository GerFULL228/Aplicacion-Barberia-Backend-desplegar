package com.sistemabarberia.fadex_backend.modules.cliente.repository;

import com.sistemabarberia.fadex_backend.auth.usuario.Entity.Usuario;
import com.sistemabarberia.fadex_backend.modules.cliente.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente,Integer> {


    boolean existsByPersona_PersonaId(Integer personaId);


    Optional<Cliente> findByPersona_Usuario_IdUsuario(Integer usuarioId);

    Optional<Cliente> findByPersonaUsuario(Usuario usuario);
}
