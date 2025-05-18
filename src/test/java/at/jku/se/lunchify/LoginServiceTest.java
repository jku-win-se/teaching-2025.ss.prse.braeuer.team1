package at.jku.se.lunchify;

import at.jku.se.lunchify.models.LoginService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

//AI-Assisted
public class LoginServiceTest {

    private LoginService loginService;

    @BeforeEach
    public void setup() {
        loginService = new LoginService();
    }

    @Test
    public void testLoginWithEmptyFields() {
        var result = loginService.login("", "");
        assertEquals(LoginService.LoginResult.EMPTY_FIELDS, result);
    }

    @Test
    public void testLoginWithValidAdminCredentials() {
        var result = loginService.login("martin", "admin");
        assertEquals(LoginService.LoginResult.SUCCESS_ADMIN, result);
        assertNotEquals(LoginService.LoginResult.SUCCESS_USER, result);
    }

    @Test
    public void testLoginWithValidUserCredentials() {
        var result = loginService.login("user", "user");
        assertNotEquals(LoginService.LoginResult.SUCCESS_ADMIN, result);
        assertEquals(LoginService.LoginResult.SUCCESS_USER, result);
    }

    @Test
    public void testLoginWithInvalidUsername() {
        var result = loginService.login("invalid@example.com", "wrongpassword");
        assertNotEquals(LoginService.LoginResult.SUCCESS_ADMIN, result);
        assertNotEquals(LoginService.LoginResult.SUCCESS_USER, result);
        assertEquals(LoginService.LoginResult.INVALID_USER, result);
    }

    @Test
    public void testLoginWithInvalidPassword() {
        var result = loginService.login("martin", "wrongpassword");
        assertNotEquals(LoginService.LoginResult.SUCCESS_ADMIN, result);
        assertNotEquals(LoginService.LoginResult.SUCCESS_USER, result);
        assertEquals(LoginService.LoginResult.INVALID_PW, result);
    }

    @Test
    public void testLoginWithActiveUser() {
        var result = loginService.login("martin", "admin");
        assertNotEquals(LoginService.LoginResult.USER_INACTIVE, result);
    }

    @Test
    public void testLoginWithInactiveUser() {
        var result = loginService.login("Inaktiv", "inaktiv");
        assertEquals(LoginService.LoginResult.USER_INACTIVE, result);
    }
}