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
    public void testCheckDateInPast2026() {
        assertEquals(false, uploadController.checkDateInPast(LocalDate.of(2026,1,1)));
    }
    @Test
    public void testCheckDateInPast2025() {
        assertEquals(true, uploadController.checkDateInPast(LocalDate.of(2025,1,1)));
    }
    @Test
    public void testCheckInvoiceValueIsPositiveNegative() {
        assertEquals(false, uploadController.checkInvoiceValueIsPositive(-47.11));
    }
    @Test
    public void testCheckInvoiceValueIsPositivePositive() {
        assertEquals(false, uploadController.checkInvoiceValueIsPositive(47.11));
    }
    @Test
    public void testGetReimbursementValueFromInvoiceTypeRestaurant() {
        assertEquals(3.0, uploadController.getReimbursementValueFromInvoiceType("Restaurant"));
    }
    @Test
    public void testGetReimbursementValueFromInvoiceTypeSupermarket() {
        assertEquals(2.5, uploadController.getReimbursementValueFromInvoiceType("Supermarkt"));
    }
    @Test
    public void testGetReimbursementValueFromInvoiceTypeUnknown() {
        assertEquals(0, uploadController.getReimbursementValueFromInvoiceType("Unknown"));
    }
}