package zeh.fluidactions;

import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;

import com.tterrag.registrate.builders.FluidBuilder.FluidTypeFactory;
import com.tterrag.registrate.util.entry.FluidEntry;

import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer.FogMode;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;

import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.fluids.*;
import static net.minecraftforge.fluids.ForgeFlowingFluid.Flowing;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.function.Consumer;
import java.util.function.Supplier;

import static zeh.fluidactions.FluidActions.REGISTRATE;

import zeh.fluidactions.foundation.utility.Color;

public class AllFluids {

    public static FluidEntry<Flowing> moltenFluid(String name, String lang, int fogColor) {
        return REGISTRATE.fluid(
                        name, FluidActions.asResource("block/fluid/molten_still"),
                        FluidActions.asResource("block/fluid/molten_still"),
                        MoltenFluidType.create(fogColor, () -> 1f / 64f))
                .lang(lang)
                .properties(b -> b.viscosity(7500).density(3500).canDrown(true)
                        .lightLevel(8).temperature(5000).motionScale(0)
                        .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY_LAVA)
                        .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_LAVA))
                .fluidProperties(p -> p.levelDecreasePerBlock(5).tickRate(25).slopeFindDistance(1).explosionResistance(100f))

                .tag(AllTags.forgeFluidTag(name)).tag(AllTags.AllFluidTags.FLUID_INTERACTIONS_EXCLUSION.tag)
                .source(ForgeFlowingFluid.Source::new) // TODO: remove when Registrate fixes FluidBuilder

                .bucket()
                .tag(AllTags.forgeItemTag("buckets/" + name))
                .build()

                .register();
    }


    // VANILLA
    public static final FluidEntry<Flowing> MOLTEN_IRON = moltenFluid("molten_iron", "Molten Iron", 0xff7f171c);
    public static final FluidEntry<Flowing> MOLTEN_GOLD = moltenFluid("molten_gold", "Molten Gold", 0xffAC9000);
    public static final FluidEntry<Flowing> MOLTEN_COPPER = moltenFluid("molten_copper", "Molten Copper", 0xffC77B41);

    public static final FluidEntry<Flowing> MOLTEN_ANDESITE = moltenFluid("molten_andesite", "Molten Andesite", 0xff7b7d7d);
    public static final FluidEntry<Flowing> MOLTEN_SOUL = moltenFluid("molten_soul", "Molten Soul", 0xff80461B);
    public static final FluidEntry<Flowing> MOLTEN_ENDER = moltenFluid("molten_ender", "Molten Ender", 0xff023020);
    public static final FluidEntry<Flowing> MOLTEN_SLIME = moltenFluid("molten_slime", "Molten Slime", 0xff32CD32);
    public static final FluidEntry<Flowing> MOLTEN_QUARTZ = moltenFluid("molten_quartz", "Molten Quartz", 0xffF5F5DC);
    public static final FluidEntry<Flowing> MOLTEN_GLASS = moltenFluid("molten_glass", "Molten Glass", 0xffF6B26B);
    public static final FluidEntry<Flowing> MOLTEN_NETHERITE = moltenFluid("molten_netherite", "Molten Netherite", 0xff5C4033);
    public static final FluidEntry<Flowing> MOLTEN_DEBRIS = moltenFluid("molten_debris", "Molten Debris", 0xff988558);

    // GENERAL
    public static final FluidEntry<Flowing> MOLTEN_ANDESITE_ALLOY = moltenFluid("molten_andesite_alloy", "Molten Andesite Alloy", 0xff757575);
    public static final FluidEntry<Flowing> MOLTEN_ZINC = moltenFluid("molten_zinc", "Molten Zinc", 0xff809a7d);
    public static final FluidEntry<Flowing> MOLTEN_BRASS = moltenFluid("molten_brass", "Molten Brass", 0xffbf8e55);
    public static final FluidEntry<Flowing> MOLTEN_REFINED_GLOWSTONE = moltenFluid("molten_refined_glowstone", "Molten Refined Glowstone", 0xffd49710);
    public static final FluidEntry<Flowing> MOLTEN_REFINED_OBSIDIAN = moltenFluid("molten_refined_obsidian", "Molten Refined Obsidian", 0xff272a3e);
    public static final FluidEntry<Flowing> MOLTEN_OSMIUM = moltenFluid("molten_osmium", "Molten Osmium", 0xff587470);
    public static final FluidEntry<Flowing> MOLTEN_BRONZE = moltenFluid("molten_bronze", "Molten Bronze", 0xff986441);
    public static final FluidEntry<Flowing> MOLTEN_TIN = moltenFluid("molten_tin", "Molten Tin", 0xffa7afaf);
    public static final FluidEntry<Flowing> MOLTEN_LEAD = moltenFluid("molten_lead", "Molten lead", 0xff58546d);
    public static final FluidEntry<Flowing> MOLTEN_URANIUM = moltenFluid("molten_uranium", "Molten Uranium", 0xff4F7942);
    public static final FluidEntry<Flowing> MOLTEN_STEEL = moltenFluid("molten_steel", "Molten Steel", 0xff71797E);
    public static final FluidEntry<Flowing> MOLTEN_ELECTRUM = moltenFluid("molten_electrum", "Molten Electrum", 0xffCC5500);
    public static final FluidEntry<Flowing> MOLTEN_ALUMINUM = moltenFluid("molten_aluminum", "Molten Aluminum", 0xff919d9c);
    public static final FluidEntry<Flowing> MOLTEN_CONSTANTAN = moltenFluid("molten_constantan", "Molten Constantan", 0xff884739);
    public static final FluidEntry<Flowing> MOLTEN_NICKEL = moltenFluid("molten_nickel", "Molten Nickel", 0xffb3bba9);
    public static final FluidEntry<Flowing> MOLTEN_SILVER = moltenFluid("molten_silver", "Molten Silver", 0xff8dadad);
    public static final FluidEntry<Flowing> MOLTEN_INVAR = moltenFluid("molten_invar", "Molten Invar", 0xff5c7c7b);

    // AD-ASTRA
    public static final FluidEntry<Flowing> MOLTEN_DESH = moltenFluid("molten_desh", "Molten Desh", 0xffd38b4c);
    public static final FluidEntry<Flowing> MOLTEN_OSTRUM = moltenFluid("molten_ostrum", "Molten Ostrum", 0xff7c5156);
    public static final FluidEntry<Flowing> MOLTEN_CALORITE = moltenFluid("molten_calorite", "Molten Calorite", 0xff904748);

    // BOTANIA
    public static final FluidEntry<Flowing> MOLTEN_MANASTEEL = moltenFluid("molten_manasteel", "Molten Manasteel", 0xff006afc);
    public static final FluidEntry<Flowing> MOLTEN_ELEMENTIUM = moltenFluid("molten_elementium", "Molten Elementium", 0xffea3298);
    public static final FluidEntry<Flowing> MOLTEN_TERRASTEEL = moltenFluid("molten_terrasteel", "Molten Terrasteel", 0xff3fca00);
    public static final FluidEntry<Flowing> MOLTEN_SPIRIT = moltenFluid("molten_spirit", "Molten Spirit", 0xff7ea37e);
    public static final FluidEntry<Flowing> MOLTEN_ESSENCE = moltenFluid("molten_essence", "Molten Essence", 0xff6ea31e);

    public static void register() {}

    public static abstract class TintedFluidType extends FluidType {

        private ResourceLocation stillTexture;
        private ResourceLocation flowingTexture;

        public TintedFluidType(Properties properties, ResourceLocation stillTexture, ResourceLocation flowingTexture) {
            super(properties);
            this.stillTexture = stillTexture;
            this.flowingTexture = flowingTexture;
        }

        @Override
        public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
            consumer.accept(new IClientFluidTypeExtensions() {

                @Override
                public ResourceLocation getStillTexture() {
                    return stillTexture;
                }

                @Override
                public ResourceLocation getFlowingTexture() {
                    return flowingTexture;
                }

                @Override
                public int getTintColor(FluidStack stack) {
                    return TintedFluidType.this.getTintColor(stack);
                }

                @Override
                public int getTintColor(FluidState state, BlockAndTintGetter getter, BlockPos pos) {
                    return TintedFluidType.this.getTintColor(state, getter, pos);
                }

                @Override
                public @NotNull Vector3f modifyFogColor(Camera camera, float partialTick, ClientLevel level,
                                                        int renderDistance, float darkenWorldAmount, Vector3f fluidFogColor) {
                    Vector3f customFogColor = TintedFluidType.this.getCustomFogColor();
                    return customFogColor == null ? fluidFogColor : customFogColor;
                }

                @Override
                public void modifyFogRender(Camera camera, FogMode mode, float renderDistance, float partialTick,
                                            float nearDistance, float farDistance, FogShape shape) {
                    float modifier = TintedFluidType.this.getFogDistanceModifier();
                    float baseWaterFog = 96.0f;
                    if (modifier != 1f) {
                        RenderSystem.setShaderFogShape(FogShape.CYLINDER);
                        RenderSystem.setShaderFogStart(-8);
                        RenderSystem.setShaderFogEnd(baseWaterFog * modifier);
                    }
                }

            });
        }

        protected abstract int getTintColor(FluidStack stack);

        protected abstract int getTintColor(FluidState state, BlockAndTintGetter getter, BlockPos pos);

        protected Vector3f getCustomFogColor() {
            return null;
        }

        protected float getFogDistanceModifier() {
            return 1f;
        }

    }

    private static class MoltenFluidType extends TintedFluidType {

        protected int tintColor = 0xffffffff;
        private Vector3f fogColor;
        private Supplier<Float> fogDistance;

        public MoltenFluidType(Properties properties, ResourceLocation stillTexture, ResourceLocation flowingTexture) {
            super(properties, stillTexture, flowingTexture);
        }

        public static FluidTypeFactory create(int color, Supplier<Float> fogDistance) {
            return (p, s, f) -> {
                MoltenFluidType fluidType = new MoltenFluidType(p, s, f);
                fluidType.fogColor = new Color(color, false).asVectorF();
                fluidType.fogDistance = fogDistance;
                fluidType.tintColor = color;
                return fluidType;
            };
        }

        @Override
        protected int getTintColor(FluidStack stack) { return tintColor; }

        @Override
        public int getTintColor(FluidState state, BlockAndTintGetter world, BlockPos pos) { return tintColor; }

        @Override
        protected Vector3f getCustomFogColor() { return fogColor; }

        @Override
        protected float getFogDistanceModifier() { return fogDistance.get(); }

        @Override
        public boolean move(FluidState state, LivingEntity entity, Vec3 movementVector, double gravity) {
            if(!entity.isOnFire()) entity.setSecondsOnFire(4);
            else entity.setRemainingFireTicks(20 * 4);
            entity.setSpeed(0.001f);
            entity.setDeltaMovement(entity.getDeltaMovement().scale(0.001f));
            return false;
        }

        @Override
        public void setItemMovement(ItemEntity entity) {
            if (entity.getItem().is(AllTags.AllItemTags.LAVA_RESISTANT.tag)) return;
            entity.playSound(SoundEvents.LAVA_EXTINGUISH, 0.5f,2.1f);
            entity.kill();
        }

    }
}
