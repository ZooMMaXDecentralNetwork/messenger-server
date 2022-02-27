package com.github.ZooMMaXDecentralNetwork.messenger.server.client;

import com.github.ZooMMaXDecentralNetwork.messenger.server.WEB;
import com.github.ZooMMaXDecentralNetwork.messenger.server.database.DB;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class UpdateMsg implements Runnable{
    @Override
    public void run() {
        List<String> servers = DB.serversAlive();
        for (String ip : servers) {
            JSONObject jObj = null;
            try {
                jObj = new JSONObject(new WEB().GET("http://"+ ip + ":3000/api/v1/syncmessageserver").get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            JSONArray jArr = jObj.getJSONArray("response");
            for (int x = 0; x < jArr.length(); x++){
                JSONObject jsonObject = jArr.getJSONObject(x);
                String sender = jsonObject.getString("sender");
                String receiver = jsonObject.getString("receiver");
                String data = jsonObject.getString("data");
                String ts = jsonObject.getString("ts");
                String hash = jsonObject.getString("hash");
                if (!DB.existsMsg(hash)){
                    DB.updMsg(sender, receiver, data, ts, hash);
                }
            }
        }
    }

}
