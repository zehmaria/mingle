package zeh.mingle.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.fluids.FluidInteractionRegistry;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import zeh.mingle.Mingle;

import java.util.List;
import java.util.Objects;

public record Interaction(Fluid fluid, Block slag, List<InteractedWith> interactions,
                          List<InteractedFrom> replacementFor) {

    public static final ResourceKey<Registry<Interaction>> KEY = ResourceKey.createRegistryKey(Mingle.as("interactions"));

    public static void register(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(KEY, Interaction.CODEC);
    }

    public static final Codec<Interaction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BuiltInRegistries.FLUID.byNameCodec().fieldOf("fluid").forGetter(Interaction::fluid),
            BuiltInRegistries.BLOCK.byNameCodec().fieldOf("slag").forGetter(Interaction::slag),

            InteractedWith.CODEC.listOf().fieldOf("interactions").forGetter(Interaction::interactions),
            InteractedFrom.CODEC.listOf().fieldOf("replacement_for").forGetter(Interaction::replacementFor)
    ).apply(instance, Interaction::new));

    public boolean interact(Level level, FluidState currentState, BlockPos currentPos, BlockPos dirPos) {
        for (InteractedWith with : this.interactions) {
            if (with.requireSource() && !currentState.isSource()) continue;
            if (with.target().is(level.getBlockState(dirPos))) {
                if (with.replace().isPresent()) Interaction.fluidMix(level, currentPos, with.getReplaceState());
                if (with.target().replace().isPresent())
                    Interaction.fluidMix(level, dirPos, with.target().getReplaceState());
                return false;
            }
        }
        FluidState dirState = level.getFluidState(dirPos);
        boolean fluidCheck = !dirState.isEmpty() && !dirState.getType().isSame(this.fluid);//!dirState.is(this.fluid);
        if (currentState.isSource()) {
            if (fluidCheck) Interaction.fluidMix(level, dirPos, this.slag.defaultBlockState());
            return false;
        } else return fluidCheck;
    }

    public void registerReplacementFor() {
        this.replacementFor().forEach(from -> {
            Objects.requireNonNull(from.getFluidType());
            FluidInteractionRegistry.addInteraction(from.getFluidType(), new FluidInteractionRegistry.InteractionInformation(
                    (Level level, BlockPos pos, BlockPos dirPos, FluidState state) ->
                            this.process(from, level, state, dirPos),
                    from.getReplaceState()));
        });
    }

    public boolean process(InteractedFrom from, Level level, FluidState state, BlockPos dirPos) {
        if (from.requireSource() && !state.isSource()) return false;
        if (from.target().is(level.getBlockState(dirPos))) {
            Interaction.fluidMix(level, dirPos, this.fluidBlockState());
            return from.isReplaceable();
        }
        return false;
    }

    public BlockState fluidBlockState() {
        return this.fluid.defaultFluidState().createLegacyBlock();
    }

    public static void fluidMix(Level level, BlockPos pos, BlockState state) {
        level.setBlockAndUpdate(pos, state);
        if (level.getServer() != null) {
            if (level.getServer().getLevel(level.dimension()) != null) {
                Objects.requireNonNull(level.getServer().getLevel(level.dimension())).sendParticles(
                        ParticleTypes.LARGE_SMOKE,
                        (double) pos.getX() + 0.5d,
                        (double) pos.getY() + 1.0d,
                        (double) pos.getZ() + 0.5d,
                        8, 0.3d, 0.0d, 0.3d, 0d
                );
            }
        }
        level.playSound(null, pos, SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 0.5f, 2.1f);
    }
}
