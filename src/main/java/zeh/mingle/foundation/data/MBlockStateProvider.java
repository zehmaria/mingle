package zeh.mingle.foundation.data;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import zeh.mingle.AllFluids;
import zeh.mingle.Mingle;

public class MBlockStateProvider extends BlockStateProvider {

    public MBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, Mingle.ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        for (AllFluids.INSTANCE entry : AllFluids.INSTANCE.values()) {
            if (entry.FLUID_TYPE.get() instanceof AllFluids.MoltenFluidType fluidType) {
                simpleBlock(entry.BLOCK.get(), models().getBuilder(entry.BLOCK.getId().getPath()).texture("particle", fluidType.stillTexture));
                simpleBlock(entry.SLAG_BLOCK.get(), models().withExistingParent(entry.SLAG_BLOCK.getId().getPath(), "block/cube_all")
                        .texture("particle", Mingle.as("block/fluid/molten_still"))
                        .texture("all", Mingle.as("block/fluid/molten_slag_crystal"))
                        .texture("inside", Mingle.as("block/fluid/molten_slag"))
                        .renderType("translucent")
                        .element().from(2, 2, 2).to(14, 14, 14)
                        .allFaces((dir, builder) -> builder.texture("#all").tintindex(0))
                        .end()
                        .element().from(0, 0, 0).to(16, 16, 16)
                        .allFaces((dir, builder) -> builder.texture("#inside").tintindex(2).cullface(dir))
                        .end()
                );
            }
        }
    }

}
