import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;

public class Authorization {
    String  userCookie;

    public String GetAutorizationCookies (String email, String password, String baseUrl) {

        step("Getting authorization cookies", () -> {
                     userCookie = given()
                    .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                    .formParam("Email", email)
                    .formParam("Password", password)
                    .when()
                    .post(baseUrl + "login")
                    .then()
                    .statusCode(302)
                    .extract().cookie("NOPCOMMERCE.AUTH");
        });
        return userCookie;
    }
}
