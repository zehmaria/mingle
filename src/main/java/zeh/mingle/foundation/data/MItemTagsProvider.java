package zeh.mingle.foundation.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import zeh.mingle.AllFluids;
import zeh.mingle.Mingle;

import java.util.concurrent.CompletableFuture;

public class MItemTagsProvider extends ItemTagsProvider {

    protected MItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, CompletableFuture<TagLookup<Block>> tagLookup, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, provider, tagLookup, Mingle.ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider pProvider) {
        for (AllFluids.INSTANCE entry : AllFluids.INSTANCE.values()) {
            TagKey<Item> key = TagKey.create(BuiltInRegistries.ITEM.key(), ResourceLocation.fromNamespaceAndPath("c", "buckets/" + entry.name));
            tag(key).add(entry.BUCKET_ITEM.get().asItem());
        }
    }

}
