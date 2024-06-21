package zeh.mingle.foundation.data;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import zeh.mingle.AllFluids;
import zeh.mingle.AllTags;
import zeh.mingle.Mingle;

import java.util.concurrent.CompletableFuture;

public class MFluidTagsProvider extends FluidTagsProvider {

    protected MFluidTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, Mingle.ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider pProvider) {
        for (Holder<Fluid> entry : AllFluids.FLUIDS.getEntries()) {
            tag(AllTags.AllFluidTags.FLUID_INTERACTIONS_EXCLUSION.tag).add(entry.value());
        }
        for (AllFluids.INSTANCE entry : AllFluids.INSTANCE.values()) {
            TagKey<Fluid> key = TagKey.create(BuiltInRegistries.FLUID.key(), ResourceLocation.fromNamespaceAndPath("c", entry.id));
            tag(key).add(entry.STILL.get());
            tag(key).add(entry.FLOWING.get());
        }
    }

}
