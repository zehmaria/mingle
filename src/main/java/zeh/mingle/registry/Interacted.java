package zeh.mingle.registry;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.fluids.FluidType;
import zeh.mingle.Mingle;

import java.util.Objects;
import java.util.Optional;

public record Interacted(String name) {

    public static final Codec<Interacted> CODEC = Codec.STRING.xmap(Interacted::new, Interacted::name);

    public ResourceLocation getRL() { return ResourceLocation.parse(isTag() ? name.substring(1) : name); }
    public boolean isValid() { return !getBlockState().isAir() || name.equals("minecraft:air"); }
    public boolean isTag() { return name.startsWith("#"); }
    public TagKey<Block> getBlockTag() { return TagKey.create(BuiltInRegistries.BLOCK.key(), getRL()); }
    public Block getBlock() { return BuiltInRegistries.BLOCK.get(getRL()); }
    public boolean isFluid() { return !getBlock().defaultBlockState().getFluidState().isEmpty(); }
    public TagKey<Fluid> getFluidTag() { return TagKey.create(BuiltInRegistries.FLUID.key(), getRL()); }
    public FluidType getFluidType() { return getBlock().defaultBlockState().getFluidState().getFluidType(); }
    public Fluid getType() { return getBlock().defaultBlockState().getFluidState().getType(); }

    public BlockState getBlockState() {
        if (isTag()) {
            Optional<Holder<Block>> any = BuiltInRegistries.BLOCK.getOrCreateTag(getBlockTag()).stream().findAny();
            return any.map(blockHolder -> blockHolder.value().defaultBlockState()).orElse(Blocks.AIR.defaultBlockState());
        } else return getBlock().defaultBlockState();
    }

    public BlockState getBlockState(Level level, BlockPos pos) {
        FluidState state = level.getFluidState(pos);
        if (!state.isSource() && !state.isEmpty()) {
            if (getType() instanceof FlowingFluid type) {
                int lvl = state.getValue(FlowingFluid.LEVEL);
                boolean fall = state.getValue(FlowingFluid.FALLING);
                return type.getFlowing(lvl, fall).createLegacyBlock();
            }
        }
        return getBlockState();
    }

    public boolean isRule() { return name.startsWith("@"); }
    public boolean isTemperature() { return name.startsWith("@temperature"); }
    public boolean isDensity() { return name.startsWith("@density"); }
    public boolean isViscosity() { return name.startsWith("@viscosity"); }
    public String getRule() throws Exception {
        if (isTemperature()) return name.replace("@temperature", "");
        if (isDensity()) return name.replace("@density", "");
        if (isViscosity()) return name.replace("@viscosity", "");
        throw new Exception("@type[?]number, " + name + ", @type is an invalid requirement");
    }
    public int getRuleValue() throws Exception { return Integer.parseInt(getRule().substring(1)); }
    public int getRuleValue(FluidState state) throws Exception {
        if (isTemperature()) return state.getType().getFluidType().getTemperature();
        if (isDensity()) return state.getType().getFluidType().getDensity();
        if (isViscosity()) return state.getType().getFluidType().getViscosity();
        throw new Exception("@type[?]number, " + name + ", target FluidState does not have @type property, " + state.toString());
    }
    public boolean testRule(FluidState state) {
        if (state.isEmpty()) return false;
        else try {
            if (getRule().startsWith("!")) return getRuleValue(state) != getRuleValue();
            else if (getRule().startsWith(">")) return getRuleValue(state) > getRuleValue();
            else if (getRule().startsWith("<")) return getRuleValue(state) < getRuleValue();
            else throw new Exception("@type[?]number, "  + name + ", [?] is an invalid requirement");
        } catch (Exception e) {
            Mingle.LOGGER.warn("Invalid @RuleSet in Mingle Fluid Interactions DataPack entry", e);
            return false;
        }
    }

    public boolean is(FluidState state) {
        if (isRule()) return testRule(state);
        else if (isTag()) return state.is(getFluidTag());
        else if (!isValid()) return false;
        else return state.getType().isSame(getType());
    }

    public boolean is(BlockState state) {
        if (isTag()) return state.is(getBlockTag());
        else if (!isValid()) return false;
        else return state.is(getBlock());
    }

    public void replaceAt(Level level, BlockPos pos) { if (isValid()) setBlock(level, pos, getBlockState(level, pos)); }
    public static void setBlock(Level level, BlockPos pos, BlockState state) {
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
