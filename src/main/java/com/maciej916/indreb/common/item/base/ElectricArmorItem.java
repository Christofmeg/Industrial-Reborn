package com.maciej916.indreb.common.item.base;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.maciej916.indreb.common.energy.impl.CapEnergyStorage;
import com.maciej916.indreb.common.energy.interfaces.IEnergy;
import com.maciej916.indreb.common.enums.EnergyTier;
import com.maciej916.indreb.common.enums.EnergyType;
import com.maciej916.indreb.common.enums.EnumLang;
import com.maciej916.indreb.common.interfaces.item.IElectricItem;
import com.maciej916.indreb.common.registries.ModCapabilities;
import com.maciej916.indreb.common.registries.ModItemGroups;
import com.maciej916.indreb.common.util.CapabilityUtil;
import com.maciej916.indreb.common.util.TextComponentUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class ElectricArmorItem extends ArmorItem implements IElectricItem {

    private static final UUID[] ARMOR_MODIFIER_UUID_PER_SLOT = new UUID[]{UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"), UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"), UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"), UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150")};
    private final int energyStored;
    private final int maxEnergy;
    private final EnergyType energyType;
    private final EnergyTier energyTier;

    public ElectricArmorItem(ArmorMaterial material, EquipmentSlot slot, Properties properties, int energyStored, int maxEnergy, EnergyType energyType, EnergyTier energyTier) {
        super(material, slot, properties.setNoRepair().stacksTo(1).tab(ModItemGroups.MAIN_ITEM_GROUP));
        this.energyStored = energyStored;
        this.maxEnergy = maxEnergy;
        this.energyType = energyType;
        this.energyTier = energyTier;
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new CapEnergyStorage(energyStored, maxEnergy, energyType, energyTier);
    }

    @Override
    public EnergyTier getEnergyTier() {
        return energyTier;
    }

    @Override
    public EnergyType getEnergyType() {
        return energyType;
    }

    @Override
    public IEnergy getEnergy(ItemStack stack) {
        return CapabilityUtil.getCapabilityHelper(stack, ModCapabilities.ENERGY).getValue();
    }

    @Override
    public boolean isRepairable(ItemStack stack) {
        return false;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return true;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return Math.round(13.0F - ((1 - getChargeRatio(stack)) * 13.0F));
    }

    public int getBarColor(ItemStack stack) {
        return Mth.hsvToRgb(0, 1.0F, 1.0F);
    }

    public static float getChargeRatio(ItemStack stack) {
        return CapabilityUtil.getCapabilityHelper(stack, ModCapabilities.ENERGY).getIfPresentElse(e -> (float) e.energyStored() / e.maxEnergy(), 0f);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return false;
    }

    @Override
    public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> list) {
        if (tab == CreativeModeTab.TAB_COMBAT || tab == CreativeModeTab.TAB_SEARCH) {
            list.add(new ItemStack(this));
        } else if (allowdedIn(tab)) {
            list.add(new ItemStack(this));

            ItemStack full = new ItemStack(this);
            IEnergy cap = CapabilityUtil.getCapabilityHelper(full, ModCapabilities.ENERGY).getValue();
            if (cap != null) {
                cap.setEnergy(cap.maxEnergy());
                full.getOrCreateTag().putInt("energyStored", cap.maxEnergy());
            }

            list.add(full);
        }
    }

    @org.jetbrains.annotations.Nullable
    @Override
    public CompoundTag getShareTag(ItemStack stack) {
        CompoundTag nbt = stack.getOrCreateTag();
        int energyStored = CapabilityUtil.getCapabilityHelper(stack, ModCapabilities.ENERGY).getIfPresentElse(IEnergy::energyStored, 0);
        nbt.putInt("energyStored", energyStored);
        return nbt;
    }

    @Override
    public void readShareTag(ItemStack stack, @org.jetbrains.annotations.Nullable CompoundTag nbt) {
        if (nbt != null) {
            CapabilityUtil.getCapabilityHelper(stack, ModCapabilities.ENERGY).ifPresent(iEnergy -> {
                iEnergy.setEnergy(nbt.getInt("energyStored"));
            });
        }
        super.readShareTag(stack, nbt);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {

        pTooltipComponents.add(TextComponentUtil.build(
                new TranslatableComponent(EnumLang.POWER_TIER.getTranslationKey()).withStyle(ChatFormatting.GRAY),
                new TranslatableComponent(energyTier.getLang().getTranslationKey()).withStyle(energyTier.getColor())
        ));

        int energyStored = CapabilityUtil.getCapabilityHelper(pStack, ModCapabilities.ENERGY).getIfPresentElse(IEnergy::energyStored, 0);
        pTooltipComponents.add(TextComponentUtil.build(
                new TranslatableComponent(EnumLang.STORED.getTranslationKey()).withStyle(ChatFormatting.GRAY),
                new TranslatableComponent(EnumLang.POWER.getTranslationKey(), TextComponentUtil.getFormattedEnergyUnit(energyStored)).withStyle(energyTier.getColor()),
                new TextComponent(" / ").withStyle(ChatFormatting.GRAY),
                new TranslatableComponent(EnumLang.POWER.getTranslationKey(), TextComponentUtil.getFormattedEnergyUnit(maxEnergy)).withStyle(energyTier.getColor())
        ));

        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
    }

    @Override
    public boolean isDamageable(ItemStack stack) {
        return false;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        if (stack.getItem().equals(this)) {
            if (slot == getSlot()) {
                CompoundTag tag = stack.getTag();
                boolean active = false;

                if (tag != null && tag.getAllKeys().contains("active")) {
                    active = tag.getBoolean("active");
                } else {
                    IEnergy energy = getEnergy(stack);
                    if (energy != null) {
                        active = energy.energyStored() > 0;
                    }
                }

                Multimap<Attribute, AttributeModifier> modifiers = HashMultimap.create();
                modifiers.putAll(super.getAttributeModifiers(slot, stack));

                modifiers.removeAll(Attributes.ARMOR);
                UUID uuid = ARMOR_MODIFIER_UUID_PER_SLOT[slot.getIndex()];

                if (active) {
                    modifiers.put(Attributes.ARMOR, new AttributeModifier(uuid, "Armor modifier", getDefense(), AttributeModifier.Operation.ADDITION));
                } else {
                    modifiers.put(Attributes.ARMOR, new AttributeModifier(uuid, "Armor modifier", 0, AttributeModifier.Operation.ADDITION));
                }

                return modifiers;
            }
        }
        return super.getAttributeModifiers(slot, stack);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (!level.isClientSide()) {
            tickElectric(stack);
        }
    }

    @Override
    public void tickElectric(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        if (tag.getAllKeys().contains("energyStored")) {
            tag.putBoolean("active", tag.getInt("energyStored") > 0);
        }
    }
}
