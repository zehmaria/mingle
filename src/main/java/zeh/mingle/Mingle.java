package zeh.mingle;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import org.slf4j.Logger;
import zeh.mingle.common.Configuration;
import zeh.mingle.foundation.data.Providers;
import zeh.mingle.registry.Interaction;

@Mod(Mingle.ID)
public class Mingle {

    public static final String ID = "mingle";
    public static final String NAME = "Mingle";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Mingle(ModContainer modContainer, IEventBus modEventBus) {
        AllTags.init();
        AllCreativeModeTabs.register(modEventBus);

        AllFluids.register(modEventBus);

        Configuration.setup(modContainer);

        modEventBus.addListener(Interaction::register);
        modEventBus.addListener(AllCreativeModeTabs::addCreative);
        modEventBus.addListener(Providers::generate);
        modEventBus.addListener(Mingle::init);

        if (FMLEnvironment.dist == Dist.CLIENT) {
            modEventBus.addListener(Mingle::addBlockColors);
            modEventBus.addListener(Mingle::addItemColors);
        }
    }

    public static void init(final FMLCommonSetupEvent event) {
        event.enqueueWork(AllFluids::registerBucketDispenserBehavior);
    }

    public static void addBlockColors(RegisterColorHandlersEvent.Block event) {
        for (AllFluids.INSTANCE entry : AllFluids.INSTANCE.values()) {
            event.register((i, l, p, t) -> entry.fog, entry.SLAG_BLOCK.get());
        }
    }

    public static void addItemColors(RegisterColorHandlersEvent.Item event) {
        for (AllFluids.INSTANCE entry : AllFluids.INSTANCE.values()) {
            event.register((i, t) -> t == 1 ? entry.color : 0xffffffff, entry.BUCKET_ITEM.get());

            event.register((i, t) ->  entry.fog, entry.SLAG_ITEM.get());        }
    }

    public static ResourceLocation as(String path) {
        return ResourceLocation.fromNamespaceAndPath(ID, path);
    }

}
