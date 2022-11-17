package com.maciej916.indreb.common.item.impl;

import com.maciej916.indreb.common.api.item.base.BaseItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.BaseCoralWallFanBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;

public class Fertilizer extends BaseItem {

    public Fertilizer() {
        super(CreativeModeTab.TAB_MATERIALS, new Item.Properties());
    }

    public InteractionResult useOn(UseOnContext pContext) {
        Level level = pContext.getLevel();
        BlockPos blockpos = pContext.getClickedPos();
        BlockPos blockpos1 = blockpos.relative(pContext.getClickedFace());
        if (applyBonemeal(pContext.getItemInHand(), level, blockpos, pContext.getPlayer())) {
            if (!level.isClientSide) {
                level.levelEvent(1505, blockpos, 0);
            }

            return InteractionResult.sidedSuccess(level.isClientSide);
        } else {
            BlockState blockstate = level.getBlockState(blockpos);
            boolean flag = blockstate.isFaceSturdy(level, blockpos, pContext.getClickedFace());
            if (flag && growWaterPlant(pContext.getItemInHand(), level, blockpos1, pContext.getClickedFace())) {
                if (!level.isClientSide) {
                    level.levelEvent(1505, blockpos1, 0);
                }

                return InteractionResult.sidedSuccess(level.isClientSide);
            } else {
                return InteractionResult.PASS;
            }
        }
    }

    @Deprecated //Forge: Use Player/Hand version
    public static boolean growCrop(ItemStack p_40628_, Level p_40629_, BlockPos p_40630_) {
        if (p_40629_ instanceof ServerLevel)
            return applyBonemeal(p_40628_, p_40629_, p_40630_, net.minecraftforge.common.util.FakePlayerFactory.getMinecraft((ServerLevel)p_40629_));
        return false;
    }

    public static boolean applyBonemeal(ItemStack p_40628_, Level p_40629_, BlockPos p_40630_, net.minecraft.world.entity.player.Player player) {
        BlockState blockstate = p_40629_.getBlockState(p_40630_);
        int hook = net.minecraftforge.event.ForgeEventFactory.onApplyBonemeal(player, p_40629_, p_40630_, blockstate, p_40628_);
        if (hook != 0) return hook > 0;
        if (blockstate.getBlock() instanceof BonemealableBlock bonemealableblock) {
            if (bonemealableblock.isValidBonemealTarget(p_40629_, p_40630_, blockstate, p_40629_.isClientSide)) {
                if (p_40629_ instanceof ServerLevel) {
                    if (bonemealableblock.isBonemealSuccess(p_40629_, p_40629_.random, p_40630_, blockstate)) {
                        bonemealableblock.performBonemeal((ServerLevel)p_40629_, p_40629_.random, p_40630_, blockstate);
                    }

                    p_40628_.shrink(1);
                }

                return true;
            }
        }

        return false;
    }

