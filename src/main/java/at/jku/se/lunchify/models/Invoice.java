package at.jku.se.lunchify.models;

import java.util.Arrays;
import java.util.Date;

public class Invoice {

    private int invoiceid;
    private int userid;
    private int invoicenumber;
    private Date date;
    private double amount;
    private double reimbursementAmount;
    enum invoicetype  {Supermarkt,Restaurant}
    private String type;
    enum invoicestatus  {eingereicht,genehmigt,abgelehnt}
    private String status;
    private boolean isanomalous;
    private byte[] file;
    private int timesChanged;

    public Invoice(int invoiceid, int userid, int invoicenumber, Date date, double amount, double reimbursementAmount, String type, boolean isanomalous, byte[] file, int timesChanged) throws Exception {
        this.invoiceid = invoiceid;
        this.userid = userid;
        this.invoicenumber = invoicenumber;
        this.date = date;
        this.amount = amount;
        this.reimbursementAmount = reimbursementAmount;
        this.type = type;
        this.status = String.valueOf(invoicestatus.eingereicht);
        this.isanomalous = isanomalous;
        this.file = file;
        this.timesChanged = timesChanged;
    }

    public Invoice(int userid, Date date, double amount, double reimbursementAmount) throws Exception {
        this.userid = userid;
        this.date = date;
        this.amount = amount;
        this.reimbursementAmount = reimbursementAmount;
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

    public int getInvoicenumber() {
        return invoicenumber;
    }

    public void setInvoicenumber(int invoicenumber) {
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
                '}';
    }
}
