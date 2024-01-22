import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import models.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import user.UserClient;

import static models.UserCreds.fromUser;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;

public class LoginUserTests {

    private static final String BASE_URL = "https://stellarburgers.nomoreparties.site";

    private String accessToken;

    @Before
    public void setUp() {
        RestAssured.baseURI = BASE_URL;

        User user = new User("PadmeAmidala", "Padme-data@yandex.ru", "padmeAmidala");
        UserClient.create(user);
    }

    @Test
    @DisplayName("Логин под существующим пользователем")
    public void checkUserLogin() {
        User user = new User("PadmeAmidala", "Padme-data@yandex.ru", "padmeAmidala");
        Response loginResponse = UserClient.login(fromUser(user));
        accessToken = loginResponse.path("accessToken");

        assertEquals("Неверный статус код", SC_OK, loginResponse.statusCode());
    }

    @Test
    @DisplayName("Логин с неверным логином и паролем")
    public void checkUserLoginBadPassword() {
        User user = new User("RonWeasley", "Ron-data@yandex.ru", "RonRon");
        Response loginResponse = UserClient.login(fromUser(user));
        accessToken = loginResponse.path("accessToken");

        loginResponse.then().assertThat().statusCode(SC_UNAUTHORIZED)
                .and()
                .body("message", equalTo("email or password are incorrect"));
    }

    @After
    @DisplayName("Удаление пользователя")
    public void tearDown() {
        if (accessToken != null) {
            UserClient.delete(accessToken).then().assertThat().body("success", equalTo(true))
                    .and()
                    .body("message", equalTo("User successfully removed"))
                    .and()
                    .statusCode(SC_ACCEPTED);
        }
    }
}
