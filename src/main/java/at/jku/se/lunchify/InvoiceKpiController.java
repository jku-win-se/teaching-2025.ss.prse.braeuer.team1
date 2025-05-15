package at.jku.se.lunchify;

import at.jku.se.lunchify.models.InvoiceKpiService;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.chart.PieChart;

import java.sql.Date;
import java.time.YearMonth;
import java.util.Map;

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
        labelInvoicesPerUserPerMonth.setText(String.valueOf(""));
        labelInvoicesPerUser.setText("");
        labelInvoiceType.setText(invoiceType);
        labelSelectedDateRange.setText(String.valueOf(fromDate+" - "+String.valueOf(toDate)));


        // Pie Chart
        PieChart.Data supermarket = new PieChart.Data("Supermarkt", service.getInvoiceCountSupermarket());
        PieChart.Data restaurant = new PieChart.Data("Restaurant", service.getInvoiceCountRestaurant());
        chartTypeDistribution.getData().addAll(supermarket, restaurant);

        //Bar-Chart
        populateMonthlyStatsChart(service);
    }

    private void populateMonthlyStatsChart(InvoiceKpiService invoiceKpi) {
        Map<YearMonth, InvoiceKpiService.InvoiceMonthlyStats> stats = invoiceKpi.getMonthlyInvoiceStats();

        XYChart.Series<String, Number> invoiceCountSeries = new XYChart.Series<>();
        invoiceCountSeries.setName("Anzahl Rechnungen");

        XYChart.Series<String, Number> reimbursementSeries = new XYChart.Series<>();
        reimbursementSeries.setName("Erstattungsbetrag (€)");

        for (Map.Entry<YearMonth, InvoiceKpiService.InvoiceMonthlyStats> entry : stats.entrySet()) {
            String monthLabel = entry.getKey().toString(); // z. B. "2024-05"
            InvoiceKpiService.InvoiceMonthlyStats data = entry.getValue();

            invoiceCountSeries.getData().add(new XYChart.Data<>(monthLabel, data.getInvoiceCount()));
            reimbursementSeries.getData().add(new XYChart.Data<>(monthLabel, data.getReimbursementTotal()));
        }

        monthlyBarChart.getData().clear();
        monthlyBarChart.getData().addAll(invoiceCountSeries, reimbursementSeries);
    }
}
