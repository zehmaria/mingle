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
                    .storageRaw(entry.storage_raw)
                    .storageBlock(entry.storage_block)
                    .addPair(Mingled.fluid(entry.id, 2),
                            Mingled.fluid("minecraft:lava", 2, "minecraft:magma_block"));
        }

        Mingled.getOrCreateKey("mingle:molten_copper")
                .addPair(Mingled.fluid("mingle:molten_copper", 0, "mingle:molten_brass"),
                        Mingled.fluid("mingle:molten_zinc", 0, "mingle:molten_brass"));
        Mingled.getOrCreateKey("mingle:molten_zinc")
                .addPair(Mingled.fluid("mingle:molten_zinc", 0, "mingle:molten_brass"),
                        Mingled.fluid("mingle:molten_copper", 0, "mingle:molten_brass"));


        Mingled.getOrCreateKey("mingle:molten_copper")
                .addPair(Mingled.fluid("mingle:molten_copper", 0, "mingle:molten_constantan"),
                        Mingled.fluid("mingle:molten_nickel", 0, "mingle:molten_constantan"));
        Mingled.getOrCreateKey("mingle:molten_nickel")
                .addPair(Mingled.fluid("mingle:molten_nickel", 0, "mingle:molten_constantan"),
                        Mingled.fluid("mingle:molten_copper", 0, "mingle:molten_constantan"));


        Mingled.getOrCreateKey("mingle:molten_gold")
                .addPair(Mingled.fluid("mingle:molten_gold", 0, "mingle:molten_electrum"),
                        Mingled.fluid("mingle:molten_silver", 0, "mingle:molten_electrum"));
        Mingled.getOrCreateKey("mingle:molten_silver")
                .addPair(Mingled.fluid("mingle:molten_silver", 0, "mingle:molten_electrum"),
                        Mingled.fluid("mingle:molten_gold", 0, "mingle:molten_electrum"));

        /*
        Mingled.getOrCreateKey("mingle:molten_nickel")
                .addPair(Mingled.fluid("mingle:molten_nickel", 0, "mingle:molten_invar"),
                        Mingled.fluid("2*mingle:molten_iron", 0, "mingle:molten_invar"));

        Mingled.getOrCreateKey("mingle:molten_tin")
                .addPair(Mingled.fluid("mingle:molten_tin", 0, "mingle:molten_bronze"),
                        Mingled.fluid("3*mingle:molten_copper", 0, "mingle:molten_bronze"));*/

        for (AllFluids.INSTANCE entry : AllFluids.INSTANCE.values())  {
            if (!entry.name.equals(AllFluids.INSTANCE.WASTE.name)) {
                Mingled.getOrCreateKey(entry.id)
                        .addPair(Mingled.fluid(entry.id, 1, "mingle:molten_waste"),
                                Mingled.fluid("#mingle:waste_mixing", 2, "mingle:molten_waste"));
            }
            Mingled.getOrCreateKey(entry.id).addPair(Mingled.fluid(entry.id, 0),
                    Mingled.fluid("@temperature!1000", 2, entry.slagId))
                    .addPair(Mingled.fluid(entry.id, 1, entry.slagId),
                            Mingled.fluid("@temperature!1000", 2))
                    .build(context);
        }
    }
}
