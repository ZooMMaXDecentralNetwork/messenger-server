package com.github.ZooMMaXDecentralNetwork.messenger.server.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DB {
    protected static final String url = "jdbc:sqlite:ZDNms.db";

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
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
        String sql2 = "CREATE TABLE IF NOT EXISTS servers (" +
                "ip TEXT NOT NULL," +
                "alive TEXT NOT NULL," +
                "count TEXT NOT NULL" +
                ");";
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            stmt.execute(sql2);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void newServer(String ip){
        String sql = "INSERT INTO servers(ip, alive, count) VALUES('"+ip+"', 'off', '1');";
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean existsServer(String ip){
        String sql = "SELECT * FROM servers WHERE ip like '"+ip+"';";
        boolean tmp = false;
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()){
                if (rs.getString("ip").equals(ip)){
                    tmp = true;
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return tmp;
    }

    public static List<String> serversAlive(){
        String sql = "SELECT * FROM servers WHERE alive like 'on';";
        List<String> tmp = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()){
                tmp.add(rs.getString("ip"));
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return tmp;
    }

    public static List<String> serversDead(){
        String sql = "SELECT * FROM servers WHERE alive like 'off';";
        List<String> tmp = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()){
                tmp.add(rs.getString("ip"));
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return tmp;
    }

    public static HashMap<String, String> getServer(String ip){
        String sql = "SELECT * FROM servers WHERE ip like '"+ip+"';";
        HashMap<String, String> tmp = new HashMap<>();
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()){
                tmp.put("ip", rs.getString("ip"));
                tmp.put("alive", rs.getString("alive"));
                tmp.put("count", rs.getString("count"));
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return tmp;
    }

    public static void updateServer(String ip, String alive, String count){
        String sql = "UPDATE servers SET alive = '"+alive+"', count = '"+count+"' WHERE ip like '"+ip+"';";
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteServer(String ip){
        String sql = "DELETE FROM servers WHERE ip like '"+ip+"';";
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean existsMsg(String hash){
        String sql = "SELECT * FROM message WHERE hash like '"+hash+"';";
        boolean tmp = false;
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()){
                if (rs.getString("hash").equals(hash)){
                    tmp = true;
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return tmp;
    }

    public static void updMsg(String sender, String receiver, String data, String ts,  String hash){
        String sql = "INSERT INTO message(sender, receiver, data, ts, hash) VALUES('" + sender + "','" + receiver + "','" + data + "','" + ts + "','" + hash + "');";
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void inMsg(String sender, String receiver, String data, String hash) {
        String sql = "INSERT INTO message (sender, receiver, data, ts, hash) VALUES('" + sender + "','" + receiver + "','" + data + "','" + System.currentTimeMillis() + "','" + hash + "');";
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<HashMap<String, String>> outMsgSrv() {
        String sql = "SELECT * FROM message;";
        List<HashMap<String, String>> tmp = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
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

    public static List<HashMap<String, String>> outMsgUsr(String receiver) {
        long ts = System.currentTimeMillis();
        String sql = "SELECT * FROM message WHERE receiver like '" + receiver + "';";
        List<HashMap<String, String>> tmp = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                long tsdb = Long.parseLong(rs.getString("ts"));
                if (ts - tsdb <= 86400000) {
                    HashMap<String, String> m = new HashMap<>();
                    m.put("sender", rs.getString("sender"));
                    m.put("receiver", rs.getString("receiver"));
                    m.put("data", rs.getString("data"));
                    m.put("ts", rs.getString("ts"));
                    m.put("hash", rs.getString("hash"));
                    tmp.add(m);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tmp;
    }

    public static List<String> hashForDelete(){
        long ts = System.currentTimeMillis();
        String sql = "SELECT * FROM message;";
        List<String> tmp = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                long tsdb = Long.parseLong(rs.getString("ts"));
                if (ts - tsdb >= 86400000) {
                    tmp.add(rs.getString("hash"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tmp;
    }

    public static void delete(String hash){
        String sql = "DELETE FROM message WHERE hash like '"+hash+"';";
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
