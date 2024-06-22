package zeh.mingle;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.fluids.FluidInteractionRegistry;
import net.neoforged.neoforge.fluids.FluidType;
import zeh.mingle.registry.Interaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EventBusSubscriber
public class AllFluidsInteractions {

    public static List<Interaction> LIST = new ArrayList<>();

    @SubscribeEvent
    public static void removeNew(final ServerStoppingEvent event) {
        Mingle.LOGGER.info("..................................");
        Mingle.LOGGER.info("..................................");
        Mingle.LOGGER.info("..................................");
    }

    @SubscribeEvent
    public static void reload(final AddReloadListenerEvent event) {
        Mingle.LOGGER.info("+++++++++++++++++++++++++++++++++");
        Mingle.LOGGER.info("+++++++++++++++++++++++++++++++++");
        Mingle.LOGGER.info("+++++++++++++++++++++++++++++++++");
    }

    @SubscribeEvent
    public static void addNew(final ServerAboutToStartEvent event) {
        Mingle.LOGGER.info("------------------------");
        Mingle.LOGGER.info("------------------------");
        Mingle.LOGGER.info("------------------------");

        if (event.getServer().registryAccess().registry(Interaction.KEY).isPresent()) {
            Registry<Interaction> reg = event.getServer().registryAccess().registry(Interaction.KEY).get();
            for (Interaction entry : reg) {
                if (!LIST.contains(entry)) {
                    LIST.add(entry);
                    FluidInteractionRegistry.addInteraction(entry.fluid().getFluidType(), new FluidInteractionRegistry.InteractionInformation(
                            (Level level, BlockPos pos, BlockPos dirPos, FluidState state) ->
                                    entry.interact(level, state, pos, dirPos),
                            entry.slag().defaultBlockState()
                    ));
                    entry.registerReplacementFor();

                }
            }
        }
        net.neoforged.neoforge.fluids.FluidInteractionRegistry.INTERACTIONS;
    }

    private static final Map<FluidType, List<FluidInteractionRegistry.InteractionInformation>> INTERACTIONS = new HashMap<>();

}
