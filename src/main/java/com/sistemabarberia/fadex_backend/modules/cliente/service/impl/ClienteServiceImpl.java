package com.sistemabarberia.fadex_backend.modules.cliente.service.impl;

import com.sistemabarberia.fadex_backend.commons.exception.BusinessException;
import com.sistemabarberia.fadex_backend.modules.barbero.repository.BarberoRepository;
import com.sistemabarberia.fadex_backend.modules.cliente.dto.request.ClienteRequestDTO;
import com.sistemabarberia.fadex_backend.modules.cliente.dto.response.ActividadRecienteResponse;
import com.sistemabarberia.fadex_backend.modules.cliente.dto.response.ClienteDetalleResumenDTO;
import com.sistemabarberia.fadex_backend.modules.cliente.dto.response.ClienteResponseDTO;
import com.sistemabarberia.fadex_backend.modules.cliente.dto.response.ClienteResumenResponseDTO;
import com.sistemabarberia.fadex_backend.modules.cliente.entity.Cliente;
import com.sistemabarberia.fadex_backend.modules.cliente.mapper.ClienteMapper;
import com.sistemabarberia.fadex_backend.modules.cliente.repository.ClienteRepository;
import com.sistemabarberia.fadex_backend.modules.cliente.service.IClienteService;
import com.sistemabarberia.fadex_backend.modules.persona.entity.Persona;
import com.sistemabarberia.fadex_backend.modules.persona.repository.PersonaRepository;
import com.sistemabarberia.fadex_backend.modules.reserva.repository.ReservaRepository;
import com.sistemabarberia.fadex_backend.modules.venta.repository.VentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ClienteServiceImpl implements IClienteService {
    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private BarberoRepository barberoRepository;

    @Autowired
    private PersonaRepository personaRepository;

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private ClienteMapper mapper;


    //LISTAR
    @Override
    public Page<ClienteResponseDTO> listarClientes(Pageable pageable) {
        return clienteRepository.findAll(pageable)
                .map(mapper::toResponseDTO);
    }


    //CREAR
    @Override
    public ClienteResponseDTO crearCliente(ClienteRequestDTO dto) {
        //Verifica que la persona existe
        Persona persona = personaRepository.findById(dto.getPersonaId())
                .orElseThrow(() -> new BusinessException(
                        "Persona no encontrada con id: " + dto.getPersonaId(),
                        HttpStatus.NOT_FOUND
                ));
        //Verifica que la persona no está ya asignada a otro Cliente
        if (clienteRepository.existsByPersona_PersonaId(dto.getPersonaId())) {
            throw new BusinessException(
                    "Esta persona ya está registrada como Cliente",
                    HttpStatus.CONFLICT
            );
        }
        //Verifica que la persona no está ya asignada a un barbero
        if (barberoRepository.existsByPersona_PersonaId(dto.getPersonaId())) {
            throw new BusinessException(
                    "Esta persona ya está registrada como Barbero",
                    HttpStatus.CONFLICT
            );
        }

        Cliente cliente = mapper.toEntity(dto, persona);
        Cliente guardado = clienteRepository.save(cliente);
        return mapper.toResponseDTO(guardado);
    }

    //Eliminar
    @Override
    public ClienteResponseDTO eliminar(Integer id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        ClienteResponseDTO dto = mapper.toResponseDTO(cliente);

        /*-----ELIMINA CLIENTE DIRECTAMENTE PERO NO SU PERSONA-----
        clienteRepository.delete(cliente);
        return dto;*/

        Integer idPersona = cliente.getPersona().getPersonaId();
        personaRepository.deleteById(idPersona); // BORRAS LA PERSONA (esto elimina cliente automáticamente por CASCADE)
        return dto;
    }

    //Buscar
    @Override
    public ClienteResponseDTO buscarCliente(Integer id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        "Cliente no encontrado con id: " + id,
                        HttpStatus.NOT_FOUND
                ));
        return mapper.toResponseDTO(cliente);
    }

    @Override
    public ClienteResumenResponseDTO obtenerResumen() {

        Long totalClientes =
                clienteRepository.contarClientes();

        Long nuevosClientes =
                clienteRepository.contarClientesNuevosMes();

        Long clientesActivos =
                reservaRepository.clientesActivosMes();

        Double retencion =
                reservaRepository.calcularRetencion();
        Double retencionAnterior =
                reservaRepository.calcularRetencionMesAnterior();

        if (retencionAnterior == null) {
            retencionAnterior = 0.0;
        }

        Double diferenciaRetencion =
                retencion - retencionAnterior;

        String deltaRetencion =
                (diferenciaRetencion >= 0 ? "+" : "") +
                        String.format("%.1f", diferenciaRetencion) +
                        "% vs anterior";

        // NUEVOS CLIENTES
        Long nuevosActual =
                clienteRepository.contarClientesNuevosMes();

        Long nuevosAnterior =
                clienteRepository.contarClientesNuevosMesAnterior();

        Long diferenciaNuevos =
                nuevosActual - nuevosAnterior;

        String deltaNuevos =
                (diferenciaNuevos >= 0 ? "+" : "") +
                        diferenciaNuevos +
                        " vs anterior";

        // TOTAL CLIENTES
        Long totalAnterior =
                clienteRepository.contarClientesHastaMesAnterior();

        Long diferenciaTotal =
                totalClientes - totalAnterior;

        String deltaTotal =
                (diferenciaTotal >= 0 ? "+" : "") +
                        diferenciaTotal +
                        " este mes";

        Long activosAnterior =
                reservaRepository.clientesActivosMesAnterior();

        Long diferenciaActivos =
                clientesActivos - activosAnterior;

        String deltaActivos =
                (diferenciaActivos >= 0 ? "+" : "") +
                        diferenciaActivos +
                        " vs anterior";

        return ClienteResumenResponseDTO.builder()
                .totalClientes(totalClientes)
                .deltaTotalClientes(deltaTotal)

                .clientesActivosMes(clientesActivos)

                .nuevosClientes(nuevosClientes)
                .deltaNuevosClientes(deltaNuevos)

                .retencion(retencion)
                .deltaRetencion(deltaRetencion)

                .deltaClientesActivos(deltaActivos)

                .build();
    }

    @Override
    public ClienteDetalleResumenDTO obtenerResumenCliente(
            Integer clienteId
    ) {

        Long totalReservas =
                reservaRepository.contarReservasCliente(clienteId);

        Long totalCortes =
                reservaRepository.contarCortesCliente(clienteId);

        Long totalCompras =
                ventaRepository.contarComprasCliente(clienteId);

        Double totalGastado =
                ventaRepository.totalGastadoCliente(clienteId);

        java.sql.Date ultimaVisita =
                reservaRepository.ultimaVisitaCliente(clienteId);

        return ClienteDetalleResumenDTO.builder()

                .totalReservas(totalReservas)

                .totalCortes(totalCortes)

                .totalCompras(totalCompras)

                .totalGastado(totalGastado)

                .ultimaVisita(
                        ultimaVisita != null
                                ? ultimaVisita.toLocalDate().toString()
                                : "Sin visitas"
                )

                .build();
    }

    @Override
    public List<ActividadRecienteResponse> obtenerActividadReciente(
            Integer idCliente
    ) {

        List<Object[]> rows =
                clienteRepository.obtenerActividadReciente(idCliente);

        return rows.stream().map(row ->

                ActividadRecienteResponse.builder()
                        .tipo((String) row[0])
                        .titulo((String) row[1])
                        .descripcion((String) row[2])
                        .fecha(((Timestamp) row[3]).toLocalDateTime())
                        .color((String) row[4])
                        .build()

        ).toList();
    }

}

