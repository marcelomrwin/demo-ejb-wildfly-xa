package com.redhat.servicea.rest;

import com.redhat.exception.BusinessException;
import com.redhat.servicea.entity.Registry;
import com.redhat.servicea.service.LocalService;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.UUID;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class ServiceARestEndpoint {

    @EJB
    LocalService localService;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "Hello World!";
    }

    @GET
    @Path("remote")
    @Produces(MediaType.TEXT_PLAIN)
    public String helloFromRemote() {
        return localService.pingRemoteService();
    }

    @POST
    @Path("process-all/{uuid}")
    public String processAllTransactionsUsingUUID(@PathParam("uuid") String uuid) {
        return localService.processAll(uuid);
    }

    @POST
    @Path("process-all")
    @Produces(MediaType.TEXT_PLAIN)
    public Response processAllTransactionsGeneratingUUID() {
        return Response.status(Response.Status.CREATED).entity(localService.processAll(UUID.randomUUID().toString())).build();
    }

    @POST
    @Path("failOnC")
    @Produces(MediaType.TEXT_PLAIN)
    public Response failProcessOnServiceC() {
        try {
            String message = localService.failOnC();
            return Response.status(Response.Status.CREATED).entity(message).build(); //DEAD CODE
        } catch (BusinessException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.toString()).build();
        }
    }

    /**
     * Forces a transaction to fail after B and C perform successfully
     *
     * @return
     */
    @POST
    @Path("failOnAafterBandC")
    @Produces(MediaType.TEXT_PLAIN)
    public Response failOnAafterSucceedOnBandC() {
        try {
            String message = localService.failOnAafterBandC();
            return Response.status(Response.Status.CREATED).entity(message).build(); //DEAD CODE
        } catch (BusinessException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.toString()).build();
        }
    }

    @POST
    @Path("XaRecovery")
    @Produces(MediaType.TEXT_PLAIN)
    public Response xaRecovery() {
        String message = localService.xaRecovery();
        return Response.status(Response.Status.CREATED).entity(message).build();
    }

    @GET
    @Path("listAll")
    public List<Registry> listAll() {
        return localService.findAll();
    }
}
