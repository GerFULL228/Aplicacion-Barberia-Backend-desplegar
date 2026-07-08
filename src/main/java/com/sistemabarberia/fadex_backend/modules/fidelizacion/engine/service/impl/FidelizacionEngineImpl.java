package com.sistemabarberia.fadex_backend.modules.fidelizacion.engine.service.impl;

import com.sistemabarberia.fadex_backend.modules.cliente.entity.Cliente;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.engine.service.IFidelizacionEngine;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.tarjeta.entity.FidelizacionTarjeta;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.tarjeta.service.IFidelizacionTarjetaService;
import com.sistemabarberia.fadex_backend.modules.reserva.entity.Reserva;
import com.sistemabarberia.fadex_backend.modules.venta.entity.Venta;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FidelizacionEngineImpl implements IFidelizacionEngine {

    private final IFidelizacionTarjetaService tarjetaService;

    @Override
    public void registrarCliente(Cliente cliente) {
        tarjetaService.crearTarjetasIniciales(cliente);
    }

    @Override
    public void procesarServicio(Reserva reserva) {
        tarjetaService.acumularPorServicio(reserva);
        if(reserva.getCliente()==null){return;}
        List<FidelizacionTarjeta> tarjetas = tarjetaService.obtenerTarjetasPorCliente(reserva.getCliente().getClienteId());
        tarjetas.forEach(tarjetaService::evaluarMeta);
    }

    @Override
    public void procesarVenta(Venta venta) {
        tarjetaService.acumularPorVenta(venta);
        if(venta.getCliente()==null){return;}
        List<FidelizacionTarjeta> tarjetas = tarjetaService.obtenerTarjetasPorCliente(venta.getCliente().getClienteId());
        tarjetas.forEach(tarjetaService::evaluarMeta);
    }
}