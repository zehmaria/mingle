package zeh.mingle.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidType;

import java.util.Optional;

public record InteractSet(Optional<String> block,
                          Optional<String> fluid,
                          Optional<Boolean> requireSource,
                          Optional<String> replace) {

    public static final Codec<InteractSet> CODEC = Codec.recursive(
            InteractSet.class.getSimpleName(),
            recursed -> RecordCodecBuilder.create(instance -> instance.group(
                    Codec.STRING.optionalFieldOf("block").forGetter(InteractSet::block),
                    Codec.STRING.optionalFieldOf("fluid").forGetter(InteractSet::fluid),
                    Codec.BOOL.optionalFieldOf("require_source").forGetter(InteractSet::requireSource),
                    Codec.STRING.optionalFieldOf("replace").forGetter(InteractSet::replace)
            ).apply(instance, InteractSet::new)));

    public boolean isBlockTag() {
        return this.block.isPresent() && this.block.get().startsWith("#");
    }

    public TagKey<Block> getBlockTag() {
        return this.block.map(s -> TagKey.create(BuiltInRegistries.BLOCK.key(), ResourceLocation.parse(s.substring(1)))).orElse(null);
    }

    public Block getBlock() {
        return this.block.map(s -> BuiltInRegistries.BLOCK.get(ResourceLocation.parse(s))).orElse(null);
    }

    public Fluid getFluid() {
        return this.fluid.map(s -> BuiltInRegistries.FLUID.get(ResourceLocation.parse(s))).orElse(null);
    }

    public FluidType getFluidType() {
        if (getFluid() != null) return getFluid().getFluidType();
        return null;
    }

    public BlockState getReplaceState() {
        if (this.replace.isEmpty()) return null;
        if (this.replace.get().startsWith("#")) {
            TagKey<Block> tag = TagKey.create(BuiltInRegistries.BLOCK.key(), ResourceLocation.parse(this.replace.get().substring(1)));
            Optional<Holder<Block>> any = BuiltInRegistries.BLOCK.getOrCreateTag(tag).stream().findAny();
            return any.map(blockHolder -> blockHolder.value().defaultBlockState()).orElse(null);
        } else return BuiltInRegistries.BLOCK.get(ResourceLocation.parse(this.replace.get())).defaultBlockState();
    }

    public boolean is(BlockState state) {
        if (this.isBlockTag()) {
            return state.is(this.getBlockTag());
        } else if (this.block.isPresent()) {
            return state.is(this.getBlock());
        } else if (this.fluid.isPresent()) {
            if (this.requireSource().isPresent() && this.requireSource().get() && !state.getFluidState().isSource()) {
                return false;
            } else return state.getFluidState().getType().isSame(this.getFluid());
        } else return false;
    }


    @SuppressWarnings("unused")
    public InteractSet(String block) {
        this(Optional.of(block), Optional.empty(), Optional.empty(), Optional.empty());
    }

    @SuppressWarnings("unused")
    public InteractSet(String block, String replace) {
        this(Optional.of(block), Optional.empty(), Optional.empty(), Optional.of(replace));
    }

    @SuppressWarnings("unused")
    public InteractSet(String fluid, boolean requireSource) {
        this(Optional.empty(), Optional.of(fluid), Optional.of(requireSource), Optional.empty());
    }

    @SuppressWarnings("unused")
    public InteractSet(String fluid, boolean requireSource, String replace) {
        this(Optional.empty(), Optional.of(fluid), Optional.of(requireSource), Optional.of(replace));
    }
}