import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import models.Ingredient;
import models.User;
import order.OrderClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import user.UserClient;

import java.util.List;

import static models.UserCreds.fromUser;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.equalTo;

@RunWith(Parameterized.class)
public class CreateOrderTests {

    private static final String BASE_URL = "https://stellarburgers.nomoreparties.site";
    private final List<String> ingridients;
    private final int statusCode;
    private String accessToken;

    public CreateOrderTests(List<String> ingridients, int statusCode) {
        this.ingridients = ingridients;
        this.statusCode = statusCode;
    }

    @Parameterized.Parameters()
    public static Object[][] params() {
        return new Object[][]{
                {List.of("61c0c5a71d1f82001bdaaa73", "61c0c5a71d1f82001bdaaa70"), SC_OK},
                {List.of(), SC_BAD_REQUEST},
                {List.of("12345", "67890"), SC_INTERNAL_SERVER_ERROR},
        };
    }

    @Before
    public void setUp() {
        RestAssured.baseURI = BASE_URL;

        User user = new User("PadmeAmidala", "Padme-data@yandex.ru", "padmeAmidala");
        UserClient.create(user);

        Response loginResponse = UserClient.login(fromUser(user));
        accessToken = loginResponse.path("accessToken");
    }

    @Test
    @DisplayName("Создание заказа авторизованным пользователем")
    public void checkCreateOrdersWithAuth() {
        Ingredient reqIng = new Ingredient(ingridients);
        OrderClient.createOrder(accessToken, reqIng).then().assertThat()
                .statusCode(statusCode);

    }

    @Test
    @DisplayName("Создание заказа неавторизованным пользователем")
    public void checkCreateOrdersWithoutAuth() {
        Ingredient reqIng = new Ingredient(ingridients);
        OrderClient.createOrder(reqIng).then().assertThat()
                .statusCode(statusCode);

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
