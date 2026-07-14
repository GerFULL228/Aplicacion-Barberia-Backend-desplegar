package com.sistemabarberia.fadex_backend.modules.ruleta.recompensa.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CanjearRecompensaRequestDTO {
    @NotBlank
    private String codigoCanje;
}