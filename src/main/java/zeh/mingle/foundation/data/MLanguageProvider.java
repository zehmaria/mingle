package zeh.mingle.foundation.data;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.LanguageProvider;
import zeh.mingle.AllFluids;
import zeh.mingle.Mingle;

public class MLanguageProvider extends LanguageProvider {

    public MLanguageProvider(PackOutput output, String locale) {
        super(output, Mingle.ID, locale);
    }

    @Override
    protected void addTranslations() {
        for (AllFluids.INSTANCE entry : AllFluids.INSTANCE.values()) {
            addFluid(entry, entry.lang);
        }
    }

    protected void addFluid(AllFluids.INSTANCE entry, String name) {
        add(entry.FLUID_TYPE.get().getDescriptionId(), name);
        add(entry.BLOCK.get(), name);
        add(entry.BUCKET_ITEM.get(), name + " Bucket");
        addTag(() -> ItemTags.create(Tags.Items.BUCKETS.location().withSuffix("/" + entry.id)), name + " Buckets");
        TagKey<Fluid> key = TagKey.create(BuiltInRegistries.FLUID.key(), ResourceLocation.fromNamespaceAndPath("c", entry.id));
        addTag(() -> key, name);
    }

}
