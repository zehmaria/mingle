package zeh.mingle.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Optional;

public record InteractSet(Optional<String> block,
                          Optional<String> fluid,
                          Optional<Boolean> requireSource,
                          Optional<InteractSet> replace) {

    public static final Codec<InteractSet> CODEC = Codec.recursive(
            InteractSet.class.getSimpleName(),
            recursed -> RecordCodecBuilder.create(instance -> instance.group(
                    Codec.STRING.optionalFieldOf("fluid").forGetter(InteractSet::fluid),
                    Codec.STRING.optionalFieldOf("block").forGetter(InteractSet::block),
                    Codec.BOOL.optionalFieldOf("require_source").forGetter(InteractSet::requireSource),
                    recursed.optionalFieldOf("replace").forGetter(InteractSet::replace)
            ).apply(instance, InteractSet::new)));


    @SuppressWarnings("unused")
    public InteractSet(String block) {
        this(Optional.of(block), Optional.empty(), Optional.empty(), Optional.empty());
    }

    @SuppressWarnings("unused")
    public InteractSet(String block, InteractSet replace) {
        this(Optional.of(block), Optional.empty(), Optional.empty(), Optional.of(replace));
    }


    @SuppressWarnings("unused")
    public InteractSet(String fluid, boolean requireSource) {
        this(Optional.empty(), Optional.of(fluid), Optional.of(requireSource), Optional.empty());
    }

    @SuppressWarnings("unused")
    public InteractSet(String fluid, boolean requireSource, InteractSet replace) {
        this(Optional.empty(), Optional.of(fluid), Optional.of(requireSource), Optional.of(replace));
    }
}