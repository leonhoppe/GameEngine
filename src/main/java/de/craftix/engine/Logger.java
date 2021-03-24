package de.craftix.engine;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger implements Serializable {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    protected String applicationName;

    public Logger(String applicationName) {
        this.applicationName = applicationName;
    }

    public void info(String message) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        System.out.println("[" + ANSI_BLUE + dtf.format(now) + ANSI_RESET + "] [" + ANSI_CYAN + "INFO" + ANSI_RESET + "] [" + applicationName + "] " + message + ANSI_RESET);
    }

    public void warning(String message) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        System.out.println("[" + ANSI_BLUE + dtf.format(now) + ANSI_RESET + "] [" + ANSI_YELLOW + "WARNING" + ANSI_RESET + "] [" + applicationName + "] " + message + ANSI_RESET);
    }

    public void error(String message) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        System.err.println(ANSI_WHITE + "[" + ANSI_BLUE + dtf.format(now) + ANSI_WHITE + "] [" + ANSI_RED + "ERROR" + ANSI_WHITE + "] [" + applicationName + "] " + ANSI_RED + message + ANSI_RESET);
    }

    public static void globalInfo(String message) { new Logger("Global").info(message); }
    public static void globalWarning(String message) { new Logger("Global").warning(message); }
    public static void globalError(String message) { new Logger("Global").error(message); }

}
