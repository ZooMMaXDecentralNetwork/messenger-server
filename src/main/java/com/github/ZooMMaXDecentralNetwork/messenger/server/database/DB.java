package com.github.ZooMMaXDecentralNetwork.messenger.server.database;

import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.github.ZooMMaXDecentralNetwork.messenger.server.Errors;
import ru.zoommax.hul.HexUtils;

public class DB {
    protected static final String url = "jdbc:sqlite:ZDNms.db";

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            new Errors().save(e.toString());
        }
    }

    public static void mkTables() {
        String sql = "CREATE TABLE IF NOT EXISTS message (" +
                "sender TEXT NOT NULL," +
                "receiver TEXT NOT NULL," +
                "data TEXT NOT NULL," +
                "ts TEXT NOT NULL," +
                "hash TEXT NOT NULL," +
                "ptp TEXT NOT NULL" +
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
            new Errors().save(e.toString());
        }
    }

    public static void newServer(String ip){
        ip = hexMe(ip);
        String sql = "INSERT INTO servers(ip, alive, count) VALUES('"+ip+"', '"+hexMe("off")+"', '"+hexMe("1")+"');";
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            new Errors().save(e.toString());
        }
    }

    public static boolean existsServer(String ip){
        ip = hexMe(ip);
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
            new Errors().save(e.toString());
        }
        return tmp;
    }

    public static List<String> serversAlive(){
        String sql = "SELECT * FROM servers WHERE alive like '"+hexMe("on")+"';";
        List<String> tmp = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()){
                String res = unHexMe(rs.getString("ip"));
                tmp.add(res);
            }
        }catch (SQLException e){
            new Errors().save(e.toString());
        }
        return tmp;
    }

    public static List<String> serversDead(){
        String sql = "SELECT * FROM servers WHERE alive like '"+hexMe("off")+"';";
        List<String> tmp = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()){
                 String res = unHexMe(rs.getString("ip"));
                tmp.add(res);
            }
        }catch (SQLException e){
            new Errors().save(e.toString());
        }
        return tmp;
    }

    public static HashMap<String, String> getServer(String ip){
        ip = hexMe(ip);
        String sql = "SELECT * FROM servers WHERE ip like '"+ip+"';";
        HashMap<String, String> tmp = new HashMap<>();
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()){
                tmp.put("ip", unHexMe(rs.getString("ip")));
                tmp.put("alive", unHexMe(rs.getString("alive")));
                tmp.put("count", unHexMe(rs.getString("count")));
            }
        }catch (SQLException e){
            new Errors().save(e.toString());
        }
        return tmp;
    }

    public static void updateServer(String ip, String alive, String count){
        ip = hexMe(ip);
        alive = hexMe(alive);
        count = hexMe(count);
        String sql = "UPDATE servers SET alive = '"+alive+"', count = '"+count+"' WHERE ip like '"+ip+"';";
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            new Errors().save(e.toString());
        }
    }

    public static void deleteServer(String ip){
        ip = hexMe(ip);
        String sql = "DELETE FROM servers WHERE ip like '"+ip+"';";
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            new Errors().save(e.toString());
        }
    }

    public static boolean existsMsg(String hash){
        hash =	hexMe(hash);
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
            new Errors().save(e.toString());
        }
        return tmp;
    }

    public static void updMsg(String sender, String receiver, String data, String ts,  String hash){
        sender = hexMe(sender);
        receiver = hexMe(receiver);
        data = hexMe(data);
        ts = hexMe(ts);
        hash = hexMe(hash);
        String ptp = hexMe("0");
        String sql = "INSERT INTO message(sender, receiver, data, ts, hash, ptp) VALUES('" + sender + "','" + receiver + "','" + data + "','" + ts + "','" + hash + "', '"+ptp+"');";
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            new Errors().save(e.toString());
        }
    }

    public static void inMsg(String sender, String receiver, String data, String hash, String ptp) {
        sender = hexMe(sender);
        receiver = hexMe(receiver);
        data = hexMe(data);
        hash = hexMe(hash);
        ptp = hexMe(ptp);
        String sql = "INSERT INTO message (sender, receiver, data, ts, hash, ptp) VALUES('" + sender + "','" + receiver + "','" + data + "'," +
                "'" + hexMe(System.currentTimeMillis()+"") + "','" + hash + "'," +
                "'"+ptp+"'" +
                ");";
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            new Errors().save(e.toString());
        }
    }

    public static List<HashMap<String, String>> outMsgSrv() {
        String sql = "SELECT * FROM message WHERE ptp like '"+hexMe("0")+"';";
        List<HashMap<String, String>> tmp = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                HashMap<String, String> m = new HashMap<>();
                m.put("sender", unHexMe(rs.getString("sender")));
                m.put("receiver", unHexMe(rs.getString("receiver")));
                m.put("data", unHexMe(rs.getString("data")));
                m.put("ts", unHexMe(rs.getString("ts")));
                m.put("hash", unHexMe(rs.getString("hash")));
                tmp.add(m);
            }
        } catch (SQLException e) {
            new Errors().save(e.toString());
        }
        return tmp;
    }

    public static List<HashMap<String, String>> outMsgUsr(String receiver) {
        receiver = hexMe(receiver);
        long ts = System.currentTimeMillis();
        String sql = "SELECT * FROM message WHERE receiver like '" + receiver + "';";
        List<HashMap<String, String>> tmp = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                long tsdb = Long.parseLong(unHexMe(rs.getString("ts")));
                if (ts - tsdb <= 86400) {
                    HashMap<String, String> m = new HashMap<>();
                    m.put("sender", unHexMe(rs.getString("sender")));
                    m.put("receiver", unHexMe(rs.getString("receiver")));
                    m.put("data", unHexMe(rs.getString("data")));
                    m.put("ts", unHexMe(rs.getString("ts")));
                    m.put("hash", unHexMe(rs.getString("hash")));
                    tmp.add(m);
                    if (unHexMe(rs.getString("ptp")).equals("1")){
                        DB.delete(unHexMe(rs.getString("hash")));
                    }
                }
            }
        } catch (SQLException e) {
            new Errors().save(e.toString());
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
                long tsdb = Long.parseLong(unHexMe(rs.getString("ts")));
                if (ts - tsdb >= 86400) {
                    tmp.add(unHexMe(rs.getString("hash")));
                }
            }
        } catch (SQLException e) {
            new Errors().save(e.toString());
        }
        return tmp;
    }

    public static void delete(String hash){
        hash = hexMe(hash);
        String sql = "DELETE FROM message WHERE hash like '"+hash+"';";
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            new Errors().save(e.toString());
        }
    }
    
    private static String unHexMe(String hex){
    	return new String(HexUtils.fromString(hex), StandardCharsets.UTF_8);
    }
    
    private static String hexMe(String toHex){
    	return HexUtils.toString(toHex.getBytes());
    }
}
