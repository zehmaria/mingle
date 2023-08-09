package zeh.fluidactions.foundation.data;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import com.tterrag.registrate.builders.*;
import org.jetbrains.annotations.Nullable;

import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.builders.BlockEntityBuilder.BlockEntityFactory;
import com.tterrag.registrate.util.entry.RegistryEntry;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import com.tterrag.registrate.util.nullness.NonNullSupplier;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntity;

import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.RegistryObject;

import zeh.fluidactions.FluidActions;

import zeh.fluidactions.content.fluids.VirtualFluid;
import zeh.fluidactions.foundation.item.TooltipModifier;

public class Enroll extends AbstractRegistrate<Enroll> {
    @Nullable
    protected Function<Item, TooltipModifier> currentTooltipModifierFactory;

    protected Enroll(String modid) {
        super(modid);
    }

    public static Enroll create(String modid) {
        return new Enroll(modid);
    }

    public Enroll setTooltipModifierFactory(@Nullable Function<Item, TooltipModifier> factory) {
        currentTooltipModifierFactory = factory;
        return self();
    }

    @Nullable
    public Function<Item, TooltipModifier> getTooltipModifierFactory() {
        return currentTooltipModifierFactory;
    }

    @Override
    public Enroll registerEventListeners(IEventBus bus) {
        return super.registerEventListeners(bus);
    }

    private static Map<RegistryEntry<?>, RegistryObject<CreativeModeTab>> tabLookup = new IdentityHashMap<>();
    private RegistryObject<CreativeModeTab> currentTab;

    public Enroll useCreativeTab(RegistryObject<CreativeModeTab> tab) {
        this.currentTab = tab;
        return this;
    }

    public boolean isInCreativeTab(RegistryEntry<?> entry, RegistryObject<CreativeModeTab> tab) {
        return tabLookup.get(entry) == tab;
    }

    @Override
    protected <R, T extends R> RegistryEntry<T> accept(String name, ResourceKey<? extends Registry<R>> type,
                                                       Builder<R, T, ?, ?> builder, NonNullSupplier<? extends T> creator,
                                                       NonNullFunction<RegistryObject<T>, ? extends RegistryEntry<T>> entryFactory) {
        RegistryEntry<T> entry = super.accept(name, type, builder, creator, entryFactory);
        if (type.equals(Registries.ITEM)) {
            if (currentTooltipModifierFactory != null) { TooltipModifier.REGISTRY.registerDeferred(entry.getId(), currentTooltipModifierFactory); }
        }
        if (currentTab != null) tabLookup.put(entry, currentTab);
        return entry;
    }

    @Override
    public <T extends BlockEntity> BlockEntityBuilder<T, Enroll> blockEntity(String name, BlockEntityFactory<T> factory) {
        return blockEntity(self(), name, factory);
    }

    @Override
    public <T extends BlockEntity, P> BlockEntityBuilder<T, P> blockEntity(P parent, String name, BlockEntityFactory<T> factory) {
        return (BlockEntityBuilder<T, P>) entry(name, (callback) -> BlockEntityBuilder.create(this, parent, name, callback, factory));
    }

    @Override
    public <T extends Entity> EntityBuilder<T, Enroll> entity(String name, EntityType.EntityFactory<T> factory, MobCategory type) {
        return this.entity(self(), name, factory, type);
    }

    @Override
    public <T extends Entity, P> EntityBuilder<T, P> entity(P parent, String name, EntityType.EntityFactory<T> factory, MobCategory type) {
        return (EntityBuilder<T, P>) this.entry(name, (callback) -> { return EntityBuilder.create(this, parent, name, callback, factory, type); });
    }

    /* Fluids */

    public <T extends ForgeFlowingFluid> FluidBuilder<T, Enroll> virtualFluid(String name,
                                                                              FluidBuilder.FluidTypeFactory typeFactory, NonNullFunction<ForgeFlowingFluid.Properties, T> factory) {
        return entry(name,
                c -> new VirtualFluidBuilder<>(self(), self(), name, c, FluidActions.asResource("fluid/" + name + "_still"),
                        FluidActions.asResource("fluid/" + name + "_flow"), typeFactory, factory));
    }

    public <T extends ForgeFlowingFluid> FluidBuilder<T, Enroll> virtualFluid(
            String name, ResourceLocation still, ResourceLocation flow, FluidBuilder.FluidTypeFactory typeFactory,
            NonNullFunction<ForgeFlowingFluid.Properties, T> factory) {
        return entry(name, c -> new VirtualFluidBuilder<>(self(), self(), name, c, still, flow, typeFactory, factory));
    }

    public FluidBuilder<VirtualFluid, Enroll> virtualFluid(String name) {
        return entry(name,
                c -> new VirtualFluidBuilder<VirtualFluid, Enroll>(self(), self(), name, c,
                        FluidActions.asResource("fluid/" + name + "_still"), FluidActions.asResource("fluid/" + name + "_flow"),
                        Enroll::defaultFluidType, VirtualFluid::new));
    }

    public FluidBuilder<VirtualFluid, Enroll> virtualFluid(String name, ResourceLocation still, ResourceLocation flow) {
        return entry(name, c -> new VirtualFluidBuilder<>(self(), self(), name, c, still, flow, Enroll::defaultFluidType, VirtualFluid::new));
    }

    public FluidBuilder<ForgeFlowingFluid.Flowing, Enroll> standardFluid(String name) {
        return fluid(name, FluidActions.asResource("fluid/" + name + "_still"), FluidActions.asResource("fluid/" + name + "_flow"));
    }

    public FluidBuilder<ForgeFlowingFluid.Flowing, Enroll> standardFluid(String name, FluidBuilder.FluidTypeFactory typeFactory) {
        return fluid(name, FluidActions.asResource("fluid/" + name + "_still"), FluidActions.asResource("fluid/" + name + "_flow"), typeFactory);
    }

    public static FluidType defaultFluidType(FluidType.Properties properties, ResourceLocation stillTexture, ResourceLocation flowingTexture) {
        return new FluidType(properties) {
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
                });
            }
        };
    }

}