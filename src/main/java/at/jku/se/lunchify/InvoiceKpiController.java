package at.jku.se.lunchify;

import at.jku.se.lunchify.models.InvoiceKpiService;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.chart.PieChart;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;


import javax.imageio.ImageIO;
import javax.swing.filechooser.FileSystemView;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Map;
import java.util.Objects;
/**
 * Testing nicht sinnvoll, nur einfache Berechnungen - muss das getestet werden?
 * z.B. Rechnungen/User/Monat -> neue Methode wäre notwendig
 */
//AI-Assisted
public class InvoiceKpiController {

    @FXML
    private Label labelUser;
    @FXML
    private Label labelMonths;
    @FXML
    private Label labelUserCount;
    @FXML
    private Label labelInvoiceCount;
    @FXML
    private Label labelSumReimbursement;
    @FXML
    private Label labelInvoicesPerUserPerMonth;
    @FXML
    private Label labelInvoicesPerUser;
    @FXML
    private Label labelInvoiceType;
    @FXML
    private Label labelSelectedDateRange;
    @FXML
    private BarChart<String, Number> monthlyBarChart;
    @FXML
    private PieChart chartTypeDistribution;

    private static String userEmail;
    private static String invoiceType;
    private static Date fromDate;
    private static Date toDate;

    private InvoiceKpiService invoiceKpiService;
    private final File homeDirectory = FileSystemView.getFileSystemView().getHomeDirectory();

    public static void initData(String email, String type, Date from, Date to) {
        userEmail = email;
        invoiceType = type;
        fromDate = from;
        toDate = to;
    }

    @FXML
    public void initialize() {
        InvoiceKpiService service = new InvoiceKpiService(userEmail, invoiceType, fromDate, toDate);

        labelUser.setText(service.getUserName());
        labelMonths.setText(String.valueOf(service.getMonths()));
        labelUserCount.setText(String.valueOf(service.getUserCount()));
        labelInvoiceCount.setText(String.valueOf(service.getInvoiceCount()));
        labelSumReimbursement.setText(String.format("%.2f €", service.getSumReimbursements()));
        labelInvoicesPerUserPerMonth.setText(String.format("%.2f", (double) service.getInvoiceCount() / service.getUserCount() / service.getMonths()));
        labelInvoicesPerUser.setText(String.format("%.2f", (double) service.getInvoiceCount() / service.getUserCount()));
        labelInvoiceType.setText(Objects.equals(invoiceType, "alle Rechnungstypen") ? "Alle Rechnungstypen" : invoiceType);
        labelSelectedDateRange.setText(fromDate + " bis " + toDate);


        // Pie Chart
        // Werte aus dem Service
        int supermarketCount = service.getInvoiceCountSupermarket();
        int restaurantCount = service.getInvoiceCountRestaurant();
        int total = supermarketCount + restaurantCount;
        // PieChart.Data mit Anzahl und Prozent
        PieChart.Data supermarket = new PieChart.Data(
                String.format("Supermarkt: %d (%.1f%%)", supermarketCount, 100.0 * supermarketCount / total),
                supermarketCount
        );
        PieChart.Data restaurant = new PieChart.Data(
                String.format("Restaurant: %d (%.1f%%)", restaurantCount, 100.0 * restaurantCount / total),
                restaurantCount
        );
        // Hinzufügen zum Chart
        chartTypeDistribution.getData().clear();
        chartTypeDistribution.getData().addAll(supermarket, restaurant);

        //Farbe für Pie-Chart
        //Platform.runLater(() -> {
        supermarket.getNode().setStyle("-fx-pie-color: #10e1c3;");
        restaurant.getNode().setStyle("-fx-pie-color: #a0e196;");

        chartTypeDistribution.setLegendVisible(false); // Pie-Chart ist selbsterklärend
        /*
        for (Node legend : chartTypeDistribution.lookupAll(".chart-legend-item")) {
            if (legend instanceof Label label) {
                if (label.getText().startsWith("Supermarkt")) {
                    label.getGraphic().setStyle("-fx-background-color: #10e1c3;");
                } else if (label.getText().startsWith("Restaurant")) {
                    label.getGraphic().setStyle("-fx-background-color: #a0e196;");
                }
            }
        }*/
        //});


        //Bar-Chart
        populateMonthlyStatsChart(service);
        invoiceKpiService = service;
    }

