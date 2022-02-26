package com.github.ZooMMaXDecentralNetwork.messenger.server;

import com.github.ZooMMaXDecentralNetwork.messenger.server.database.DB;

public class Main {
    public static void main(String[] args) {
        DB.mkTables();
    }
}
