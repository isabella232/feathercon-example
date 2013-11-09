package com.xoom.oss.fs.resources;

import com.xoom.oss.fc.dto.User;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UsersResource {

    @GET
    @Path("/{emailAddress: .*@.*}")
    public User byEmail(@PathParam("emailAddress") String emailAddress) {
        return new User("bob", "loblaw", emailAddress);
    }
}
