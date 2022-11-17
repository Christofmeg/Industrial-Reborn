package com.maciej916.indreb.common.api.capability;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;
import org.jetbrains.annotations.NotNull;

public class FluidHandlerStack extends FluidHandlerItemStack {

    /**
     * @param container The container itemStack, data is stored on it directly as NBT.
     * @param capacity  The maximum capacity of this fluid tank.
     */
    public FluidHandlerStack(@NotNull ItemStack container, int capacity) {
        super(container, capacity);
    }

    public ItemStack setFluidStack(FluidStack fluid) {
        setFluid(fluid);
        return container;
    }
}