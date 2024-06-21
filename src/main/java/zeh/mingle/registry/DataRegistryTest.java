package zeh.mingle.registry;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import zeh.mingle.Mingle;

public class DataRegistryTest {

    public static final ResourceKey<Registry<DataTest>> KEY = ResourceKey.createRegistryKey(ResourceLocation.tryParse("interactions"));

    public static void register(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(KEY, DataTest.CODEC);
    }

}
