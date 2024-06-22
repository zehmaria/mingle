package zeh.mingle.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

public record InteractedWith(boolean requireSource,
                             Optional<String> replace,
                             InteractSet target) {

    public static final Codec<InteractedWith> CODEC = RecordCodecBuilder.create(config -> config.group(
            Codec.BOOL.fieldOf("require_source").forGetter(InteractedWith::requireSource),
            Codec.STRING.optionalFieldOf("replace").forGetter(InteractedWith::replace),
            InteractSet.CODEC.fieldOf("target").forGetter(InteractedWith::target)
    ).apply(config, InteractedWith::new));

    public BlockState getReplaceState() {
        if (this.replace.isEmpty()) return null;
        if (this.replace.get().startsWith("#")) {
            TagKey<Block> tag = TagKey.create(BuiltInRegistries.BLOCK.key(), ResourceLocation.parse(this.replace.get().substring(1)));
            Optional<Holder<Block>> any = BuiltInRegistries.BLOCK.getOrCreateTag(tag).stream().findAny();
            return any.map(blockHolder -> blockHolder.value().defaultBlockState()).orElse(null);
        } else return BuiltInRegistries.BLOCK.get(ResourceLocation.parse(this.replace.get())).defaultBlockState();
    }

    @SuppressWarnings("unused")
    public InteractedWith(boolean requireSource, InteractSet target) {
        this(requireSource, Optional.empty(), target);
    }

    @SuppressWarnings("unused")
    public InteractedWith(boolean requireSource, String replace, InteractSet target) {
        this(requireSource, Optional.of(replace), target);
    }

}