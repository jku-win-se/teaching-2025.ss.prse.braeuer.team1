package at.jku.se.lunchify;

import at.jku.se.lunchify.models.*;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;


import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import static java.sql.Date.valueOf;

/**
 * Testing nicht sinnvoll - wenn dann können nur Eingabewerte auf leer überprüft werden - notwendig?
 *
 * Klasse zum Setzen der Filter für Auswertungen
 */

public class ReportController {
    @FXML
    protected Label warningText;
    @FXML
    protected ComboBox<String> allUsers;
    @FXML
    protected ChoiceBox<String> invoiceType;
    @FXML
    protected DatePicker dateFrom;
    @FXML
    protected DatePicker dateTo;
    @FXML
    protected CheckBox isAnomalous;

    protected String selectedMail;
    protected User selectedUser;
    protected java.sql.Date selectedDateFrom;
    protected java.sql.Date selectedDateTo;
    protected String selectedInvoiceType;
    protected boolean inputCorrect = false;
    protected boolean selectedIsAnomalous;

    private UserDAO userDAO;

    private final Date today = new Date();
    private final LocalDate heuteVorEinemJahr = LocalDate.now().minusYears(1);
    private final Date todayLastYear = Date.from(heuteVorEinemJahr.atStartOfDay(ZoneId.systemDefault()).toInstant());
    private final LocalDate monatsersterVormonat = LocalDate.now().minusMonths(1).withDayOfMonth(1);
    private final LocalDate monatsletzterVormonat = LocalDate.now().withDayOfMonth(1).minusDays(1);

    public void initialize() {
        userDAO = new UserDAO();
        allUsers.setValue("alle Benutzer");
        dateFrom.setValue(monatsersterVormonat);
        dateTo.setValue(monatsletzterVormonat);
        invoiceType.setValue("alle Rechnungstypen");
    }

    public void showAllUsers() {
        allUsers.setItems(userDAO.getAllUserMailsWithAll());
    }

    private void setSelectedData() {
        if (dateFrom.getValue()==null || dateTo.getValue()==null) {
            warningText.setText("Alle Filter setzen!");
        }
        else {
            selectedMail = allUsers.getSelectionModel().getSelectedItem();
            selectedUser = userDAO.getUserByEmail(selectedMail);
            selectedDateFrom = valueOf(dateFrom.getValue());
            selectedDateTo = valueOf(dateTo.getValue());
            selectedInvoiceType = invoiceType.getValue();
            selectedIsAnomalous = isAnomalous.isSelected();
        }
    }

    private void checkSelectedData() {
         if (selectedDateTo.before(selectedDateFrom)) {
            warningText.setText("Bis-Datum darf nicht vor dem Von-Datum liegen!");
        } else if (selectedDateTo.after(today)) {
            warningText.setText("Bis-Datum darf nicht in der Zukunft liegen!");
        } else if (selectedDateFrom.before(todayLastYear)) {
            warningText.setText("Auswertung für max. 12 Monate zurück!");
        } else {
            inputCorrect = true;
        }
    }

    public void onInvoiceIndicatorsButtonClick() throws IOException {
        setSelectedData();
        checkSelectedData();
        if(inputCorrect) {

            // Übergabe der Parameter an neuen Controller
            InvoiceKpiController.initData(
                    selectedMail,
                    selectedInvoiceType,
                    selectedDateFrom,
                    selectedDateTo
            );

            LunchifyApplication.baseController.showCenterView("invoiceKpi-view.fxml");
        }
    }

    public void onInvoiceStatisticsButtonClick() throws IOException {
        setSelectedData();
        checkSelectedData();
        if (inputCorrect) {
            // Übergabe der Parameter an neuen Controller
            InvoiceStatisticsController.initData(
                    selectedMail,
                    selectedInvoiceType,
                    selectedDateFrom,
                    selectedDateTo,
                    selectedIsAnomalous
            );
            LunchifyApplication.baseController.showCenterView("invoiceStatistics-view.fxml");
        }
    }
}

