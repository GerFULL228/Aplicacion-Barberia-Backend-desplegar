package com.sistemabarberia.fadex_backend;

import com.sistemabarberia.fadex_backend.auth.usuario.Entity.Usuario;
import com.sistemabarberia.fadex_backend.auth.usuario.service.UsuarioSecurityService;
import com.sistemabarberia.fadex_backend.commons.exception.BusinessException;
import com.sistemabarberia.fadex_backend.modules.barbero.entity.Barbero;
import com.sistemabarberia.fadex_backend.modules.barbero.repository.BarberoRepository;
import com.sistemabarberia.fadex_backend.modules.cliente.entity.Cliente;
import com.sistemabarberia.fadex_backend.modules.cliente.repository.ClienteRepository;
import com.sistemabarberia.fadex_backend.modules.persona.entity.Persona;
import com.sistemabarberia.fadex_backend.modules.reserva.dto.Request.ReservaRequest;
import com.sistemabarberia.fadex_backend.modules.reserva.dto.Response.ReservaDTO;
import com.sistemabarberia.fadex_backend.modules.reserva.entity.Reserva;
import com.sistemabarberia.fadex_backend.modules.reserva.entity.TipoReserva;
import com.sistemabarberia.fadex_backend.modules.reserva.mapper.ReservaMapper;
import com.sistemabarberia.fadex_backend.modules.reserva.repository.ReservaRepository;
import com.sistemabarberia.fadex_backend.modules.reserva.service.ReservaService;
import com.sistemabarberia.fadex_backend.modules.servicio.entity.Servicio;
import com.sistemabarberia.fadex_backend.modules.servicio.repository.ServicioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservaServiceTest {

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private BarberoRepository barberoRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ServicioRepository servicioRepository;

    @Mock
    private ReservaMapper reservaMapper;

    @Mock
    private UsuarioSecurityService securityService;

    @InjectMocks
    private ReservaService reservaService;

    private ReservaRequest request;
    private Usuario usuario;
    private Cliente cliente;
    private Barbero barbero;
    private Servicio servicio;
    private Reserva reserva;

    @BeforeEach
    void setUp() {

        request = new ReservaRequest(
                1,
                1,
                1L,
                LocalDate.now(),
                LocalTime.of(10, 0)
        );

        usuario = Usuario.builder()
                .idUsuario(1)
                .build();

        Persona persona = Persona.builder()
                .nombre("Gerson")
                .build();

        cliente = new Cliente();
        cliente.setClienteId(1);

        barbero = Barbero.builder()
                .barberoId(1)
                .persona(persona)
                .build();

        servicio = new Servicio();
        servicio.setServicioId(1L);
        servicio.setDuracion(60);
        servicio.setPrecio(BigDecimal.valueOf(50));

        reserva = new Reserva();
        reserva.setCliente(cliente);
        reserva.setBarbero(barbero);
        reserva.setServicio(servicio);
        reserva.setHoraInicio(LocalTime.of(10,0));
        reserva.setHoraFin(LocalTime.of(11,0));
        reserva.setTipoReserva(TipoReserva.RESERVA_VIRTUAL);

    }

    @Test
    void deberiaCrearReservaCorrectamenteComoAdmin() {

        ReservaDTO dto = new ReservaDTO(
                1L,
                "Cliente Test",
                "Gerson",
                "corte",

                LocalDate.now(),
                LocalTime.of(10,0),
                LocalTime.of(11,0),
                TipoReserva.RESERVA_VIRTUAL,
                BigDecimal.valueOf(50)
        );

        when(securityService.getUsuarioLogueado()).thenReturn(usuario);
        when(securityService.getRolePrincipal()).thenReturn("ROLE_admin");

        when(clienteRepository.findById(1))
                .thenReturn(Optional.of(cliente));

        when(barberoRepository.findById(1))
                .thenReturn(Optional.of(barbero));

        when(servicioRepository.findById(1L))
                .thenReturn(Optional.of(servicio));

        when(reservaRepository.existeConflicto(
                anyInt(),
                any(),
                any(),
                any()
        )).thenReturn(false);

        when(reservaRepository.save(any(Reserva.class)))
                .thenReturn(reserva);

        when(reservaMapper.toDto(any(Reserva.class)))
                .thenReturn(dto);

        ReservaDTO resultado = reservaService.crearReserva(request);

        assertNotNull(resultado);

        verify(reservaRepository).save(any(Reserva.class));
    }

    @Test
    void deberiaLanzarExcepcionSiExisteConflictoHorario() {

        when(securityService.getUsuarioLogueado()).thenReturn(usuario);
        when(securityService.getRolePrincipal()).thenReturn("ROLE_admin");

        when(clienteRepository.findById(1))
                .thenReturn(Optional.of(cliente));

        when(barberoRepository.findById(1))
                .thenReturn(Optional.of(barbero));

        when(servicioRepository.findById(1L))
                .thenReturn(Optional.of(servicio));

        when(reservaRepository.existeConflicto(
                anyInt(),
                any(),
                any(),
                any()
        )).thenReturn(true);

        assertThrows(
                BusinessException.class,
                () -> reservaService.crearReserva(request)
        );

        verify(reservaRepository, never()).save(any());
    }

    @Test
    void deberiaCalcularHoraFinCorrectamente() {

        when(securityService.getUsuarioLogueado()).thenReturn(usuario);
        when(securityService.getRolePrincipal()).thenReturn("ROLE_admin");

        when(clienteRepository.findById(1))
                .thenReturn(Optional.of(cliente));

        when(barberoRepository.findById(1))
                .thenReturn(Optional.of(barbero));

        when(servicioRepository.findById(1L))
                .thenReturn(Optional.of(servicio));

        when(reservaRepository.existeConflicto(
                anyInt(),
                any(),
                any(),
                any()
        )).thenReturn(false);

        when(reservaRepository.save(any(Reserva.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(reservaMapper.toDto(any()))
                .thenReturn(mock(ReservaDTO.class));

        reservaService.crearReserva(request);

        ArgumentCaptor<Reserva> captor =
                ArgumentCaptor.forClass(Reserva.class);

        verify(reservaRepository).save(captor.capture());

        Reserva reservaGuardada = captor.getValue();

        assertEquals(
                LocalTime.of(11,0),
                reservaGuardada.getHoraFin()
        );
    }
}