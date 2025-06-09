package at.jku.se.lunchify.models;

import java.sql.*;

import at.jku.se.lunchify.security.PasswordService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class UserDAO {
    String jdbcUrl = "jdbc:postgresql://aws-0-eu-central-1.pooler.supabase.com:6543/postgres";
    String username = "postgres.yxshntkgvmksefegyfhz";
    String dbPassword = "CaMaKe25!";
    PasswordService passwordService = new PasswordService();

    /**
     * Returns a list of all user e-mails in the database
     * <p>
     * This method return an ObservableList of all emails (String) in the database
     * <p>
     * @return ObservableList of Strings if successful, stacktrace if an Exception emerges
     */
    public ObservableList<String> getAllUserMails() {
        ObservableList<String> users = FXCollections.observableArrayList();

        String sql = "SELECT email FROM \"User\"";
        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, dbPassword);
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet resultSet = ps.executeQuery()) {

            while (resultSet.next()) {
                String emailToEdit = resultSet.getString("email");
                users.add(emailToEdit);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return users;
    }
    /**
     * Returns a list of all user e-mails in the database + a standard value
     * <p>
     * This method return an ObservableList of all emails (String) in the database and a standard value
     * <p>
     * @return ObservableList of Strings if successful, stacktrace if an Exception emerges
     */
    public ObservableList<String> getAllUserMailsWithAll() {
        ObservableList<String> usersWithAll = this.getAllUserMails();
        usersWithAll.addFirst("alle Benutzer");
        return usersWithAll;
    }

    /**
     * Returns a User object
     * <p>
     * Returns a User object by email value
     * <p>
     * @param email email of the user to search for
     * @return User Object if successful, stacktrace if an Exception emerges
     */
    public User getUserByEmail(String email) {
        User user = null;
        String sql = "SELECT * FROM \"User\" WHERE email = ?";
        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, dbPassword);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);

            try (ResultSet resultSet = ps.executeQuery()) {
                if (resultSet.next()) {
                    int userid = resultSet.getInt("userid");
                    String emailTo = resultSet.getString("email");
                    String firstNameTo = resultSet.getString("firstname");
                    String surnameTo = resultSet.getString("surname");
                    String passwordTo = resultSet.getString("password");
                    String typeTo = resultSet.getString("type");
                    boolean isActive = resultSet.getBoolean("isactive");
                    boolean isAnomalous = resultSet.getBoolean("isanomalous");
                    connection.close();

                    user = new User(userid, emailTo, firstNameTo, surnameTo, typeTo, isActive, isAnomalous, passwordTo);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }
    /**
     * Returns a list of all anomalous users in the database
     * <p>
     * Returns a list of all anomalous users in the database for reports
     * <p>
     * @return ObservableList of Users if successful, stacktrace if an Exception emerges
     */
    public ObservableList<User> getAnomalousUsers() {
        ObservableList<User> users = FXCollections.observableArrayList();

        String sql = "SELECT * FROM \"User\" WHERE isanomalous = TRUE";
        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, dbPassword);
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet resultSet = ps.executeQuery()) {

            while (resultSet.next()) {
                int userid = resultSet.getInt("userid");
                String emailToEdit = resultSet.getString("email");
                String firstNameToEdit = resultSet.getString("firstname");
                String surnameToEdit = resultSet.getString("surname");
                String passwordToEdit = "";
                String typeToEdit = resultSet.getString("type");
                boolean isActive = resultSet.getBoolean("isactive");
                boolean isAnomalous = resultSet.getBoolean("isanomalous");

                users.add(new User(userid, emailToEdit, firstNameToEdit, surnameToEdit, typeToEdit, isActive, isAnomalous, passwordToEdit));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return users;
    }

    /**
     * Updates an User from the database
     * <p>
     * This function updates an User from the database and a boolean if successfull or not
     * <p>
     * @param user User to update
     * @return return true if User was updated, false if not
     */
    public boolean updateUser(User user) throws SQLException {
        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, dbPassword);
             PreparedStatement ps = connection.prepareStatement("update \"User\" SET email = ?, firstname = ?, surname = ?, type = ?, isactive = ?, password = ? where userid = ?;")) {
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getFirstname());
            ps.setString(3, user.getSurname());
            ps.setString(4, user.getType());
            ps.setBoolean(5, user.isIsactive());
            ps.setString(6, user.getPassword());
            ps.setInt(7, user.getUserid());
            ps.executeUpdate();
            ps.close();
            connection.close();
            return true;
        }
            catch (SQLException e) {
                e.printStackTrace();
                return false;
        }
    }

    /**
     * Inserts an User into the database
     * <p>
     * This function inserts an User into the database and a boolean if successfull or not
     * <p>
     * @param user User to insert
     * @return return true if new User was created, false if not
     */
    public boolean insertUser(User user) throws SQLException {
        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, dbPassword);
             PreparedStatement ps = connection.prepareStatement("insert into \"User\" (email, firstname, surname, type, isactive, password, isanomalous) values (?,?,?,?,?,?,?);")){
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getFirstname());
            ps.setString(3, user.getSurname());
            ps.setString(4, user.getType());
            ps.setBoolean(5, user.isIsactive());
            ps.setString(6, passwordService.hashPassword(user.getPassword().trim()));
            ps.setBoolean(7, false);
            ps.executeUpdate();
            ps.close();
            connection.close();
            return true;
        }
        catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Checks whether an User already exists
     * <p>
     * This function checks if the given User already exists
     * <p>
     * @param user User to check for
     * @return return true if User was found, false if not
     */
    public boolean checkUserAlreadyExists(User user) {
        if (user==null) return false;
        else return getUserByEmail(user.getEmail()) != null;
    }
}