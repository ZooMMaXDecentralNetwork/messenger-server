package com.github.ZooMMaXDecentralNetwork.messenger.server.client;

import com.github.ZooMMaXDecentralNetwork.messenger.server.WEB;
import com.github.ZooMMaXDecentralNetwork.messenger.server.database.DB;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class GetServers implements Runnable{
    @Override
    public void run() {
        List<String> servers = DB.serversAlive();
        for (String ip : servers) {
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(new WEB().GET("http://"+ip+":3000/api/v1/getservers").get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            JSONArray jArr = jsonObject.getJSONArray("servers");
            for (int x = 0; x< jArr.length(); x++){
                String maybeIp = jArr.getString(x);
                if (!DB.existsServer(maybeIp)){
                    DB.newServer(maybeIp);
                }
            }
        }
    }
}
