package com.sistemabarberia.fadex_backend.modules.servicio.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sistemabarberia.fadex_backend.commons.response.ApiResponse;
import com.sistemabarberia.fadex_backend.modules.servicio.dto.request.ServicioRequestDTO;
import com.sistemabarberia.fadex_backend.modules.servicio.dto.response.ServicioResponseDTO;
import com.sistemabarberia.fadex_backend.modules.servicio.service.IServicioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/servicio")
@RequiredArgsConstructor
public class ServicioController {

    private final IServicioService servicioService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ServicioResponseDTO>>> listar() {
        return ResponseEntity.ok(
                ApiResponse.ok(
                        "Servicios obtenidos correctamente",
                        servicioService.listar()
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ServicioResponseDTO>> obtenerPorId(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                ApiResponse.ok(
                        "Servicio obtenido correctamente",
                        servicioService.obtenerPorId(id)
                )
        );
    }

    @GetMapping("/categoria/{categoriaId}")
    public ResponseEntity<ApiResponse<List<ServicioResponseDTO>>> listarPorCategoria(
            @PathVariable Long categoriaId
    ) {
        return ResponseEntity.ok(
                ApiResponse.ok(
                        "Servicios por categoría obtenidos correctamente",
                        servicioService.listarPorCategoria(categoriaId)
                )
        );
    }

    @PreAuthorize("hasAuthority('SERVICIO_CREATE')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ServicioResponseDTO>> crear(
            @RequestPart("servicio") String servicio,
            @RequestPart(value = "archivos", required = false) List<MultipartFile> archivos
    ) throws Exception {

        ObjectMapper objectMapper = new ObjectMapper();

        ServicioRequestDTO request =
                objectMapper.readValue(servicio, ServicioRequestDTO.class);

        ServicioResponseDTO responseDTO =
                servicioService.crear(request, archivos);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.ok(
                                "Servicio creado correctamente",
                                responseDTO
                        )
                );
    }

    @PreAuthorize("hasAuthority('SERVICIO_UPDATE_ALL')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ServicioResponseDTO>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ServicioRequestDTO dto
    ) {

        ServicioResponseDTO actualizado =
                servicioService.actualizar(id, dto);

        return ResponseEntity.ok(
                ApiResponse.ok(
                        "Servicio actualizado correctamente",
                        actualizado
                )
        );
    }

    @PreAuthorize("hasAuthority('SERVICIO_DELETE_ALL')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(
            @PathVariable Long id
    ) {

        servicioService.eliminar(id);

        return ResponseEntity.ok(
                ApiResponse.ok(
                        "Servicio eliminado correctamente",
                        null
                )
        );
    }
}