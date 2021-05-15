package de.craftix.engine.var;

import de.craftix.engine.GameEngine;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;
import java.util.stream.Stream;

public class YamlConfiguration implements Serializable {

    public static YamlConfiguration createConfiguration(String path, String name) {
        File file = new File(path + "/" + name + ".yml");
        if (file.exists()) return new YamlConfiguration(file.getPath());
        try {
            new File(path).mkdirs();
            if (!file.exists()) file.createNewFile();
        }catch (Exception e) { GameEngine.throwError(e); }
        return new YamlConfiguration(file.getPath());
    }

    private final HashMap<String, Object> variables = new HashMap<>();
    private final File file;

    public YamlConfiguration(String path) {
        file = new File(path);
        if (!file.exists()) throw new NullPointerException("File does not exist");
        loadVariables();
    }

    private void loadVariables() {
        ArrayList<String> lines = new ArrayList<>();
        try (Stream<String> stream = Files.lines(file.toPath(), StandardCharsets.UTF_8)) {
            stream.forEach(lines::add);
        }catch (Exception e) { GameEngine.throwError(e); }
        for (String line : lines) {
            String[] separator = line.split(": ");
            variables.put(separator[0], separator[1]);
        }
    }

    private void saveVariables() {
        ArrayList<String> lines = new ArrayList<>();
        for (String path : variables.keySet()) lines.add(path + ": " + variables.get(path).toString());
        Collections.sort(lines);
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < lines.size(); i++) {
            out.append(lines.get(i));
            if (i != lines.size() - 1) out.append("\n");
        }
        try {
            if (file.delete()) file.createNewFile();
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(out.toString());
            writer.close();
        }catch (Exception e) { GameEngine.throwError(e); }
    }

    @Override
    public String toString() {
        ArrayList<String> lines = new ArrayList<>();
        for (String path : variables.keySet()) lines.add(path + ": " + variables.get(path).toString());
        Collections.sort(lines);
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < lines.size(); i++) {
            if (i != lines.size() - 1) out.append(lines.get(i)).append("\n");
            else out.append(lines.get(i));
        }
        return out.toString();
    }

    public void saveConfiguration() { saveVariables(); }

    public void setValue(String path, Object value) {
        variables.remove(path);
        variables.put(path, value);
    }

    public Object getObject(String path) { return variables.get(path); }
    public String getString(String path) { return variables.get(path).toString(); }
    public Integer getInt(String path) { return Integer.parseInt(variables.get(path).toString()); }
    public Float getFloat(String path) { return Float.parseFloat(variables.get(path).toString()); }
    public Double getDouble(String path) { return Double.parseDouble(variables.get(path).toString()); }
    public Boolean getBoolean(String path) { return Boolean.getBoolean(variables.get(path).toString()); }
    public Long getLong(String path) { return Long.parseLong(variables.get(path).toString()); }
    public Vector2 getVector(String path) { return Transform.parse(variables.get(path).toString()).position; }
    public Dimension getDimension(String path) { return Transform.parse(variables.get(path).toString()).scale; }
    public Quaternion getQuaternion(String path) { return Transform.parse(variables.get(path).toString()).rotation; }
    public Transform getTransform(String path) { return Transform.parse(variables.get(path).toString()); }
    public UUID getUUID(String path) { return UUID.fromString(variables.get(path).toString()); }

}
