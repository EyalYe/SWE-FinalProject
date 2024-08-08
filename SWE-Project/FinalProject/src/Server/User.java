package Server;

public abstract class User {
    private String username;
    private String password;
    private String email;
    private String phoneNumber;
    private String address;
    private boolean loggedIn;
    private String type;

    public User(String username, String password, String email, String phoneNumber, String address, String type) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.loggedIn = false;
        this.type = type;

    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setLoggedIn(boolean b) {
        loggedIn = b;
    }

    public boolean checkPassword(String password) {
        return this.password.equals(password);
    }
    public String getType(){
        return type;
    }

}
