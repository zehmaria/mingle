package zeh.mingle.interactions.network;

import net.neoforged.neoforge.network.handling.IPayloadContext;
import zeh.mingle.AllFluidsInteractions;
import zeh.mingle.Mingle;
import zeh.mingle.interactions.Interactions;

public class PayloadHandler {
    public static class Client {
        private static final Client INSTANCE = new Client();

        public static Client getInstance() {
            return INSTANCE;
        }

        public void handleInteractions(InteractionsPacket packet, IPayloadContext context) {
            context.enqueueWork(() -> Interactions.ENGINE.map = packet.map());
            Mingle.LOGGER.info("11111111111111111");
            Mingle.LOGGER.info("11111111111111111");
            Mingle.LOGGER.info("11111111111111111");
            Mingle.LOGGER.info("11111111111111111");
            Mingle.LOGGER.info("11111111111111111");
            Mingle.LOGGER.info("11111111111111111");
            Mingle.LOGGER.info(packet.map().values().stream().toList().toString());
        }
    }
}