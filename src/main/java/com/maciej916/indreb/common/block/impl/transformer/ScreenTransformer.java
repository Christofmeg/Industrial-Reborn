package com.maciej916.indreb.common.block.impl.transformer;

import com.maciej916.indreb.IndReb;
import com.maciej916.indreb.common.screen.BetterScreen;
import com.maciej916.indreb.common.screen.button.GuiTransformerButton;
import com.maciej916.indreb.common.screen.widgets.GuiText;
import com.maciej916.indreb.common.screen.widgets.GuiTransformerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class ScreenTransformer extends BetterScreen<MenuTransformer> {

    public ScreenTransformer(MenuTransformer container, Inventory inv, Component name) {
        super(container, inv, name);
    }

    @Override
    public void init() {
        super.init();

        BlockEntityTransformer be = (BlockEntityTransformer) getBlockEntity();

        addRenderableOnlyComponent(new GuiText(this, 88, 5, 8, 30, Component.translatable("gui."+ IndReb.MODID + ".input")));
        addRenderableOnlyComponent(new GuiText(this, 88, 5, 8, 46, Component.translatable("gui."+ IndReb.MODID + ".output")));

        addRenderableOnlyComponent(new GuiTransformerInfo(this, be));
        addRenderableComponent(new GuiTransformerButton(this, 140, 32, be.changeMode()));

        drawComponents(true);
    }

    @Override
    public ResourceLocation getGuiLocation() {
        return new ResourceLocation(IndReb.MODID, "textures/gui/container/transformer.png");
    }

}
