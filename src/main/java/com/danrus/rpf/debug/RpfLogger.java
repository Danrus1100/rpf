package com.danrus.rpf.debug;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class RpfLogger {
    private final Logger logger = LoggerFactory.getLogger("RPF");
    private static final RpfLogger INSTANCE = new RpfLogger();

    private RpfLogger() {}

    public static RpfLogger get() { return INSTANCE; }


    public void info(String message) { logger.info(wrapMsg(message)); }

    public void warn(String message) { logger.warn(wrapMsg(message)); }
    public void warn(String message, Throwable t) { logger.warn(wrapMsg(message), t); }

    public void error(String message) { logger.error(wrapMsg(message)); }
    public void error(String message, Throwable t) { logger.error(wrapMsg(message), t); }

    private static String wrapMsg(String message) {
        return "[RPF] " + message;
    }
}
