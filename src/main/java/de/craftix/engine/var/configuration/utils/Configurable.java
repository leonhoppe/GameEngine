package de.craftix.engine.var.configuration.utils;

public class Configurable<T> {
    private final String configPath;
    private final T defaultValue;
    private T value;

    public Configurable(String configPath, T defaultValue) {
        this.configPath = configPath;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
    }

    public String getConfigPath() { return configPath; }
    public T getDefaultValue() { return defaultValue; }
    public T getValue() { return value; }
    public void setValue(T newValue) { this.value = newValue; }

    protected Class<T> getClassType() { return (Class<T>) value.getClass(); }
    protected void setValueFromManager(Object newValue) { this.value = (T) newValue; }

    @Override
    public String toString() { return value.toString(); }
}
