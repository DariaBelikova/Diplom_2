package order;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import models.Ingredient;

import static io.restassured.RestAssured.given;

public class OrderClient {

    private final static String ORDER_URL = "/api/orders";
    @Step("Создание заказа авторизованным пользователем")
    public static Response createOrder(String accessToken, Ingredient reqIng) {
        return given()
                .header("Content-type", "application/json")
                .and()
                .header("authorization", accessToken)
                .and()
                .body(reqIng)
                .when()
                .post(ORDER_URL);
    }
    @Step("Создание заказа неавторизованным пользователем")
    public static Response createOrder(Ingredient reqIng) {
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(reqIng)
                .when()
                .post(ORDER_URL);
    }
    @Step("Получение списка заказов")
    public static Response getListOrders(String accessToken) {
        return given()
                .header("Content-type", "application/json")
                .and()
                .header("authorization", accessToken)
                .when()
                .get(ORDER_URL);
    }
}
