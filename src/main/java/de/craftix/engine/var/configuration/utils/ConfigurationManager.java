package de.craftix.engine.var.configuration.utils;

import de.craftix.engine.GameEngine;
import de.craftix.engine.var.Dimension;
import de.craftix.engine.var.Quaternion;
import de.craftix.engine.var.Transform;
import de.craftix.engine.var.Vector2;
import de.craftix.engine.var.configuration.file.YamlConfiguration;

import java.io.File;

public final class ConfigurationManager {

    private final File file;
    private final Configurable<?>[] configValues;
    private final YamlConfiguration config;

    public ConfigurationManager(File file, Configurable<?>... configValues) {
        this.file = file;
        this.configValues = configValues;

        if (file.exists()) {
            config = YamlConfiguration.loadConfiguration(file);
            setDefaults();
            loadData();
        }
        else {
            config = new YamlConfiguration();
            setDefaults();
        }

        saveConfig();
    }

    private void setDefaults() {
        for (Configurable<?> value : configValues) {
            config.addDefault(value.getConfigPath(), value.getDefaultValue());
        }
    }

    private void loadData() {
        for (Configurable<?> value : configValues) {
            String path = value.getConfigPath();
            if (value.getClassType() == Dimension.class) {
                Dimension val = new Dimension();
                val.width = (float) config.getDouble(path + ".width");
                val.height = (float) config.getDouble(path + ".height");
                value.setValueFromManager(val);
            }
            else if (value.getClassType() == Quaternion.class) {
                Quaternion val = Quaternion.euler((float) config.getDouble(path));
                value.setValueFromManager(val);
            }
            else if (value.getClassType() == Vector2.class) {
                Vector2 val = new Vector2();
                val.x = (float) config.getDouble(path + ".x");
                val.y = (float) config.getDouble(path + ".y");
                value.setValueFromManager(val);
            }
            else if (value.getClassType() == Transform.class) {
                Transform val = new Transform();
                val.position.x = (float) config.getDouble(path + ".position.x");
                val.position.y = (float) config.getDouble(path + ".position.y");
                val.scale.width = (float) config.getDouble(path + ".dimensions.width");
                val.scale.height = (float) config.getDouble(path + ".dimensions.height");
                val.rotation = Quaternion.euler((float) config.getDouble(path + ".rotation"));
                value.setValueFromManager(val);
            }
            else value.setValueFromManager(config.get(value.getConfigPath()));
        }
    }

    public void saveConfig() {
        for (Configurable<?> value : configValues) {
            String path = value.getConfigPath();
            if (value.getClassType() == Dimension.class) {
                Dimension val = (Dimension) value.getValue();
                config.set(path + ".width", val.width);
                config.set(path + ".height", val.height);
            }
            else if (value.getClassType() == Quaternion.class) {
                Quaternion val = (Quaternion) value.getValue();
                config.set(path, val.getAngleDeg());
            }
            else if (value.getClassType() == Vector2.class) {
                Vector2 val = (Vector2) value.getValue();
                config.set(path + ".x", val.x);
                config.set(path + ".y", val.y);
            }
            else if (value.getClassType() == Transform.class) {
                Transform val = (Transform) value.getValue();
                config.set(path + ".position.x", val.position.x);
                config.set(path + ".position.y", val.position.y);
                config.set(path + ".dimensions.width", val.scale.width);
                config.set(path + ".dimensions.height", val.scale.height);
                config.set(path + ".rotation", val.rotation.getAngleDeg());
            }
            else config.set(value.getConfigPath(), value.getValue());
        }
        config.save(file);
    }

    public void reloadConfig() {
        try {
            config.load(file);
        }catch (Exception e) {
            GameEngine.throwError(e);
        }

        loadData();
    }

}
