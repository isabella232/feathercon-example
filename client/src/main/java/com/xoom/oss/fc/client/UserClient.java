package com.xoom.oss.fc.client;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.xoom.oss.fc.dto.User;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;

public class UserClient {

    private final String host;
    private final int port;

    public UserClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public User byEmail(String emailAddress) {
        Client client = Client.create(new DefaultClientConfig(JacksonJsonProvider.class));
        WebResource resource = client.resource(String.format("http://%s:%d/api/users/%s", host, port, emailAddress));
        return resource.get(User.class);
    }
}
