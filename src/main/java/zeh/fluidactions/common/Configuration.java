package zeh.fluidactions.common;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.conversion.ObjectConverter;
import com.tterrag.registrate.util.entry.FluidEntry;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.IConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.tuple.Pair;
import zeh.fluidactions.FluidActions;

@Mod.EventBusSubscriber(modid = FluidActions.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Configuration {

	public static ForgeConfigSpec COMMON_CONFIG;
	public static ForgeConfigSpec.BooleanValue FLUID_IRON;

	public static final ForgeConfigSpec INTERACTIONS_SPEC;
	public static final Interactions INTERACTIONS;
	public static List<Interaction> interactionList;

	private static final ObjectConverter CONVERTER = new ObjectConverter();

	public static class Interactions {
		public Interactions(ForgeConfigSpec.Builder builder) {
			builder.comment("#### Explanation ")
					.comment(" ")
					.comment(" before = requires a valid block or blockTag, or the String \"ignore\"")
					.comment(" molten = requires a valid fluid (non-tag), or else it ignores the whole interaction")
					.comment(" after = requires a valid block or blockTag, or the String \"ignore\"")
					.comment(" slag = requires a valid block or blockTag")
					.comment(" ")
					.comment(" before + lava source = molten (consumes lava)")
					.comment(" molten + water source = after (consumes water)")
					.comment(" flowing fluid + molten = slag (does not destroy molten)")
					.comment(" any fluid + flowing molten = slag (does not destroy molten)")
					.comment(" ")
					.comment("#### List of interactions ")

					.define("interactions", new ArrayList<>());
			builder.build();
		}
	}

	static {
		final Pair<Interactions, ForgeConfigSpec> groupsPair = new ForgeConfigSpec.Builder().configure(Interactions::new);
		INTERACTIONS_SPEC = groupsPair.getRight();
		INTERACTIONS = groupsPair.getLeft();

		//ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();

		//COMMON_BUILDER.comment("Which fluids to add").push("fluids");

		//FLUID_IRON = COMMON_BUILDER.define("moltenIron", false);

		//COMMON_BUILDER.pop();

		//COMMON_CONFIG = COMMON_BUILDER.build();
	}

	public static void setup() {
		//ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Configuration.COMMON_CONFIG);

		String name = FluidActions.ID + "-interactions.toml";
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Configuration.INTERACTIONS_SPEC, name);
		//File df = new File(FMLPaths.GAMEDIR.get().resolve(FMLConfig.defaultConfigPath()) + "/" + name);
		File df = new File(FMLPaths.GAMEDIR.get().resolve("config") + "/" + name);

		if (!df.exists()) {
			try {
				FileUtils.copyInputStreamToFile(Objects.requireNonNull(FluidActions.class.getClassLoader().getResourceAsStream(name)), df);
			} catch (IOException e) { FluidActions.LOGGER.error("Error creating default config for " + name); }
		}
	}

	private static synchronized void load(final ModConfig config) {
		if (config.getModId().equals(FluidActions.ID)) {
			IConfigSpec<?> spec = config.getSpec();
			CommentedConfig configData = config.getConfigData();
			if (spec == INTERACTIONS_SPEC) {
				Configuration.interactionList = CONVERTER.toObject(configData, InteractionsConfig::new).interactions;
			}
		}
	}

	@SubscribeEvent
	@SuppressWarnings("unused")
	static void configLoad(final ModConfigEvent.Loading evt) {
		load(evt.getConfig());
	}
	@SubscribeEvent
	@SuppressWarnings("unused")
	static void configReload(final ModConfigEvent.Reloading evt) { load(evt.getConfig()); }

}