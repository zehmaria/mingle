package zeh.mingle.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import zeh.mingle.Mingle;

import java.util.List;

@EventBusSubscriber
public record Interaction(List<InteractedSet> interactions) {

    public static final Codec<Interaction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            InteractedSet.CODEC.listOf().fieldOf("interactions").forGetter(Interaction::interactions)
    ).apply(instance, Interaction::new));
    private static boolean hasLoadedOnce = false;
    public static final ResourceKey<Registry<Interaction>> KEY = ResourceKey.createRegistryKey(Mingle.as("interactions"));

    public static void register(DataPackRegistryEvent.NewRegistry event) { event.dataPackRegistry(KEY, Interaction.CODEC); }

    @SubscribeEvent
    public static void addNew(final ServerAboutToStartEvent event) {
        if (hasLoadedOnce) return;
        event.getServer().registryAccess().registry(Interaction.KEY)
                .ifPresent(it -> it.forEach(Interaction::registerInteractions));
        hasLoadedOnce = true;
    }
    public void registerInteractions() { interactions().forEach(InteractedSet::add); }

}
