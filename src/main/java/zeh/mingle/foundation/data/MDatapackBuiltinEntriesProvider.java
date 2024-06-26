package zeh.mingle.foundation.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.PackOutput;
import net.minecraft.data.worldgen.BootstrapContext;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import zeh.mingle.AllFluids;
import zeh.mingle.Mingle;
import zeh.mingle.registry.Interaction;
import zeh.mingle.registry.Mingled;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class MDatapackBuiltinEntriesProvider extends DatapackBuiltinEntriesProvider {

    public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Interaction.KEY, MDatapackBuiltinEntriesProvider::bootstrap);

    public MDatapackBuiltinEntriesProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
        super(output, provider, BUILDER, Set.of(Mingle.ID));
    }

    public static void bootstrap(BootstrapContext<Interaction> context) {
        for (AllFluids.INSTANCE entry : AllFluids.INSTANCE.values()) {
            Mingled.getOrCreateKey(entry.id)
                    .storageRaw(entry.storage_raw) // change this to use temperature instead?
                    .storageBlock(entry.storage_block)
                    .addWaterAndLava();
        }

        Mingled.getOrCreateKey("mingle:molten_gold")
                .addPair(Mingled.fluid("mingle:molten_gold", 0, "mingle:molten_iron"),
                        Mingled.fluid("mingle:molten_copper", 0, "mingle:molten_iron"));

        Mingled.getOrCreateKey("mingle:molten_copper")
                .addPair(Mingled.fluid("mingle:molten_copper", 0, "mingle:molten_iron"),
                        Mingled.fluid("mingle:molten_gold", 0, "mingle:molten_iron"));


        for (AllFluids.INSTANCE entry : AllFluids.INSTANCE.values())  {
            if (!entry.name.equals(AllFluids.INSTANCE.WASTE.name)) {
                Mingled.getOrCreateKey(entry.id)
                        .addPair(Mingled.fluid(entry.id, 1, "mingle:molten_waste"),
                                Mingled.fluid("#mingle:waste_mixing", 2, "mingle:molten_waste"))
                        .addPair(Mingled.fluid(entry.id, 2, "mingle:molten_waste"),
                                Mingled.fluid("mingle:molten_waste", 2))

                        .addPair(Mingled.fluid(entry.id, 0),
                                Mingled.fluid("@temperature!1000", 2, entry.slagId))
                        .addPair(Mingled.fluid(entry.id, 1, "minecraft:stone"),
                                Mingled.fluid("@temperature!1000", 2, entry.slagId));
            }
            Mingled.getOrCreateKey(entry.id).build(context);
        }
    }
}
