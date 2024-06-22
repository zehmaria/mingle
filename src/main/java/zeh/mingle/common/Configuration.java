package zeh.mingle.common;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.ModConfigSpec;
import zeh.mingle.Mingle;

@EventBusSubscriber(modid = Mingle.ID, bus = EventBusSubscriber.Bus.MOD)
public class Configuration {

    public static ModConfigSpec COMMON_CONFIG;
    public static ModConfigSpec.BooleanValue FLUID_IRON;

    static {
        //ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();

        //COMMON_BUILDER.comment("Which fluids to add").push("fluids");

        //FLUID_IRON = COMMON_BUILDER.define("moltenIron", false);

        //COMMON_BUILDER.pop();

        //COMMON_CONFIG = COMMON_BUILDER.build();
    }

    public static void setup(ModContainer modContainer) {
        // modContainer.registerConfig(ModConfig.Type.COMMON, Configuration.COMMON_CONFIG);
    }

}