import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import com.github.javafaker.Faker;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;

public class VoteTest {
    Faker faker = new Faker();
    String baseUrl = "http://demowebshop.tricentis.com/";

    @Test
    void VoteUnauthorizedTest() {
        Response response =
                given()
                        .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                        .body("pollAnswerId=3")
                        .when()
                        .post(baseUrl + "poll/vote")
                        .then()
                        .statusCode(200)
                        .body("error", is("Only registered users can vote."))
                       .extract().response();
        System.out.println(response.asString());
    }

    @Test
    public void notSubscriberEmptyEmail() {
        Response response = given()
                .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                .body("email=")
                .post(baseUrl + "subscribenewsletter");
        response.then().statusCode(200);
        response.then().body("Success", equalTo(false));
        response.then().body("Result", equalTo("Enter valid email"));
    }

    @Test
    public void subscriberValidEmail() {
        String email = faker.internet().emailAddress();
        Response response = given()
                .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                .body(String.format("email=%s", email))
                .post(baseUrl + "subscribenewsletter");
        response.then().statusCode(200);
        response.then().body("Success", equalTo(true));
        response.then().body("Result", equalTo("Thank you for signing up! A verification" +
                " email has been sent. We appreciate your interest."));
    }
}
