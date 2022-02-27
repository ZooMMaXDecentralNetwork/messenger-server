package com.github.ZooMMaXDecentralNetwork.messenger.server;

import com.github.ZooMMaXDecentralNetwork.messenger.server.client.ClientMain;
import com.github.ZooMMaXDecentralNetwork.messenger.server.database.AutoDelete;
import com.github.ZooMMaXDecentralNetwork.messenger.server.database.DB;
import com.github.ZooMMaXDecentralNetwork.messenger.server.database.UpdateServersInfo;
import com.github.ZooMMaXDecentralNetwork.messenger.server.server.ProccesBuilder;
import com.github.ZooMMaXDecentralNetwork.messenger.server.server.ServerMain;

public class Main {
    public static void main(String[] args) {
        if (args.length > 0){
            if (!DB.existsServer(args[0])){
                DB.newServer(args[0]);
            }
        }
        DB.mkTables();
        new Thread(new ServerMain()).start();
        new Thread(new ProccesBuilder()).start();
        new Thread(new AutoDelete()).start();
        new Thread(new ClientMain()).start();
        new Thread(new UpdateServersInfo()).start();
    }
}
