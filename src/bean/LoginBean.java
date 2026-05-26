package bean;

public class LoginBean {
    private String email;
    private String password;

    public LoginBean() {
        // Costruttore vuoto obbligatorio per i Java Bean
    }

    public String getEmail() { return email; }
    public void setEmail(String email) {
        this.email = (email != null) ? email.toLowerCase().trim() : null;
    }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public boolean isValid() {
        return email != null && !email.isEmpty() && password != null && !password.isEmpty();
    }
}
