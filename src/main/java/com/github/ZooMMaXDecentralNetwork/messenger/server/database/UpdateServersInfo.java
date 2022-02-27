package com.github.ZooMMaXDecentralNetwork.messenger.server.database;

import com.github.ZooMMaXDecentralNetwork.messenger.server.WEB;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class UpdateServersInfo implements Runnable{

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(30000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            List<String> deadIp = DB.serversDead();
            for (String ip : deadIp) {
                HashMap<String, String> server = DB.getServer(ip);
                int count = Integer.parseInt(server.get("count"));
                if (count > 1000) {
                    DB.deleteServer(ip);
                } else {
                    String result = null;
                    try {
                        result = new WEB().GET("http://" + ip + ":3000/api/v1/ping").get();
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                        result = "dead";
                    }
                    if (result.equals("dead")) {
                        count++;
                        DB.updateServer(ip, "off", "" + count);
                    } else if (result.equals("alive")){
                        count = 0;
                        DB.updateServer(ip, "on", "" + count);
                    }else {
                        count += 5;
                        DB.updateServer(ip, "off", "" + count);
                    }
                }
            }

            List<String> aliveIp = DB.serversAlive();
            for (String ip : aliveIp) {
                HashMap<String, String> server = DB.getServer(ip);
                int count = Integer.parseInt(server.get("count"));
                String result = null;
                try {
                    result = new WEB().GET("http://" + ip + ":3000/api/v1/ping").get();
                } catch (InterruptedException | ExecutionException e) {
                    result = "dead";
                }
                if (result.equals("dead")) {
                    count = 0;
                    DB.updateServer(ip, "off", "" + count);
                } else if (result.equals("alive")){
                    count++;
                    DB.updateServer(ip, "on", "" + count);
                }else {
                    count = 0;
                    DB.updateServer(ip, "off", "" + count);
                }
            }
        }
    }
}
