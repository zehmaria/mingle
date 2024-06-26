package zeh.mingle;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AllCreativeModeTabs {
    private static final DeferredRegister<CreativeModeTab> REGISTER =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Mingle.ID);


    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MINGLE_TAB = REGISTER.register(Mingle.ID,
            () -> CreativeModeTab.builder()
                    .title(Component.literal(Mingle.NAME))
                    .icon(Items.BUCKET::getDefaultInstance)
                    .build());

    public static void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == MINGLE_TAB.getKey()) {
            AllFluids.ITEMS.getEntries().forEach((object) -> event.accept(object.get()));
            //AllFluids.SLAG_BLOCKS.getEntries().forEach((object) -> event.accept(object.get()));
        }
    }

    public static void register(IEventBus modEventBus) {
        REGISTER.register(modEventBus);
    }
}