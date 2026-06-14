//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.danrus.rpf.compat.rprenames;

import com.danrus.rpf.compat.RpfCompatInitializer;

import com.hiword9.rprenames.mod.RPRenames;
import com.hiword9.rprenames.mod.item_group.RPRenamesItemGroup;
import net.fabricmc.loader.api.FabricLoader;


public class RpRenamesCompat implements RpfCompatInitializer {

    public void init() {
        // if >=26.1
        com.hiword9.rprenames.mod.Settings.setConfigDir(FabricLoader.getInstance().getConfigDir());

        RpfParser parser = new RpfParser(RPRenames.updatableRenamesManager);
        RenamesBridge.itemSetter = parser::updateClientItem;
        RenamesBridge.parser = parser::parse;
        RenamesBridge.active = true;
        RPRenames.updatableRenamesManager.parsers().remove(RPRenames.itemModelParser);
    }

    public static void update() {
        RPRenamesItemGroup.update();
    }
}
