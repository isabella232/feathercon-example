package com.xoom.oss.fs.resources;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.client.urlconnection.HTTPSProperties;
import com.xoom.oss.fc.dto.User;
import com.xoom.oss.feathercon.FeatherCon;
import com.xoom.oss.feathercon.JerseyServerBuilder;
import com.xoom.oss.feathercon.SSLConfiguration;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.File;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class UsersResourceTest {
    private FeatherCon server;

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
        server.stop();
    }

    @Test
    public void testByEmail() throws Exception {
        server = new JerseyServerBuilder("com.xoom.oss.fs.resources", "/api/*").withPort(19060).build();
        server.start();

        Client client = Client.create(new DefaultClientConfig(JacksonJsonProvider.class));
        WebResource resource = client.resource("http://localhost:19060/api/users/bob@example.com");
        User user = resource.get(User.class);
        System.out.println(user);
    }

    @Test
    public void testOverSSL() throws Exception {
        SSLConfiguration sslConfig = new SSLConfiguration.Builder()
                .withKeyStoreFile(new File(getClass().getResource("/keystore.jks").getFile()))
                .withKeyStorePassword("changeit")
                .withSslPort(0)
                .withSslOnly(true)
                .build();
        server = new JerseyServerBuilder("com.xoom.oss.fs.resources", "/api/*")
                .withSslConfiguration(sslConfig)
                .build();
        // uncomment for full network SSL debug - quite interesting
        // System.setProperty("javax.net.debug", "ssl");
        server.start();

        HostnameVerifier hostnameVerifier = new HostnameVerifier() {
            @Override
            public boolean verify(String hostName, SSLSession sslSession) {
                try {
                    Certificate[] peerCertificates = sslSession.getPeerCertificates();
                    X509Certificate certificate = (X509Certificate) peerCertificates[0];
                    String distinguishedName = certificate.getSubjectX500Principal().getName();
                    String commonName = distinguishedName.split(",")[0].split("=")[1];
                    if (!hostName.equals(commonName)) {
                        System.out.printf("sought hostname is %s, but found %s in cert CN\n", hostName, commonName);
                    }
                } catch (SSLPeerUnverifiedException e) {
                    e.printStackTrace();
                }
                return true;
            }
        };
        X509TrustManager x509TrustManager = new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] x509Certificates, String authType) throws CertificateException {
                System.out.printf("checkClientTrusted, authType %s\n", authType);
            }

            @Override
            public void checkServerTrusted(X509Certificate[] x509Certificates, String authType) throws CertificateException {
                System.out.printf("checkServerTrusted, authType %s\n", authType);
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                System.out.println("getAcceptedIssuers");
                return new X509Certificate[0];
            }
        };

        SSLContext ssl = SSLContext.getInstance("TLS");
        ssl.init(null, new TrustManager[]{x509TrustManager}, null);

        // https://blogs.oracle.com/enterprisetechtips/entry/consuming_restful_web_services_with
        HTTPSProperties httpsProperties = new HTTPSProperties(hostnameVerifier, ssl);
        DefaultClientConfig clientConfig = new DefaultClientConfig(JacksonJsonProvider.class);
        clientConfig.getProperties().put(HTTPSProperties.PROPERTY_HTTPS_PROPERTIES, httpsProperties);

        Client client = Client.create(clientConfig);
        WebResource resource = client.resource(String.format("https://localhost:%d/api/users/bob@example.com", server.getHttpsPort()));
        User user = resource.get(User.class);
        System.out.println(user);
    }
}
