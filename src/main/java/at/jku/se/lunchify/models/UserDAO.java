package at.jku.se.lunchify.models;

import java.sql.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class UserDAO {
    String jdbcUrl = "jdbc:postgresql://aws-0-eu-central-1.pooler.supabase.com:6543/postgres";
    String username = "postgres.yxshntkgvmksefegyfhz";
    String DBpassword = "CaMaKe25!";

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
                connection.close();
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
                connection.close();
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
        String sql = "SELECT * FROM \"User\"";
        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, DBpassword);
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet resultSet = ps.executeQuery()) {

            while (resultSet.next()) {
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

        return user;
    }


}