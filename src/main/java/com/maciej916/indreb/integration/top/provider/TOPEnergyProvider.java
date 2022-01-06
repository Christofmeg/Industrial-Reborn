package com.maciej916.indreb.integration.top.provider;

import com.maciej916.indreb.IndReb;
import com.maciej916.indreb.common.energy.impl.BasicEnergyStorage;
import com.maciej916.indreb.common.entity.block.IndRebBlockEntity;
import com.maciej916.indreb.common.util.TextComponentUtil;
import mcjty.theoneprobe.api.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;


public class TOPEnergyProvider implements IProbeInfoProvider {

    @Override
    public ResourceLocation getID() {
        return new ResourceLocation(IndReb.MODID, "energy_info");
    }

    @Override
    public void addProbeInfo(ProbeMode probeMode, IProbeInfo iProbeInfo, Player player, Level level, BlockState blockState, IProbeHitData iProbeHitData) {
        BlockEntity blockEntity = level.getBlockEntity(iProbeHitData.getPos());
        if (blockEntity instanceof IndRebBlockEntity entity) {
            BasicEnergyStorage energy = entity.getEnergyStorage();
            if (energy != null) {

                Color filledColor = new Color(76, 178, 13);
                Color alternateFilledColor = new Color(49, 114, 6);
                Color borderColor = new Color(49, 64, 6);

                IProbeInfo horizontalPane = iProbeInfo.horizontal(iProbeInfo.defaultLayoutStyle().alignment(ElementAlignment.ALIGN_CENTER));
                horizontalPane.progress(energy.energyStored(), energy.maxEnergy(), iProbeInfo.defaultProgressStyle()
                        .numberFormat(NumberFormat.COMPACT)
                        .suffix(" / " + TextComponentUtil.getFormattedEnergyUnit(energy.maxEnergy()) + " IE")
                        .borderColor(borderColor)
                        .filledColor(filledColor)
                        .alternateFilledColor(alternateFilledColor));
            }
        }
    }
}
