package com.github.ZooMMaXDecentralNetwork.messenger.server.server;

import com.github.ZooMMaXDecentralNetwork.messenger.server.Errors;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProccesBuilder implements Runnable{
    List<byte[]> inputData = new ArrayList<>();
    @Override
    public void run() {
        while (true) {
            inputData = ServerMain.getInputData();
            ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
            for (byte[] a : inputData) {
                Runnable worker = null;
                try {
                    worker = new DataSaver(a);
                } catch (NoSuchAlgorithmException e) {
                    new Errors().save(e.toString());
                }
                executor.execute(worker);
            }
            executor.shutdown();
            while (!executor.isTerminated()){
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                new Errors().save(e.toString());
            }
        }
    }
}
