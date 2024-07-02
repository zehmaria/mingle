package zeh.mingle.fluids;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.NotNull;
import zeh.mingle.AllFluids;
import zeh.mingle.AllTags;
import zeh.mingle.Mingle;
import zeh.mingle.registry.Interacted;

import java.util.Objects;

public abstract class MoltenWasteFlowingFluid extends MoltenFlowingFluid {
    protected MoltenWasteFlowingFluid(Properties properties) {
        super(properties);
        waste = new Interacted(AllFluids.INSTANCE.WASTE.id);
        slag = new Interacted(AllFluids.INSTANCE.WASTE.slagId);
    }

    private final Interacted waste;
    private final Interacted slag;

    private boolean wasteIt(Level level, BlockPos pos) {
        boolean wasted = false;
        for (Direction dir : LiquidBlock.POSSIBLE_FLOW_DIRECTIONS) {
            BlockPos dirPos = pos.relative(dir);
            FluidState dirState = level.getFluidState(dirPos);
            if (dirState.getType() instanceof MoltenFlowingFluid && !(dirState.getType() instanceof MoltenWasteFlowingFluid)) {
                waste.replaceAt(level, dirPos);
                wasted = true;
            }
        }
        BlockPos above = pos.above();
        FluidState aboveState = level.getFluidState(above);
        if (aboveState.getType() instanceof MoltenFlowingFluid && !(aboveState.getType() instanceof MoltenWasteFlowingFluid)) {
            if (aboveState.isSource()) waste.replaceAt(level, above);
            else slag.replaceAt(level, above);
            wasted = true;
        }
        return wasted;
    }

    @Override
    protected void spread(Level level, BlockPos pos, FluidState state) {
        wasteIt(level, pos);
        super.spread(level, pos, state);
    }

    @Override
    public int getDropOff(@NotNull LevelReader level) {
        return 1;
    }

    public static class Flowing extends MoltenWasteFlowingFluid {
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

    public static class Source extends MoltenWasteFlowingFluid {
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