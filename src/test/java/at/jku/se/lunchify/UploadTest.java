package at.jku.se.lunchify;

import at.jku.se.lunchify.models.LoginService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

//AI-Assisted
public class UploadTest {

    private UploadController uploadController;

    @BeforeEach
    public void setup() {
        uploadController = new UploadController();
    }

    @Test
    public void testDateCheckFail() {
        assertEquals(false, uploadController.checkDateInPast(LocalDate.of(2026,1,1)));
    }
    @Test
    public void testDateCheckPass() {
        assertEquals(true, uploadController.checkDateInPast(LocalDate.of(2025,1,1)));
    }
    @Test
    public void testInvoiceValueIsNumberFail() {
        assertEquals(false, uploadController.checkInvoiceValueIsNumber("Test"));
    }
    @Test
    public void testInvoiceValueIsNumberPass() {
        assertEquals(true, uploadController.checkInvoiceValueIsNumber("47.11"));
    }
    @Test
    public void testInvoiceValueIsPositiveFail() {
        assertEquals(false, uploadController.checkInvoiceValueIsPositive("-47.11"));
    }
    @Test
    public void testInvoiceValueIsPositivePass() {
        assertEquals(true, uploadController.checkInvoiceValueIsPositive("47.11"));
    }
    @Test
    public void testReimbursementValueFromInvoiceTypeUnknown() {
        assertEquals(0, uploadController.getReimbursementValueFromInvoiceType("Unknown"));
    }
    @Test
    public void testReimbursementValueFromInvoiceTypeRestaurant() {
        assertEquals(3.0, uploadController.getReimbursementValueFromInvoiceType("Restaurant"));
    }
    @Test
    public void testReimbursementValueFromInvoiceTypeSupermarkt() {
        assertEquals(2.5, uploadController.getReimbursementValueFromInvoiceType("Supermarkt"));
    }
}