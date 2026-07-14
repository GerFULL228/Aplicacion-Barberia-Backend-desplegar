package com.sistemabarberia.fadex_backend.modules.fidelizacion.engine.service;

import com.sistemabarberia.fadex_backend.modules.cliente.entity.Cliente;
import com.sistemabarberia.fadex_backend.modules.reserva.entity.Reserva;
import com.sistemabarberia.fadex_backend.modules.venta.entity.Venta;

public interface IFidelizacionEngine {
    void registrarCliente(Cliente cliente);
    void procesarServicio(Reserva reserva);
    void procesarVenta(Venta venta);
}
