package courier;

import io.qameta.allure.junit4.DisplayName;
import models.Courier;
import models.CourierCredentials;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import utils.DataGenerator;
import static org.hamcrest.Matchers.*;

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
        // Создаём курьера перед тестами логина
        courierClient.createCourier(courier).then().statusCode(201);
        // Получаем id для последующего удаления
        var loginResp = courierClient.loginCourier(
                new CourierCredentials(courier.getLogin(), courier.getPassword()));
        courierId = loginResp.then().extract().path("id");
    }

    @After
    public void tearDown() {
        if (courierId > 0) {
            courierClient.deleteCourier(courierId);
        }
    }

    @Test
    @DisplayName("Курьер может авторизоваться")
    public void courierCanLogin() {
        CourierCredentials creds = new CourierCredentials(courier.getLogin(), courier.getPassword());
        var response = courierClient.loginCourier(creds);
        response.then()
                .statusCode(200)
                .body("id", notNullValue());
    }

    @Test
    @DisplayName("Ошибка при неверном логине")
    public void loginWithWrongLogin() {
        CourierCredentials creds = new CourierCredentials("wrongLogin", courier.getPassword());
        var response = courierClient.loginCourier(creds);
        response.then()
                .statusCode(404)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Ошибка при неверном пароле")
    public void loginWithWrongPassword() {
        CourierCredentials creds = new CourierCredentials(courier.getLogin(), "wrongPass");
        var response = courierClient.loginCourier(creds);
        response.then()
                .statusCode(404)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Ошибка при отсутствии логина в запросе")
    public void loginWithoutLogin() {
        CourierCredentials creds = new CourierCredentials(null, courier.getPassword());
        var response = courierClient.loginCourier(creds);
        response.then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Ошибка при отсутствии пароля")
    public void loginWithoutPassword() {
        CourierCredentials creds = new CourierCredentials(courier.getLogin(), null);
        var response = courierClient.loginCourier(creds);
        response.then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Авторизация несуществующего пользователя")
    public void loginNonExistentUser() {
        CourierCredentials creds = new CourierCredentials("ghost", "ghostPass");
        var response = courierClient.loginCourier(creds);
        response.then()
                .statusCode(404)
                .body("message", equalTo("Учетная запись не найдена"));
    }
}
