package bean;

public class LoginBean {
    private String email;
    private String password;

    public LoginBean() {
        //evitiamo il costruttore di defaut
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    // Validazione semplice: evita di chiamare il controller se i campi sono vuoti
    public boolean isValid() {
        return email != null && !email.isEmpty() && password != null && !password.isEmpty();
    }
}
