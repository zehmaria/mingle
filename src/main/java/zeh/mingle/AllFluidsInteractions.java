package zeh.mingle;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
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

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.fluids.FluidInteractionRegistry;
import zeh.mingle.common.Configuration;
import zeh.mingle.common.Interaction;
import zeh.mingle.foundation.utility.Iterate;
import zeh.mingle.interactions.Interactions;
import zeh.mingle.registry.DataRegistryTest;
import zeh.mingle.registry.DataTest;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@EventBusSubscriber
public class AllFluidsInteractions {

    public static List<FluidInteraction> LIST = new ArrayList<>();

    @SubscribeEvent
    public static void removeNew(final ServerStoppingEvent event) {
        Mingle.LOGGER.info("--------AAAAAAAAAAAAAAAAAAAAAAAA");
        Mingle.LOGGER.info("--------AAAAAAAAAAAAAAAAAAAAAAAA");
        Mingle.LOGGER.info("--------AAAAAAAAAAAAAAAAAAAAAAAA");
        Mingle.LOGGER.info("--------AAAAAAAAAAAAAAAAAAAAAAAA");
        Mingle.LOGGER.info("--------AAAAAAAAAAAAAAAAAAAAAAAA");
        Mingle.LOGGER.info("--------AAAAAAAAAAAAAAAAAAAAAAAA");
    }
    @SubscribeEvent
    public static void reload(final AddReloadListenerEvent event) {
        Mingle.LOGGER.info("--------AAAAAAAAAAAAAAAAAAAAAAAA");
        Mingle.LOGGER.info("--------AAAAAAAAAAAAAAAAAAAAAAAA");
        Mingle.LOGGER.info("--------AAAAAAAAAAAAAAAAAAAAAAAA");
        Mingle.LOGGER.info("--------AAAAAAAAAAAAAAAAAAAAAAAA");
        Mingle.LOGGER.info("--------AAAAAAAAAAAAAAAAAAAAAAAA");
        Mingle.LOGGER.info("--------AAAAAAAAAAAAAAAAAAAAAAAA");
    }
    @SubscribeEvent
    public static void addNew(final ServerAboutToStartEvent event) {
        Mingle.LOGGER.info("------------------------");
        Mingle.LOGGER.info("------------------------");
        Mingle.LOGGER.info("------------------------");
        Mingle.LOGGER.info("------------------------");
        Mingle.LOGGER.info("------------------------");
        Mingle.LOGGER.info(event.toString());
        Mingle.LOGGER.info(event.getServer().toString());

        if (event.getServer().registryAccess().registry(DataRegistryTest.KEY).isPresent()) {
            Registry<DataTest> a = event.getServer().registryAccess().registry(DataRegistryTest.KEY).get();
            for (DataTest b : a) {
                Mingle.LOGGER.info(b.toString());
                Mingle.LOGGER.info(b.aaaa());
                Mingle.LOGGER.info(b.planks().toString());
            }
        }
        AllFluidsInteractions.registerFluidInteractions();
    }

    public static void registerFluidInteractions() {

        for (Interaction cfg : Configuration.interactionList) {
            if (Objects.requireNonNull(BuiltInRegistries.FLUID.get(ResourceLocation.tryParse(cfg.molten)))
                    .defaultFluidState().isEmpty()) continue;
            LIST.add(new FluidInteraction(cfg));
        }
        for (FluidInteraction elem : LIST) {
            FluidInteractionRegistry.addInteraction(NeoForgeMod.LAVA_TYPE.value(), new FluidInteractionRegistry.InteractionInformation(
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
                    beforeTag = TagKey.create(BuiltInRegistries.BLOCK.key(), ResourceLocation.tryParse(cfg.before.substring(1)));
                } else beforeBlock = BuiltInRegistries.BLOCK.get(ResourceLocation.tryParse(cfg.before));
            }

            molten = BuiltInRegistries.FLUID.get(ResourceLocation.tryParse(cfg.molten));
            if (molten != null) moltenState = molten.defaultFluidState().createLegacyBlock();

            if (Objects.equals(cfg.after, "ignore")) hasAfter = false;
            else {
                hasAfter = true;
                if (cfg.after.startsWith("#")) {
                    afterTag = TagKey.create(BuiltInRegistries.BLOCK.key(), ResourceLocation.tryParse(cfg.after.substring(1)));
                } else afterBlock = BuiltInRegistries.BLOCK.get(ResourceLocation.tryParse(cfg.after));
            }

            if (cfg.slag.startsWith("#")) {
                slagTag = TagKey.create(BuiltInRegistries.BLOCK.key(), ResourceLocation.tryParse(cfg.slag.substring(1)));
            } else slagBlock = BuiltInRegistries.BLOCK.get(ResourceLocation.tryParse(cfg.slag));
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
                if (BuiltInRegistries.BLOCK.getOrCreateTag(afterTag).stream().findAny().isPresent()) {
                    return BuiltInRegistries.BLOCK.getOrCreateTag(afterTag).stream().findAny().get().value().defaultBlockState();
                }
            }
            return Blocks.AIR.defaultBlockState();
        }
        public BlockState getSlag() {
            if (slagBlock != null) {
                return slagBlock.defaultBlockState();
            } else if (slagTag != null) {
                if (BuiltInRegistries.BLOCK.getOrCreateTag(slagTag).stream().findAny().isPresent()) {
                    return BuiltInRegistries.BLOCK.getOrCreateTag(slagTag).stream().findAny().get().value().defaultBlockState();
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
