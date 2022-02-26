package com.github.ZooMMaXDecentralNetwork.messenger.server.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DB {
    protected static final String url = "jdbc:sqlite:ZDNms.db";
    static{
        try {
            Class.forName("org.sqlite.JDBC");
        }catch (ClassNotFoundException e){
            e.printStackTrace();
        }
    }

    public static void mkTables() {
        String sql = "CREATE TABLE IF NOT EXISTS message (" +
                "sender TEXT NOT NULL," +
                "receiver TEXT NOT NULL," +
                "data TEXT NOT NULL," +
                "ts TEXT NOT NULL," +
                "hash TEXT NOT NULL" +
                ");";
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void inMsg(String sender, String receiver, String data, String hash){
        String sql = "INSERT INTO message (sender, receiver, data, ts, hash) VALUES('"+sender+"','"+receiver+"','"+data+"','"+System.currentTimeMillis()+"','"+hash+"');";
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<HashMap<String, String>> outMsgSrv(){
        String sql = "SELECT * FROM message;";
        List<HashMap<String, String>> tmp = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()){
                HashMap<String, String> m = new HashMap<>();
                m.put("sender", rs.getString("sender"));
                m.put("receiver", rs.getString("receiver"));
                m.put("data", rs.getString("data"));
                m.put("ts", rs.getString("ts"));
                m.put("hash", rs.getString("hash"));
                tmp.add(m);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tmp;
    }
}
