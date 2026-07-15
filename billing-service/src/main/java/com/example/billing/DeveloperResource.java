package com.example.billing;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/developers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DeveloperResource {

    @GET
    public List<Developer> listAll() {
        return Developer.listAll();
    }

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id) {
        Developer developer = Developer.findById(id);
        if (developer == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(developer).build();
    }

    @GET
    @Path("/specialty/{specialty}")
    public List<Developer> getBySpecialty(@PathParam("specialty") String specialty) {
        return Developer.findBySpecialty(specialty);
    }

    @POST
    @Transactional
    public Response create(Developer developer) {
        developer.persist();
        return Response.status(Response.Status.CREATED).entity(developer).build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response update(@PathParam("id") Long id, Developer updatedDeveloper) {
        Developer developer = Developer.findById(id);
        if (developer == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        developer.name = updatedDeveloper.name;
        developer.specialty = updatedDeveloper.specialty;

        return Response.ok(developer).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response delete(@PathParam("id") Long id) {
        Developer developer = Developer.findById(id);
        if (developer == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        developer.delete();
        return Response.noContent().build();
    }
}
