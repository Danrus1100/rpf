package com.danrus.rpf.logging;

import com.danrus.rpf.api.AbstractTestResultCollector;
import com.danrus.rpf.api.TestsResultCollector;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class LoggingTestsResultCollector extends AbstractTestResultCollector {
    public LoggingTestsResultCollector(ResourceLocation modelLocation) {
        super(modelLocation);
    }
}
