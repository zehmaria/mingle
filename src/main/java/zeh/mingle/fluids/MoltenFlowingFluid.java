package zeh.mingle.fluids;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import org.jetbrains.annotations.NotNull;

public abstract class MoltenFlowingFluid extends BaseFlowingFluid {
    protected MoltenFlowingFluid(Properties properties) {
        super(properties);
    }

    @Override
    public void animateTick(Level level, BlockPos pos, @NotNull FluidState state, @NotNull RandomSource random) {
        BlockPos above = pos.above();
        if (level.getBlockState(above).isAir() && !level.getBlockState(above).isSolidRender(level, above)) {
            if (random.nextInt(100) == 0) {
                double d0 = (double) pos.getX() + random.nextDouble();
                double d1 = (double) pos.getY() + 1.0D;
                double d2 = (double) pos.getZ() + random.nextDouble();

                level.addParticle(ParticleTypes.LAVA, d0, d1, d2, 0.0D, 0.0D, 0.0D);
                level.playLocalSound(d0, d1, d2, SoundEvents.LAVA_POP, SoundSource.BLOCKS, 0.2F + random.nextFloat() * 0.2F, 0.9F + random.nextFloat() * 0.15F, false);
            }

            if (random.nextInt(200) == 0) {
                level.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.LAVA_AMBIENT, SoundSource.BLOCKS, 0.2F + random.nextFloat() * 0.2F, 0.9F + random.nextFloat() * 0.15F, false);
            }
        }
    }

    @Override
    public ParticleOptions getDripParticle() {
        return null;
    }

    @Override
    public int getSlopeFindDistance(@NotNull LevelReader level) {
        return level.dimensionType().ultraWarm() ? 2 : 1;
    }

    @Override
    public int getDropOff(@NotNull LevelReader level) {
        return level.dimensionType().ultraWarm() ? 2 : 3;
    }

    @Override
    protected boolean canBeReplacedWith(@NotNull FluidState state, @NotNull BlockGetter
            level, @NotNull BlockPos pos, @NotNull Fluid fluidIn, @NotNull Direction direction) {
        //return fluidIn instanceof MoltenWasteFlowingFluid;
        return false;
    }

    @Override
    public int getTickDelay(@NotNull LevelReader level) {
        return level.dimensionType().ultraWarm() ? 10 : 30;
    }


    public static class Flowing extends MoltenFlowingFluid {
        public Flowing(Properties properties) {
            super(properties);
            registerDefaultState(getStateDefinition().any().setValue(LEVEL, 7));
        }

        protected void createFluidStateDefinition(StateDefinition.@NotNull Builder<Fluid, FluidState> builder) {
            super.createFluidStateDefinition(builder);
            builder.add(LEVEL);
        }

        public int getAmount(FluidState state) {
            return state.getValue(LEVEL);
        }

        public boolean isSource(@NotNull FluidState state) {
            return false;
        }
    }

    public static class Source extends MoltenFlowingFluid {
        public Source(Properties properties) {
            super(properties);
        }

        public int getAmount(@NotNull FluidState state) {
            return 8;
        }

        public boolean isSource(@NotNull FluidState state) {
            return true;
        }
    }
}