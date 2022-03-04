package com.github.ZooMMaXDecentralNetwork.messenger.server;

import com.github.ZooMMaXDecentralNetwork.messenger.server.client.GetServers;
import com.github.ZooMMaXDecentralNetwork.messenger.server.client.UpdateMsg;
import com.github.ZooMMaXDecentralNetwork.messenger.server.database.AutoDelete;
import com.github.ZooMMaXDecentralNetwork.messenger.server.database.DB;
import com.github.ZooMMaXDecentralNetwork.messenger.server.database.UpdateServersInfo;
import com.github.ZooMMaXDecentralNetwork.messenger.server.server.ProccesBuilder;
import com.github.ZooMMaXDecentralNetwork.messenger.server.server.ServerMain;

import java.io.IOException;
import java.util.Timer;

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
        new Timer().schedule(new ProccesBuilder(), 0, 1000);
        new Timer().schedule(new AutoDelete(), 0 , 10000);
        new Timer().schedule(new GetServers(), 0, 30000);
        new Timer().schedule(new UpdateMsg(), 0, 1000);
        new Timer().schedule(new UpdateServersInfo(), 0, 30000);
        System.out.println("Server started");
    }
}
