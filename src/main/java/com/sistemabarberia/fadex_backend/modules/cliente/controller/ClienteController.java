package com.sistemabarberia.fadex_backend.modules.cliente.controller;

import com.sistemabarberia.fadex_backend.commons.response.ApiResponse;
import com.sistemabarberia.fadex_backend.commons.response.PageResponse;
import com.sistemabarberia.fadex_backend.modules.cliente.dto.request.ClienteRequestDTO;
import com.sistemabarberia.fadex_backend.modules.cliente.dto.response.ActividadRecienteResponse;
import com.sistemabarberia.fadex_backend.modules.cliente.dto.response.ClienteDetalleResumenDTO;
import com.sistemabarberia.fadex_backend.modules.cliente.dto.response.ClienteResponseDTO;
import com.sistemabarberia.fadex_backend.modules.cliente.dto.response.ClienteResumenResponseDTO;
import com.sistemabarberia.fadex_backend.modules.cliente.service.IClienteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/clientes")
public class ClienteController {

    @Autowired
    private IClienteService clienteService;

    /*CRUD BASICO*/


    //Listar
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ClienteResponseDTO>>> listarClientes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ClienteResponseDTO> result = clienteService.listarClientes(pageable);
        return ResponseEntity.ok(ApiResponse.ok("Clientes obtenidos correctamente", result));
    }

    //Buscar
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ClienteResponseDTO>> buscarCliente(@PathVariable Integer id) {
        ClienteResponseDTO cliente = clienteService.buscarCliente(id);
        return ResponseEntity.ok(ApiResponse.ok("Cliente obtenido correctamente", cliente));
    }


    //Crear cliente
    @PostMapping
    public ResponseEntity<ApiResponse<ClienteResponseDTO>> crearCliente(@Valid @RequestBody ClienteRequestDTO requestDTO) {
        ClienteResponseDTO creado = clienteService.crearCliente(requestDTO);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Cliente creado correctamente", creado));
    }

    //{"personaId": 1}


    //Eliminar cliente
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<ApiResponse<ClienteResponseDTO>> eliminar(@PathVariable Integer id) {
        ClienteResponseDTO eliminado = clienteService.eliminar(id);
        return ResponseEntity.ok(ApiResponse.ok("Cliente eliminado correctamente", eliminado));
    }
    /*AVANZADOS*/

    @GetMapping("/resumen")
    public ResponseEntity<ApiResponse<ClienteResumenResponseDTO>> obtenerResumen() {

        ClienteResumenResponseDTO resumen =
                clienteService.obtenerResumen();

        return ResponseEntity.ok(
                ApiResponse.ok(
                        "Resumen obtenido correctamente",
                        resumen
                )
        );
    }

    @GetMapping("/{id}/resumen")
    public ResponseEntity<ApiResponse<ClienteDetalleResumenDTO>>
    obtenerResumenCliente(@PathVariable Integer id) {

        ClienteDetalleResumenDTO resumen =
                clienteService.obtenerResumenCliente(id);

        return ResponseEntity.ok(
                ApiResponse.ok(
                        "Resumen del cliente obtenido correctamente",
                        resumen
                )
        );
    }

    @GetMapping("/{id}/actividad")
    public ResponseEntity<ApiResponse<List<ActividadRecienteResponse>>>
    obtenerActividad(@PathVariable Integer id) {

        List<ActividadRecienteResponse> actividad =
                clienteService.obtenerActividadReciente(id);

        return ResponseEntity.ok(
                ApiResponse.ok(
                        "Actividad reciente obtenida correctamente",
                        actividad
                )
        );
    }

}
