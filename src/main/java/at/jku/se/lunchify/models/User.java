package at.jku.se.lunchify.models;


public class User {

    private int userid;
    private String email;
    private String firstname;
    private String surname;
    enum Usertype {ADMIN,USER}
    private String type;
    private boolean isactive;
    private boolean isanomalous;
    private String password;

    /**
     * Constructor for a User object
     * <p>
     * Constructor for a new User Object with all attributes
     */
    public User(int userid, String email, String firstname, String surname, String type, boolean isactive, boolean isanomalous, String password) throws Exception {
        this.userid = userid;
        this.email = email;
        this.firstname = firstname;
        this.surname = surname;
        this.type = type;
        this.isactive = isactive;
        this.isanomalous = isanomalous;
        this.password = password;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isIsactive() {
        return isactive;
    }

    public void setIsactive(boolean isactive) {
        this.isactive = isactive;
    }

    public boolean isIsanomalous() {
        return isanomalous;
    }

    public void setIsanomalous(boolean isanomalous) {
        this.isanomalous = isanomalous;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
                "userid=" + userid +
                ", email='" + email + '\'' +
                ", firstname='" + firstname + '\'' +
                ", surname='" + surname + '\'' +
                ", type='" + type + '\'' +
                ", isactive=" + isactive +
                ", isanomalous=" + isanomalous +
                ", password='" + password + '\'' +
                '}';
    }
}