    private void populateMonthlyStatsChart(InvoiceKpiService invoiceKpi) {
        Map<YearMonth, InvoiceKpiService.InvoiceMonthlyStats> stats = invoiceKpi.getMonthlyInvoiceStats();

        XYChart.Series<String, Number> invoiceCountSeries = new XYChart.Series<>();
        invoiceCountSeries.setName("Anzahl Rechnungen");

        XYChart.Series<String, Number> reimbursementSeries = new XYChart.Series<>();
        reimbursementSeries.setName("Erstattungsbetrag (€)");

        for (Map.Entry<YearMonth, InvoiceKpiService.InvoiceMonthlyStats> entry : stats.entrySet()) {
            String monthLabel = entry.getKey().toString(); // z.B. "2024-05"
            InvoiceKpiService.InvoiceMonthlyStats data = entry.getValue();

            invoiceCountSeries.getData().add(new XYChart.Data<>(monthLabel, data.getInvoiceCount()));
            reimbursementSeries.getData().add(new XYChart.Data<>(monthLabel, data.getReimbursementTotal()));
        }

        monthlyBarChart.getData().clear();
        monthlyBarChart.getData().addAll(invoiceCountSeries, reimbursementSeries);
        monthlyBarChart.setLegendSide(Side.TOP);
        monthlyBarChart.setLegendVisible(false);

        // Farben & Werte als Labels setzen – nach dem Rendern
        //Platform.runLater(() -> {
        for (int i = 0; i < monthlyBarChart.getData().size(); i++) {
            XYChart.Series<String, Number> series = monthlyBarChart.getData().get(i);
            String color = (i == 0) ? "#10e1c3" : "#a0e196"; // Rechnungen /Erstattungen

            for (XYChart.Data<String, Number> data : series.getData()) {
                Node node = data.getNode();
                if (node != null) {
                    node.setStyle("-fx-bar-fill: " + color + ";");

                    StackPane barPane = (StackPane) data.getNode();
                    Label label = new Label(data.getYValue().toString());
                    label.setStyle("-fx-font-size: 10px; -fx-text-fill: black; -fx-font-weight: bold;");
                    barPane.getChildren().add(label);
                    StackPane.setAlignment(label, Pos.TOP_CENTER);
                }
            }
        }
        for (Node legend : monthlyBarChart.lookupAll(".chart-legend-item")) {
            if (legend instanceof Label label) {
                if (label.getText().equals("Anzahl Rechnungen")) {
                    label.getGraphic().setStyle("-fx-background-color: #10e1c3;");
                } else if (label.getText().equals("Erstattungsbetrag (€)")) {
                    label.getGraphic().setStyle("-fx-background-color: #a0e196;");
                }
            }
        }
        //});
    }

    public void onExportPdfButtonClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("PDF speichern");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF-Dateien", "*.pdf"));
        // Standardverzeichnis: Desktop
        fileChooser.setInitialDirectory(homeDirectory);
        // Vorgeschlagener Dateiname mit aktuellem Datum
        String date = LocalDate.now().toString(); // z.B. 2025-05-16
        fileChooser.setInitialFileName("Lunchify_Kennzahlenreport_" + date + ".pdf");
        // Dialog öffnen
        File file = fileChooser.showSaveDialog(null);
        if (file == null) return;

