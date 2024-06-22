package zeh.mingle.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record InteractedFrom(InteractSet target, InteractSet fluid) {

    public static final Codec<InteractedFrom> CODEC = RecordCodecBuilder.create(config -> config.group(
            InteractSet.CODEC.fieldOf("target").forGetter(InteractedFrom::target),
            InteractSet.CODEC.fieldOf("interaction").forGetter(InteractedFrom::fluid)
    ).apply(config, InteractedFrom::new));

}