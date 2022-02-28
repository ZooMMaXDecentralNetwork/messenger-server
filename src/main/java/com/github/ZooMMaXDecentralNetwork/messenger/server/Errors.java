package com.github.ZooMMaXDecentralNetwork.messenger.server;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Errors {
    File f = new File("errors.log");
    public void mkLogFile() throws IOException {
        if (!f.exists()) {
            FileWriter fw = new FileWriter(f.getAbsoluteFile(), false);
            fw.write("");
            fw.flush();
            fw.close();
        }
    }

    public void save(String e){
        FileWriter fw = null;
        try {
            fw = new FileWriter(f.getAbsoluteFile(), true);
            fw.write("\n"+e);
            fw.flush();
            fw.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
