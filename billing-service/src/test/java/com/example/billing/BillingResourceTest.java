package com.example.billing;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.greaterThan;

@QuarkusTest
class BillingResourceTest {

    @Test
    void testCreateInvoice() {
        Invoice invoice = new Invoice();
        invoice.customerId = "CUST-001";

        InvoiceItem item1 = new InvoiceItem();
        item1.description = "Software License";
        item1.quantity = 10;
        item1.unitPrice = new BigDecimal("99.99");

        InvoiceItem item2 = new InvoiceItem();
        item2.description = "Support Package";
        item2.quantity = 1;
        item2.unitPrice = new BigDecimal("499.00");

        invoice.items = List.of(item1, item2);

        given()
            .contentType(ContentType.JSON)
            .body(invoice)
            .when().post("/billing/invoices")
            .then()
                .statusCode(201)
                .body("customerId", is("CUST-001"))
                .body("status", is("DRAFT"))
                .body("totalAmount", greaterThan(0f))
                .body("id", notNullValue());
    }

    @Test
    void testListInvoices() {
        given()
            .when().get("/billing/invoices")
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON);
    }

    @Test
    void testGetInvoiceById() {
        Invoice invoice = new Invoice();
        invoice.customerId = "CUST-002";
        invoice.totalAmount = new BigDecimal("100.00");

        Integer id = given()
            .contentType(ContentType.JSON)
            .body(invoice)
            .when().post("/billing/invoices")
            .then()
                .statusCode(201)
                .extract().path("id");

        given()
            .when().get("/billing/invoices/" + id)
            .then()
                .statusCode(200)
                .body("customerId", is("CUST-002"));
    }

    @Test
    void testIssueInvoice() {
        Invoice invoice = new Invoice();
        invoice.customerId = "CUST-003";
        invoice.totalAmount = new BigDecimal("250.00");

        Integer id = given()
            .contentType(ContentType.JSON)
            .body(invoice)
            .when().post("/billing/invoices")
            .then()
                .statusCode(201)
                .extract().path("id");

        given()
            .when().put("/billing/invoices/" + id + "/issue")
            .then()
                .statusCode(200)
                .body("status", is("ISSUED"))
                .body("issueDate", notNullValue());
    }

    @Test
    void testMarkAsPaid() {
        Invoice invoice = new Invoice();
        invoice.customerId = "CUST-004";
        invoice.totalAmount = new BigDecimal("150.00");

        Integer id = given()
            .contentType(ContentType.JSON)
            .body(invoice)
            .when().post("/billing/invoices")
            .then()
                .statusCode(201)
                .extract().path("id");

        given()
            .when().put("/billing/invoices/" + id + "/pay")
            .then()
                .statusCode(200)
                .body("status", is("PAID"))
                .body("paidDate", notNullValue());
    }

    @Test
    void testDeleteInvoice() {
        Invoice invoice = new Invoice();
        invoice.customerId = "CUST-005";
        invoice.totalAmount = new BigDecimal("75.00");

        Integer id = given()
            .contentType(ContentType.JSON)
            .body(invoice)
            .when().post("/billing/invoices")
            .then()
                .statusCode(201)
                .extract().path("id");

        given()
            .when().delete("/billing/invoices/" + id)
            .then()
                .statusCode(204);

        given()
            .when().get("/billing/invoices/" + id)
            .then()
                .statusCode(404);
    }
}