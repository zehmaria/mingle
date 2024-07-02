package zeh.mingle.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import zeh.mingle.AllFluids;
import zeh.mingle.fluids.MoltenFlowingFluid;

import java.util.Objects;

public class SlagBlock extends HalfTransparentBlock {

    public SlagBlock(int color, BlockBehaviour.Properties props) {
        super(props);
        this.color = color;
    }

    private  final int color;

    @Override
    protected float getShadeBrightness(@NotNull BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos) {
        return 0.8F;
    }

    @Override
    protected boolean useShapeForLightOcclusion(@NotNull BlockState state) {
        return true;
    }

    @Override
    protected boolean propagatesSkylightDown(@NotNull BlockState state, @NotNull BlockGetter reader, @NotNull BlockPos pos) {
        return true;
    }

    @Override
    public Integer getBeaconColorMultiplier(@NotNull BlockState state, @NotNull LevelReader world, @NotNull BlockPos pos, @NotNull BlockPos beaconPos) {
        return color;
    }

    @Override
    public void stepOn(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull Entity entity) {
        super.stepOn(level, pos, state, entity);
        breakIt(level, pos);
    }

    @Override
    public void neighborChanged(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Block neighborBlock, @NotNull BlockPos neighborPos, boolean movedByPiston) {
        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, movedByPiston);
        breakIfAlone(level, pos);
    }

    public void breakIfAlone(Level level, BlockPos pos) {
        boolean hasMolten = false;
        for (Direction dir : Direction.values()) {
            if (level.getFluidState(pos.relative(dir)).getType() instanceof MoltenFlowingFluid) hasMolten = true;
        }
        if (!hasMolten) breakIt(level, pos);
    }

    public void breakIt(Level level, BlockPos pos){
        level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
        if (level.getServer() != null) {
            if (level.getServer().getLevel(level.dimension()) != null) {
                Objects.requireNonNull(level.getServer().getLevel(level.dimension())).sendParticles(
                        ParticleTypes.DUST_PLUME,
                        (double) pos.getX() + 0.5d,
                        (double) pos.getY() + 1.0d,
                        (double) pos.getZ() + 0.5d,
                        8, 0.3d, 0.0d, 0.3d, 0d
                );
            }
        }
        level.playSound(null, pos, SoundEvents.GLASS_BREAK, SoundSource.BLOCKS, 0.5f, 2.1f);
    }

}
