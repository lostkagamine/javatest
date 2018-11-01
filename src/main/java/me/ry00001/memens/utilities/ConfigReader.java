package me.ry00001.memens.utilities;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;

/**
 * Config reader
 * @author ry00001
 */
public class ConfigReader {
    private File file;
    private ObjectMapper m;

    public ConfigReader(File file) {
        this.setFile(file);
        this.m = new ObjectMapper();
    }

    public void setFile(File file) {
        this.file = file;
    }

    public File getFile() {
        return this.file;
    }

    public Config read() throws IOException {
        return this.m.readValue(this.file, Config.class);
    }
}