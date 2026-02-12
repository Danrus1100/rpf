package com.danrus.rpf.compat.rprenames.impl;

//? if rprenames {
import com.HiWord9.RPRenames.mod.RPRenames;
import com.HiWord9.RPRenames.mod.RPRenamesItemGroup;
//? }

public class RpRenamesCompat {

    public static void init() {
        //? if rprenames {
        RpfParser parser = new RpfParser(RPRenames.renamesManager);

        RenamesBridge.itemSetter = parser::updateClientItem;
        RenamesBridge.parser = parser::parse;
        RenamesBridge.active = true;

        RPRenames.renamesManager.parsers.remove(RPRenames.itemModelParser);
        //? }
    }

    public static void update() {
        //? if rprenames
        RPRenamesItemGroup.update();
    }
}
