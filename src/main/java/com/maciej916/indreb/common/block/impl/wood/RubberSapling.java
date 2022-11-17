package com.maciej916.indreb.common.block.impl.wood;

import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.material.Material;

public class RubberSapling extends SaplingBlock {

    public RubberSapling(AbstractTreeGrower tree) {
        super(tree, Properties.of(Material.PLANT).noCollission().randomTicks().strength(0.0F).instabreak().sound(SoundType.GRASS));
    }
}
