package zeh.mingle;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

import net.minecraft.resources.ResourceLocation;

import zeh.mingle.common.Configuration;
import zeh.mingle.foundation.data.Providers;
import zeh.mingle.interactions.Interactions;
import zeh.mingle.interactions.network.InteractionsPacket;
import zeh.mingle.registry.DataRegistryTest;

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

        modEventBus.addListener(DataRegistryTest::register);
        modEventBus.addListener(AllCreativeModeTabs::addCreative);
        modEventBus.addListener(Providers::generate);
        modEventBus.addListener(Mingle::init);

        if (FMLEnvironment.dist == Dist.CLIENT) {
            modEventBus.addListener(Mingle::addItemColors);
        }
    }

    public static void init(final FMLCommonSetupEvent event) {
        //event.enqueueWork(AllFluidsInteractions::registerFluidInteractions);
        event.enqueueWork(AllFluids::registerBucketDispenserBehavior);
    }

    public static void addItemColors(RegisterColorHandlersEvent.Item event) {
        for (AllFluids.INSTANCE entry : AllFluids.INSTANCE.values()) {
            event.register((i, t) -> t == 1 ? entry.color : 0xFFFFFFFF, entry.BUCKET_ITEM.get());
        }
    }

    public static ResourceLocation asResource(String path) {
        return ResourceLocation.fromNamespaceAndPath(ID, path);
    }

}
