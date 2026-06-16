package com.sistemabarberia.fadex_backend.modules.reclamo.service;

public interface IReclamoEmailService {
    void enviarConfirmacionCliente(String email, String nombreCliente, String numeroReclamo);
    void enviarCambioEstado(String email, String nombreCliente, String numeroReclamo, String estado);
}
