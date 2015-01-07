package com.mdd.service;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/api")
public class DevFestResource {

    private NameGenerator _generator = new NameGenerator();

    @GET
    @Path("hello")
    @Produces(MediaType.TEXT_PLAIN)
    public String getHello() {
        return "Bazinga";
    }


    @GET
    @Path("name/{gender}")
    @Produces(MediaType.APPLICATION_JSON)
    public Person getSingleNameByGender(@PathParam("gender") String gender) {

        if(gender != null && !gender.isEmpty() && genderValid(gender))
        {
            return _generator.getRandomName(gender);
        } else {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
    }

    private boolean genderValid(String gender){

        if(gender.length() != 1) return false;
        if(gender.equalsIgnoreCase("m")) return true;
        if(gender.equalsIgnoreCase("f")) return true;
        return false;
    }

    @GET
    @Path("name")
    @Produces(MediaType.APPLICATION_JSON)
    public Person getSingleName() {

        return  _generator.getRandomName();
    }

    @GET
    @Path("xname")
    @Produces(MediaType.APPLICATION_XML)
    public Person getSingleNameXml() {

        return _generator.getRandomName();
    }

    @GET
    @Path("names/{count}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Person> getPersonList(@PathParam("count") int count) {

        if(!countValid(count)){
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }

        return _generator.getRandomPersons(count);
    }

    private boolean countValid(int count) {
        return count > 0 && count < 101;
    }
}
