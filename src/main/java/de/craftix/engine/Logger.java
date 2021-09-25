package de.craftix.engine;

import java.io.PrintStream;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger implements Serializable {
    private static boolean printSystemLog = true;

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
    protected boolean systemLog;

    public Logger(String applicationName) {
        this.applicationName = applicationName;
        this.systemLog = false;
    }
    public Logger(String applicationName, boolean systemLog) {
        this.applicationName = applicationName;
        this.systemLog = systemLog;
    }

    public void info(Object message) {
        if (systemLog && !printSystemLog) return;
        printInfoSyntax(getStream());
        getStream().println(message.toString() + ANSI_RESET);
    }

    public void warning(Object message) {
        if (systemLog && !printSystemLog) return;
        printWarningSyntax(getStream());
        getStream().println(message.toString() + ANSI_RESET);
    }

    public void error(Object message) {
        if (systemLog && !printSystemLog) return;
        printErrorSyntax(getStream());
        getStream().println(message.toString() + ANSI_RESET);
    }

    public static void globalInfo(Object message) { new Logger("Global").info(message); }
    public static void globalWarning(Object message) { new Logger("Global").warning(message); }
    public static void globalError(Object message) { new Logger("Global").error(message); }

    public PrintStream getStream() {
        return new PrintStream(System.out);
    }

    public void printInfoSyntax(PrintStream stream) {
        if (systemLog && !printSystemLog) return;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        stream.print(ANSI_RESET + "[" + ANSI_BLUE + dtf.format(now) + ANSI_RESET + "] [" + ANSI_CYAN + "INFO" + ANSI_RESET + "] [" + applicationName + "] ");
    }

    public void printWarningSyntax(PrintStream stream) {
        if (systemLog && !printSystemLog) return;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        stream.print(ANSI_RESET + "[" + ANSI_BLUE + dtf.format(now) + ANSI_RESET + "] [" + ANSI_YELLOW + "WARNING" + ANSI_RESET + "] [" + applicationName + "] ");
    }

    public void printErrorSyntax(PrintStream stream) {
        if (systemLog && !printSystemLog) return;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        stream.print(ANSI_RESET + "[" + ANSI_BLUE + dtf.format(now) + ANSI_RESET + "] [" + ANSI_RED + "ERROR" + ANSI_RESET + "] [" + applicationName + "] " + ANSI_RED);
    }

    public static void printSystemLog(boolean value) { printSystemLog = value; }
}
