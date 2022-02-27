package com.github.ZooMMaXDecentralNetwork.messenger.server.server;

import com.github.ZooMMaXDecentralNetwork.messenger.server.database.DB;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpServer;
import org.json.JSONArray;
import org.json.JSONObject;
import ru.zoommax.hul.HexUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

public class ServerMain implements Runnable {
    static List<byte[]> inputData = new ArrayList<>();

    @Override
    public void run() {
        int serverPort = 3000;
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(serverPort), 0);

            server.createContext("/api/v1/inputmessage", exchange -> {
                Headers requestHeaders = exchange.getRequestHeaders();
                int contentLength = Integer.parseInt(requestHeaders.getFirst("Content-length"));

                InputStream is = exchange.getRequestBody();

                byte[] data = is.readAllBytes();
                inputData.add(data);
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, contentLength);
                OutputStream os = exchange.getResponseBody();
                os.write(data);
                exchange.close();
            });

            server.createContext("/api/v1/syncmessageserver", exchange -> {
                String respText = "";
                Callable task = () -> {
                    List<HashMap<String, String>> result = DB.outMsgSrv();
                    JSONArray jArr = new JSONArray();
                    for (HashMap<String, String> a : result){
                        JSONObject jObj = new JSONObject();
                        jObj.put("sender", a.get("sender"));
                        jObj.put("receiver", a.get("receiver"));
                        jObj.put("data", a.get("data"));
                        jObj.put("ts", a.get("ts"));
                        jObj.put("hash", a.get("hash"));
                        jArr.put(jObj);
                    }
                    return new JSONObject().put("response", jArr);
                };
                FutureTask<JSONObject> future = new FutureTask<>(task);
                new Thread(future).start();
                JSONObject result = null;
                try {
                    result = future.get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
                respText = result.toString();
                exchange.sendResponseHeaders(200, respText.getBytes().length);
                OutputStream output = exchange.getResponseBody();
                output.write(respText.getBytes());
                output.flush();
                exchange.close();
            });

            server.createContext("/api/v1/syncmessageclient", exchange -> {
                String request = HexUtils.toString(decode(exchange.getRequestURI().getRawQuery()).getBytes(StandardCharsets.UTF_8));
                String respText = "";
                String finalRequest = request;
                Callable task = () -> {
                    List<HashMap<String, String>> result = DB.outMsgUsr(finalRequest);
                    JSONArray jArr = new JSONArray();
                    for (HashMap<String, String> a : result){
                        JSONObject jObj = new JSONObject();
                        jObj.put("sender", a.get("sender"));
                        jObj.put("receiver", a.get("receiver"));
                        jObj.put("data", a.get("data"));
                        jObj.put("ts", a.get("ts"));
                        jObj.put("hash", a.get("hash"));
                        jArr.put(jObj);
                    }
                    return new JSONObject().put("response", jArr);
                };
                FutureTask<JSONObject> future = new FutureTask<>(task);
                new Thread(future).start();
                JSONObject result = null;
                try {
                    result = future.get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
                respText = result.toString();
                exchange.sendResponseHeaders(200, respText.getBytes().length);
                OutputStream output = exchange.getResponseBody();
                output.write(respText.getBytes());
                output.flush();
                exchange.close();
            });

            server.createContext("/api/v1/ping", exchange -> {
                String respText = "alive";
                String ip = exchange.getRemoteAddress().getHostName();
                if (!DB.existsServer(ip)){
                    DB.newServer(ip);
                }
                exchange.sendResponseHeaders(200, respText.getBytes().length);
                OutputStream output = exchange.getResponseBody();
                output.write(respText.getBytes());
                output.flush();
                exchange.close();
            });

            server.createContext("/api/v1/getservers", exchange -> {
                String respText = "";
                List<String> servers = DB.serversAlive();
                JSONObject jsonObject = new JSONObject().put("servers", servers);
                respText = jsonObject.toString();
                exchange.sendResponseHeaders(200, respText.getBytes().length);
                OutputStream output = exchange.getResponseBody();
                output.write(respText.getBytes());
                output.flush();
                exchange.close();
            });



            server.setExecutor(null); // creates a default executor
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static String decode(final String encoded) {
        try {
            return encoded == null ? null : URLDecoder.decode(encoded, "UTF-8");
        } catch (final UnsupportedEncodingException e) {
            throw new RuntimeException("UTF-8 is a required encoding", e);
        }
    }

    public static List<byte[]> getInputData(){
        List<byte[]> tmp = new ArrayList<>();
        tmp.addAll(inputData);
        inputData.clear();
        return tmp;
    }
}
