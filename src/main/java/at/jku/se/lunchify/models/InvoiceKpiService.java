package at.jku.se.lunchify.models;

import java.sql.Date;

public class InvoiceKpiService {

    private final String jdbcUrl;
    private final String dbUsername;
    private final String dbPassword;
    private final String userEmail;
    private final String invoicetype;
    private final Date fromDate;
    private final Date toDate;

    /*private final int months; // muss über fromDate und toDate ermittelt werden, befindet sich beides in einem Monat -> 1, ansonsten die anzahl der betroffenen monate des zeitraums
    private final String userName; //muss über userEmail abgefragt werden, falls "alle Benutzer" ->"Alle Benutzer"
    private final int userCount; //Bei ausgewähltem Benutzer =1 (userEmail), falls "alle Benutzer" ausgewählt wurde muss gezählt werden
    private final int sumReimbursements; //Summe der rückerstatteten Beträge von "GENEHMIGT" Rechnungen von ausgewählten Benutzer
    private final int invoiceCount; //Anzahl der "GENEHMIGT" Rechnungen des/der ausgewählten Benutzer
    private final int invoiceCountSupermarket; //Anzahl der "GENEHMIGT" Rechnungen des/der ausgewählten Benutzer vom Typ Supermarkt
    private final int invoiceCountRestaurant; //Anzahl der "GENEHMIGT" Rechnungen des/der ausgewählten Benutzer vom Typ Restaurant
*/

    public InvoiceKpiService(String userEmail, String invoicetype, Date fromDate, Date toDate) {
        this.userEmail = userEmail;
        this.invoicetype = invoicetype;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.jdbcUrl = "jdbc:postgresql://aws-0-eu-central-1.pooler.supabase.com:6543/postgres";
        this.dbUsername = "postgres.yxshntkgvmksefegyfhz";
        this.dbPassword = "CaMaKe25!";
    }





}
