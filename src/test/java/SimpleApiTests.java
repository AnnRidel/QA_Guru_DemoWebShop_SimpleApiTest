import com.codeborne.selenide.Condition;
import org.openqa.selenium.Cookie;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static io.qameta.allure.Allure.step;

public class SimpleApiTests {
    String baseUrl = "http://demowebshop.tricentis.com/";
    String email = "test11@test.ru";
    String password = "testtest";
    String userCookie = null;
    String itemsAmount = null;

    @Test
    public void notSubscriberEmptyEmail() {
        step("An attempt to subscribe with an invalid email", () ->{
        Response response = given()
                .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                .body("email=")
                .post(baseUrl + "subscribenewsletter");
        response.then().statusCode(200);
        response.then().body("Success", equalTo(false));
        response.then().body("Result", equalTo("Enter valid email"));
    });}

    @Test
    public void subscriberValidEmail() {
        step("An attempt to subscribe with a valid email", () ->{
                Response response = given()
                .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                .body(String.format("email=%s", email))
                .post(baseUrl + "subscribenewsletter");
        response.then().statusCode(200);
        response.then().body("Success", equalTo(true));
        response.then().body("Result", equalTo("Thank you for signing up! A verification" +
                " email has been sent. We appreciate your interest."));});
    }

    @Test
    void VoteUnauthorizedTest() {
        step("Voting by an unauthorized user", () -> {
            Response response =
                    given()
                            .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                            .body("pollAnswerId=3")
                            .when()
                            .post(baseUrl + "poll/vote")
                            .then()
                            .statusCode(200)
                            .extract().response();
            response.then().body("error", equalTo("Only registered users can vote."));
        });
    }

    @Test
    public void voteAuthorizedTest() {
        step("Authorization", () -> {
            Authorization authorization = new Authorization();
            userCookie = authorization.GetAutorizationCookies(email, password, baseUrl);
        });
        step("Voting by an authorized user", () -> {
            Response response =
                    given()
                            .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                            .cookie("NOPCOMMERCE.AUTH", userCookie)
                            .body("pollAnswerId=3")
                            .when()
                            .post(baseUrl + "poll/vote")
                            .then()
                            .statusCode(200)
                            .extract().response();
            assert response.path("html").toString().contains("<div class=\"poll\" id=\"poll-block-1");
        });
    }

    @Test
    void checkAmountOfProductsAddedToTheCart() {
        step("Authorization", () -> {
            Authorization authorization = new Authorization();
            userCookie = authorization.GetAutorizationCookies(email, password, baseUrl);
        });
        step("Add item to the cart", () -> {
            Response response = given()
                    .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                    .cookie("NOPCOMMERCE.AUTH", userCookie)
                    .body("addtocart_31.EnteredQuantity=3")
                    .when()
                    .post(baseUrl + "addproducttocart/details/31/1")
                    .then()
                    .statusCode(200)
                    .extract()
                    .response();
            itemsAmount = response.path("updatetopcartsectionhtml");
        });
        step("Set cookies", () -> {
            open(baseUrl + "favicon.ico");
            getWebDriver().manage().addCookie(
                    new Cookie("NOPCOMMERCE.AUTH", userCookie));
        });
        step("Verify amount of items in the cart", () -> {
            open(baseUrl);
            $(".cart-qty").shouldHave(Condition.text(itemsAmount));
        });
    }
}