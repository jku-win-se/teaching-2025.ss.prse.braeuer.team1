package at.jku.se.lunchify;
import at.jku.se.lunchify.models.InvoiceSettingService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InvoiceSettingServiceTest {

    InvoiceSettingService service;

    @BeforeEach
    void setUp() {
        service = new InvoiceSettingService();
    }

    @Test
    void testValidInput_withPositiveNumber() {
        assertTrue(service.isValidInput("5.50"));
    }

    @Test
    void testValidInput_withEmptyString() {
        assertTrue(service.isValidInput(""));
    }

    @Test
    void testValidInput_withNegativeNumber() {
        assertFalse(service.isValidInput("-2.0"));
    }

    @Test
    void testValidInput_withInvalidString() {
        assertFalse(service.isValidInput("abc"));
    }
    /*
    // Achtung: Diese Tests verändern den Datenbankzustand!
    @Test
    void testUpdateOnlySupermarketValue() {
        boolean result = service.updateInvoiceSettings("2.5", "");
        assertTrue(result);
    }

    @Test
    void testUpdateOnlyRestaurantValue() {
        boolean result = service.updateInvoiceSettings("", "3.5");
        assertTrue(result);
    }

    @Test
    void testUpdateBothValues() {
        boolean result = service.updateInvoiceSettings("1.0", "1.5");
        assertTrue(result);
    }

    @Test
    void testUpdateWithInvalidValues() {
        boolean result = service.updateInvoiceSettings("abc", "2.0");
        assertFalse(result); // sollte intern failen
    }

     */
}