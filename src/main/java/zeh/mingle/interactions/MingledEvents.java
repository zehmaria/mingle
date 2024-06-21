package zeh.mingle.interactions;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import zeh.mingle.Mingle;
import zeh.mingle.interactions.network.InteractionsPacket;
import zeh.mingle.interactions.network.PayloadHandler;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class MingledEvents {

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        /*
        final PayloadRegistrar registrar = event.registrar(Mingle.ID).versioned("1.0.0");
        Mingle.LOGGER.info("bbbbbbbbbbbbbbbbbbbbbbb");
        Mingle.LOGGER.info("bbbbbbbbbbbbbbbbbbbbbbb");
        Mingle.LOGGER.info("bbbbbbbbbbbbbbbbbbbbbbb");
        Mingle.LOGGER.info("bbbbbbbbbbbbbbbbbbbbbbb");
        Mingle.LOGGER.info("bbbbbbbbbbbbbbbbbbbbbbb");
        Mingle.LOGGER.info("bbbbbbbbbbbbbbbbbbbbbbb");

        //Sync (optional)
        Interactions.ENGINE.subscribeAsSyncable(InteractionsPacket::new);
        registrar.playToClient(InteractionsPacket.TYPE, InteractionsPacket.STREAM_CODEC,
                PayloadHandler.Client.getInstance()::handleInteractions);

         */
    }

}
