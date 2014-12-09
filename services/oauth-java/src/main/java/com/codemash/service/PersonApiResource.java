package com.codemash.service;

import com.codemash.service.data.NameGenerator;
import com.codemash.service.data.Person;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by david on 12/8/14.
 */

@Path("/api")
public class PersonApiResource {
    private NameGenerator mNameGenerator = new NameGenerator();

        @GET
        @Path("hello")
        @Produces(MediaType.TEXT_PLAIN)
        public String getHello() {
            return "Bazinga";
        }

        @GET
        @Path("long")
        @Produces(MediaType.TEXT_PLAIN)
        public String getLong() {

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return "Bazinga";
        }

        @GET
        @Path("name/{gender}")
        @Produces(MediaType.APPLICATION_JSON)
        public Person getSingleNameByGender(@PathParam("gender") String gender) {

            if(gender != null && !gender.isEmpty() && genderValid(gender))
            {
                Person result = mNameGenerator.getRandomName(gender);

                return result;
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

            Person result = mNameGenerator.getRandomName();

            return result;
        }

        @GET
        @Path("xname")
        @Produces(MediaType.APPLICATION_XML)
        public Person getSingleNameXml() {

            Person result = mNameGenerator.getRandomName();

            return result;
        }

        @GET
        @Path("names/{count}")
        @Produces(MediaType.APPLICATION_JSON)
        public List<Person> getPersonList(@PathParam("count") int count) {

            if(!countValid(count)){
                throw new WebApplicationException(Response.Status.BAD_REQUEST);
            }

            return mNameGenerator.getRandomPersons(count);

        }

        private boolean countValid(int count) {
            return count > 0 && count < 101;
        }
}
