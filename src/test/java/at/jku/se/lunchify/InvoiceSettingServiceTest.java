package at.jku.se.lunchify;
import at.jku.se.lunchify.models.Invoice;
import at.jku.se.lunchify.models.InvoiceSettingService;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

//AI-Assisted
class InvoiceSettingServiceTest {

    static InvoiceSettingService service;
    private static double originalSupermarketValue;
    private static double originalRestaurantValue;

    @BeforeAll
    static void init() {
        service = new InvoiceSettingService();
        originalSupermarketValue = service.getCurrentSupermarketValue();
        originalRestaurantValue = service.getCurrentRestaurantValue();
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

    @Test
    void testUpdateWithInvalidValues() {
        boolean result = service.updateInvoiceSettings("abc", "2.0");
        assertFalse(result); // sollte intern failen
    }

    @Test
    void testUpdateBothInvoiceSettings() {
        double newSupermarket = originalSupermarketValue + 1.11;
        double newRestaurant = originalRestaurantValue + 2.22;

        boolean updated = service.updateInvoiceSettings(
                String.valueOf(newSupermarket),
                String.valueOf(newRestaurant)
        );

        assertTrue(updated);
        assertEquals(newSupermarket, service.getCurrentSupermarketValue(), 0.001);
        assertEquals(newRestaurant, service.getCurrentRestaurantValue(), 0.001);
    }

    @Test
    void testUpdateOnlySupermarketValue() {
        double newSupermarket = originalSupermarketValue + 5.55;

        boolean updated = service.updateInvoiceSettings(
                String.valueOf(newSupermarket),
                ""
        );

        assertTrue(updated);
        assertEquals(newSupermarket, service.getCurrentSupermarketValue(), 0.001);
        assertEquals(originalRestaurantValue, service.getCurrentRestaurantValue(), 0.001);
    }
    

    @Test
    void testGetReimbursementValue() {
        assertEquals(2, service.getReimbursementValue(String.valueOf(Invoice.Invoicetype.RESTAURANT), 2));
        assertEquals(2, service.getReimbursementValue(String.valueOf(Invoice.Invoicetype.SUPERMARKT), 2));
        assertEquals(service.getReimbursementValue(String.valueOf(Invoice.Invoicetype.RESTAURANT), 10), service.getCurrentRestaurantValue());
        assertEquals(service.getReimbursementValue(String.valueOf(Invoice.Invoicetype.SUPERMARKT), 10), service.getCurrentSupermarketValue());
    }

    @AfterAll
    static void restoreOriginalValues() {
        service.updateInvoiceSettings(
                String.valueOf(originalSupermarketValue),
                String.valueOf(originalRestaurantValue)
        );
    }
}