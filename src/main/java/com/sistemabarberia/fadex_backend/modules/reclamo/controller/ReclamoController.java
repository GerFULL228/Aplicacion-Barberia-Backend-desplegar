package com.sistemabarberia.fadex_backend.modules.reclamo.controller;

import com.sistemabarberia.fadex_backend.commons.response.ApiResponse;
import com.sistemabarberia.fadex_backend.commons.response.PageResponse;
import com.sistemabarberia.fadex_backend.modules.reclamo.dto.ReclamoFiltro;
import com.sistemabarberia.fadex_backend.modules.reclamo.dto.ReclamoResumen;
import com.sistemabarberia.fadex_backend.modules.reclamo.dto.request.ReclamoPublicoRequest;
import com.sistemabarberia.fadex_backend.modules.reclamo.dto.request.ReclamoRequest;
import com.sistemabarberia.fadex_backend.modules.reclamo.dto.request.ReclamoSolucionRequest;
import com.sistemabarberia.fadex_backend.modules.reclamo.dto.response.ReclamoResponse;
import com.sistemabarberia.fadex_backend.modules.reclamo.service.IReclamoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("api/v1/reclamos")
@RequiredArgsConstructor
public class ReclamoController {

    private final IReclamoService reclamoService;

   //@PreAuthorize("hasAuthority('RECLAMO_CREATE')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ReclamoResponse>> crear(@RequestPart("reclamo") @Valid ReclamoRequest request, @RequestPart(value = "archivos", required = false) List<MultipartFile> archivos) {
        ReclamoResponse reclamo = reclamoService.crearReclamo(request, archivos);
        return ResponseEntity.ok(ApiResponse.ok("Reclamo creado correctamente", reclamo));
    }

}