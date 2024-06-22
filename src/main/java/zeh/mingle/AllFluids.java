package zeh.mingle;

import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer.FogMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import zeh.mingle.foundation.utility.Color;

import java.util.function.Consumer;
import java.util.function.Supplier;

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
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Mingle.ID);

    public static void register(IEventBus bus) {
        FLUIDS.register(bus);
        FLUID_TYPES.register(bus);
        BLOCKS.register(bus);
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
        IRON("molten_iron", "Molten Iron", 0xff5e0000),
        //7f171c
        GOLD("molten_gold", "Molten Gold", 0xffF5BD02),
        //F9D000
        COPPER("molten_copper", "Molten Copper", 0xffDE5244),
        //C77B41
        ANDESITE("molten_andesite", "Molten Andesite", 0xffa2a3a3),
        SOUL("molten_soul", "Molten Soul", 0xff41230e),
        ENDER("molten_ender", "Molten Ender", 0xff034830),
        SLIME("molten_slime", "Molten Slime", 0xff70dc70),
        QUARTZ("molten_quartz", "Molten Quartz", 0xffF5F5DC),
        GLASS("molten_glass", "Molten Glass", 0xffF6B26B),
        NETHERITE("molten_netherite", "Molten Netherite", 0xff3b2921),
        DEBRIS("molten_debris", "Molten Debris", 0xff88774f),

        // GENERAL
        ANDESITE_ALLOY("molten_andesite_alloy", "Molten Andesite Alloy", 0xff757575),
        ZINC("molten_zinc", "Molten Zinc", 0xff809a7d),
        BRASS("molten_brass", "Molten Brass", 0xffbf8e55),
        REFINED_GLOWSTONE("molten_refined_glowstone", "Molten Refined Glowstone", 0xffd49710),
        REFINED_OBSIDIAN("molten_refined_obsidian", "Molten Refined Obsidian", 0xff272a3e),
        OSMIUM("molten_osmium", "Molten Osmium", 0xff587470),
        BRONZE("molten_bronze", "Molten Bronze", 0xff986441),
        TIN("molten_tin", "Molten Tin", 0xffa7afaf),
        LEAD("molten_lead", "Molten lead", 0xff58546d),
        URANIUM("molten_uranium", "Molten Uranium", 0xff4F7942),
        STEEL("molten_steel", "Molten Steel", 0xff71797E),
        ELECTRUM("molten_electrum", "Molten Electrum", 0xffCC5500),
        ALUMINUM("molten_aluminum", "Molten Aluminum", 0xff919d9c),
        CONSTANTAN("molten_constantan", "Molten Constantan", 0xff884739),
        NICKEL("molten_nickel", "Molten Nickel", 0xffb3bba9),
        SILVER("molten_silver", "Molten Silver", 0xff8dadad),
        INVAR("molten_invar", "Molten Invar", 0xff5c7c7b),

        // AD-ASTRA
        DESH("molten_desh", "Molten Desh", 0xffd38b4c),
        OSTRUM("molten_ostrum", "Molten Ostrum", 0xff7c5156),
        CALORITE("molten_calorite", "Molten Calorite", 0xff904748),

        // BOTANIA
        MANASTEEL("molten_manasteel", "Molten Manasteel", 0xff006afc),
        ELEMENTIUM("molten_elementium", "Molten Elementium", 0xffea3298),
        TERRASTEEL("molten_terrasteel", "Molten Terrasteel", 0xff3fca00),
        SPIRIT("molten_spirit", "Molten Spirit", 0xff7ea37e),
        ESSENCE("molten_essence", "Molten Essence", 0xff6ea31e);

        public final DeferredHolder<Fluid, FlowingFluid> STILL;
        public final DeferredHolder<Fluid, FlowingFluid> FLOWING;
        public final DeferredBlock<LiquidBlock> BLOCK;
        public final DeferredItem<BucketItem> BUCKET_ITEM;
        public final Supplier<FluidType> FLUID_TYPE;
        public BaseFlowingFluid.Properties PROPS;

        public final String id;
        public final String lang;
        public final int color;

        INSTANCE(String name, String lang, int color) {
            this.id = name;
            this.lang = lang;
            this.color = color;
            this.FLUID_TYPE = FLUID_TYPES.register(name + "_type",
                    () -> new MoltenFluidType(FluidType.Properties.create().density(3500).viscosity(7500).temperature(1500)
                            .canDrown(true).lightLevel(12).temperature(5000).motionScale(0.00233).canSwim(false)
                            .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY_LAVA)
                            .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_LAVA)
                            .sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH),
                            LIQUID, LIQUID_FLOW, OVERLAY, RENDER_OVERLAY, color, () -> 1f / 64f));
            this.STILL = FLUIDS.register(name, () -> new BaseFlowingFluid.Source(PROPS));
            this.FLOWING = FLUIDS.register("flowing_" + name, () -> new BaseFlowingFluid.Flowing(PROPS));
            this.BLOCK = BLOCKS.register(name, () -> new LiquidBlock(STILL.get(),
                    BlockBehaviour.Properties.of().noCollission().strength(100.0F).noLootTable()
                            .replaceable().pushReaction(PushReaction.DESTROY)
                            .lightLevel((data) -> 15)
                            .liquid()) {
                @Override
                public void animateTick(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull RandomSource random) {
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
            });
            this.BUCKET_ITEM = ITEMS.register(name + "_bucket",
                    () -> new BucketItem(STILL.get(), new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));
            this.PROPS = new BaseFlowingFluid.Properties(FLUID_TYPE, STILL, FLOWING)
                    .levelDecreasePerBlock(3).tickRate(25).slopeFindDistance(1).explosionResistance(100f)
                    .block(BLOCK).bucket(BUCKET_ITEM);
        }

        public boolean init() {
            return true;
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