    public static boolean growWaterPlant(ItemStack p_40632_, Level p_40633_, BlockPos p_40634_, @Nullable Direction p_40635_) {
        if (p_40633_.getBlockState(p_40634_).is(Blocks.WATER) && p_40633_.getFluidState(p_40634_).getAmount() == 8) {
            if (p_40633_ instanceof ServerLevel) {
                RandomSource random = p_40633_.getRandom();

                label76:
                for(int i = 0; i < 128; ++i) {
                    BlockPos blockpos = p_40634_;
                    BlockState blockstate = Blocks.SEAGRASS.defaultBlockState();

                    for(int j = 0; j < i / 16; ++j) {
                        blockpos = blockpos.offset(random.nextInt(3) - 1, (random.nextInt(3) - 1) * random.nextInt(3) / 2, random.nextInt(3) - 1);
                        if (p_40633_.getBlockState(blockpos).isCollisionShapeFullBlock(p_40633_, blockpos)) {
                            continue label76;
                        }
                    }

                    Holder<Biome> biome = p_40633_.getBiome(blockpos);
                    if (biome.is(Biomes.WARM_OCEAN)) {
                        if (i == 0 && p_40635_ != null && p_40635_.getAxis().isHorizontal()) {
                            blockstate = ForgeRegistries.BLOCKS.tags().getTag(BlockTags.WALL_CORALS).getRandomElement(p_40633_.random).orElseThrow().defaultBlockState().setValue(BaseCoralWallFanBlock.FACING, p_40635_);
                        } else if (random.nextInt(4) == 0) {
                            blockstate = ForgeRegistries.BLOCKS.tags().getTag(BlockTags.UNDERWATER_BONEMEALS).getRandomElement(random).orElseThrow().defaultBlockState();
                        }
                    }

                    if (blockstate.is(BlockTags.WALL_CORALS)) {
                        for(int k = 0; !blockstate.canSurvive(p_40633_, blockpos) && k < 4; ++k) {
                            blockstate = blockstate.setValue(BaseCoralWallFanBlock.FACING, Direction.Plane.HORIZONTAL.getRandomDirection(random));
                        }
                    }

                    if (blockstate.canSurvive(p_40633_, blockpos)) {
                        BlockState blockstate1 = p_40633_.getBlockState(blockpos);
                        if (blockstate1.is(Blocks.WATER) && p_40633_.getFluidState(blockpos).getAmount() == 8) {
                            p_40633_.setBlock(blockpos, blockstate, 3);
                        } else if (blockstate1.is(Blocks.SEAGRASS) && random.nextInt(10) == 0) {
                            ((BonemealableBlock)Blocks.SEAGRASS).performBonemeal((ServerLevel)p_40633_, random, blockpos, blockstate1);
                        }
                    }
                }

                p_40632_.shrink(1);
            }
            return true;
        } else {
            return false;
        }
    }

    public static void addGrowthParticles(LevelAccessor p_40639_, BlockPos p_40640_, int p_40641_) {
        if (p_40641_ == 0) {
            p_40641_ = 15;
        }

        BlockState blockstate = p_40639_.getBlockState(p_40640_);
        if (!blockstate.isAir()) {
            double d0 = 0.5D;
            double d1;
            if (blockstate.is(Blocks.WATER)) {
                p_40641_ *= 3;
                d1 = 1.0D;
                d0 = 3.0D;
            } else if (blockstate.isSolidRender(p_40639_, p_40640_)) {
                p_40640_ = p_40640_.above();
                p_40641_ *= 3;
                d0 = 3.0D;
                d1 = 1.0D;
            } else {
                d1 = blockstate.getShape(p_40639_, p_40640_).max(Direction.Axis.Y);
            }

            p_40639_.addParticle(ParticleTypes.HAPPY_VILLAGER, (double)p_40640_.getX() + 0.5D, (double)p_40640_.getY() + 0.5D, (double)p_40640_.getZ() + 0.5D, 0.0D, 0.0D, 0.0D);
            RandomSource random = p_40639_.getRandom();

            for(int i = 0; i < p_40641_; ++i) {
                double d2 = random.nextGaussian() * 0.02D;
                double d3 = random.nextGaussian() * 0.02D;
                double d4 = random.nextGaussian() * 0.02D;
                double d5 = 0.5D - d0;
                double d6 = (double)p_40640_.getX() + d5 + random.nextDouble() * d0 * 2.0D;
                double d7 = (double)p_40640_.getY() + random.nextDouble() * d1;
                double d8 = (double)p_40640_.getZ() + d5 + random.nextDouble() * d0 * 2.0D;
                if (!p_40639_.getBlockState((new BlockPos(d6, d7, d8)).below()).isAir()) {
                    p_40639_.addParticle(ParticleTypes.HAPPY_VILLAGER, d6, d7, d8, d2, d3, d4);
                }
            }

        }
    }
}
