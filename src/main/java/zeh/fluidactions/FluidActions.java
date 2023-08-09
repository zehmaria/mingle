package zeh.fluidactions;

import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import zeh.fluidactions.common.Configuration;
import zeh.fluidactions.foundation.data.Enroll;
import zeh.fluidactions.foundation.data.LangMerger;
import zeh.fluidactions.foundation.data.TagGen;
import zeh.fluidactions.foundation.item.ItemDescription;
import zeh.fluidactions.foundation.item.TooltipHelper.Palette;


@Mod(FluidActions.ID)
public class FluidActions {

    public static final String ID = "fluidactions";
    public static final String NAME = "Fluid Interactions";

    public static final Logger LOGGER = LogUtils.getLogger();
    public static final Enroll REGISTRATE = Enroll.create(ID);

    static {
        REGISTRATE.setTooltipModifierFactory(item -> new ItemDescription.Modifier(item, Palette.STANDARD));
    }

    public FluidActions() {
        onCtor();
    }

    public static void onCtor() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        REGISTRATE.registerEventListeners(modEventBus);

        AllTags.init();

        AllBlocks.register();
        AllItems.register();

        AllFluids.register();
        AllBlockEntityTypes.register();
        AllCreativeModeTabs.register(modEventBus);

        Configuration.setup();

        modEventBus.addListener(FluidActions::init);
        modEventBus.addListener(EventPriority.LOW, FluidActions::gatherData);
    }

    public static void init(final FMLCommonSetupEvent event) {
        event.enqueueWork(AllFluidsInteractions::registerFluidInteractions);
    }

    public static void gatherData(GatherDataEvent event) {
        TagGen.datagen();
        DataGenerator gen = event.getGenerator();
        PackOutput output = gen.getPackOutput();
        if (event.includeClient()) {
            LangMerger.attachToRegistrateProvider(gen, output);
        }
    }

    public static ResourceLocation asResource(String path) {
        return new ResourceLocation(ID, path);
    }

}
