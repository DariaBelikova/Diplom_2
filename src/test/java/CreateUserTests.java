import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import models.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import user.UserClient;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;

public class CreateUserTests {

    private static final String BASE_URL = "https://stellarburgers.nomoreparties.site";

    private String accessToken;


    @Before
    public void setUp() {
        RestAssured.baseURI = BASE_URL;
    }


    @Test
    @DisplayName("Создание пользователя")
    public void checkCreateUser() {

        User user = new User("Hermione", "Hermione-data@yandex.ru", "hermione");

        UserClient userClient = new UserClient();

        Response response = userClient.create(user);

        accessToken = response.path("accessToken");
        assertEquals("Неверный статус код", SC_OK, response.statusCode());

    }

    @Test
    @DisplayName("Создание двух одинаковых пользователей")
    @Description("Нельзя создать двух одинаковых пользователей")
    public void checkDoubleCreateNewUser() {

        User user = new User("Hermione", "Hermione-data@yandex.ru", "hermione");

        UserClient userClient = new UserClient();
        Response response = userClient.create(user);
        accessToken = response.path("accessToken");

        Response responseSecondUser = userClient.create(user);

        responseSecondUser.then().assertThat().statusCode(SC_FORBIDDEN)
                .and()
                .body("message", equalTo("User already exists"));


    }

    @Test
    @DisplayName("Создание пользователя без пароля")
    public void checkCreateUserWithoutPassword() {
        User courierWithoutPassword = new User("HermioneGranger", "", "Hermione");

        UserClient userClient = new UserClient();
        Response response = userClient.create(courierWithoutPassword);

        response.then().assertThat().statusCode(SC_FORBIDDEN)
                .and()
                .body("message", equalTo("Email, password and name are required fields"));


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
