package com.example.billing;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.hasSize;

@QuarkusTest
class DeveloperResourceTest {

    @Test
    void testCreateDeveloper() {
        Developer developer = new Developer();
        developer.name = "Alice Smith";
        developer.specialty = "Java";

        given()
            .contentType(ContentType.JSON)
            .body(developer)
            .when().post("/developers")
            .then()
                .statusCode(201)
                .body("name", is("Alice Smith"))
                .body("specialty", is("Java"))
                .body("id", notNullValue());
    }

    @Test
    void testListDevelopers() {
        given()
            .when().get("/developers")
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON);
    }

    @Test
    void testGetDeveloperById() {
        Developer developer = new Developer();
        developer.name = "Bob Johnson";
        developer.specialty = "Python";

        Integer id = given()
            .contentType(ContentType.JSON)
            .body(developer)
            .when().post("/developers")
            .then()
                .statusCode(201)
                .extract().path("id");

        given()
            .when().get("/developers/" + id)
            .then()
                .statusCode(200)
                .body("name", is("Bob Johnson"))
                .body("specialty", is("Python"));
    }

    @Test
    void testGetDevelopersBySpecialty() {
        Developer dev1 = new Developer();
        dev1.name = "Charlie Brown";
        dev1.specialty = "Quarkus";

        Developer dev2 = new Developer();
        dev2.name = "Diana Prince";
        dev2.specialty = "Quarkus";

        Developer dev3 = new Developer();
        dev3.name = "Eve Adams";
        dev3.specialty = "React";

        given()
            .contentType(ContentType.JSON)
            .body(dev1)
            .when().post("/developers")
            .then()
                .statusCode(201);

        given()
            .contentType(ContentType.JSON)
            .body(dev2)
            .when().post("/developers")
            .then()
                .statusCode(201);

        given()
            .contentType(ContentType.JSON)
            .body(dev3)
            .when().post("/developers")
            .then()
                .statusCode(201);

        given()
            .when().get("/developers/specialty/Quarkus")
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("$", hasSize(2))
                .body("[0].specialty", is("Quarkus"))
                .body("[1].specialty", is("Quarkus"));
    }

    @Test
    void testUpdateDeveloper() {
        Developer developer = new Developer();
        developer.name = "Frank Miller";
        developer.specialty = "JavaScript";

        Integer id = given()
            .contentType(ContentType.JSON)
            .body(developer)
            .when().post("/developers")
            .then()
                .statusCode(201)
                .extract().path("id");

        Developer updated = new Developer();
        updated.name = "Frank Miller";
        updated.specialty = "TypeScript";

        given()
            .contentType(ContentType.JSON)
            .body(updated)
            .when().put("/developers/" + id)
            .then()
                .statusCode(200)
                .body("name", is("Frank Miller"))
                .body("specialty", is("TypeScript"));
    }

    @Test
    void testDeleteDeveloper() {
        Developer developer = new Developer();
        developer.name = "Grace Hopper";
        developer.specialty = "COBOL";

        Integer id = given()
            .contentType(ContentType.JSON)
            .body(developer)
            .when().post("/developers")
            .then()
                .statusCode(201)
                .extract().path("id");

        given()
            .when().delete("/developers/" + id)
            .then()
                .statusCode(204);

        given()
            .when().get("/developers/" + id)
            .then()
                .statusCode(404);
    }

    @Test
    void testGetDevelopersBySpecialtyNotFound() {
        given()
            .when().get("/developers/specialty/NonExistentSpecialty")
            .then()
                .statusCode(200)
                .body("$", hasSize(0));
    }
}
