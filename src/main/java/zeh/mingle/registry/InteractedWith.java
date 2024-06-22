package zeh.mingle.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Optional;

public record InteractedWith(boolean requireSource,
                             Optional<InteractSet> replace,
                             InteractSet target) {

    public static final Codec<InteractedWith> CODEC = RecordCodecBuilder.create(config -> config.group(
            Codec.BOOL.fieldOf("require_source").forGetter(InteractedWith::requireSource),
            InteractSet.CODEC.optionalFieldOf("replace").forGetter(InteractedWith::replace),
            InteractSet.CODEC.fieldOf("target").forGetter(InteractedWith::target)
    ).apply(config, InteractedWith::new));


    @SuppressWarnings("unused")
    public InteractedWith(boolean requireSource, InteractSet target) {
        this(requireSource, Optional.empty(), target);
    }

    @SuppressWarnings("unused")
    public InteractedWith(boolean requireSource, InteractSet replace, InteractSet target) {
        this(requireSource, Optional.of(replace), target);
    }

}