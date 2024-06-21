package zeh.mingle.foundation.data;

import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.NotNull;
import zeh.mingle.Mingle;
import zeh.mingle.interactions.Interactions;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class MInteractionsProvider implements DataProvider {

    private final PackOutput.PathProvider PROVIDER;
    public MInteractionsProvider(PackOutput packOutput) {
        this.PROVIDER = packOutput.createPathProvider(PackOutput.Target.DATA_PACK, "interactions");
    }

    @Override
    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput cachedOutput) {
        Set<ResourceLocation> set = Sets.newHashSet();
        List<CompletableFuture<?>> list = new ArrayList<>();
        this.buildData(mingledData -> {
            if (!set.add(mingledData.getId())) {
                throw new IllegalStateException("Duplicate recipe " + mingledData.getId());
            } else {
                list.add(DataProvider.saveStable(cachedOutput, mingledData.serializeData(), this.PROVIDER.json(mingledData.getId())));
            }
        });
        return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
    }

    @NotNull
    @Override
    public String getName() {
        return "Interactions";
    }

    public void buildData(Consumer<MingledData<?>> mingledDataConsumer) {
        addMingledData(EntityType.BLAZE, FluidTags.LAVA, 300, 15, mingledDataConsumer);
    }

    private void addMingledData(EntityType<?> entityType, Fluid fluid, int powerpermb, int tickpermb, Consumer<MingledData<?>> mingledDataConsumer) {
        ResourceLocation entityRL = BuiltInRegistries.ENTITY_TYPE.getKey(entityType);
        ResourceLocation fluidRL = BuiltInRegistries.FLUID.getKey(fluid);
        Interactions data = new Interactions(entityRL, fluidRL.toString(), powerpermb, tickpermb);
        mingledDataConsumer.accept(new MingledData<>(Interactions.CODEC, data, Interactions.NAME + "/" + entityRL.getNamespace() + "_" + entityRL.getPath()));
    }

    private void addMingledData(EntityType<?> entityType, TagKey<Fluid> fluid, int powerpermb, int tickpermb, Consumer<MingledData<?>> mingledDataConsumer) {
        ResourceLocation entityRL = BuiltInRegistries.ENTITY_TYPE.getKey(entityType);
        String fluidRL = "#" + fluid.location();
        Interactions data = new Interactions(entityRL, fluidRL, powerpermb, tickpermb);
        mingledDataConsumer.accept(new MingledData<>(Interactions.CODEC, data, Interactions.NAME + "/" + entityRL.getNamespace() + "_" + entityRL.getPath()));
    }

    public static class MingledData<T extends Interactions> {

        private final Codec<T> codec;
        private final T data;
        private final ResourceLocation id;

        private MingledData(Codec<T> codec, T data, String id) {
            this.codec = codec;
            this.data = data;
            this.id = Mingle.asResource(id);
        }

        private MingledData(Codec<T> codec, T data, ResourceLocation id) {
            this.codec = codec;
            this.data = data;
            this.id = id;
        }

        public JsonObject serializeData() {
            DataResult<JsonElement> element = codec.encodeStart(JsonOps.INSTANCE, data);
            return element.getOrThrow().getAsJsonObject();
        }

        public ResourceLocation getId() {
            return this.id;
        }

    }

}
