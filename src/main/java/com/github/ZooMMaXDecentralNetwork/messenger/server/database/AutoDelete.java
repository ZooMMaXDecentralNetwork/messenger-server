package com.github.ZooMMaXDecentralNetwork.messenger.server.database;

import java.util.List;

public class AutoDelete implements Runnable{
    @Override
    public void run() {
        List<String> forDelete = DB.hashForDelete();
        for(String s : forDelete){
            DB.delete(s);
        }
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
