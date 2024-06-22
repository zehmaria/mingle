package zeh.mingle.foundation.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.PackOutput;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import zeh.mingle.AllFluids;
import zeh.mingle.Mingle;
import zeh.mingle.registry.*;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class MDatapackBuiltinEntriesProvider extends DatapackBuiltinEntriesProvider {

    public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Interaction.KEY, MDatapackBuiltinEntriesProvider::bootstrap);

    public MDatapackBuiltinEntriesProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
        super(output, provider, BUILDER, Set.of(Mingle.ID));
    }

    //public static final ResourceKey<Interactions> OAK = ResourceKey.create(Interactions.KEY, ResourceLocation.fromNamespaceAndPath(Mingle.ID, "test2"));

    public static void bootstrap(BootstrapContext<Interaction> context) {

        Mingled.createKey("molten_copper")
                .setFluid(AllFluids.INSTANCE.COPPER.STILL.get())
                .setSlag(Blocks.COBBLESTONE)
                .addWith(Mingled.with(true, Mingled.block("#c:storage_blocks/iron"), Mingled.consumeWater()))
                .addWith(Mingled.with(true, Mingled.consumeLava()))
                .addFrom(Mingled.from(Mingled.block("#c:storage_blocks/iron"), Mingled.consumeLava()))
                .build(context);

    }

}
