package com.redhat.serviceb.rest;

import com.redhat.serviceb.entity.Registry;
import com.redhat.serviceb.service.LocalServiceB;
import com.redhat.serviceb.service.RemoteServiceBBean;
import jakarta.ejb.EJB;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.ArrayList;
import java.util.List;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class ServiceBRestEndpoint {

    @EJB
    LocalServiceB ejb;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "Hello World!";
    }

    @GET
    @Path("listAll")
    public List<Registry> listAllRegistries() {
        return ejb.listRegistries();
    }

}
