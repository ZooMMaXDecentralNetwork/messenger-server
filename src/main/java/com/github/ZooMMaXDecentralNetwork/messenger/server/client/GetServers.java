package com.github.ZooMMaXDecentralNetwork.messenger.server.client;

import com.github.ZooMMaXDecentralNetwork.messenger.server.Errors;
import com.github.ZooMMaXDecentralNetwork.messenger.server.WEB;
import com.github.ZooMMaXDecentralNetwork.messenger.server.database.DB;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GetServers implements Runnable{
    @Override
    public void run() {
        while (true) {
            List<String> servers = DB.serversAlive();
            ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
            for (String ip : servers) {
                Runnable runnable = () -> {
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(new WEB().GET("http://" + ip + ":3000/api/v1/getservers").get());
                    } catch (InterruptedException | ExecutionException e) {
                        new Errors().save(e.toString());
                    }
                    if (jsonObject != null) {
                        JSONArray jArr = jsonObject.getJSONArray("servers");
                        for (int x = 0; x < jArr.length(); x++) {
                            String maybeIp = jArr.getString(x);
                            if (!DB.existsServer(maybeIp)) {
                                DB.newServer(maybeIp);
                            }
                        }
                    }
                };
                executorService.execute(runnable);
            }
            executorService.shutdown();
            while (!executorService.isTerminated()){
            }
            try {
                Thread.sleep(30000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
