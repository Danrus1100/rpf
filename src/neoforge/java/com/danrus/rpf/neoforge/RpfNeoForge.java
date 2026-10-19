package com.danrus.rpf.neoforge;

import com.danrus.rpf.Rpf;
import com.danrus.rpf.compat.RpfCompatPlugin;
import com.danrus.rpf.core.init.RpfCommands;
import net.minecraft.commands.CommandSourceStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.common.NeoForge;

@Mod(value = RpfNeoForge.MOD_ID, dist = Dist.CLIENT)
public final class RpfNeoForge {
    public static final String MOD_ID = "rpf";

    public RpfNeoForge() {
        RpfCompatPlugin.setModLoadedPredicate(ModList.get()::isLoaded);
        Rpf.init(FMLPaths.CONFIGDIR.get());
        NeoForge.EVENT_BUS.addListener(this::registerClientCommands);
    }

    private void registerClientCommands(RegisterClientCommandsEvent event) {
        new RpfCommands<>(event.getDispatcher()).init();
    }
}
