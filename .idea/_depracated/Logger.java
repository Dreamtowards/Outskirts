package outskirts.util.logging;

import outskirts.util.FileUtils;

import java.io.*;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class Logger {

    int stackDepth = 2;

    public void info(Object msg, Object... args) {
        println(String.format("[%s][%s][%s/INFO]: %s", time(), stack(), thread(), message(msg, args)),
                System.out);
    }

    public void warn(Object msg, Object... args) {
        println(String.format("[%s][%s][%s/WARN]: %s", time(), stack(), thread(), message(msg, args)),
                System.err);
    }

    protected void println(String message, PrintStream stream) {
        stream.println(message);
    }

    private String message(Object msg, Object... args) {
        if (msg == null)
            return  "null";

        try {
            return String.format(msg.toString(), args);
        } catch (Throwable t) {
            return msg.toString();
        }
    }

    private String thread() {
        return Thread.currentThread().getName();
    }

    private String time() {
        return new SimpleDateFormat("HH:mm:ss").format(new Date());
    }

    private String stack() {
        StackTraceElement stackTrace = new Throwable().getStackTrace()[stackDepth];
        return stackTrace.getFileName() + ":" + stackTrace.getLineNumber();
    }
}
