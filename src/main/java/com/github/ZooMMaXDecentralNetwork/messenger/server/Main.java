package com.github.ZooMMaXDecentralNetwork.messenger.server;

import com.github.ZooMMaXDecentralNetwork.messenger.server.client.ClientMain;
import com.github.ZooMMaXDecentralNetwork.messenger.server.database.AutoDelete;
import com.github.ZooMMaXDecentralNetwork.messenger.server.database.DB;
import com.github.ZooMMaXDecentralNetwork.messenger.server.database.UpdateServersInfo;
import com.github.ZooMMaXDecentralNetwork.messenger.server.server.ProccesBuilder;
import com.github.ZooMMaXDecentralNetwork.messenger.server.server.ServerMain;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) throws IOException {
        new  Errors().mkLogFile();
        DB.mkTables();
        if (args.length > 0){
            if (!DB.existsServer(args[0])){
                DB.newServer(args[0]);
            }
        }

        new Thread(new ServerMain()).start();
        new Thread(new ProccesBuilder()).start();
        new Thread(new AutoDelete()).start();
        new Thread(new ClientMain()).start();
        new Thread(new UpdateServersInfo()).start();
    }
}
