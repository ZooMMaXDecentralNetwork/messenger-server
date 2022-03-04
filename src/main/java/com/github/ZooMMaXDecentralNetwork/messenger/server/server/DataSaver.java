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
        this.sender = jObj.getString("sender");
        this.receiver = jObj.getString("receiver");
        this.data = jObj.getString("data");
        this.ptp = jObj.getString("peertopeer");
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        String toHash = new String(request, StandardCharsets.UTF_8) + new Random().nextInt();
        this.hash = HexUtils.toString(md.digest(toHash.getBytes(StandardCharsets.UTF_8)));
    }

    @Override
    public void run() {
        DB.inMsg(sender, receiver, data, hash, ptp);
    }
}
