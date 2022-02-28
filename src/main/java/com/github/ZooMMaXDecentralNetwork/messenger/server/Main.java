package com.github.ZooMMaXDecentralNetwork.messenger.server;

import com.github.ZooMMaXDecentralNetwork.messenger.server.client.ClientMain;
import com.github.ZooMMaXDecentralNetwork.messenger.server.database.AutoDelete;
import com.github.ZooMMaXDecentralNetwork.messenger.server.database.DB;
import com.github.ZooMMaXDecentralNetwork.messenger.server.database.UpdateServersInfo;
import com.github.ZooMMaXDecentralNetwork.messenger.server.server.ProccesBuilder;
import com.github.ZooMMaXDecentralNetwork.messenger.server.server.ServerMain;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        
        DB.mkTables();
        
        if (args.length > 0){
            if (!DB.existsServer(args[0])){
                DB.newServer(args[0]);
            }
        }
        Thread[] tt = new Thread[5];
        Thread t1 = new Thread(new ServerMain());
        Thread t2 = new Thread(new ProccesBuilder());
        Thread t3 = new Thread(new AutoDelete());
        Thread t4 = new Thread(new ClientMain());
        Thread t5 = new Thread(new UpdateServersInfo());
        tt[0] = t1;
        tt[1] = t2;
        tt[2] = t3;
        tt[3] = t4;
        tt[4] = t5;
        while (true){
        	for(Thread t : tt){
        		if(!t.isAlive()){
        			t.start();
        			System.out.println(t.getName());
        		}
        		Thread.sleep(5000);
        	}
     
        }
    }
}
