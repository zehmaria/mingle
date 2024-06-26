package zeh.mingle.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.fluids.FluidInteractionRegistry;

public record InteractedSet(
        InteractedPair fluid,
        InteractedPair target
) {

    public static final Codec<InteractedSet> CODEC = RecordCodecBuilder.create(config -> config.group(
            InteractedPair.CODEC.fieldOf("fluid").forGetter(InteractedSet::fluid),
            InteractedPair.CODEC.fieldOf("target").forGetter(InteractedSet::target)
    ).apply(config, InteractedSet::new));

    public void add() {
        if (fluid.placed().isFluid()) FluidInteractionRegistry.addInteraction(fluid.placed().getFluidType(),
                new FluidInteractionRegistry.InteractionInformation(this::check, this::nothing));
    }

    public boolean check(Level level, BlockPos pos, BlockPos dirPos, FluidState state) {
        if (!fluid.complies(state)) return false;

        if (target.is(level.getBlockState(dirPos)) && !fluid.placed().is(level.getBlockState(dirPos))) {
            fluid.swapAt(level, pos);
            target.swapAt(level, dirPos);
            return true;
        } else if (pos.above().equals(dirPos)) return check(level, pos, pos.below(), state);

        return false;
    }

    public void nothing(Level level, BlockPos pos, BlockPos dirPos, FluidState state) { }

}