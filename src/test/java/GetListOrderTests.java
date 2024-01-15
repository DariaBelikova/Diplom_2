import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import models.Ingredient;
import models.User;
import order.OrderClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import user.UserClient;

import java.util.List;

import static models.UserCreds.fromUser;
import static org.apache.http.HttpStatus.SC_ACCEPTED;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.equalTo;

public class GetListOrderTests {

    private static final String BASE_URL = "https://stellarburgers.nomoreparties.site";
    private String accessToken;

    @Before
    public void setUp() {
        RestAssured.baseURI = BASE_URL;

        User user = new User("PadmeAmidala", "Padme-data@yandex.ru", "padmeAmidala");
        UserClient.create(user);

        Response loginResponse = UserClient.login(fromUser(user));
        accessToken = loginResponse.path("accessToken");

        Ingredient reqIng = new Ingredient(List.of("61c0c5a71d1f82001bdaaa73", "61c0c5a71d1f82001bdaaa70"));
        OrderClient.createOrder(accessToken, reqIng).then().assertThat()
                .statusCode(SC_OK);
    }

    @Test
    @DisplayName("Получение списка заказов")
    public void checkGetListOrderWithAuth() {
        OrderClient.getListOrders(accessToken).then().assertThat().body("success", equalTo(true))
                .and()
                .statusCode(SC_OK);
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
