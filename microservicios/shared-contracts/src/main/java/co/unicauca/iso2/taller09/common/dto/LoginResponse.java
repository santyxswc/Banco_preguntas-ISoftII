package co.unicauca.iso2.taller09.common.dto;

import java.io.Serializable;

public class LoginResponse implements Serializable {
    private boolean authenticated;
    private UserDTO user;
    private String message;
    private String token;

    public LoginResponse() {}

    public LoginResponse(boolean authenticated, UserDTO user, String message) {
        this.authenticated = authenticated;
        this.user = user;
        this.message = message;
    }

    public static LoginResponse success(UserDTO user) {
        return new LoginResponse(true, user, "Autenticación exitosa");
    }

    public static LoginResponse success(UserDTO user, String token) {
        LoginResponse response = success(user);
        response.setToken(token);
        return response;
    }

    public static LoginResponse failure(String message) {
        return new LoginResponse(false, null, message);
    }

    public boolean isAuthenticated() { return authenticated; }
    public void setAuthenticated(boolean authenticated) { this.authenticated = authenticated; }

    public UserDTO getUser() { return user; }
    public void setUser(UserDTO user) { this.user = user; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}
