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

import static org.hamcrest.Matchers.equalTo;

public class CourierCreateTest {
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
    @DisplayName("Создание курьера - успешный сценарий")
    @Description("Проверяем, что курьера можно создать, ответ содержит ok: true и статус 201")
    public void createCourierSuccessTest() {
        var response = courierClient.createCourier(courier);
        response.then()
                .statusCode(HttpStatus.SC_CREATED)
                .body("ok", equalTo(true));
    }

    @Test
    @DisplayName("Нельзя создать двух одинаковых курьеров")
    @Description("Проверяем, что при попытке создать курьера с уже существующим логином возвращается 409 и сообщение об ошибке")
    public void createDuplicatedCourierTest() {
        courierClient.createCourier(courier).then().statusCode(HttpStatus.SC_CREATED);
        var response = courierClient.createCourier(courier);
        response.then()
                .statusCode(HttpStatus.SC_CONFLICT)
                .body("message", equalTo("Этот логин уже используется. Попробуйте другой."));
    }

    @Test
    @DisplayName("Ошибка при отсутствии обязательного поля (логин)")
    @Description("Проверяем, что при создании курьера без логина возвращается 400 и сообщение об ошибке")
    public void createCourierWithoutLoginTest() {
        courier.setLogin(null);
        var response = courierClient.createCourier(courier);
        response.then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Ошибка при отсутствии пароля")
    @Description("Проверяем, что при создании курьера без пароля возвращается 400 и сообщение об ошибке")
    public void createCourierWithoutPasswordTest() {
        courier.setPassword(null);
        var response = courierClient.createCourier(courier);
        response.then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Успешный запрос возвращает ok: true")
    @Description("Проверяем, что тело успешного ответа содержит ok: true")
    public void createCourierReturnsOkTrueTest() {
        var response = courierClient.createCourier(courier);
        response.then().body("ok", equalTo(true));
    }
}