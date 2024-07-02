package zeh.mingle.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

public record InteractedPair(
        Interacted placed,
        Integer required, // 0 = Source Only; 1 = Flowing Only; 2 = Both; 3 = Not fluid.
        Interacted replace
) {

    public static final Codec<InteractedPair> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Interacted.CODEC.fieldOf("placed").forGetter(InteractedPair::placed),
            Codec.intRange(0, 3).lenientOptionalFieldOf("required", 3).forGetter(it -> it.required),
            Interacted.CODEC.lenientOptionalFieldOf("replace", new Interacted("ignore")).forGetter(it -> it.replace)
    ).apply(instance, InteractedPair::new));

    public InteractedPair(String name) { this(new Interacted(name), 3, new Interacted("ignore")); }
    public InteractedPair(String name, String replace) { this(new Interacted(name), 3, new Interacted(replace)); }
    public InteractedPair(String name, int require) { this(new Interacted(name), require, new Interacted("ignore")); }
    public InteractedPair(String name, int require, String replace) { this(new Interacted(name), require, new Interacted(replace)); }

    public boolean complies(FluidState state) {
        return !((required == 0 && !state.isSource()) || (required == 1 && state.isSource()));
    }
    public boolean is(BlockState state) {
        return required != 3 ? complies(state.getFluidState()) && placed.is(state.getFluidState()) : placed.is(state);
    }
    public void swapAt(Level level, BlockPos pos) {
        replace.replaceAt(level, pos);
    }

}