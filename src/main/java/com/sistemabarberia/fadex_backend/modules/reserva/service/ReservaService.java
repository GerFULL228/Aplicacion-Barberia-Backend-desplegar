package com.sistemabarberia.fadex_backend.modules.reserva.service;

import com.sistemabarberia.fadex_backend.auth.usuario.Entity.Usuario;
import com.sistemabarberia.fadex_backend.auth.usuario.service.UsuarioSecurityService;
import com.sistemabarberia.fadex_backend.commons.exception.BusinessException;
import com.sistemabarberia.fadex_backend.commons.exception.ResourceNotFoundException;
import com.sistemabarberia.fadex_backend.commons.response.ApiResponse;
import com.sistemabarberia.fadex_backend.modules.barbero.entity.Barbero;

import com.sistemabarberia.fadex_backend.modules.barbero.repository.BarberoRepository;
import com.sistemabarberia.fadex_backend.modules.cliente.entity.Cliente;
import com.sistemabarberia.fadex_backend.modules.cliente.repository.ClienteRepository;
import com.sistemabarberia.fadex_backend.modules.reserva.dto.Request.ReservaRequest;
import com.sistemabarberia.fadex_backend.modules.reserva.dto.Response.ReservaDTO;
import com.sistemabarberia.fadex_backend.modules.reserva.entity.EstadoReserva;
import com.sistemabarberia.fadex_backend.modules.reserva.entity.Reserva;
import com.sistemabarberia.fadex_backend.modules.reserva.entity.TipoReserva;
import com.sistemabarberia.fadex_backend.modules.reserva.mapper.ReservaMapper;
import com.sistemabarberia.fadex_backend.modules.reserva.repository.ReservaRepository;

import com.sistemabarberia.fadex_backend.modules.servicio.entity.Servicio;

import com.sistemabarberia.fadex_backend.modules.servicio.repository.ServicioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final BarberoRepository barberoRepository;
    private final ClienteRepository clienteRepository;
    private final ServicioRepository servicioRepository;
    private final ReservaMapper reservaMapper;
    private final UsuarioSecurityService securityService;

  @Transactional
  public ReservaDTO crearReserva(ReservaRequest request){

      Usuario usuario = securityService.getUsuarioLogueado();
      String rol = securityService.getRolePrincipal();
      System.out.println("ROL DEL USUARIO: " + rol);
      Barbero barbero ;
      Cliente cliente;
      Servicio servicio = servicioRepository.findById(request.servicioId()).orElseThrow(()-> new ResourceNotFoundException("Servicio no encontrado"));

      switch (rol) {
          case "ROLE_admin" ->{
              cliente = clienteRepository.findById(request.clienteId()).orElseThrow(()-> new ResourceNotFoundException("Cliente no encontrado"));
              barbero = barberoRepository.findById(request.barberoId()).orElseThrow(()-> new ResourceNotFoundException("Barbero no encontrado"));
          }
          case "ROLE_barbero" ->{
              barbero = barberoRepository.findByPersona_Usuario_IdUsuario(usuario.getIdUsuario()).orElseThrow(
                      ()-> new ResourceNotFoundException("Barbero no encontrado")
              );
              cliente = clienteRepository.findById(request.clienteId()).orElseThrow(()-> new ResourceNotFoundException("Cliente no encontrado"));
          }
          case "ROLE_cliente" ->{
              cliente = clienteRepository.findByPersona_Usuario_IdUsuario(usuario.getIdUsuario()).orElseThrow(()-> new ResourceNotFoundException("Cliente no encontrado"));
              barbero = barberoRepository.findById(request.barberoId()).orElseThrow(()-> new ResourceNotFoundException("Barbero no encontrado"));
          }
          default ->{
              throw new BusinessException("no existe otro rol", HttpStatus.FORBIDDEN);
          }
      }
      LocalTime horaFin = request.horaInicio().plusMinutes(servicio.getDuracion());
      validarConflicto(barbero,request.fecha(),request.horaInicio(),horaFin);
      Reserva reserva= crearReservaInterna(cliente,servicio,barbero, request.fecha(),request.horaInicio(),TipoReserva.RESERVA_VIRTUAL,EstadoReserva.CONFIRMADA);
      return reservaMapper.toDto(reserva);
  }

  private Reserva crearReservaInterna( Cliente cliente,
                                       Servicio servicio,
                                       Barbero barbero,
                                       LocalDate fecha,
                                       LocalTime horaInicio,
                                       TipoReserva tipo,
                                       EstadoReserva estado
                                       ){

      LocalTime horaFin = horaInicio.plusMinutes(servicio.getDuracion());
      Reserva reserva = new Reserva();
      reserva.setCliente(cliente);
      reserva.setBarbero(barbero);
      reserva.setFecha(fecha);
      reserva.setServicio(servicio);
      reserva.setHoraInicio(horaInicio);
      reserva.setTipoReserva(tipo);
      reserva.setHoraFin(horaFin);
      reserva.setTotal(servicio.getPrecio());
      reserva.setEstadoReserva(estado);
      return reservaRepository.save(reserva);
  }

  private void validarConflicto(Barbero barbero, LocalDate fecha,
   LocalTime horaInicio, LocalTime horaFin){

      if (reservaRepository.existeConflicto(barbero.getBarberoId(), fecha, horaInicio, horaFin)){
          throw new BusinessException("hay cruce de horario: el barbero " + barbero.getPersona().getNombre() + " esta ocupado", HttpStatus.BAD_REQUEST);
      }
  }

  public List<ReservaDTO> ListarReservasPorCliente(Usuario usuario){
     Cliente cliente = clienteRepository.findByPersonaUsuario(usuario).orElseThrow(()-> new ResourceNotFoundException("Cliente no encontrado"));

     List<Reserva> reservas = reservaRepository.findByCliente_ClienteId(cliente.getClienteId());

      return reservaMapper.toDtoLista(reservas);
  }

  public List<ReservaDTO> ListarReservasAdmin(){
      return reservaRepository.findAll().stream().map(reservaMapper::toDto).toList();
  }

    public List<ReservaDTO> ListarReservasBarbero(Usuario usuario){
       Barbero barbero = barberoRepository.findByPersonaUsuario(usuario).orElseThrow(()-> new ResourceNotFoundException("Barbero no encontrado"));

        return reservaRepository.findByBarbero_BarberoId(barbero.getBarberoId()).stream().map(reservaMapper::toDto).toList();
    }

}
