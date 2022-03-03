package com.github.ZooMMaXDecentralNetwork.messenger.server.server;

import com.github.ZooMMaXDecentralNetwork.messenger.server.database.DB;
import org.json.JSONObject;
import ru.zoommax.hul.HexUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class DataSaver implements Runnable{

    String sender;
    String receiver;
    String data;
    String hash;
    String ptp;

    public DataSaver(byte[] request) throws NoSuchAlgorithmException {
        JSONObject jObj = new JSONObject(new String(request, StandardCharsets.UTF_8));
        this.sender = HexUtils.toString(jObj.getString("sender").getBytes(StandardCharsets.UTF_8));
        this.receiver = HexUtils.toString(jObj.getString("receiver").getBytes(StandardCharsets.UTF_8));
        this.data = HexUtils.toString(jObj.getString("data").getBytes(StandardCharsets.UTF_8));
        this.ptp = HexUtils.toString(jObj.getString("peertopeer").getBytes(StandardCharsets.UTF_8));
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        String toHash = new String(request, StandardCharsets.UTF_8) + new Random().nextInt();
        this.hash = HexUtils.toString(md.digest(toHash.getBytes(StandardCharsets.UTF_8)));
    }

    @Override
    public void run() {
        DB.inMsg(sender, receiver, data, hash, ptp);
    }
}
