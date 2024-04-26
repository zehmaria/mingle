package zeh.fluidactions;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.fluids.FluidInteractionRegistry;
import net.minecraftforge.registries.ForgeRegistries;
import zeh.fluidactions.common.Configuration;
import zeh.fluidactions.common.Interaction;
import zeh.fluidactions.foundation.utility.Iterate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AllFluidsInteractions {

    public static List<FluidInteraction> LIST = new ArrayList<>();
    public static void registerFluidInteractions() {

        for (Interaction cfg : Configuration.interactionList) {
            if (Objects.requireNonNull(ForgeRegistries.FLUIDS.getValue(new ResourceLocation(cfg.molten)))
                    .defaultFluidState().isEmpty()) continue;
            LIST.add(new FluidInteraction(cfg));
        }

        for (FluidInteraction elem : LIST) {
            FluidInteractionRegistry.addInteraction(ForgeMod.LAVA_TYPE.get(), new FluidInteractionRegistry.InteractionInformation(
                    (Level level, BlockPos pos, BlockPos dirPos, FluidState state) -> elem.processRaw(level, state, dirPos),
                    Blocks.OBSIDIAN.defaultBlockState()
            ));
            FluidInteractionRegistry.addInteraction(elem.molten.getFluidType(), new FluidInteractionRegistry.InteractionInformation(
                    (Level level, BlockPos pos, BlockPos dirPos, FluidState state) ->
                            elem.interact(level, state, level.getFluidState(dirPos), pos, dirPos)
                                    || elem.interact(level, state, level.getFluidState(pos.below()), pos, pos.below()),
                    elem.getSlag()
            ));
        }

    }


    public static class FluidInteraction {

        Fluid molten;
        BlockState moltenState;
        Block beforeBlock;
        TagKey<Block> beforeTag;
        Block afterBlock;
        TagKey<Block> afterTag;
        Block slagBlock;
        TagKey<Block> slagTag;
        boolean hasBefore;
        boolean hasAfter;

        static BlockState air = Blocks.AIR.defaultBlockState();
        static BlockState obsidian = Blocks.OBSIDIAN.defaultBlockState();

        public FluidInteraction(Interaction cfg) {
            if (Objects.equals(cfg.before, "ignore")) hasBefore = false;
            else {
                hasBefore = true;
                if (cfg.before.startsWith("#")) {
                    beforeTag = TagKey.create(ForgeRegistries.BLOCKS.getRegistryKey(),
                            new ResourceLocation(cfg.before.substring(1)));
                } else beforeBlock = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(cfg.before));
            }

            molten = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(cfg.molten));
            if (molten != null) moltenState = molten.defaultFluidState().createLegacyBlock();

            if (Objects.equals(cfg.after, "ignore")) hasAfter = false;
            else {
                hasAfter = true;
                if (cfg.after.startsWith("#")) {
                    afterTag = TagKey.create(ForgeRegistries.BLOCKS.getRegistryKey(),
                            new ResourceLocation(cfg.after.substring(1)));
                } else afterBlock = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(cfg.after));
            }

            if (cfg.slag.startsWith("#")) {
                slagTag = TagKey.create(ForgeRegistries.BLOCKS.getRegistryKey(),
                        new ResourceLocation(cfg.slag.substring(1)));
            } else slagBlock = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(cfg.slag));
        }

        public boolean processRaw(Level level, FluidState state, BlockPos dirPos) {
            if (state.isSource() && this.hasBefore) {
                if ((beforeBlock != null && level.getBlockState(dirPos).is(beforeBlock)) ||
                        (beforeTag != null && level.getBlockState(dirPos).is(beforeTag))) {
                    fluidMix(level, dirPos, moltenState);
                    return true;
                }
            }
            return false;
        }
        private BlockState getAfter() {
            if (afterBlock != null) {
                return afterBlock.defaultBlockState();
            } else if (afterTag != null) {
                if (Objects.requireNonNull(ForgeRegistries.BLOCKS.tags()).getTag(afterTag)
                        .stream().findAny().isPresent()) {
                    return Objects.requireNonNull(ForgeRegistries.BLOCKS.tags()).getTag(afterTag)
                            .stream().findAny().get().defaultBlockState();
                }
            }
            return Blocks.AIR.defaultBlockState();
        }
        public BlockState getSlag() {
            if (slagBlock != null) {
                return slagBlock.defaultBlockState();
            } else if (slagTag != null) {
                if (Objects.requireNonNull(ForgeRegistries.BLOCKS.tags()).getTag(slagTag)
                        .stream().findAny().isPresent()) {
                    return Objects.requireNonNull(ForgeRegistries.BLOCKS.tags()).getTag(slagTag)
                            .stream().findAny().get().defaultBlockState();
                }
            }
            return Blocks.AIR.defaultBlockState();
        }
        private boolean interact(Level level, FluidState state, FluidState dirState, BlockPos pos, BlockPos dirPos) {
            if (state.isSource()) {
                if (dirState.is(Fluids.WATER) && hasAfter) {
                    fluidMix(level, pos, getAfter());
                    fluidMix(level, dirPos, air);
                    for (Direction side : Iterate.directions) {
                        BlockPos nextDirPos = dirPos.relative(side);
                        if (level.getFluidState(nextDirPos).is(Fluids.WATER)) fluidMix(level, nextDirPos, air);
                    }
                } else if (dirState.is(Fluids.LAVA)) {
                    fluidMix(level, dirPos, obsidian);
                    for (Direction side : Iterate.directions) {
                        BlockPos nextDirPos = dirPos.relative(side);
                        if (level.getFluidState(nextDirPos).is(Fluids.LAVA)) fluidMix(level, nextDirPos, obsidian);
                        if (level.getFluidState(nextDirPos).is(Fluids.FLOWING_LAVA)) fluidMix(level, nextDirPos, getSlag());
                    }
                } else if (!dirState.isEmpty() && !dirState.is(molten) &&
                        !AllTags.AllFluidTags.FLUID_INTERACTIONS_EXCLUSION.matches(dirState)) {
                    fluidMix(level, dirPos, getSlag());
                }
                return false;
            } else {
                return !dirState.isEmpty() && !dirState.is(molten)
                        && !AllTags.AllFluidTags.FLUID_INTERACTIONS_EXCLUSION.matches(dirState);
            }
        }
        public void fluidMix(Level level, BlockPos pos, BlockState state) {
            level.setBlockAndUpdate(pos, state);
            level.getServer().getLevel(level.dimension()).sendParticles(
                    ParticleTypes.LARGE_SMOKE,
                    (double) pos.getX() + 0.5d,
                    (double) pos.getY() + 1.0d,
                    (double) pos.getZ() + 0.5d,
                    8, 0.3d, 0.0d, 0.3d, 0d
            );
            level.playSound(null, pos, SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 0.5f,2.1f);
        }

    }

}
