package com.tieto.food.ui;

import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;

import org.apache.wicket.util.time.Duration;
import org.eclipse.jetty.jmx.MBeanContainer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.bio.SocketConnector;
import org.eclipse.jetty.server.ssl.SslSocketConnector;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.webapp.WebAppContext;

public class Start {
    public static void main(String[] args) throws Exception {
        startJettyServer(8080, "/");
    }

    private static void startJettyServer(int port, String contextPath)
            throws Exception {
        int timeout = (int) Duration.ONE_HOUR.getMilliseconds();

        Server server = new Server();
        SocketConnector connector = new SocketConnector();

        // Set some timeout options to make debugging easier.
        connector.setMaxIdleTime(timeout);
        connector.setSoLingerTime(-1);
        connector.setPort(port);
        server.addConnector(connector);

        Resource keystore = Resource.newClassPathResource("/keystore");
        if (keystore != null && keystore.exists()) {
            // if a keystore for a SSL certificate is available, start a SSL
            // connector on port 8443.
            // By default, the quickstart comes with a Apache Wicket Quickstart
            // Certificate that expires about half way september 2021. Do not
            // use this certificate anywhere important as the passwords are
            // available in t he source.


            connector.setConfidentialPort(8443);
            SslContextFactory factory = new SslContextFactory();
            factory.setKeyStoreResource(keystore);
            factory.setKeyStorePassword("wicket");
            factory.setTrustStoreResource(keystore);
            factory.setKeyManagerPassword("wicket");
            SslSocketConnector sslConnector = new SslSocketConnector(factory);
            sslConnector.setMaxIdleTime(timeout);
            sslConnector.setPort(8443);
            sslConnector.setAcceptors(4);
            server.addConnector(sslConnector);

            System.out
                    .println("SSL access to the quickstart has been enabled on port 8443");
            System.out
                    .println("You can access the application using SSL on https://localhost:8443");
            System.out.println();
        }

        WebAppContext bb = new WebAppContext();
        bb.setServer(server);
        bb.setContextPath(contextPath);
        bb.setWar("src/main/webapp");

        // START JMX SERVER
        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        MBeanContainer mBeanContainer = new MBeanContainer(mBeanServer);
        server.getContainer().addEventListener(mBeanContainer);
        mBeanContainer.start();

        server.setHandler(bb);

        System.out.println("------------- starting jetty ...");
        server.start();
        System.out.println();
        System.out.println("    http://localhost:" + port + contextPath);
        System.out.println();
        System.out
                .println("------------- starting jetty ... done. press ENTER to stop.");
        System.in.read();
        System.out.println("------------- stopping jetty ...");
        server.stop();
        server.join();
        System.out.println("------------- stopping jetty ... done");
    }
}