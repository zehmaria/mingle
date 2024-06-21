package zeh.mingle.interactions.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import zeh.mingle.Mingle;
import zeh.mingle.interactions.Interactions;

import java.util.HashMap;
import java.util.Map;

public record InteractionsPacket(Map<ResourceLocation, Interactions> map) implements CustomPacketPayload {

    public static final Type<InteractionsPacket> TYPE = new Type<>(Mingle.asResource("interactions"));

    public static StreamCodec<ByteBuf, InteractionsPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.map(HashMap::new, ResourceLocation.STREAM_CODEC, Interactions.STREAM_CODEC),
            InteractionsPacket::map,
            InteractionsPacket::new
    );

    public InteractionsPacket(FriendlyByteBuf buf) {
        this(
                buf.readMap(FriendlyByteBuf::readResourceLocation, buff ->
                        new Interactions(buff.readResourceLocation(), buff.readUtf(), buff.readInt(), buff.readInt())
                )
        );
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}