package zeh.fluidactions.foundation.data;

import java.util.function.Function;

import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import com.tterrag.registrate.util.nullness.NonnullType;

import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Half;

import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;

public class BlockStateGen {

    // Functions

    public static <T extends Block> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> axisBlockProvider(
            boolean customItem) {
        return (c, p) -> axisBlock(c, p, getBlockModel(customItem, c, p));
    }

    public static <T extends Block> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> directionalBlockProvider(
            boolean customItem) {
        return (c, p) -> p.directionalBlock(c.get(), getBlockModel(customItem, c, p));
    }

    public static <T extends Block> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> directionalBlockProviderIgnoresWaterlogged(
            boolean customItem) {
        return (c, p) -> directionalBlockIgnoresWaterlogged(c, p, getBlockModel(customItem, c, p));
    }

    public static <T extends Block> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> horizontalBlockProvider(
            boolean customItem) {
        return (c, p) -> p.horizontalBlock(c.get(), getBlockModel(customItem, c, p));
    }

    public static <T extends Block> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> horizontalAxisBlockProvider(
            boolean customItem) {
        return (c, p) -> horizontalAxisBlock(c, p, getBlockModel(customItem, c, p));
    }

    public static <T extends Block> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> simpleCubeAll(
            String path) {
        return (c, p) -> p.simpleBlock(c.get(), p.models()
                .cubeAll(c.getName(), p.modLoc("block/" + path)));
    }

    // Utility

    private static <T extends Block> Function<BlockState, ModelFile> getBlockModel(boolean customItem,
                                                                                   DataGenContext<Block, T> c, RegistrateBlockstateProvider p) {
        return $ -> customItem ? AssetLookup.partialBaseModel(c, p) : AssetLookup.standardModel(c, p);
    }

    // Generators

    public static <T extends Block> void directionalBlockIgnoresWaterlogged(DataGenContext<Block, T> ctx,
                                                                            RegistrateBlockstateProvider prov, Function<BlockState, ModelFile> modelFunc) {
        prov.getVariantBuilder(ctx.getEntry())
                .forAllStatesExcept(state -> {
                    Direction dir = state.getValue(BlockStateProperties.FACING);
                    return ConfiguredModel.builder()
                            .modelFile(modelFunc.apply(state))
                            .rotationX(dir == Direction.DOWN ? 180
                                    : dir.getAxis()
                                    .isHorizontal() ? 90 : 0)
                            .rotationY(dir.getAxis()
                                    .isVertical() ? 0 : (((int) dir.toYRot()) + 180) % 360)
                            .build();
                }, BlockStateProperties.WATERLOGGED);
    }

    public static <T extends Block> void axisBlock(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov,
                                                   Function<BlockState, ModelFile> modelFunc) {
        axisBlock(ctx, prov, modelFunc, false);
    }

    public static <T extends Block> void axisBlock(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov,
                                                   Function<BlockState, ModelFile> modelFunc, boolean uvLock) {
        prov.getVariantBuilder(ctx.getEntry())
                .forAllStatesExcept(state -> {
                    Axis axis = state.getValue(BlockStateProperties.AXIS);
                    return ConfiguredModel.builder()
                            .modelFile(modelFunc.apply(state))
                            .uvLock(uvLock)
                            .rotationX(axis == Axis.Y ? 0 : 90)
                            .rotationY(axis == Axis.X ? 90 : axis == Axis.Z ? 180 : 0)
                            .build();
                }, BlockStateProperties.WATERLOGGED);
    }

    public static <T extends Block> void simpleBlock(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov,
                                                     Function<BlockState, ModelFile> modelFunc) {
        prov.getVariantBuilder(ctx.getEntry())
                .forAllStatesExcept(state -> {
                    return ConfiguredModel.builder()
                            .modelFile(modelFunc.apply(state))
                            .build();
                }, BlockStateProperties.WATERLOGGED);
    }

    public static <T extends Block> void horizontalAxisBlock(DataGenContext<Block, T> ctx,
                                                             RegistrateBlockstateProvider prov, Function<BlockState, ModelFile> modelFunc) {
        prov.getVariantBuilder(ctx.getEntry())
                .forAllStates(state -> {
                    Axis axis = state.getValue(BlockStateProperties.HORIZONTAL_AXIS);
                    return ConfiguredModel.builder()
                            .modelFile(modelFunc.apply(state))
                            .rotationY(axis == Axis.X ? 90 : 0)
                            .build();
                });
    }

    public static <T extends Block> void cubeAll(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov,
                                                 String textureSubDir) {
        cubeAll(ctx, prov, textureSubDir, ctx.getName());
    }

    public static <T extends Block> void cubeAll(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov,
                                                 String textureSubDir, String name) {
        String texturePath = "block/" + textureSubDir + name;
        prov.simpleBlock(ctx.get(), prov.models()
                .cubeAll(ctx.getName(), prov.modLoc(texturePath)));
    }

    public static <P extends Block> NonNullBiConsumer<DataGenContext<Block, P>, RegistrateBlockstateProvider> naturalStoneTypeBlock(
            String type) {
        return (c, p) -> {
            ConfiguredModel[] variants = new ConfiguredModel[4];
            for (int i = 0; i < variants.length; i++)
                variants[i] = ConfiguredModel.builder()
                        .modelFile(p.models()
                                .cubeAll(type + "_natural_" + i, p.modLoc("block/palettes/stone_types/natural/" + type + "_" + i)))
                        .buildLast();
            p.getVariantBuilder(c.get())
                    .partialState()
                    .setModels(variants);
        };
    }

    public static <P extends TrapDoorBlock> NonNullBiConsumer<DataGenContext<Block, P>, RegistrateBlockstateProvider> uvLockedTrapdoorBlock(
            P block, ModelFile bottom, ModelFile top, ModelFile open) {
        return (c, p) -> {
            p.getVariantBuilder(block)
                    .forAllStatesExcept(state -> {
                        int xRot = 0;
                        int yRot = ((int) state.getValue(TrapDoorBlock.FACING)
                                .toYRot()) + 180;
                        boolean isOpen = state.getValue(TrapDoorBlock.OPEN);
                        if (!isOpen)
                            yRot = 0;
                        yRot %= 360;
                        return ConfiguredModel.builder()
                                .modelFile(isOpen ? open : state.getValue(TrapDoorBlock.HALF) == Half.TOP ? top : bottom)
                                .rotationX(xRot)
                                .rotationY(yRot)
                                .uvLock(!isOpen)
                                .build();
                    }, TrapDoorBlock.POWERED, TrapDoorBlock.WATERLOGGED);
        };
    }

    public static Function<BlockState, ConfiguredModel[]> mapToAir(@NonnullType RegistrateBlockstateProvider p) {
        return state -> ConfiguredModel.builder()
                .modelFile(p.models()
                        .getExistingFile(p.mcLoc("block/air")))
                .build();
    }

}