        try (PdfWriter writer = new PdfWriter(file.getAbsolutePath());
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {

            // Titel
            document.add(new Paragraph("Lunchify Kennzahlenbericht").setBold().setFontSize(16));

            // Tabellenwerte
            Table table = new Table(2);
            table.addCell("Ausgewählter Benutzer").addCell( labelUser.getText());
            table.addCell("Zeitraum").addCell(labelSelectedDateRange.getText());
            table.addCell("Rechnungstyp").addCell(labelInvoiceType.getText());
            table.addCell("Anzahl Monate").addCell( labelMonths.getText());
            table.addCell("Anzahl Benutzer").addCell(labelUserCount.getText());
            table.addCell("Anzahl erstattete Rechnungen").addCell(labelInvoiceCount.getText());
            table.addCell("Gesamter Erstattungsbetrag").addCell(labelSumReimbursement.getText());
            table.addCell("Eingereichte Rechnungen pro Benutzer").addCell(labelInvoicesPerUser.getText());
            table.addCell("Eingereichte Rechnungen pro Benutzer pro Monat").addCell(labelInvoicesPerUserPerMonth.getText());

            document.add(table);
            document.add(new Paragraph(""));//Zeilenumbruch

            // Diagramm einfügen
            BufferedImage pieImage = chartToImage(chartTypeDistribution);
            document.add(new Paragraph(""));//Zeilenumbruch
            BufferedImage barImage = chartToImage(monthlyBarChart);

            ByteArrayOutputStream pieOut = new ByteArrayOutputStream();
            ByteArrayOutputStream barOut = new ByteArrayOutputStream();
            ImageIO.write(pieImage, "png", pieOut);
            ImageIO.write(barImage, "png", barOut);

            ImageData pieImgData = ImageDataFactory.create(pieOut.toByteArray());
            ImageData barImgData = ImageDataFactory.create(barOut.toByteArray());

            document.add(new com.itextpdf.layout.element.Image(pieImgData).scaleToFit(400, 400));
            document.add(new com.itextpdf.layout.element.Image(barImgData).scaleToFit(500, 300));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private BufferedImage chartToImage(Node chartNode) {
        WritableImage fxImage = chartNode.snapshot(new SnapshotParameters(), null);
        return SwingFXUtils.fromFXImage(fxImage, null);
    }

    public void onExportCsvButtonClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("CSV speichern");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV-Dateien", "*.csv"));
        // Standardverzeichnis: Desktop
        fileChooser.setInitialDirectory(homeDirectory);
        // Vorgeschlagener Dateiname mit aktuellem Datum
        String date = LocalDate.now().toString(); // z.B. 2025-05-16
        fileChooser.setInitialFileName("Lunchify_Kennzahlenreport_" + date + ".csv");
        // Dialog öffnen
        File file = fileChooser.showSaveDialog(null);
        if (file == null) return;

        try (PrintWriter writer = new PrintWriter(file)) {
            writer.println("Kennzahl,Wert");
            writer.printf("Ausgewählter Benutzer,%s%n", labelUser.getText());
            writer.printf("Zeitraum,%s%n", labelSelectedDateRange.getText());
            writer.printf("Rechnungstyp,%s%n", labelInvoiceType.getText());
            writer.printf("Anzahl Monate,%s%n", labelMonths.getText());
            writer.printf("Anzahl Benutzer,%s%n", labelUserCount.getText());
            writer.printf("Anzahl erstattete Rechnungen,%s%n", labelInvoiceCount.getText());
            writer.printf("Gesamter Erstattungsbetrag,%s%n", labelSumReimbursement.getText());
            writer.printf("Eingereichte Rechnungen pro Benutzer,%s%n", labelInvoicesPerUser.getText());
            writer.printf("Eingereichte Rechnungen pro Benutzer pro Monat,%s%n", labelInvoicesPerUserPerMonth.getText());

            // Exportiere Bar-Chart Daten (monatliche Stats)
            writer.println("Monat;Anzahl Rechnungen;Erstattungsbetrag (€)");
            Map<YearMonth, InvoiceKpiService.InvoiceMonthlyStats> stats = invoiceKpiService.getMonthlyInvoiceStats();
            for (Map.Entry<YearMonth, InvoiceKpiService.InvoiceMonthlyStats> entry : stats.entrySet()) {
                YearMonth month = entry.getKey();
                InvoiceKpiService.InvoiceMonthlyStats data = entry.getValue();
                writer.printf("%s;%d;%.2f%n", month.toString(), data.getInvoiceCount(), data.getReimbursementTotal());
            }
            writer.println(); // Leerzeile

            // Exportiere Pie-Chart Daten (Supermarkt vs Restaurant)
            writer.println("Rechnungstyp;Anzahl");
            writer.printf("Supermarkt;%d%n", invoiceKpiService.getInvoiceCountSupermarket());
            writer.printf("Restaurant;%d%n", invoiceKpiService.getInvoiceCountRestaurant());

            writer.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
