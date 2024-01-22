package models;

public class UserCreds {

    private final String email;
    private final String password;

    public UserCreds(String email, String password) {
        this.email = email;
        this.password = password;
    }


    public static UserCreds fromUser(User user) {
        return new UserCreds(user.getEmail(), user.getPassword());
    }

    @Override
    public String toString() {
        return "CourierCreds{" +
                "login='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
