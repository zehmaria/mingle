package zeh.fluidactions;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

import net.minecraft.resources.ResourceLocation;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import zeh.fluidactions.common.Configuration;
import zeh.fluidactions.foundation.data.Enroll;

@Mod(FluidActions.ID)
public class FluidActions {

    public static final String ID = "fluidactions";
    public static final String NAME = "Fluid Interactions";

    public static final Logger LOGGER = LogUtils.getLogger();
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    public static final Enroll REGISTRATE = Enroll.create(ID);

    public FluidActions() {
        onCtor();
    }

    public static void onCtor() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        REGISTRATE.registerEventListeners(modEventBus);

        AllTags.init();
        AllCreativeModeTabs.register(modEventBus);

        AllFluids.register();

        Configuration.setup();

        modEventBus.addListener(FluidActions::init);
    }

    public static void init(final FMLCommonSetupEvent event) {
        event.enqueueWork(AllFluidsInteractions::registerFluidInteractions);
    }

    public static ResourceLocation asResource(String path) {
        return new ResourceLocation(ID, path);
    }

}
