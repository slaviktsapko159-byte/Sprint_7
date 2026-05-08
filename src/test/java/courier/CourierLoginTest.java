package courier;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import models.Courier;
import models.CourierCredentials;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import utils.DataGenerator;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class CourierLoginTest {
    private CourierClient courierClient;
    private Courier courier;
    private int courierId;

    @Before
    public void setUp() {
        courierClient = new CourierClient();
        courier = new Courier(
                DataGenerator.getRandomLogin(),
                DataGenerator.getRandomPassword(),
                DataGenerator.getRandomFirstName()
        );
        courierClient.createCourier(courier).then().statusCode(HttpStatus.SC_CREATED);
    }

    @After
    public void tearDown() {
        if (courier.getLogin() != null && courier.getPassword() != null) {
            var loginResp = courierClient.loginCourier(
                    new CourierCredentials(courier.getLogin(), courier.getPassword()));
            if (loginResp.statusCode() == HttpStatus.SC_OK) {
                courierId = loginResp.then().extract().path("id");
            }
        }
        if (courierId > 0) {
            courierClient.deleteCourier(courierId);
        }
    }

    @Test
    @DisplayName("Курьер может авторизоваться")
    @Description("Проверяем, что зарегистрированный курьер может авторизоваться и получить id")
    public void courierCanLoginTest() {
        CourierCredentials creds = new CourierCredentials(courier.getLogin(), courier.getPassword());
        var response = courierClient.loginCourier(creds);
        response.then()
                .statusCode(HttpStatus.SC_OK)
                .body("id", notNullValue());
    }

    @Test
    @DisplayName("Ошибка при неверном логине")
    @Description("При авторизации с неверным логином возвращается 404 и сообщение об ошибке")
    public void loginWithWrongLoginTest() {
        CourierCredentials creds = new CourierCredentials("wrongLogin", courier.getPassword());
        var response = courierClient.loginCourier(creds);
        response.then()
                .statusCode(HttpStatus.SC_NOT_FOUND)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Ошибка при неверном пароле")
    @Description("При авторизации с неверным паролем возвращается 404 и сообщение об ошибке")
    public void loginWithWrongPasswordTest() {
        CourierCredentials creds = new CourierCredentials(courier.getLogin(), "wrongPass");
        var response = courierClient.loginCourier(creds);
        response.then()
                .statusCode(HttpStatus.SC_NOT_FOUND)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Ошибка при отсутствии логина в запросе")
    @Description("При авторизации без поля логин возвращается 400 и сообщение о недостатке данных")
    public void loginWithoutLoginTest() {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("password", courier.getPassword());

        given()
                .baseUri("https://qa-scooter.praktikum-services.ru")
                .header("Content-type", "application/json")
                .body(requestBody)
                .post("/api/v1/courier/login")
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Ошибка при отсутствии пароля")
    @Description("При авторизации без поля пароль возвращается 400 и сообщение о недостатке данных")
    public void loginWithoutPasswordTest() {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("login", courier.getLogin());

        given()
                .baseUri("https://qa-scooter.praktikum-services.ru")
                .header("Content-type", "application/json")
                .body(requestBody)
                .post("/api/v1/courier/login")
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Авторизация несуществующего пользователя")
    @Description("При попытке авторизации с несуществующими учётными данными возвращается 404")
    public void loginNonExistentUserTest() {
        CourierCredentials creds = new CourierCredentials("ghost", "ghostPass");
        var response = courierClient.loginCourier(creds);
        response.then()
                .statusCode(HttpStatus.SC_NOT_FOUND)
                .body("message", equalTo("Учетная запись не найдена"));
    }
}
