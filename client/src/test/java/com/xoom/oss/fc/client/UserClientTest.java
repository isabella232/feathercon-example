package com.xoom.oss.fc.client;

import com.google.gson.Gson;
import com.xoom.oss.fc.dto.User;
import com.xoom.oss.feathercon.FeatherCon;
import com.xoom.oss.feathercon.ServletConfiguration;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class UserClientTest {
    FeatherCon server;

    @Before
    public void setUp() throws Exception {
        ServletConfiguration servletConfiguration = new ServletConfiguration.Builder().withServlet(new HttpServlet() {
            @Override
            protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
                Assert.assertEquals("/api/users/aenewman@example.com", req.getRequestURI());
                User u = new User("alfred e.", "newman", "aenewman@example.com");
                resp.setContentType("application/json");
                resp.getWriter().print(new Gson().toJson(u));
            }
        }).withPathSpec("/*").build();
        server = new FeatherCon.Builder().withServletConfiguration(servletConfiguration).withPort(0).build();
        server.start();
    }

    @After
    public void tearDown() throws Exception {
        server.stop();
    }

    @Test
    public void testByEmail() throws Exception {
        UserClient userClient = new UserClient("localhost", server.getHttpPort());
        User user = userClient.byEmail("aenewman@example.com");
        System.out.println(user);
    }
}
