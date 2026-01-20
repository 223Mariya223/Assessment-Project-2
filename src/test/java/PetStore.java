import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class PetStore {

    @BeforeClass
    public void setup() {
        RestAssured.baseURI = "https://petstore.swagger.io/v2";
    }

    // --- POSITIVE SCENARIOS ---

    @Test(priority = 1)
    public void testCreatePet() {
        PetClass myPet = new PetClass(12345L, "Doggie", "available");

        given()
            .contentType(ContentType.JSON)
            .body(myPet)
        .when()
            .post("/pet")
        .then()
            .statusCode(200)
            .body("name", equalTo("Doggie"))
            .body("status", equalTo("available"));
    }

    @Test(priority = 2)
    public void testGetPetById() {
        given()
            .pathParam("petId", 12345)
        .when()
            .get("/pet/{petId}")
        .then()
            .statusCode(200)
            .body("id", equalTo(12345))
            .body("name", equalTo("Doggie"));
    }

    @Test(priority = 3)
    public void testUpdatePet() {
        PetClass updatedPet = new PetClass(12345L, "Doggie-Updated", "sold");

        given()
            .contentType(ContentType.JSON)
            .body(updatedPet)
        .when()
            .put("/pet")
        .then()
            .statusCode(200)
            .body("name", equalTo("Doggie-Updated"))
            .body("status", equalTo("sold"));
    }

    @Test(priority = 4)
    public void testDeletePet() {
        given()
            .pathParam("petId", 12345)
        .when()
            .delete("/pet/{petId}")
        .then()
            .statusCode(200);
    }

    // --- NEGATIVE SCENARIOS ---

    @Test
    public void testGetNonExistentPet() {
        // ID that likely doesn't exist
        given()
            .pathParam("petId", 99999999)
        .when()
            .get("/pet/{petId}")
        .then()
            .statusCode(404)
            .body("message", equalTo("Pet not found"));
    }

    @Test
    public void testCreatePetWithInvalidData() {
        // Sending a string where an ID (long) is expected or malformed JSON
        String invalidBody = "{ \"id\": \"not-a-number\", \"name\": \"broken\" }";

        given()
            .contentType(ContentType.JSON)
            .body(invalidBody)
        .when()
            .post("/pet")
        .then()
            .statusCode(anyOf(is(400), is(500))); 
            // Swagger Petstore often returns 500 for parsing errors instead of 400
    }

    @Test
    public void testDeletePetWithInvalidId() {
        // Providing a non-numeric ID in the path
        given()
            .pathParam("petId", "abc_123")
        .when()
            .delete("/pet/{petId}")
        .then()
            .statusCode(404); // Or 400 depending on API strictness
    }
}