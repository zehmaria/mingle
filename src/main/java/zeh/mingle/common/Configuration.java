package zeh.mingle.common;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.conversion.ObjectConverter;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.config.IConfigSpec;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.tuple.Pair;
import zeh.mingle.Mingle;

@EventBusSubscriber(modid = Mingle.ID, bus = EventBusSubscriber.Bus.MOD)
public class Configuration {

	public static ModConfigSpec COMMON_CONFIG;
	public static ModConfigSpec.BooleanValue FLUID_IRON;

	public static final ModConfigSpec INTERACTIONS_SPEC;
	public static final Interactions INTERACTIONS;
	public static List<Interaction> interactionList;

	private static final ObjectConverter CONVERTER = new ObjectConverter();

	public static class Interactions {
		public Interactions(ModConfigSpec.Builder builder) {
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
		final Pair<Interactions, ModConfigSpec> groupsPair = new ModConfigSpec.Builder().configure(Interactions::new);
		INTERACTIONS_SPEC = groupsPair.getRight();
		INTERACTIONS = groupsPair.getLeft();

		//ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();

		//COMMON_BUILDER.comment("Which fluids to add").push("fluids");

		//FLUID_IRON = COMMON_BUILDER.define("moltenIron", false);

		//COMMON_BUILDER.pop();

		//COMMON_CONFIG = COMMON_BUILDER.build();
	}

	public static void setup(ModContainer modContainer) {
		//ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Configuration.COMMON_CONFIG);

		String name = Mingle.ID + "-interactions.toml";
		modContainer.registerConfig(ModConfig.Type.COMMON, Configuration.INTERACTIONS_SPEC, name);
		File df = new File(FMLPaths.GAMEDIR.get().resolve("config") + "/" + name);

		if (!df.exists()) {
			try {
				FileUtils.copyInputStreamToFile(Objects.requireNonNull(Mingle.class.getClassLoader().getResourceAsStream(name)), df);
			} catch (IOException e) { Mingle.LOGGER.error("Error creating default config for " + name); }
		}

		Mingle.LOGGER.info("config setup");
	}

	private static synchronized void load(final ModConfig config) {
		if (config.getModId().equals(Mingle.ID)) {
			IConfigSpec<?> spec = config.getSpec();
			CommentedConfig configData = config.getConfigData();
			if (spec == INTERACTIONS_SPEC) {
				Configuration.interactionList = CONVERTER.toObject(configData, InteractionsConfig::new).interactions;
			}
		}
	}
	@SubscribeEvent
	@SuppressWarnings("unused")
	public static void configLoad(final ModConfigEvent.Loading evt) {
		Mingle.LOGGER.info("config load");
		load(evt.getConfig());
	}
	@SubscribeEvent
	@SuppressWarnings("unused")
	public static void configReload(final ModConfigEvent.Reloading evt) {
		Mingle.LOGGER.info("config reload");
		load(evt.getConfig());
	}

}