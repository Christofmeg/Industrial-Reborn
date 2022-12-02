package com.maciej916.indreb.common.block.impl.machines.standard.fermenter;

import com.maciej916.indreb.common.api.blockentity.IndRebBlockEntity;
import com.maciej916.indreb.common.api.screen.IndRebContainerMenu;
import com.maciej916.indreb.common.screen.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;

public class MenuFermenter extends IndRebContainerMenu {

    public MenuFermenter(int containerId, Inventory playerInventory, FriendlyByteBuf extraData) {
        this((IndRebBlockEntity) playerInventory.player.level.getBlockEntity(extraData.readBlockPos()), containerId, playerInventory, playerInventory.player, new SimpleContainerData(BlockEntityFermenter.SYNC_DATA_SLOTS));
    }

    public MenuFermenter(IndRebBlockEntity entity, int containerId, Inventory playerInventory, Player player, ContainerData data) {
        super(ModMenuTypes.FERMENTER.get(), entity, containerId, playerInventory, player, data);
    }

    @Override
    public void init() {
        this.playerInvTop = this.playerInvTop + 22;
        super.init();
    }
}
