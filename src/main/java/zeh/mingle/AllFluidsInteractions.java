package zeh.mingle;

import net.minecraft.core.BlockPos;
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
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.fluids.FluidInteractionRegistry;
import net.neoforged.neoforge.fluids.FluidType;
import zeh.mingle.common.Configuration;
import zeh.mingle.registry.Interaction;

import java.util.*;

@EventBusSubscriber
public class AllFluidsInteractions {

    public static List<FluidInteraction> LIST = new ArrayList<>();

    @SubscribeEvent
    public static void removeNew(final ServerStoppingEvent event) {
        Mingle.LOGGER.info("..................................");
        Mingle.LOGGER.info("..................................");
        Mingle.LOGGER.info("..................................");
    }
    @SubscribeEvent
    public static void reload(final AddReloadListenerEvent event) {
        Mingle.LOGGER.info("+++++++++++++++++++++++++++++++++");
        Mingle.LOGGER.info("+++++++++++++++++++++++++++++++++");
        Mingle.LOGGER.info("+++++++++++++++++++++++++++++++++");
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

        if (event.getServer().registryAccess().registry(Interaction.KEY).isPresent()) {
            Registry<Interaction> a = event.getServer().registryAccess().registry(Interaction.KEY).get();
            for (Interaction b : a) {
                Mingle.LOGGER.info(b.toString());
                Mingle.LOGGER.info(b.fluid().toString());
                Mingle.LOGGER.info(b.slag().toString());
            }
        }
        AllFluidsInteractions.registerFluidInteractions();
    }
    private static final Map<FluidType, List<FluidInteractionRegistry.InteractionInformation>> INTERACTIONS = new HashMap<>();

    public static void registerFluidInteractions() {

        for (zeh.mingle.common.Interaction cfg : Configuration.interactionList) {
            if (Objects.requireNonNull(BuiltInRegistries.FLUID.get(ResourceLocation.tryParse(cfg.molten)))
                    .defaultFluidState().isEmpty()) continue;
            LIST.add(new FluidInteraction(cfg));
        }
        for (FluidInteraction elem : LIST) {

            /*
            INTERACTIONS.computeIfAbsent(elem.molten.getFluidType(), s -> new ArrayList<>()).add(new FluidInteractionRegistry.InteractionInformation(
                    (Level level, BlockPos pos, BlockPos dirPos, FluidState state) ->
                            elem.interact(level, state, level.getFluidState(dirPos), pos, dirPos)
                                    || elem.interact(level, state, level.getFluidState(pos.below()), pos, pos.below()),
                    elem.getSlag()
            ));*/
            /*
            FluidInteractionRegistry.addInteraction(NeoForgeMod.LAVA_TYPE.value(), new FluidInteractionRegistry.InteractionInformation(
                    (Level level, BlockPos pos, BlockPos dirPos, FluidState state) ->
                            elem.processRaw(level, state, dirPos),
                    Blocks.OBSIDIAN.defaultBlockState()
            ));*/
            FluidInteractionRegistry.addInteraction(elem.molten.getFluidType(), new FluidInteractionRegistry.InteractionInformation(
                    (Level level, BlockPos pos, BlockPos dirPos, FluidState state) ->
                            elem.interact(level, state, level.getFluidState(dirPos), pos, dirPos),
                    elem.getSlag()
            ));
            //|| elem.interact(level, state, level.getFluidState(pos.below()), pos, pos.below())
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

        public FluidInteraction(zeh.mingle.common.Interaction cfg) {
            if (Objects.equals(cfg.before, "ignore")) hasBefore = false;
            else {
                hasBefore = true;
                if (cfg.before.startsWith("#")) {
                    beforeTag = TagKey.create(BuiltInRegistries.BLOCK.key(), ResourceLocation.parse(cfg.before.substring(1)));
                } else beforeBlock = BuiltInRegistries.BLOCK.get(ResourceLocation.parse(cfg.before));
            }

            molten = BuiltInRegistries.FLUID.get(ResourceLocation.parse(cfg.molten));
            moltenState = molten.defaultFluidState().createLegacyBlock();

            if (Objects.equals(cfg.after, "ignore")) hasAfter = false;
            else {
                hasAfter = true;
                if (cfg.after.startsWith("#")) {
                    afterTag = TagKey.create(BuiltInRegistries.BLOCK.key(), ResourceLocation.parse(cfg.after.substring(1)));
                } else afterBlock = BuiltInRegistries.BLOCK.get(ResourceLocation.parse(cfg.after));
            }

            if (cfg.slag.startsWith("#")) {
                slagTag = TagKey.create(BuiltInRegistries.BLOCK.key(), ResourceLocation.parse(cfg.slag.substring(1)));
            } else slagBlock = BuiltInRegistries.BLOCK.get(ResourceLocation.parse(cfg.slag));
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
        private boolean interact(Level level, FluidState state, FluidState dirState, BlockPos pos, BlockPos dirPos) {
            if (state.isSource()) {
                if (dirState.is(Fluids.WATER) && hasAfter) {
                    fluidMix(level, pos, getAfter());
                    fluidMix(level, dirPos, air);
                } else if (dirState.is(Fluids.LAVA)) {
                    fluidMix(level, dirPos, obsidian);
                    // IF fluid is source some other molten mix it
                } else if (!dirState.isEmpty() && !dirState.getType().isSame(molten)) {
                    fluidMix(level, dirPos, getSlag());
                }
                return false;
            } else {
                return !dirState.isEmpty() && !dirState.getType().isSame(molten);
            }
        }
        public void fluidMix(Level level, BlockPos pos, BlockState state) {
            level.setBlockAndUpdate(pos, state);
            if (level.getServer() != null) {
                if (level.getServer().getLevel(level.dimension()) != null) {
                    Objects.requireNonNull(level.getServer().getLevel(level.dimension())).sendParticles(
                            ParticleTypes.LARGE_SMOKE,
                            (double) pos.getX() + 0.5d,
                            (double) pos.getY() + 1.0d,
                            (double) pos.getZ() + 0.5d,
                            8, 0.3d, 0.0d, 0.3d, 0d
                    );
                }
            }
            level.playSound(null, pos, SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 0.5f,2.1f);
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

    }

}
