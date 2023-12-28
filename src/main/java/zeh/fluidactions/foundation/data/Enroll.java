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
import net.minecraftforge.registries.RegistryObject;

import org.jetbrains.annotations.Nullable;

public class Enroll extends AbstractRegistrate<Enroll> {

    private static final Map<RegistryEntry<?>, RegistryObject<CreativeModeTab>> TAB_LOOKUP = new IdentityHashMap<>();
    private RegistryObject<CreativeModeTab> currentTab;

    protected Enroll(String modid) {
        super(modid);
    }

    public static Enroll create(String modid) {
        return new Enroll(modid);
    }

    public static boolean isInCreativeTab(RegistryEntry<?> entry, RegistryObject<CreativeModeTab> tab) {
        return TAB_LOOKUP.get(entry) == tab;
    }

    @Nullable
    public Enroll setCreativeTab(RegistryObject<CreativeModeTab> tab) {
        currentTab = tab;
        return self();
    }

    public RegistryObject<CreativeModeTab> getCreativeTab() {
        return currentTab;
    }

    @Override
    public Enroll registerEventListeners(IEventBus bus) {
        return super.registerEventListeners(bus);
    }

    @Override
    protected <R, T extends R> RegistryEntry<T> accept(String name, ResourceKey<? extends Registry<R>> type,
                                                       Builder<R, T, ?, ?> builder, NonNullSupplier<? extends T> creator,
                                                       NonNullFunction<RegistryObject<T>, ? extends RegistryEntry<T>> entryFactory) {
        RegistryEntry<T> entry = super.accept(name, type, builder, creator, entryFactory);
        if (currentTab != null) TAB_LOOKUP.put(entry, currentTab);
        return entry;
    }

}