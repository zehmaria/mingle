package zeh.mingle.interactions;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import zeh.mingle.Mingle;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public class MingledDataReloadListener<T extends MingledData> extends SimpleJsonResourceReloadListener {

    private static final Gson GSON = new Gson();
    public Map<ResourceLocation,T> map = new HashMap<>();
    private final Codec<T> codec;
    private final String folderName;
    private static final Map<String, MingledDataReloadListener<? extends MingledData>> LOADED_MINGLED_DATA = new HashMap<>();

    public MingledDataReloadListener(Gson gson, String folder, Codec<T> codec){
        super(gson, "interactions/" + folder);
        this.codec = codec;
        this.folderName = "interactions/" + folder;
        LOADED_MINGLED_DATA.put(folder, this);
    }

    public MingledDataReloadListener(String folder, Codec<T> codec) {
        this(GSON, folder, codec);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> pObject, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        Map<ResourceLocation, T> newMap = new HashMap<>();

        for (Map.Entry<ResourceLocation, JsonElement> element: pObject.entrySet()) {
            codec.decode(JsonOps.INSTANCE, element.getValue())
                    .ifSuccess(result -> newMap.put(result.getFirst().getKey(), result.getFirst()))
                    .ifError(partial -> Mingle.LOGGER.error("Failed to parse data json for {} due to: {}", element.getKey(), partial.message()));
        }

        this.map = newMap;
        Mingle.LOGGER.info("Data loader for {} loaded {} jsons", this.folderName, this.map.size());
    }

    public <P extends CustomPacketPayload> MingledDataReloadListener<T> subscribeAsSyncable(final Function<Map<ResourceLocation, T>, P> packetFactory) {
        NeoForge.EVENT_BUS.addListener(this.getDatapackSyncListener(packetFactory));
        return this;
    }

    private <P extends CustomPacketPayload> Consumer<OnDatapackSyncEvent> getDatapackSyncListener(final Function<Map<ResourceLocation, T>, P> packetFactory) {
        return event -> {
            Mingle.LOGGER.info("PPPPPPPPPPPPPPPP");
            Mingle.LOGGER.info("PPPPPPPPPPPPPPPP");
            Mingle.LOGGER.info("PPPPPPPPPPPPPPPP");
            Mingle.LOGGER.info("PPPPPPPPPPPPPPPP");
            ServerPlayer player = event.getPlayer();
            P packet = packetFactory.apply(this.map);

            if (player == null) {
                PacketDistributor.sendToAllPlayers(packet);
            } else {
                PacketDistributor.sendToPlayer(player, packet);
            }
        };
    }

    public Optional<T> matches(ResourceLocation entitytype) {
        if (map.containsKey(entitytype)) {
            return Optional.of(map.get(entitytype));
        }
        return Optional.empty();
    }

    public static MingledDataReloadListener<? extends MingledData> fromString(String name) {
        return LOADED_MINGLED_DATA.get(name);
    }

}
