package zeh.mingle;

import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer.FogMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.*;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.*;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import zeh.mingle.foundation.utility.Color;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public final class AllFluids {

    private static final ResourceLocation OVERLAY = ResourceLocation.tryParse("block/water_overlay");
    private static final ResourceLocation RENDER_OVERLAY = ResourceLocation.tryParse("misc/underwater");
    private static final ResourceLocation LIQUID = Mingle.as("block/fluid/molten_still");
    private static final ResourceLocation LIQUID_FLOW = Mingle.as("block/fluid/molten_flow");
    private static final DispenseItemBehavior BUCKET_DISPENSE_BEHAVIOR = new DefaultDispenseItemBehavior() {
        @NotNull
        @Override
        public ItemStack execute(@NotNull BlockSource source, @NotNull ItemStack stack) {
            Level world = source.level();
            DispensibleContainerItem bucket = (DispensibleContainerItem) stack.getItem();
            BlockPos pos = source.pos().relative(source.state().getValue(DispenserBlock.FACING));
            if (bucket.emptyContents(null, world, pos, null, stack)) {
                bucket.checkExtraContent(null, world, stack, pos);
                return new ItemStack(Items.BUCKET);
            }
            return super.execute(source, stack);
        }
    };

    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(Registries.FLUID, Mingle.ID);
    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(NeoForgeRegistries.FLUID_TYPES, Mingle.ID);
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Mingle.ID);
    public static final DeferredRegister.Blocks SLAG_BLOCKS = DeferredRegister.createBlocks(Mingle.ID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Mingle.ID);

    public static void register(IEventBus bus) {
        FLUIDS.register(bus);
        FLUID_TYPES.register(bus);
        BLOCKS.register(bus);
        SLAG_BLOCKS.register(bus);
        ITEMS.register(bus);
        for (AllFluids.INSTANCE entry : AllFluids.INSTANCE.values()) {
            if (!entry.init()) return; // Makes sure the fluids are registered at the right time.
        }
    }

    public static void registerBucketDispenserBehavior() {
        for (Holder<Item> bucket : ITEMS.getEntries()) {
            DispenserBlock.registerBehavior(bucket.value(), BUCKET_DISPENSE_BEHAVIOR);
        }
    }

    public enum INSTANCE {
        WASTE("molten_waste", 0xff111111),

        IRON("molten_iron", 0xff910000, true),
        GOLD("molten_gold", 0xffF5BD02, true),
        COPPER("molten_copper", 0xffDE5244, true),

        ANDESITE("molten_andesite", 0xffa2a3a3),
        SOUL("molten_soul", 0xff41230e),
        ENDER("molten_ender", 0xff034830),
        SLIME("molten_slime", 0xff70dc70),
        QUARTZ("molten_quartz", 0xffF5F5DC),
        GLASS("molten_glass", 0xffF6B26B),
        NETHERITE("molten_netherite", 0xff3b2921),
        DEBRIS("molten_debris", 0xff88774f),

        // GENERAL
        ANDESITE_ALLOY("molten_andesite_alloy", 0xff757575),
        BRASS("molten_brass", 0xffbf8e55),
        REFINED_GLOWSTONE("molten_refined_glowstone", 0xffd49710),
        REFINED_OBSIDIAN("molten_refined_obsidian", 0xff272a3e),
        BRONZE("molten_bronze", 0xff986441),
        STEEL("molten_steel", 0xff71797E),
        ELECTRUM("molten_electrum", 0xffCC5500),
        CONSTANTAN("molten_constantan", 0xff884739),
        INVAR("molten_invar", 0xff5c7c7b),

        ZINC("molten_zinc", 0xff809a7d, true),
        OSMIUM("molten_osmium", 0xff587470, true),
        TIN("molten_tin", 0xffa7afaf, true),
        LEAD("molten_lead", 0xff58546d, true),
        URANIUM("molten_uranium", 0xff4F7942, true),
        ALUMINUM("molten_aluminum", 0xff919d9c, true),
        NICKEL("molten_nickel", 0xffb3bba9, true),
        SILVER("molten_silver", 0xff8dadad, true),

        // AD-ASTRA
        DESH("molten_desh", 0xffd38b4c, true),
        OSTRUM("molten_ostrum", 0xff7c5156, true),
        CALORITE("molten_calorite", 0xff904748, true),

        // BOTANIA
        MANASTEEL("molten_manasteel", 0xff006afc),
        ELEMENTIUM("molten_elementium", 0xffea3298),
        TERRASTEEL("molten_terrasteel", 0xff3fca00),
        SPIRIT("molten_spirit", 0xff7ea37e),
        ESSENCE("molten_essence", 0xff6ea31e);

        public final DeferredBlock<Block> SLAG_BLOCK;
        public final DeferredHolder<Fluid, FlowingFluid> STILL;
        public final DeferredHolder<Fluid, FlowingFluid> FLOWING;
        public final DeferredBlock<LiquidBlock> BLOCK;
        public final DeferredItem<BucketItem> BUCKET_ITEM;
        public final Supplier<FluidType> FLUID_TYPE;
        public MoltenFlowingFluid.Properties PROPS;

        public final String id;
        public final String slagId;
        public final String name;
        public final String lang;
        public final String slag;
        public final int color;
        public final String storage_raw;
        public final String storage_block;
        public final boolean isOre;
        public final TagKey<Fluid> tag;

        INSTANCE(String name, int color) {
            this(name, color, false);
        }

        INSTANCE(String name, int color, boolean isOre) {
            this.tag = TagKey.create(BuiltInRegistries.FLUID.key(), ResourceLocation.fromNamespaceAndPath("c", name));
            this.isOre = isOre;
            if (isOre) {
                this.storage_raw = "#c:storage_blocks/raw_" + name.replace("molten_", "");
                this.storage_block = "#c:storage_blocks/" + name.replace("molten_", "");
            } else {
                this.storage_block = null;
                this.storage_raw = null;
            }

            this.id = Mingle.ID + ":" + name;
            this.name = name;
            this.lang = Arrays.stream(name.split("_"))
                    .map(String::toLowerCase).map(StringUtils::capitalize).collect(Collectors.joining(" "));
            this.color = color;
            this.slag = name + "_slag";
            this.slagId = Mingle.ID + ":" + this.slag;

            this.SLAG_BLOCK = SLAG_BLOCKS.register(this.slag,
                    () -> new SlagBlock(color, Block.Properties.ofFullCopy(Blocks.GLASS)
                            //.sound(SoundType.GLASS)
                            .lightLevel((data) -> 5)
                            .instabreak().noLootTable().replaceable()
                            .noOcclusion().isSuffocating((s, w, p) -> false).isViewBlocking((s, w, p) -> false)
                            .isValidSpawn((s, r, p, e) -> false)
                            .ignitedByLava()
            ));

            this.FLUID_TYPE = FLUID_TYPES.register(name + "_type",
                    () -> new MoltenFluidType(FluidType.Properties.create().density(4500).viscosity(9000).temperature(1000)
                            .canDrown(true).lightLevel(12).motionScale(0.00233).canSwim(false)
                            .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY_LAVA)
                            .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_LAVA)
                            .sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH),
                            LIQUID, LIQUID_FLOW, OVERLAY, RENDER_OVERLAY, color, () -> 1f / 64f));
            this.STILL = FLUIDS.register(name, () -> new MoltenFlowingFluid.Source(PROPS));
            this.FLOWING = FLUIDS.register("flowing_" + name, () -> new MoltenFlowingFluid.Flowing(PROPS));
            this.BLOCK = BLOCKS.register(name, () -> new LiquidBlock(
                            STILL.get(),
                            BlockBehaviour.Properties.of().noCollission().strength(100.0F).noLootTable()
                                    .replaceable().pushReaction(PushReaction.DESTROY)
                                    .lightLevel((data) -> 15).liquid()
                    ) {
                        @Override
                        protected void onPlace(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                               @NotNull BlockState oldState, boolean isMoving) {
                            if (!net.neoforged.neoforge.fluids.FluidInteractionRegistry.canInteract(level, pos)) {
                                level.scheduleTick(pos, state.getFluidState().getType(), this.fluid.getTickDelay(level));
                                for (Direction direction : Direction.values()) {
                                    if (interactExtra(state, level, pos, pos.relative(direction))) return;
                                }
                            }
                        }

                        @Override
                        protected void neighborChanged(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                                       @NotNull Block block, @NotNull BlockPos fromPos, boolean isMoving) {
                            FluidState dirState = level.getFluidState(fromPos);
                            // TODO: REMOVE
                            Mingle.LOGGER.info("fluid: " + dirState.getType().getFluidType().getDescriptionId());
                            Mingle.LOGGER.info("temperature: " + dirState.getType().getFluidType().getTemperature());
                            Mingle.LOGGER.info("density: " + dirState.getType().getFluidType().getDensity());
                            Mingle.LOGGER.info("density: " + dirState.getType().getFluidType().getViscosity());

                            if (!net.neoforged.neoforge.fluids.FluidInteractionRegistry.canInteract(level, pos)) {
                                level.scheduleTick(pos, state.getFluidState().getType(), this.fluid.getTickDelay(level));
                                interactExtra(state, level, pos, fromPos);
                            }
                        }

                        private boolean interactExtra(BlockState state, Level level, BlockPos pos, BlockPos dirPos) {
                            FluidState dirState = level.getFluidState(dirPos);
                            if (!dirState.isEmpty() && !dirState.getType().isSame(this.fluid)) {
                                if (!(dirState.getType() instanceof MoltenFlowingFluid)) {
                                    // TODO: temperature check. if = 1000 mix instead
                                    // Interacted.setBlock(level, state.getFluidState().isSource() ? dirPos : pos, Blocks.AMETHYST_BLOCK.defaultBlockState());
                                }
                            }
                            return false;
                        }
                    }
            );
            this.BUCKET_ITEM = ITEMS.register(name + "_bucket", () -> new BucketItem(STILL.get(),
                    new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));
            this.PROPS = new MoltenFlowingFluid.Properties(FLUID_TYPE, STILL, FLOWING)
                    .explosionResistance(100f).block(BLOCK).bucket(BUCKET_ITEM);
        }

        public boolean init() {
            return true;
        }
    }

    public static class SlagBlock extends HalfTransparentBlock {

        public SlagBlock(int color, BlockBehaviour.Properties props) {
            super(props);
            this.color = color;
        }

        private  final int color;

        @Override
        protected float getShadeBrightness(@NotNull BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos) {
            return 0.8F;
        }

        @Override
        protected boolean useShapeForLightOcclusion(@NotNull BlockState state) {
            return true;
        }

        @Override
        protected boolean propagatesSkylightDown(@NotNull BlockState state, @NotNull BlockGetter reader, @NotNull BlockPos pos) {
            return true;
        }

        @Override
        public Integer getBeaconColorMultiplier(@NotNull BlockState state, @NotNull LevelReader world, @NotNull BlockPos pos, @NotNull BlockPos beaconPos) {
            return color;
        }

        @Override
        public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
            super.neighborChanged(state, level, pos, neighborBlock, neighborPos, movedByPiston);
            boolean hasMolten = false;
            for (Direction dir : Direction.values()) {
                if (level.getFluidState(pos.relative(dir)).getType() instanceof MoltenFlowingFluid) {
                    hasMolten = true;
                }
            }
            if (!hasMolten) {
                level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                level.playSound(null, pos, SoundEvents.GLASS_BREAK, SoundSource.BLOCKS, 0.5f, 2.1f);
            }
        }

    }

    public abstract static class MoltenFlowingFluid extends BaseFlowingFluid {
        protected MoltenFlowingFluid(Properties properties) {
            super(properties);
        }

        @Override
        public void animateTick(Level level, BlockPos pos, @NotNull FluidState state, @NotNull RandomSource random) {
            BlockPos above = pos.above();
            if (level.getBlockState(above).isAir() && !level.getBlockState(above).isSolidRender(level, above)) {
                if (random.nextInt(100) == 0) {
                    double d0 = (double) pos.getX() + random.nextDouble();
                    double d1 = (double) pos.getY() + 1.0D;
                    double d2 = (double) pos.getZ() + random.nextDouble();

                    level.addParticle(ParticleTypes.LAVA, d0, d1, d2, 0.0D, 0.0D, 0.0D);
                    level.playLocalSound(d0, d1, d2, SoundEvents.LAVA_POP, SoundSource.BLOCKS, 0.2F + random.nextFloat() * 0.2F, 0.9F + random.nextFloat() * 0.15F, false);
                }

                if (random.nextInt(200) == 0) {
                    level.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.LAVA_AMBIENT, SoundSource.BLOCKS, 0.2F + random.nextFloat() * 0.2F, 0.9F + random.nextFloat() * 0.15F, false);
                }
            }
        }

        @Override
        public ParticleOptions getDripParticle() {
            return null;
        }

        @Override
        public int getSlopeFindDistance(@NotNull LevelReader level) {
            return level.dimensionType().ultraWarm() ? 2 : 1;
        }

        @Override
        public int getDropOff(@NotNull LevelReader level) {
            return level.dimensionType().ultraWarm() ? 2 : 3;
        }

        @Override
        protected boolean canBeReplacedWith(@NotNull FluidState state, @NotNull BlockGetter
                level, @NotNull BlockPos pos, @NotNull Fluid fluidIn, @NotNull Direction direction) {
            return false;
        }

        @Override
        public int getTickDelay(@NotNull LevelReader level) {
            return level.dimensionType().ultraWarm() ? 10 : 30;
        }

        public static class Flowing extends MoltenFlowingFluid {
            public Flowing(Properties properties) {
                super(properties);
                registerDefaultState(getStateDefinition().any().setValue(LEVEL, 7));
            }

            protected void createFluidStateDefinition(StateDefinition.@NotNull Builder<Fluid, FluidState> builder) {
                super.createFluidStateDefinition(builder);
                builder.add(LEVEL);
            }

            public int getAmount(FluidState state) {
                return state.getValue(LEVEL);
            }

            public boolean isSource(@NotNull FluidState state) {
                return false;
            }
        }

        public static class Source extends MoltenFlowingFluid {
            public Source(Properties properties) {
                super(properties);
            }

            public int getAmount(@NotNull FluidState state) {
                return 8;
            }

            public boolean isSource(@NotNull FluidState state) {
                return true;
            }
        }
    }

    public static class MoltenFluidType extends FluidType {

        public final ResourceLocation stillTexture;
        public final ResourceLocation flowingTexture;
        public final ResourceLocation overlayTexture;
        public final ResourceLocation renderOverlayTexture;
        private final int color;
        private final Vector3f fogColor;
        private final Supplier<Float> fogDistance;

        public MoltenFluidType(Properties properties,
                               ResourceLocation stillTexture, ResourceLocation flowingTexture,
                               ResourceLocation overlayTexture, ResourceLocation renderOverlayTexture,
                               int color, Supplier<Float> fog) {
            super(properties);
            this.stillTexture = stillTexture;
            this.flowingTexture = flowingTexture;
            this.overlayTexture = overlayTexture;
            this.renderOverlayTexture = renderOverlayTexture;
            this.color = color;
            this.fogColor = new Color(color, false).asVectorF();
            this.fogDistance = fog;
        }

        @Override
        public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
            consumer.accept(new IClientFluidTypeExtensions() {

                @Override
                public @NotNull ResourceLocation getStillTexture() {
                    return stillTexture;
                }

                @Override
                public @NotNull ResourceLocation getFlowingTexture() {
                    return flowingTexture;
                }

                @Override
                public ResourceLocation getOverlayTexture() {
                    return overlayTexture;
                }

                @Nullable
                @Override
                public ResourceLocation getRenderOverlayTexture(@NotNull Minecraft mc) {
                    return renderOverlayTexture;
                }

                @Override
                public int getTintColor() {
                    return color;
                }

                @Override
                public @NotNull Vector3f modifyFogColor(@NotNull Camera camera, float partialTick, @NotNull ClientLevel level,
                                                        int renderDistance, float darkenWorldAmount, @NotNull Vector3f fluidFogColor) {
                    return fogColor == null ? fluidFogColor : fogColor;
                }

                @Override
                public void modifyFogRender(@NotNull Camera camera, @NotNull FogMode mode, float renderDistance, float partialTick,
                                            float nearDistance, float farDistance, @NotNull FogShape shape) {
                    float modifier = fogDistance.get();
                    float baseWaterFog = 96.0f;
                    if (modifier != 1f) {
                        RenderSystem.setShaderFogShape(FogShape.CYLINDER);
                        RenderSystem.setShaderFogStart(-8);
                        RenderSystem.setShaderFogEnd(baseWaterFog * modifier);
                    }
                }

            });
        }

        @Override
        public boolean move(@NotNull FluidState state, LivingEntity entity, @NotNull Vec3 movementVector, double gravity) {
            if (!entity.isOnFire()) entity.setRemainingFireTicks(4);
            else entity.setRemainingFireTicks(20 * 4);
            entity.setSpeed(0.001f);
            entity.setDeltaMovement(entity.getDeltaMovement().scale(0.001f));
            return false;
        }

        @Override
        public void setItemMovement(ItemEntity entity) {
            if (entity.getItem().is(AllTags.AllItemTags.LAVA_RESISTANT.tag)) return;
            entity.playSound(SoundEvents.LAVA_EXTINGUISH, 0.5f, 2.1f);
            entity.kill();
        }

    }

}
