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

public class UpdateUserTests {

    private static final String BASE_URL = "https://stellarburgers.nomoreparties.site";

    private String accessToken;

    @Before
    public void setUp() {
        RestAssured.baseURI = BASE_URL;

        User user = new User("PadmeAmidala", "Padme-data@yandex.ru", "padmeAmidala");
        UserClient userClient = new UserClient();
        userClient.create(user);

        Response loginResponse = UserClient.login(fromUser(user));
        accessToken = loginResponse.path("accessToken");

    }

    @Test
    @DisplayName("Изменение данных пользователя с авторизацией")
    public void checkUpdateDataUserWithAuth() {
        User userUpdate = new User("Luke", "Luke-data@yandex.ru", "lukeSkywalker");
        UserClient.authUser(accessToken, userUpdate)
                .then().assertThat().body("success", equalTo(true))
                .and()
                .statusCode(SC_OK);

    }

    @Test
    @DisplayName("Изменение данных пользователя без авторизации")
    public void checkUpdateDataUserWithoutAuth() {
        User userUpdate = new User("Luke", "Luke-data@yandex.ru", "lukeSkywalker");
        UserClient.authUser(userUpdate)
                .then().assertThat().body("success", equalTo(false))
                .and()
                .body("message", equalTo("You should be authorised"))
                .and()
                .statusCode(SC_UNAUTHORIZED);

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
