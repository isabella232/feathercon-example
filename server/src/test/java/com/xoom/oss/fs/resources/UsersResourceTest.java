package com.xoom.oss.fs.resources;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.xoom.oss.fc.dto.User;
import com.xoom.oss.feathercon.FeatherCon;
import com.xoom.oss.feathercon.JerseyServerBuilder;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class UsersResourceTest {
    private FeatherCon server;

    @Before
    public void setUp() throws Exception {
        server = new JerseyServerBuilder("com.xoom.oss.fs.resources", "/api/*").withPort(19060).build();
        server.start();
    }

    @After
    public void tearDown() throws Exception {
        server.stop();
    }

    @Test
    public void testByEmail() throws Exception {
        Client client = Client.create(new DefaultClientConfig(JacksonJsonProvider.class));
        WebResource resource = client.resource("http://localhost:19060/api/users/bob@example.com");
        User user = resource.get(User.class);
        System.out.println(user);
    }
}
