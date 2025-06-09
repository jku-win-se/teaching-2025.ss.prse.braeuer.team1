package at.jku.se.lunchify.models;

import javafx.scene.control.Alert;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.sql.Date;


/**
 * Testing nicht sinnvoll, weil Entitätsklasse
 */

public class Invoice {

    private int invoiceid;
    private int userid;
    private String invoicenumber;
    private Date date;
    private double amount;
    private double reimbursementAmount;
    //public enum Invoicetype {SUPERMARKT, RESTAURANT}
    private String type;
    public enum Invoicestatus {EINGEREICHT, GENEHMIGT, ABGELEHNT}
    private String status;
    private boolean isanomalous;
    private byte[] file;
    private int timesChanged;
    private Date requestDate;

    /**
     * Constructor for new Invoices
     * <p>
     * Constructor for new Invoices which are being updated or displayed (requestDate is the date of the upload), not to be used for initial upload/commit
     */
    public Invoice(int invoiceid, int userid, String invoicenumber, Date date, double amount, double reimbursementAmount, String type, boolean isanomalous, byte[] file, int timesChanged,Date requestDate){
        this.invoiceid = invoiceid;
        this.userid = userid;
        this.invoicenumber = invoicenumber;
        this.date = date;
        this.amount = amount;
        this.reimbursementAmount = reimbursementAmount;
        this.type = type;
        this.status = String.valueOf(Invoicestatus.EINGEREICHT);
        this.isanomalous = isanomalous;
        this.file = file;
        this.timesChanged = timesChanged;
        this.requestDate = requestDate;
    }

    /**
     * Constructor for new Invoices
     * <p>
     * Constructor for new Invoices which are being uploaded (initial upload)
     */
    public Invoice(int userid, String invoicenumber, Date date, double amount, double reimbursementAmount, String type, boolean isanomalous, byte[] file, int timesChanged){
        this.userid = userid;
        this.invoicenumber = invoicenumber;
        this.date = date;
        this.amount = amount;
        this.reimbursementAmount = reimbursementAmount;
        this.type = type;
        this.status = String.valueOf(Invoicestatus.EINGEREICHT);
        this.isanomalous = isanomalous;
        this.file = file;
        this.timesChanged = timesChanged;
        this.requestDate = Date.valueOf(LocalDate.now());
    }

    /**
     * Constructor for new Invoices
     * <p>
     * Full constructor for new Invoices
     */
    public Invoice(int invoiceid, int userid, String invoicenumber, Date date, double amount, double reimbursementAmount, String type, String status, boolean isanomalous, byte[] file, int timesChanged,Date requestDate){
        this.invoiceid = invoiceid;
        this.userid = userid;
        this.invoicenumber = invoicenumber;
        this.date = date;
        this.amount = amount;
        this.reimbursementAmount = reimbursementAmount;
        this.type = type;
        this.status = status;
        this.isanomalous = isanomalous;
        this.file = file;
        this.timesChanged = timesChanged;
        this.requestDate = requestDate;
    }

    /**
     * Constructor for new Invoices
     * <p>
     * For editing/displaying Invoices in the application (displaying which can be edited/displayed)
     */
    public Invoice(int userid, Date date, double amount, double reimbursementAmount,Date requestDate){
        this.userid = userid;
        this.date = date;
        this.amount = amount;
        this.reimbursementAmount = reimbursementAmount;
        this.requestDate = requestDate;
    }

    /**
     * Constructor for new Invoices
     * <p>
     * For Objects exporting Invoice data
     */
    public Invoice(int invoiceid, Date date, double amount, double reimbursementAmount, String type, String status,Date requestDate){
        this.invoiceid = invoiceid;
        this.date = date;
        this.amount = amount;
        this.reimbursementAmount = reimbursementAmount;
        this.type = type;
        this.status = status;
        this.requestDate = requestDate;
    }

    public int getInvoiceid() {
        return invoiceid;
    }

    public void setInvoiceid(int invoiceid) {
        this.invoiceid = invoiceid;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public String getInvoicenumber() {
        return invoicenumber;
    }

    public void setInvoicenumber(String invoicenumber) {
        this.invoicenumber = invoicenumber;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
            this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isIsanomalous() {
        return isanomalous;
    }

    public void setIsanomalous(boolean isanomalous) {
        this.isanomalous = isanomalous;
    }

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }

    public double getReimbursementAmount() {
        return reimbursementAmount;
    }

    public void setReimbursementAmount(double reimbursementAmount) {
        this.reimbursementAmount = reimbursementAmount;
    }

    public int getTimesChanged() {
        return timesChanged;
    }

    public void setTimesChanged(int timesChanged) {
        this.timesChanged = timesChanged;
    }

    public Date getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(Date requestDate) {
        this.requestDate = requestDate;
    }

    //AI-generated
    /**
     * Method opening PDF files from the current selected invoice
     */
    public void openPDF() throws IOException {
        File tempFile = File.createTempFile("invoice_", ".pdf");
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(file);
        }

        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().open(tempFile);
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Fehler");
            alert.setHeaderText("PDF kann nicht geöffnet werden!");
        }
    }

    @Override
    public String toString() {
        return "Invoice{" +
                "invoiceid=" + invoiceid +
                ", userid=" + userid +
                ", invoicenumber=" + invoicenumber +
                ", date=" + date +
                ", amount=" + amount +
                ", reimbursementAmount=" + reimbursementAmount +
                ", type='" + type + '\'' +
                ", status='" + status + '\'' +
                ", isanomalous=" + isanomalous +
                ", file=" + Arrays.toString(file) +
                ", timesChanged=" + timesChanged +
                ", requestDate=" + requestDate +
                '}';
    }
    public String toStringWithoutFile() {
        return "Invoice{" +
                "invoiceid=" + invoiceid +
                ", userid=" + userid +
                ", invoicenumber=" + invoicenumber +
                ", date=" + date +
                ", amount=" + amount +
                ", reimbursementAmount=" + reimbursementAmount +
                ", type='" + type + '\'' +
                ", status='" + status + '\'' +
                ", isanomalous=" + isanomalous +
                ", timesChanged=" + timesChanged +
                ", requestDate=" + requestDate +
                '}';
    }

}
