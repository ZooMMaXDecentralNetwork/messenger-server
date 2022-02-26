package com.github.ZooMMaXDecentralNetwork.messenger.server.server;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Random;

public class ServerMain implements Runnable {
    @Override
    public void run() {
        int serverPort = 3000;
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(serverPort), 0);

            server.createContext("/api/v1/inputmessage", exchange -> {

            });

            server.createContext("/api/v1/syncmessageserver", exchange -> {

            });

            server.createContext("/api/v1/syncmessageclient", exchange -> {

            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
