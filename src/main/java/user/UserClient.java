package user;


import io.qameta.allure.Step;
import io.restassured.response.Response;
import models.User;
import models.UserCreds;

import static io.restassured.RestAssured.given;

public class UserClient {

    private static final String CREATE_USER_URL = "/api/auth/register";
    private static final String LOGIN_USER_URL = "/api/auth/login";
    private static final String USER_URL = "/api/auth/user";

    private static String accessToken;

    @Step("Создание пользователя {user}")
    public static Response create(User user) {
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(user)
                .when()
                .post(CREATE_USER_URL);
    }

    @Step("Авторизация пользователя с кредами {userCreds}")
    public static Response login(UserCreds userCreds) {
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(userCreds)
                .when()
                .post(LOGIN_USER_URL);
    }

    @Step("Удаление пользователя")
    public static Response delete(String accessToken) {
        return given()
                .header("Content-type", "application/json")
                .and()
                .header("authorization", accessToken)
                .when()
                .delete(USER_URL);
    }
    @Step("Авторизация пользователя с токеном")
    public static Response authUser(String accessToken, User user) {
        return given()
                .header("Content-type", "application/json")
                .and()
                .header("authorization", accessToken)
                .and()
                .body(user)
                .when()
                .patch(USER_URL);
    }
    @Step("Авторизация пользователя без токена")
    public static Response authUser(User user) {
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(user)
                .when()
                .patch(USER_URL);
    }


}
