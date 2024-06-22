package zeh.mingle.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidType;

public record InteractedFrom(InteractSet target, InteractSet interaction) {

    public static final Codec<InteractedFrom> CODEC = RecordCodecBuilder.create(config -> config.group(
            InteractSet.CODEC.fieldOf("target").forGetter(InteractedFrom::target),
            InteractSet.CODEC.fieldOf("interaction").forGetter(InteractedFrom::interaction)
    ).apply(config, InteractedFrom::new));

    public FluidType getFluidType() {
        return this.interaction.getFluidType();
    }

    public BlockState getReplaceState() {
        return this.interaction().getReplaceState();
    }

    public boolean isReplaceable() {
        return this.interaction.replace().isPresent();
    }

    public boolean requireSource() {
        return this.interaction.requireSource().isPresent() && this.interaction.requireSource().get();
    }

}