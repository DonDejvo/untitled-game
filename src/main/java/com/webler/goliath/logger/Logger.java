package com.webler.goliath.logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {
    public static final int LEVEL_INFO = 0b0001;
    public static final int LEVEL_WARN = 0b0010;
    public static final int LEVEL_ERROR = 0b0100;
    public static final int LEVEL_ALL = LEVEL_INFO | LEVEL_WARN | LEVEL_ERROR;
    private static int logLevel = LEVEL_ALL;

    public static void log(String message, int level) {
        if((logLevel & level) != 0) {
            String dateTimeString = getCurrentDateTimeString();
            String levelString = switch (level) {
                case LEVEL_ERROR -> "ERROR";
                case LEVEL_WARN -> "WARN";
                default -> "INFO";
            };
            System.out.printf("(%s) (%s) %s\n", levelString, dateTimeString, message);
        }
    }

    private static String getCurrentDateTimeString() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuu/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
    }

    public static int getLogLevel() {
        return logLevel;
    }

    public static void setLogLevel(int logLevel) {
        Logger.logLevel = logLevel;
    }
}
