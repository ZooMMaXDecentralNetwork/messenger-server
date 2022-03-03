package com.github.ZooMMaXDecentralNetwork.messenger.server.database;

import com.github.ZooMMaXDecentralNetwork.messenger.server.Errors;
import com.github.ZooMMaXDecentralNetwork.messenger.server.WEB;

import java.util.HashMap;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UpdateServersInfo extends TimerTask {

    @Override
    public void run() {
            ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

            List<String> deadIp = DB.serversDead();
            for (String ip : deadIp) {
                Runnable runnable = () -> {
                    HashMap<String, String> server = DB.getServer(ip);
                    int count = Integer.parseInt(server.get("count"));
                    if (count > 1000) {
                        DB.deleteServer(ip);
                    } else {
                        String result = null;
                        try {
                            result = new WEB().GET("http://" + ip + ":3000/api/v1/ping").get();
                        } catch (InterruptedException | ExecutionException | IllegalArgumentException e) {
                            new Errors().save(e.toString());
                            result = "dead";
                        }
                        if (result.equals("dead")) {
                            count++;
                            DB.updateServer(ip, "off", "" + count);
                        } else if (result.equals("alive")) {
                            count = 0;
                            DB.updateServer(ip, "on", "" + count);
                        } else {
                            count += 5;
                            DB.updateServer(ip, "off", "" + count);
                        }
                    }
                };
                executorService.execute(runnable);
            }

            List<String> aliveIp = DB.serversAlive();
            for (String ip : aliveIp) {
                Runnable runnable = () -> {
                    HashMap<String, String> server = DB.getServer(ip);
                    int count = Integer.parseInt(server.get("count"));
                    String result = null;
                    try {
                        result = new WEB().GET("http://" + ip + ":3000/api/v1/ping").get();
                    } catch (InterruptedException | ExecutionException e) {
                        new Errors().save(e.toString());
                        result = "dead";
                    }
                    if (result.equals("dead")) {
                        count = 0;
                        DB.updateServer(ip, "off", "" + count);
                    } else if (result.equals("alive")) {
                        count++;
                        if (count > 1000){
                            count = 1000;
                        }
                        DB.updateServer(ip, "on", "" + count);
                    } else {
                        count = 0;
                        DB.updateServer(ip, "off", "" + count);
                    }
                };
                executorService.execute(runnable);
            }
            executorService.shutdown();
            while (!executorService.isTerminated()){
            }
    }
}
