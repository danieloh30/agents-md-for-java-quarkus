package com.example.billing;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Path("/billing/invoices")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BillingResource {

    @GET
    public List<Invoice> listAll() {
        return Invoice.listAll();
    }

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id) {
        Invoice invoice = Invoice.findById(id);
        if (invoice == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(invoice).build();
    }

    @GET
    @Path("/customer/{customerId}")
    public List<Invoice> getByCustomer(@PathParam("customerId") String customerId) {
        return Invoice.findByCustomerId(customerId);
    }

    @GET
    @Path("/overdue")
    public List<Invoice> getOverdue() {
        return Invoice.findOverdue();
    }

    @POST
    @Transactional
    public Response create(Invoice invoice) {
        invoice.status = Invoice.InvoiceStatus.DRAFT;
        invoice.issueDate = LocalDateTime.now();

        BigDecimal total = BigDecimal.ZERO;
        if (invoice.items != null) {
            for (InvoiceItem item : invoice.items) {
                item.invoice = invoice;
                item.calculateTotal();
                total = total.add(item.totalPrice);
            }
        }
        invoice.totalAmount = total;

        invoice.persist();
        return Response.status(Response.Status.CREATED).entity(invoice).build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response update(@PathParam("id") Long id, Invoice updatedInvoice) {
        Invoice invoice = Invoice.findById(id);
        if (invoice == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        invoice.customerId = updatedInvoice.customerId;
        invoice.status = updatedInvoice.status;
        invoice.dueDate = updatedInvoice.dueDate;
        invoice.paidDate = updatedInvoice.paidDate;

        return Response.ok(invoice).build();
    }

    @PUT
    @Path("/{id}/issue")
    @Transactional
    public Response issue(@PathParam("id") Long id) {
        Invoice invoice = Invoice.findById(id);
        if (invoice == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        invoice.status = Invoice.InvoiceStatus.ISSUED;
        invoice.issueDate = LocalDateTime.now();

        return Response.ok(invoice).build();
    }

    @PUT
    @Path("/{id}/pay")
    @Transactional
    public Response markAsPaid(@PathParam("id") Long id) {
        Invoice invoice = Invoice.findById(id);
        if (invoice == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        invoice.status = Invoice.InvoiceStatus.PAID;
        invoice.paidDate = LocalDateTime.now();

        return Response.ok(invoice).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response delete(@PathParam("id") Long id) {
        Invoice invoice = Invoice.findById(id);
        if (invoice == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        invoice.delete();
        return Response.noContent().build();
    }
}
