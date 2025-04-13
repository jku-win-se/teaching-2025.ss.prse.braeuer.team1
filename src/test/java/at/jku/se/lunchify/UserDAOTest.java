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
        assertFalse(userDAO.checkUserAlreadyExists(userDAO.getUserByEmail("")));
    }

    @Test
    public void testInsertUser() throws Exception {
        User userToInsert = userDAO.getUserByEmail("carina@lunchify.at");
        assertTrue(userDAO.checkUserAlreadyExists(userToInsert));
        assertFalse(userDAO.insertUser(userToInsert));
    }

    @Test
    public void testUpdateUser() throws Exception {
        User userToUpdate = userDAO.getUserByEmail("carina@lunchify.at");
        userToUpdate.setEmail("martin");
        assertFalse(userDAO.updateUser(userToUpdate));
    }

    @Test
    public void testGetUserByEmail() throws Exception {
        User testUser = userDAO.getUserByEmail("carina@lunchify.at");
        assertEquals(testUser.getEmail(), "carina@lunchify.at");
    }
}