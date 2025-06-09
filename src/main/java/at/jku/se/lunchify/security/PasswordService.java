package at.jku.se.lunchify.security;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


public class PasswordService {
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * Hashing a password
     * <p>
     * This function hashes a password with the BCrypt method
     * <p>
     * @param rawPassword Password to hash
     * @return return the hashed password
     */
    public String hashPassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    /**
     * verifying a hashed password
     * <p>
     * This function checks if a hashed password is the same as given raw password
     * <p>
     * @param rawPassword Password to check for
     * @param hashedPassword Hashed Password, same as raw password if unhashed
     * @return return the hashed password
     */
    public boolean verifyPassword(String rawPassword, String hashedPassword) {
        return passwordEncoder.matches(rawPassword, hashedPassword);
    }
}
