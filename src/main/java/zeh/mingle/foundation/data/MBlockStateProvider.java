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
            if(entry.FLUID_TYPE.get() instanceof AllFluids.MoltenFluidType fluidType) {
                simpleBlock(entry.BLOCK.get(), models().getBuilder(entry.BLOCK.getId().getPath()).texture("particle", fluidType.stillTexture));
            }
        }
    }

}
