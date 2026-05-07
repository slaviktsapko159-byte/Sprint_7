package courier;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import models.Courier;
import models.CourierCredentials;

import static io.restassured.RestAssured.given;

public class CourierClient {
    private static final String BASE_URL = "https://qa-scooter.praktikum-services.ru";
    private static final String COURIER_PATH = "/api/v1/courier";
    private static final String LOGIN_PATH = "/api/v1/courier/login";

    @Step("Создание курьера")
    public Response createCourier(Courier courier) {
        return given()
                .baseUri(BASE_URL)
                .header("Content-type", "application/json")
                .body(courier)
                .post(COURIER_PATH);
    }

    @Step("Логин курьера")
    public Response loginCourier(CourierCredentials credentials) {
        return given()
                .baseUri(BASE_URL)
                .header("Content-type", "application/json")
                .body(credentials)
                .post(LOGIN_PATH);
    }

    @Step("Удаление курьера по id")
    public Response deleteCourier(int courierId) {
        return given()
                .baseUri(BASE_URL)
                .delete(COURIER_PATH + "/" + courierId);
    }
}
