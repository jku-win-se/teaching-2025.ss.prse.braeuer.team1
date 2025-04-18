package at.jku.se.lunchify.models;

import java.sql.*;

import at.jku.se.lunchify.security.PasswordService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class UserDAO {
    String jdbcUrl = "jdbc:postgresql://aws-0-eu-central-1.pooler.supabase.com:6543/postgres";
    String username = "postgres.yxshntkgvmksefegyfhz";
    String DBpassword = "CaMaKe25!";
    PasswordService passwordService = new PasswordService();

    // Methode zum Abrufen aller Benutzer
    public ObservableList<User> getAllUsers() {
        ObservableList<User> users = FXCollections.observableArrayList();

        String sql = "SELECT * FROM \"User\"";
        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, DBpassword);
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet resultSet = ps.executeQuery()) {

            while (resultSet.next()) {
                int userid = resultSet.getInt("userid");
                String emailToEdit = resultSet.getString("email");
                String firstNameToEdit = resultSet.getString("firstname");
                String surnameToEdit = resultSet.getString("surname");
                String passwordToEdit = resultSet.getString("password");
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

    public ObservableList<String> getAllUserMails() {
        ObservableList<String> users = FXCollections.observableArrayList();

        String sql = "SELECT email FROM \"User\"";
        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, DBpassword);
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

    public ObservableList<String> getAllUserMailsWithAll() {
        ObservableList<String> usersWithAll = this.getAllUserMails();
        usersWithAll.addFirst("alle Benutzer");
        return usersWithAll;
    }

    public User getUserByEmail(String email) {
        User user = null;
        String sql = "SELECT * FROM \"User\" WHERE email = ?";
        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, DBpassword);
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

    public boolean updateUser(User user) throws SQLException {
        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, DBpassword);
        PreparedStatement ps = connection.prepareStatement("update \"User\" SET email = ?, firstname = ?, surname = ?, type = ?, isactive = ?, password = ? where userid = ?;")) {
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getFirstname());
            ps.setString(3, user.getSurname());
            ps.setString(4, user.getType());
            ps.setBoolean(5, user.isIsactive());
            ps.setString(6, passwordService.hashPassword(user.getPassword().trim()));
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

    public boolean insertUser(User user) throws SQLException {
        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, DBpassword);
        PreparedStatement ps = connection.prepareStatement("insert into \"User\" (email, firstname, surname, type, isactive, password) values (?,?,?,?,?,?);");){
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getFirstname());
            ps.setString(3, user.getSurname());
            ps.setString(4, user.getType());
            ps.setBoolean(5, user.isIsactive());
            ps.setString(6, passwordService.hashPassword(user.getPassword().trim()));
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

    public boolean checkUserAlreadyExists(User user) {
        if (user==null) return false;
        else return getUserByEmail(user.getEmail()) != null;
    }
}