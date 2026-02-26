    package com.danrus.rpf.debug;

    import com.danrus.rpf.Rpf;
    import com.danrus.rpf.core.init.config.RpfConfig;
    import com.danrus.rpf.api.TestsResultCollector;
    import com.danrus.rpf.impl.DummyTestsResultsCollector;
    import net.fabricmc.loader.api.FabricLoader;
    import net.minecraft.resources.ResourceLocation;
    import org.jetbrains.annotations.Nullable;

    import java.nio.file.Path;
    import java.util.*;
    import java.util.function.Consumer;
    import java.util.function.Supplier;
    import java.util.stream.Stream;

    public class RpfDebugSystem {
        private static final RpfDebugSystem INSTANCE = new RpfDebugSystem();
        private RpfDebugSystem() {}
        public static RpfDebugSystem getInstance() { return INSTANCE; }

        private final TestsResultCollector DUMMY_COLLECTOR = new DummyTestsResultsCollector();
        private final Map<ResourceLocation, ItemDump> alreadyLogged = new HashMap<>();
        private final Map<ResourceLocation, ItemDump> alreadyErrored = new HashMap<>();
        private boolean debugOutput;

        private void processItem(TestsResultCollector collector,
                                 Map<ResourceLocation, ItemDump> targetMap,
                                 ItemDump.Type type,
                                 Consumer<String> logger) {

            if (!debugOutput) return;

            ResourceLocation loc = collector.getModelLocation();
            targetMap.computeIfAbsent(loc, key -> {
                List<String> log = collector.getStringsToLog();
                List<String> fullLog = new ArrayList<>();
                fullLog.add(key.toString() + ":");
                fullLog.addAll(log);

                fullLog.forEach(logger);

                return new ItemDump(type, key, fullLog);
            });
        }

        public void logItem(TestsResultCollector collector) {
            processItem(collector, alreadyLogged, ItemDump.Type.INFO, RpfLogger.get()::info);
        }

        public void errorItem(TestsResultCollector collector) {
            processItem(collector, alreadyErrored, ItemDump.Type.ERROR, RpfLogger.get()::error);
        }

        public TestsResultCollector optimiseCollector(Supplier<TestsResultCollector> forDebug) {
            return debugOutput ? forDebug.get() : DUMMY_COLLECTOR;
        }

        public void toggleDebug() {
            debugOutput = !debugOutput;
            Rpf.getConfig().setDebug(debugOutput);
            Rpf.getConfig().save();
        }

        public boolean isDebug() { return debugOutput; }

        public void clear() {
            alreadyLogged.clear();
            alreadyErrored.clear();
        }

        @Nullable
        public Path exportDump(ResourceLocation location) {
            List<ItemDump> dumpsToExport = new ArrayList<>();

            ItemDump info = alreadyLogged.get(location);
            ItemDump error = alreadyErrored.get(location);

            if (info != null) dumpsToExport.add(info);
            if (error != null) dumpsToExport.add(error);

            if (dumpsToExport.isEmpty()) return null;

            return ItemDump.saveCombined(location, dumpsToExport);
        }

        public List<ResourceLocation> getDatabaseKeys() {
            return Stream.concat(
                            alreadyLogged.keySet().stream(),
                            alreadyErrored.keySet().stream()
                    ).distinct()
                    .sorted(Comparator.naturalOrder())
                    .toList();
        }
    }
