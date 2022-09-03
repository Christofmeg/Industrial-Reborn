package com.maciej916.indreb;

import com.maciej916.indreb.common.network.ModNetworking;
import com.maciej916.indreb.common.proxy.ModProxy;
import com.maciej916.indreb.common.registries.Config;
import com.maciej916.indreb.common.registries.ModFluids;
import com.maciej916.indreb.common.registries.ModGeneration;
import com.maciej916.indreb.common.registries.ModRecipeType;
import com.maciej916.indreb.datagen.DataGenerators;
import com.maciej916.indreb.integration.top.TOPPlugin;
import mcjty.theoneprobe.TheOneProbe;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("indreb")
public class IndReb {
    public static final String MODID = "indreb";
    public static final Logger LOGGER = LogManager.getLogger();

    public IndReb() {
//        final ModLoadingContext modLoadingContext = ModLoadingContext.get();

        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::onCommonSetup);

        DataGenerators.GLM.register(modEventBus);

        ModFluids.FLUIDS.register(modEventBus);
        ModFluids.FLUID_BLOCKS.register(modEventBus);
        ModFluids.FLUID_ITEMS.register(modEventBus);

        ModRecipeType.RECIPE_TYPES.register(modEventBus);

        Config.init();
    }

    private void onCommonSetup(final FMLCommonSetupEvent event) {
        ModProxy.init();
        ModNetworking.init();
        ModGeneration.init();

        if (ModList.get().isLoaded(TheOneProbe.MODID)) {
            TOPPlugin.registerCompatibility();
        }
    }
}
