package com.danrus.rpf.logging;

import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ItemModelsSelectLogger {
    private static final Logger LOGGER = LoggerFactory.getLogger(ItemModelsSelectLogger.class);
    private final List<Identifier> alreadyLogged = new ArrayList<>();

    public boolean isItemLogged(Identifier location) {
        return alreadyLogged.contains(location);
    }

    public void info(ModelTestsResultCollector collector) {
        Identifier location = collector.getModelLocation();
        if (alreadyLogged.contains(location)) return;
        alreadyLogged.add(location);
        LOGGER.info("Info for {}", location.toString());
        collector.getStringsToLog().forEach(s -> LOGGER.info(" - {}", s));
    }

    public void error(ModelTestsResultCollector collector) {
        Identifier location = collector.getModelLocation();
        LOGGER.info("Error for {}", location.toString());
        collector.getStringsToLog().forEach(s -> LOGGER.error(" - {}", s));
    }

    public void onReload() {
        alreadyLogged.clear();
    }
}
