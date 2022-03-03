package com.github.ZooMMaXDecentralNetwork.messenger.server.database;

import com.github.ZooMMaXDecentralNetwork.messenger.server.Errors;

import java.util.List;
import java.util.TimerTask;

public class AutoDelete extends TimerTask {
    @Override
    public void run() {
        List<String> forDelete = DB.hashForDelete();
        for(String s : forDelete){
            DB.delete(s);
        }
    }
}
