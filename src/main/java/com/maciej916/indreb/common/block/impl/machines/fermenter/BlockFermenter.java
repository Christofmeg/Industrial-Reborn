package com.maciej916.indreb.common.block.impl.machines.fermenter;

import com.maciej916.indreb.common.block.BlockElectricMachine;
import com.maciej916.indreb.common.config.ServerConfig;
import com.maciej916.indreb.common.enums.EnergyTier;
import com.maciej916.indreb.common.enums.EnumLang;
import com.maciej916.indreb.common.fluid.Biomass;
import com.maciej916.indreb.common.interfaces.block.IHasMenu;
import com.maciej916.indreb.common.interfaces.block.IStateActive;
import com.maciej916.indreb.common.interfaces.block.IStateFacing;
import com.maciej916.indreb.common.util.CapabilityUtil;
import com.maciej916.indreb.common.util.TextComponentUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

import javax.annotation.Nullable;
import java.util.List;

public class BlockFermenter extends BlockElectricMachine implements IStateFacing, IHasMenu, IStateActive {

    public BlockFermenter() {
        super(EnergyTier.STANDARD,0, 0);
    }

    @Override
    public AbstractContainerMenu getMenu(int windowId, Level level, BlockPos pos, Inventory playerInventory, Player playerEntity) {
        return new MenuFermenter(windowId, level, pos, playerInventory, playerEntity);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BlockEntityFermenter(pos, state);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult trace) {
        if (!level.isClientSide) {
            if (!player.isShiftKeyDown()) {
                BlockEntity blockEntity = level.getBlockEntity(pos);
                if (blockEntity instanceof BlockEntityFermenter be) {
                    ItemStack stack = player.getItemInHand(hand);
                    if (!stack.isEmpty()) {
                        ItemStack newStack = stack.copy();
                        newStack.setCount(1);
                        IFluidHandlerItem cap = CapabilityUtil.getCapabilityHelper(newStack, ForgeCapabilities.FLUID_HANDLER_ITEM).getValue();
                        if (cap != null) {
                            FluidStack fluid = cap.getFluidInTank(1);
                            if (fluid.getFluid() == Biomass.STILL_FLUID) {
                                if (be.fluidInputStorage.fillFluid(fluid, true) == fluid.getAmount()) {
                                    be.fluidInputStorage.fillFluid(fluid, false);

                                    cap.drain(fluid.getAmount(), IFluidHandler.FluidAction.EXECUTE);
                                    player.addItem(cap.getContainer());
                                    stack.shrink(1);

                                    return InteractionResult.PASS;
                                }
                            }
                        }
                    }
                }
            }
        }

        return super.use(state, level, pos, player, hand, trace);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable BlockGetter pLevel, List<Component> pTooltip, TooltipFlag pFlag) {
        super.appendHoverText(pStack, pLevel, pTooltip, pFlag);

        pTooltip.add(TextComponentUtil.build(
                Component.translatable(EnumLang.ACCEPT.getTranslationKey()).withStyle(ChatFormatting.GRAY),
                Component.translatable(EnumLang.POWER_TICK.getTranslationKey(), TextComponentUtil.getFormattedEnergyUnit(ServerConfig.standard_tier_transfer.get())).withStyle(getEnergyTier().getColor())
        ));

        pTooltip.add(TextComponentUtil.build(
                Component.translatable(EnumLang.CAPACITY.getTranslationKey()).withStyle(ChatFormatting.GRAY),
                Component.translatable(EnumLang.POWER.getTranslationKey(), TextComponentUtil.getFormattedEnergyUnit(ServerConfig.fermenter_energy_capacity.get())).withStyle(getEnergyTier().getColor())
        ));
    }
}
