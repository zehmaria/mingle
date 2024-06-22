package zeh.mingle.foundation.data;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.loaders.DynamicFluidContainerModelBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.internal.versions.neoforge.NeoForgeVersion;
import zeh.mingle.AllFluids;
import zeh.mingle.Mingle;

public class MItemModelsProvider extends ItemModelProvider {

    public MItemModelsProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Mingle.ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        for (AllFluids.INSTANCE entry : AllFluids.INSTANCE.values()) {
            withExistingParent(entry.BUCKET_ITEM.getId().getPath(), ResourceLocation.fromNamespaceAndPath(NeoForgeVersion.MOD_ID, "item/bucket"))
                    .customLoader(DynamicFluidContainerModelBuilder::begin)
                    .fluid(entry.STILL.get());
        }
    }

}
