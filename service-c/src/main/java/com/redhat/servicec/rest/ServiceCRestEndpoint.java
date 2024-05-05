package com.redhat.servicec.rest;


import com.redhat.servicec.entity.Registry;
import com.redhat.servicec.service.LocalServiceC;
import com.redhat.servicec.service.RemoteServiceCBean;
import jakarta.ejb.EJB;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class ServiceCRestEndpoint {

    @EJB
    LocalServiceC ejb;

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
