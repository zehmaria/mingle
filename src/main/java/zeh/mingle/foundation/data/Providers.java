package zeh.mingle.foundation.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class Providers {
    public static void generate(@NotNull GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> provider = event.getLookupProvider();
        ExistingFileHelper helper = event.getExistingFileHelper();

        MBlockTagsProvider blockTags = new MBlockTagsProvider(output, provider, helper);

        generator.addProvider(event.includeClient(), new MBlockStateProvider(output, helper));
        generator.addProvider(event.includeClient(), new MItemModelsProvider(output, helper));

        generator.addProvider(event.includeClient(), new MLanguageProvider(output, "en_us"));

        generator.addProvider(event.includeServer(), new MFluidTagsProvider(output, provider, helper));
        generator.addProvider(event.includeServer(), blockTags);
        generator.addProvider(event.includeServer(), new MItemTagsProvider(output, provider, blockTags.contentsGetter(), helper));

        generator.addProvider(event.includeServer(), new MDatapackBuiltinEntriesProvider(output, provider));
    }
}
