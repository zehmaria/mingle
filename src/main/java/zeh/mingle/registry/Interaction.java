package zeh.mingle.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import zeh.mingle.Mingle;

import java.util.List;

public record Interaction(Fluid fluid, Block slag, List<InteractedWith> interactions, List<InteractedFrom> replacementFor) {

    public static final ResourceKey<Registry<Interaction>> KEY = ResourceKey.createRegistryKey(Mingle.as("interactions"));

    public static void register(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(KEY, Interaction.CODEC);
    }

    public static final Codec<Interaction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BuiltInRegistries.FLUID.byNameCodec().fieldOf("fluid").forGetter(Interaction::fluid),
            BuiltInRegistries.BLOCK.byNameCodec().fieldOf("slag").forGetter(Interaction::slag),

            InteractedWith.CODEC.listOf().fieldOf("interactions").forGetter(Interaction::interactions),
            InteractedFrom.CODEC.listOf().fieldOf("replacement_for").forGetter(Interaction::replacementFor)
    ).apply(instance, Interaction::new));

}
