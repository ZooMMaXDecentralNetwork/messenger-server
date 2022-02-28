package com.github.ZooMMaXDecentralNetwork.messenger.server.client;

import com.github.ZooMMaXDecentralNetwork.messenger.server.Errors;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientMain implements Runnable{
    @Override
    public void run() {
        while (true){
            Thread[] t = new Thread[2];
            t[0] = new Thread(new GetServers());
            t[1] = new Thread(new UpdateMsg());
            ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
            for (Thread thread : t){
                executorService.execute(thread);
            }
            executorService.shutdown();
            try {
                Thread.sleep(30000);
            } catch (InterruptedException e) {
                new Errors().save(e.toString());
            }
        }
    }
}
