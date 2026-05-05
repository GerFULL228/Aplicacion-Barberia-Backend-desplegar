package com.sistemabarberia.fadex_backend.commons.storage;

import com.sistemabarberia.fadex_backend.commons.exception.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Value("${app.base-url}")
    private String baseUrl;

    private static final long MAX_SIZE = 2 * 1024 * 1024; // 2MB

    private static final List<String> TIPOS_IMAGEN = List.of("image/jpeg", "image/png", "image/webp");


    public String guardarArchivo(MultipartFile archivo, String subCarpeta, List<String> tiposPermitidos) {

        if (archivo == null || archivo.isEmpty()) {
            throw new BusinessException("Archivo inválido o vacío", HttpStatus.BAD_REQUEST);
        }

        if (archivo.getSize() > MAX_SIZE) {
            throw new BusinessException("Archivo demasiado grande (máx 2MB)", HttpStatus.BAD_REQUEST);
        }

        String contentType = archivo.getContentType();
        if (tiposPermitidos != null && !tiposPermitidos.contains(contentType)) {
            throw new BusinessException("Formato no permitido: " + contentType, HttpStatus.BAD_REQUEST);
        }

        String extension = obtenerExtensionSegura(contentType);

        LocalDate ahora = LocalDate.now();
        String fechaPath = String.format("%d/%02d", ahora.getYear(), ahora.getMonthValue());

        try {
            Path rutaDestino = Paths.get(uploadDir, subCarpeta, fechaPath);

            if (!Files.exists(rutaDestino)) {
                Files.createDirectories(rutaDestino);
            }

            String nuevoNombre = UUID.randomUUID().toString() + extension;
            Path rutaCompleta = rutaDestino.resolve(nuevoNombre);

            Files.copy(archivo.getInputStream(), rutaCompleta, StandardCopyOption.REPLACE_EXISTING);

            String rutaRelativa = subCarpeta + "/" + fechaPath + "/" + nuevoNombre;
            return baseUrl + "/uploads/" + rutaRelativa;

        } catch (IOException e) {
            throw new BusinessException("Error al guardar el archivo", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private String obtenerExtensionSegura(String contentType) {
        return switch (contentType) {
            case "image/jpeg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            default -> throw new BusinessException("Tipo de archivo no soportado", HttpStatus.BAD_REQUEST);
        };
    }


    public void eliminarArchivo(String rutaRelativa) {
        if (rutaRelativa == null || rutaRelativa.isBlank()) return;

        try {
            Path rutaCompleta = Paths.get(uploadDir, rutaRelativa).normalize().toAbsolutePath();

            boolean eliminado = Files.deleteIfExists(rutaCompleta);

            if (eliminado) {
                System.out.println("Archivo eliminado: " + rutaCompleta);
            } else {
                System.out.println("Archivo no encontrado: " + rutaCompleta);
            }

        } catch (IOException e) {
            throw new BusinessException("Error al eliminar archivo", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    public String urlAPathRelativo(String urlPublica) {
        if (urlPublica == null || urlPublica.isBlank()) return null;
        if (!urlPublica.startsWith(baseUrl + "/uploads/")) {
            throw new BusinessException("URL no válida: " + urlPublica, HttpStatus.BAD_REQUEST);
        }
        return urlPublica.replace(baseUrl + "/uploads/", "");
    }
}