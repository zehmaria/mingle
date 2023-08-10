package zeh.fluidactions.foundation.data;

import java.util.IdentityHashMap;
import java.util.Map;

import com.tterrag.registrate.builders.*;

import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.util.entry.RegistryEntry;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import com.tterrag.registrate.util.nullness.NonNullSupplier;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries-.RegistryObject;

public class Enroll extends AbstractRegistrate<Enroll> {

    protected Enroll(String modid) {
        super(modid);
    }

    public static Enroll create(String modid) {
        return new Enroll(modid);
    }

    @Override
    public Enroll registerEventListeners(IEventBus bus) {
        return super.registerEventListeners(bus);
    }

    private static Map<RegistryEntry<?>, RegistryObject<CreativeModeTab>> tabLookup = new IdentityHashMap<>();
    private RegistryObject<CreativeModeTab> currentTab;

    public boolean isInCreativeTab(RegistryEntry<?> entry, RegistryObject<CreativeModeTab> tab) {
        return tabLookup.get(entry) == tab;
    }

    @Override
    protected <R, T extends R> RegistryEntry<T> accept(String name, ResourceKey<? extends Registry<R>> type,
                                                       Builder<R, T, ?, ?> builder, NonNullSupplier<? extends T> creator,
                                                       NonNullFunction<RegistryObject<T>, ? extends RegistryEntry<T>> entryFactory) {
        RegistryEntry<T> entry = super.accept(name, type, builder, creator, entryFactory);
        if (currentTab != null) tabLookup.put(entry, currentTab);
        return entry;
    }

}