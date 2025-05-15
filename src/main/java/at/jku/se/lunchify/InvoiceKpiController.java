package at.jku.se.lunchify;

import at.jku.se.lunchify.models.InvoiceKpiService;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.control.Label;
import javafx.scene.chart.PieChart;

import java.sql.Date;

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
    private BarChart<String, Number> chartMonthlyData;
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
    }
}
