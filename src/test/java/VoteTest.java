import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.core.Is.is;

public class VoteTest {

    @Test
    void VoteUnauthorizedTest() {
        Response response =
                given()
                        .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                        .body("pollAnswerId=3")
                        .when()
                        .post("http://demowebshop.tricentis.com/poll/vote")
                        .then()
                        .statusCode(200)
                        .body("error", is("Only registered users can vote."))
                       .extract().response();
        System.out.println(response.asString());
    }
}
