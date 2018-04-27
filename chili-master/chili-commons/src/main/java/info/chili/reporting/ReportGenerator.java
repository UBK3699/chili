/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package info.chili.reporting;

import ar.com.fdvs.dj.core.DynamicJasperHelper;
import ar.com.fdvs.dj.core.layout.ClassicLayoutManager;
import ar.com.fdvs.dj.domain.DynamicReport;
import ar.com.fdvs.dj.domain.DynamicReportOptions;
import ar.com.fdvs.dj.domain.Style;
import ar.com.fdvs.dj.domain.builders.StyleBuilder;
import ar.com.fdvs.dj.domain.constants.Border;
import ar.com.fdvs.dj.domain.constants.Font;
import ar.com.fdvs.dj.domain.constants.HorizontalAlign;
import ar.com.fdvs.dj.domain.constants.Transparency;
import ar.com.fdvs.dj.domain.constants.VerticalAlign;
import com.google.common.base.Strings;
import com.google.common.io.Files;
import info.chili.commons.pdf.PDFUtils;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import javax.ws.rs.core.Response;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;

/**
 *
 * @author anuyalamanchili
 */
public class ReportGenerator {
//TODO support for email report

    public static <T> Response generateReport(List<T> data, String reportName, String format, String location, String... columnOrder) {
        DynamicReport dynamicReport = null;
        if (Strings.isNullOrEmpty(reportName)) {
            reportName = "report";
        }
        String filename = reportName + UUID.randomUUID().toString() + "." + format;
        if (data.size() > 0) {
            dynamicReport = new ReflectiveReportBuilder(data, columnOrder).build();
            try {
                DynamicReportOptions dynamicReportOptions = setDynamicStylesOptions();
                dynamicReportOptions.getPage().setWidth(dynamicReportOptions.getPage().getWidth() + 300);
                dynamicReportOptions.setLeftMargin(0);
                dynamicReportOptions.setTopMargin(0);
                Style titleStyle = createTitleStyle();
                dynamicReport.setOptions(dynamicReportOptions);
                dynamicReport.setTitle(reportName);
                dynamicReport.setTitleStyle(titleStyle);
                dynamicReport.setOptions(dynamicReportOptions);
                JasperPrint jasperPrint = DynamicJasperHelper.generateJasperPrint(dynamicReport, new ClassicLayoutManager(), data);
                if (format.equalsIgnoreCase("xlsx")) {
                    jasperPrint.setProperty("net.sf.jasperreports.export.xls.freeze.row", "3");
                    jasperPrint.setProperty("net.sf.jasperreports.export.xls.collapse.row.span", "true");
                    JRXlsxExporter exporter = new JRXlsxExporter();
                    exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
                    exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, location + filename);
                    exporter.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
                    exporter.exportReport();
                } else if(format.equalsIgnoreCase("xls")) {
                    jasperPrint.setProperty("net.sf.jasperreports.export.xls.freeze.row", "3");
                    jasperPrint.setProperty("net.sf.jasperreports.export.xls.collapse.row.span", "true");
                    JRXlsExporter exporter = new JRXlsExporter();
                    exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
                    exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, location + filename);
                    exporter.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
                    exporter.exportReport();
                } else if (format.equalsIgnoreCase("pdf")) {
                    jasperPrint.setProperty("net.sf.jasperreports.export.xls.freeze.row", "3");
                    JasperExportManager.exportReportToPdfFile(jasperPrint, location + filename);
                } else if (format.equalsIgnoreCase("html")) {
                    JasperExportManager.exportReportToHtmlFile(jasperPrint, location + filename);
                } else if (format.equalsIgnoreCase("xml")) {
                    JasperExportManager.exportReportToXmlFile(jasperPrint, location + filename, false);
                }
            } catch (JRException ex) {
                ex.printStackTrace();
                return Response.serverError().build();
            }
        }
        return Response.ok(filename.getBytes()).build();
    }

    public static <T> String generateExcelOrderedReport(List<T> data, String reportName, String location, String[] columnOrder) {
        DynamicReport dynamicReport = null;
        String filename = reportName + UUID.randomUUID().toString() + ".xlsx";
        DynamicReportOptions dynamicReportOptions = setDynamicStylesOptions();
        dynamicReportOptions.getPage().setWidth(dynamicReportOptions.getPage().getWidth() + 300);
        dynamicReportOptions.setLeftMargin(0);
        dynamicReportOptions.setTopMargin(0);
        Style titleStyle = createTitleStyle();
        if (data.size() > 0) {
            dynamicReport = new ReflectiveReportBuilder(data, columnOrder).build();
            try {
                // the below code is necessary, if columns are set using AbstractColumn class
                // dynamicReport.setColumns(columns);
                dynamicReport.setOptions(dynamicReportOptions);
                dynamicReport.setTitle(reportName);
                dynamicReport.setTitleStyle(titleStyle);
                dynamicReport.setOptions(dynamicReportOptions);
                JasperPrint jasperPrint = DynamicJasperHelper.generateJasperPrint(dynamicReport, new ClassicLayoutManager(), data);
                jasperPrint.setProperty("net.sf.jasperreports.export.xls.freeze.row", "3");
                JRXlsxExporter exporter = new JRXlsxExporter();
                exporter.setParameter(JRExporterParameter.JASPER_PRINT,
                        jasperPrint);
                exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME,
                        location + filename);
                exporter.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
                exporter.exportReport();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
        return filename;
    }

    private static DynamicReportOptions setDynamicStylesOptions() {

        StyleBuilder oddRowBackgroundStyle = new StyleBuilder(true);
        oddRowBackgroundStyle.setBackgroundColor(Color.decode("#BDC3C7"));

        StyleBuilder defaultHeaderStyle = new StyleBuilder(true);
        defaultHeaderStyle.setBackgroundColor(Color.decode("#D7BDE2"));
        defaultHeaderStyle.setTextColor(Color.BLACK);
        defaultHeaderStyle.setFont(Font.ARIAL_MEDIUM_BOLD);
        defaultHeaderStyle.setBorder(Border.THIN());
        defaultHeaderStyle.setBorderColor(Color.BLACK);
        defaultHeaderStyle.setHorizontalAlign(HorizontalAlign.CENTER);
        defaultHeaderStyle.setVerticalAlign(VerticalAlign.MIDDLE);
        defaultHeaderStyle.setTransparency(Transparency.OPAQUE);
        defaultHeaderStyle.setStretchWithOverflow(true);

        DynamicReportOptions dynamicReportOptions = new DynamicReportOptions();
        dynamicReportOptions.setUseFullPageWidth(true);
        dynamicReportOptions.setDefaultHeaderStyle(defaultHeaderStyle.build());
        dynamicReportOptions.setPrintBackgroundOnOddRows(true);
        dynamicReportOptions.setOddRowBackgroundStyle(oddRowBackgroundStyle.build());
        dynamicReportOptions.setTitleNewPage(false);
        dynamicReportOptions.setIgnorePagination(true);
        return dynamicReportOptions;
    }

    private static Style createTitleStyle() {
        StyleBuilder styleBuilder = new StyleBuilder(true);
        styleBuilder.setFont(Font.ARIAL_BIG_BOLD);
        styleBuilder.setBorder(Border.THIN());
        styleBuilder.setBorderColor(Color.BLACK);
        styleBuilder.setBackgroundColor(Color.decode("#819FF7"));
        styleBuilder.setTextColor(Color.WHITE);
        styleBuilder.setHorizontalAlign(HorizontalAlign.CENTER);
        styleBuilder.setVerticalAlign(VerticalAlign.MIDDLE);
        styleBuilder.setTransparency(Transparency.OPAQUE);
        return styleBuilder.build();
    }

    public static <T> String generateExcelReport(List<T> data, String reportName, String location) {
        DynamicReport dynamicReport = null;
        String filename = reportName + UUID.randomUUID().toString() + ".xls";
        if (data.size() > 0) {
            dynamicReport = new ReflectiveReportBuilder(data).build();
            try {
                dynamicReport.setTitle(data.get(0).getClass().getSimpleName());
                JasperPrint jasperPrint = DynamicJasperHelper.generateJasperPrint(dynamicReport, new ClassicLayoutManager(), data);
                JRXlsExporter exporter = new JRXlsExporter();
                exporter.setParameter(JRExporterParameter.JASPER_PRINT,
                        jasperPrint);
                exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME,
                        location + filename);
                exporter.exportReport();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
        return filename;
    }

    /**
     * converts html generated from templating service or other to pdf
     *
     * @param html
     * @param reportName
     * @return
     */
    public static Response generatePDFReportFromHtml(String html, String reportName) {
        byte[] pdf = PDFUtils.convertToPDF(html);
        return Response
                .ok(pdf)
                .header("content-disposition", "filename = " + reportName + ".pdf")
                .header("Content-Type", "application/pdf")
                .header("Content-Length", pdf.length)
                .build();
    }

    /**
     * converts html generated from templating service or other to pdf repot on
     * server and return the name
     *
     * @param html
     * @param reportName
     * @return
     */
    public static Response createPDFReportFromHtml(String html, String reportName, String reportLocation) {
        byte[] pdf = PDFUtils.convertToPDF(html);
        File file = new File(reportLocation + reportName + UUID.randomUUID().toString() + ".pdf");
        try {
            Files.write(pdf, file);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        return Response.ok(reportName + ".pdf".getBytes()).header("content-disposition", "inline; filename = " + reportName + ".pdf")
                .header("Content-Length", reportName + ".pdf".length()).build();
    }

}