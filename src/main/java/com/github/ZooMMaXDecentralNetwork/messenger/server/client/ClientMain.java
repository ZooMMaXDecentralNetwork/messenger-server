package com.github.ZooMMaXDecentralNetwork.messenger.server.client;

public class ClientMain implements Runnable{
    @Override
    public void run() {
        while (true){
            new Thread(new GetServers()).start();
            new Thread(new UpdateMsg()).start();
            try {
                Thread.sleep(30000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
