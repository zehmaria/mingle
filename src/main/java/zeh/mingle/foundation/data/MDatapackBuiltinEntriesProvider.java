package zeh.mingle.foundation.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.PackOutput;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import zeh.mingle.Mingle;
import zeh.mingle.registry.DataRegistryTest;
import zeh.mingle.registry.DataTest;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class MDatapackBuiltinEntriesProvider extends DatapackBuiltinEntriesProvider {


    public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(DataRegistryTest.KEY, MDatapackBuiltinEntriesProvider::bootstrap);

    public MDatapackBuiltinEntriesProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
        super(output, provider, BUILDER, Set.of(Mingle.ID));
    }

    public static final ResourceKey<DataTest> OAK = ResourceKey.create(DataRegistryTest.KEY, ResourceLocation.fromNamespaceAndPath(Mingle.ID, "test2"));

    public static void bootstrap(BootstrapContext<DataTest> context) {
        context.register(OAK, new DataTest(Blocks.OAK_PLANKS, "testing"));
    }

}
