package zeh.mingle.interactions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME)
public record Interactions(ResourceLocation entitytype, String fluid, int powerpermb, int tickpermb) implements MingledData {
    @Override
    public ResourceLocation getKey() {
        return entitytype();
    }

    public static final Codec<Interactions> CODEC = RecordCodecBuilder.create(soulDataInstance ->
            soulDataInstance.group(
                            ResourceLocation.CODEC.fieldOf("entity").forGetter(Interactions::entitytype),
                            Codec.STRING.fieldOf("fluid").forGetter(Interactions::fluid),
                            Codec.INT.fieldOf("power/mb").forGetter(Interactions::powerpermb),
                            Codec.INT.fieldOf("tick/mb").forGetter(Interactions::tickpermb))
                    .apply(soulDataInstance, Interactions::new));

    public static StreamCodec<ByteBuf, Interactions> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC,
            Interactions::entitytype,
            ByteBufCodecs.STRING_UTF8,
            Interactions::fluid,
            ByteBufCodecs.INT,
            Interactions::powerpermb,
            ByteBufCodecs.INT,
            Interactions::tickpermb,
            Interactions::new
    );

    public static final String NAME = "engine";
    public static final MingledDataReloadListener<Interactions> ENGINE = new MingledDataReloadListener<>(NAME, CODEC);

    @SubscribeEvent
    public static void addResource(AddReloadListenerEvent event) {
        //event.addListener(ENGINE);
    }
}
