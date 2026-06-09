import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class ApiMockingExample {

    private WireMockServer wireMockServer;
    private static final int PORT = 8089;

    @BeforeClass
    public void setup() {
        // 1. Initialize and start the WireMock server locally
        wireMockServer = new WireMockServer(PORT);
        wireMockServer.start();

        // 2. Configure WireMock client context to the local instance
        configureFor("localhost", PORT);

        // 3. Define the stub behavior (Mocking the API response)
        stubFor(get(urlEqualTo("/api/user/100"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"id\": 100, \"name\": \"Alex\", \"role\": \"Admin\"}")));

        // 4. Point REST Assured to the local mock server configuration
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = PORT;
    }

    @Test
    public void testMockedUserEndpoint() {
        // Use REST Assured to query the mocked endpoint and assert results
        given()
                .accept(ContentType.JSON)
        .when()
                .get("/api/user/100")
        .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", equalTo(100))
                .body("name", equalTo("Alex"))
                .body("role", equalTo("Admin"));
    }

    @AfterClass
    public void teardown() {
        // Shut down the server safely to release network ports
        if (wireMockServer != null && wireMockServer.isRunning()) {
            wireMockServer.stop();
        }
    }
}
