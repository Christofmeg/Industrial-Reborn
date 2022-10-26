package com.maciej916.indreb.common.item.impl.bronze;

import com.maciej916.indreb.common.registries.ModItemGroups;
import com.maciej916.indreb.common.registries.ModTiers;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.*;

public class BronzePickaxe extends PickaxeItem {

    public BronzePickaxe() {
        super(ModTiers.BRONZE, 1, -2.8F, new Properties().tab(ModItemGroups.MAIN_ITEM_GROUP));

    }

    @Override
    public void fillItemCategory(CreativeModeTab pCategory, NonNullList<ItemStack> pItems) {
        if (pCategory == CreativeModeTab.TAB_TOOLS) pItems.add(new ItemStack(this));
        super.fillItemCategory(pCategory, pItems);
    }

}
