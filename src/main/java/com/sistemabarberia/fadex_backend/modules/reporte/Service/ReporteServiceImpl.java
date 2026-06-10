package com.sistemabarberia.fadex_backend.modules.reporte.Service;

import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.UnitValue;
import com.sistemabarberia.fadex_backend.modules.reporte.dto.ResumenDiaDTO;
import com.sistemabarberia.fadex_backend.modules.reserva.entity.Reserva;
import com.sistemabarberia.fadex_backend.modules.reserva.repository.ReservaRepository;
import com.itextpdf.layout.element.Table;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReporteServiceImpl implements ReporteService {
    private final ReservaRepository reservaRepository;

    @Override
    public List<ResumenDiaDTO> getResumenSemanal(LocalDate desde, LocalDate hasta) {
        List<Object[]> rows = reservaRepository.resumenSemanal(desde, hasta);
        return rows.stream().map(r -> new ResumenDiaDTO(
                (String) r[0],
                ((Number) r[1]).longValue(),
                ((Number) r[2]).longValue(),
                ((Number) r[3]).longValue(),
                r[4] != null ? (BigDecimal) r[4] : BigDecimal.ZERO
        )).toList();
    }

    // ─── PDF RESERVAS ───────────────────────────────────────────
    @Override
    public byte[] generarReservasPdf(LocalDate desde, LocalDate hasta) {
        List<Reserva> reservas = reservaRepository.findReservasPorPeriodo(desde, hasta);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdf = new PdfDocument(writer);
        Document doc = new Document(pdf);

        doc.add(new Paragraph("Reporte de Reservas")
                .setBold().setFontSize(16));
        doc.add(new Paragraph("Período: " + desde + " al " + hasta));

        Table table = new Table(UnitValue.createPercentArray(new float[]{1, 2, 2, 2, 2, 2}))
                .useAllAvailableWidth();
        table.addHeaderCell("ID");
        table.addHeaderCell("Fecha");
        table.addHeaderCell("Cliente");
        table.addHeaderCell("Barbero");
        table.addHeaderCell("Servicio");
        table.addHeaderCell("Total");

        for (Reserva r : reservas) {
            table.addCell(String.valueOf(r.getId()));
            table.addCell(r.getFecha().toString());
            table.addCell(r.getCliente().getPersona().getNombre());
            table.addCell(r.getBarbero().getPersona().getNombre());
            table.addCell(r.getServicio().getNombre());
            table.addCell("S/ " + r.getTotal());
        }

        doc.add(table);
        doc.close();
        return out.toByteArray();
    }

    // ─── EXCEL RESERVAS ─────────────────────────────────────────
    @Override
    public byte[] generarReservasExcel(LocalDate desde, LocalDate hasta) {
        List<Reserva> reservas = reservaRepository.findReservasPorPeriodo(desde, hasta);
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Reservas");
            Row header = sheet.createRow(0);
            String[] cols = {"ID", "Fecha", "Cliente", "Barbero", "Servicio", "Estado", "Total"};
            for (int i = 0; i < cols.length; i++) {
                header.createCell(i).setCellValue(cols[i]);
            }

            int rowNum = 1;
            for (Reserva r : reservas) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(r.getId());
                row.createCell(1).setCellValue(r.getFecha().toString());
                row.createCell(2).setCellValue(r.getCliente().getPersona().getNombre());
                row.createCell(3).setCellValue(r.getBarbero().getPersona().getNombre());
                row.createCell(4).setCellValue(r.getServicio().getNombre());
                row.createCell(5).setCellValue(r.getEstadoReserva().toString());
                row.createCell(6).setCellValue(r.getTotal().doubleValue());
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generando Excel de reservas", e);
        }
    }

    // ─── PDF VENTAS ─────────────────────────────────────────────
    @Override
    public byte[] generarVentasPdf(LocalDate desde, LocalDate hasta) {
        List<Reserva> reservas = reservaRepository.findReservasPorPeriodo(desde, hasta);
        BigDecimal total = reservaRepository.calcularIngresosPorPeriodo(desde, hasta);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdf = new PdfDocument(writer);
        Document doc = new Document(pdf);

        doc.add(new Paragraph("Reporte de Ventas e Ingresos")
                .setBold().setFontSize(16));
        doc.add(new Paragraph("Período: " + desde + " al " + hasta));
        doc.add(new Paragraph("Total ingresos: S/ " + (total != null ? total : BigDecimal.ZERO))
                .setBold());

        Table table = new Table(UnitValue.createPercentArray(new float[]{2, 2, 3, 2}))
                .useAllAvailableWidth();
        table.addHeaderCell("Fecha");
        table.addHeaderCell("Servicio");
        table.addHeaderCell("Estado");
        table.addHeaderCell("Total");

        for (Reserva r : reservas) {
            if (r.getEstadoReserva().toString().equals("FINALIZADA")) {
                table.addCell(r.getFecha().toString());
                table.addCell(r.getServicio().getNombre());
                table.addCell(r.getEstadoReserva().toString());
                table.addCell("S/ " + r.getTotal());
            }
        }

        doc.add(table);
        doc.close();
        return out.toByteArray();
    }

    // ─── EXCEL VENTAS ────────────────────────────────────────────
    @Override
    public byte[] generarVentasExcel(LocalDate desde, LocalDate hasta) {
        List<Reserva> reservas = reservaRepository.findReservasPorPeriodo(desde, hasta);
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Ventas");
            Row header = sheet.createRow(0);
            String[] cols = {"Fecha", "Servicio", "Estado", "Total"};
            for (int i = 0; i < cols.length; i++) {
                header.createCell(i).setCellValue(cols[i]);
            }

            int rowNum = 1;
            for (Reserva r : reservas) {
                if (r.getEstadoReserva().toString().equals("FINALIZADA")) {
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(r.getFecha().toString());
                    row.createCell(1).setCellValue(r.getServicio().getNombre());
                    row.createCell(2).setCellValue(r.getEstadoReserva().toString());
                    row.createCell(3).setCellValue(r.getTotal().doubleValue());
                }
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generando Excel de ventas", e);
        }
    }

    // ─── PDF CLIENTES ────────────────────────────────────────────
    @Override
    public byte[] generarClientesPdf(LocalDate desde, LocalDate hasta) {
        List<Reserva> reservas = reservaRepository.findReservasPorPeriodo(desde, hasta);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdf = new PdfDocument(writer);
        Document doc = new Document(pdf);

        doc.add(new Paragraph("Reporte de Clientes").setBold().setFontSize(16));
        doc.add(new Paragraph("Período: " + desde + " al " + hasta));

        Table table = new Table(UnitValue.createPercentArray(new float[]{2, 2, 2, 2}))
                .useAllAvailableWidth();
        table.addHeaderCell("Cliente");
        table.addHeaderCell("Fecha");
        table.addHeaderCell("Servicio");
        table.addHeaderCell("Total");

        for (Reserva r : reservas) {
            table.addCell(r.getCliente().getPersona().getNombre());
            table.addCell(r.getFecha().toString());
            table.addCell(r.getServicio().getNombre());
            table.addCell("S/ " + r.getTotal());
        }

        doc.add(table);
        doc.close();
        return out.toByteArray();
    }

    // ─── EXCEL CLIENTES ──────────────────────────────────────────
    @Override
    public byte[] generarClientesExcel(LocalDate desde, LocalDate hasta) {
        List<Reserva> reservas = reservaRepository.findReservasPorPeriodo(desde, hasta);
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Clientes");
            Row header = sheet.createRow(0);
            String[] cols = {"Cliente", "Fecha", "Servicio", "Estado", "Total"};
            for (int i = 0; i < cols.length; i++) {
                header.createCell(i).setCellValue(cols[i]);
            }

            int rowNum = 1;
            for (Reserva r : reservas) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(r.getCliente().getPersona().getNombre());
                row.createCell(1).setCellValue(r.getFecha().toString());
                row.createCell(2).setCellValue(r.getServicio().getNombre());
                row.createCell(3).setCellValue(r.getEstadoReserva().toString());
                row.createCell(4).setCellValue(r.getTotal().doubleValue());
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generando Excel de clientes", e);
        }
    }

    // ─── PDF BARBEROS ────────────────────────────────────────────
    @Override
    public byte[] generarBarberosPdf(LocalDate desde, LocalDate hasta) {
        List<Reserva> reservas = reservaRepository.findReservasPorPeriodo(desde, hasta);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdf = new PdfDocument(writer);
        Document doc = new Document(pdf);

        doc.add(new Paragraph("Reporte de Actividad de Barberos").setBold().setFontSize(16));
        doc.add(new Paragraph("Período: " + desde + " al " + hasta));

        Table table = new Table(UnitValue.createPercentArray(new float[]{2, 2, 2, 2}))
                .useAllAvailableWidth();
        table.addHeaderCell("Barbero");
        table.addHeaderCell("Fecha");
        table.addHeaderCell("Servicio");
        table.addHeaderCell("Total");

        for (Reserva r : reservas) {
            table.addCell(r.getBarbero().getPersona().getNombre());
            table.addCell(r.getFecha().toString());
            table.addCell(r.getServicio().getNombre());
            table.addCell("S/ " + r.getTotal());
        }

        doc.add(table);
        doc.close();
        return out.toByteArray();
    }

    // ─── EXCEL BARBEROS ──────────────────────────────────────────
    @Override
    public byte[] generarBarberosExcel(LocalDate desde, LocalDate hasta) {
        List<Reserva> reservas = reservaRepository.findReservasPorPeriodo(desde, hasta);
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Barberos");
            Row header = sheet.createRow(0);
            String[] cols = {"Barbero", "Fecha", "Servicio", "Estado", "Total"};
            for (int i = 0; i < cols.length; i++) {
                header.createCell(i).setCellValue(cols[i]);
            }

            int rowNum = 1;
            for (Reserva r : reservas) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(r.getBarbero().getPersona().getNombre());
                row.createCell(1).setCellValue(r.getFecha().toString());
                row.createCell(2).setCellValue(r.getServicio().getNombre());
                row.createCell(3).setCellValue(r.getEstadoReserva().toString());
                row.createCell(4).setCellValue(r.getTotal().doubleValue());
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generando Excel de barberos", e);
        }
    }

}
