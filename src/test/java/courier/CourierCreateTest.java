package courier;

import io.qameta.allure.junit4.DisplayName;
import models.Courier;
import models.CourierCredentials;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import utils.DataGenerator;
import static org.hamcrest.Matchers.*;

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
        if (courierId > 0) {
            courierClient.deleteCourier(courierId);
        }
    }

    @Test
    @DisplayName("Создание курьера - успешный сценарий")
    public void createCourierSuccess() {
        var response = courierClient.createCourier(courier);
        response.then()
                .statusCode(201)
                .body("ok", equalTo(true));

        // После создания получаем id для удаления
        var loginResponse = courierClient.loginCourier(
                new CourierCredentials(courier.getLogin(), courier.getPassword()));
        courierId = loginResponse.then().extract().path("id");
    }

    @Test
    @DisplayName("Нельзя создать двух одинаковых курьеров")
    public void createDuplicatedCourier() {
        courierClient.createCourier(courier).then().statusCode(201);
        var response = courierClient.createCourier(courier);
        response.then()
                .statusCode(409)
                .body("message", equalTo("Этот логин уже используется. Попробуйте другой."));
        // Получим id для удаления после теста
        var loginResponse = courierClient.loginCourier(
                new CourierCredentials(courier.getLogin(), courier.getPassword()));
        courierId = loginResponse.then().extract().path("id");
    }

    @Test
    @DisplayName("Ошибка при отсутствии обязательного поля (логин)")
    public void createCourierWithoutLogin() {
        courier.setLogin(null);
        var response = courierClient.createCourier(courier);
        response.then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Ошибка при отсутствии пароля")
    public void createCourierWithoutPassword() {
        courier.setPassword(null);
        var response = courierClient.createCourier(courier);
        response.then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Успешный запрос возвращает ok: true")
    public void createCourierReturnsOkTrue() {
        var response = courierClient.createCourier(courier);
        response.then().body("ok", equalTo(true));
        // Получим id для удаления
        var loginResponse = courierClient.loginCourier(
                new CourierCredentials(courier.getLogin(), courier.getPassword()));
        courierId = loginResponse.then().extract().path("id");
    }
}
