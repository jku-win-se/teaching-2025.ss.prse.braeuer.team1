package at.jku.se.lunchify;

import at.jku.se.lunchify.models.InvoiceDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

//AI-Assisted
public class InvoiceDAOTest {

    private InvoiceDAO invoiceDAO;

    @BeforeEach
    public void setup() {
        invoiceDAO = new InvoiceDAO();
    }

    @Test
    public void testCheckDateInPast2026() {
        assertEquals(false, invoiceDAO.checkDateInPast(LocalDate.of(2026,1,1)));
    }
    @Test
    public void testCheckDateInPast2025() {
        assertEquals(true, invoiceDAO.checkDateInPast(LocalDate.of(2025,1,1)));
    }
    @Test
    public void testCheckInvoiceValueIsPositiveNegative() {
        assertEquals(false, invoiceDAO.checkInvoiceValueIsPositive(-47.11));
    }
    @Test
    public void testCheckInvoiceValueIsPositivePositive() {
        assertEquals(true, invoiceDAO.checkInvoiceValueIsPositive(47.11));
    }
    @Test
    public void testGetReimbursementValueFromInvoiceTypeRestaurant() {
        assertEquals(3.0, invoiceDAO.getReimbursementValueFromInvoiceType("Restaurant"));
    }
    @Test
    public void testGetReimbursementValueFromInvoiceTypeSupermarket() {
        assertEquals(2.5, invoiceDAO.getReimbursementValueFromInvoiceType("Supermarkt"));
    }
    @Test
    public void testGetReimbursementValueFromInvoiceTypeUnknown() {
        assertEquals(0, invoiceDAO.getReimbursementValueFromInvoiceType("Unknown"));
    }
}