package at.jku.se.lunchify;

import at.jku.se.lunchify.models.User;
import at.jku.se.lunchify.models.UserDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

//AI-Assisted
public class UserDAOTest {

    private UserDAO userDAO;

    @BeforeEach
    public void setup() {
        userDAO = new UserDAO();
    }

    @Test
    public void testCheckUserAlreadyExists() throws SQLException {
        assertTrue(userDAO.checkUserAlreadyExists(userDAO.getUserByEmail("carina@lunchify.at")));
        assertFalse(userDAO.checkUserAlreadyExists(userDAO.getUserByEmail("test@lunchify.at")));
    }

    @Test
    public void testInsertUser() throws Exception {
        User userToInsert = new User (1, "test@lunchify.at", "test", "test", "admin", true, false, "");
        assertFalse(userDAO.checkUserAlreadyExists(userToInsert));
        userDAO.insertUser(userToInsert);
        assertTrue(userDAO.checkUserAlreadyExists(userToInsert));
    }

    @Test
    public void testUpdateUser() throws Exception {
        User userToUpdate = userDAO.getUserByEmail("carina@lunchify.at");
        userToUpdate.setIsactive(false);
        userDAO.updateUser(userToUpdate);
        assertFalse(userToUpdate.isIsactive());
    }
}