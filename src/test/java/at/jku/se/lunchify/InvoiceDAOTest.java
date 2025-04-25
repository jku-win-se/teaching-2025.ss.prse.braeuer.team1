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
        assertFalse(invoiceDAO.checkDateInPast(LocalDate.of(2026,1,1)));
    }
    @Test
    public void testCheckDateInPast2025() {
        assertTrue(invoiceDAO.checkDateInPast(LocalDate.of(2025,1,1)));
    }
    @Test
    public void testCheckInvoiceValueIsPositiveNegative() {
        assertFalse(invoiceDAO.checkInvoiceValueIsPositive(-47.11));
    }
    @Test
    public void testCheckInvoiceValueIsPositivePositive() {
        assertTrue(invoiceDAO.checkInvoiceValueIsPositive(47.11));
    }
}