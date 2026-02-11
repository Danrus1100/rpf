package com.danrus.rpf.logging;

import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ItemModelsSelectLogger {
    private static final Logger LOGGER = LoggerFactory.getLogger(ItemModelsSelectLogger.class);
    private final List<ResourceLocation> alreadyLogged = new ArrayList<>();

    public boolean isItemLogged(ResourceLocation location) {
        return alreadyLogged.contains(location);
    }

    public void info(ModelTestsResultCollector collector) {
        collector.getStringsToLog().forEach(LOGGER::info);
    }

    public void error(ModelTestsResultCollector collector) {
        collector.getStringsToLog().forEach(LOGGER::error);
    }
}
