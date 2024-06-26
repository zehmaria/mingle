package zeh.mingle.registry;

import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Mingled {

    private final ResourceKey<Interaction> KEY;
    private final List<InteractedSet> pairs;
    private static final Map<String, Mingled> entries = new HashMap<>();
    private final String id;

    public Mingled(String id) {
        this.KEY = ResourceKey.create(Interaction.KEY, ResourceLocation.parse(id));
        this.id = id;
        this.pairs = new ArrayList<>();
    }

    public static Mingled getOrCreateKey(String id) {
        entries.computeIfAbsent(id, k -> new Mingled(id));
        return entries.get(id);
    }

    public void build(BootstrapContext<Interaction> context) {
        context.register(KEY, new Interaction(pairs));
    }

    public Mingled addPair(InteractedPair required, InteractedPair target) {
        pairs.add(new InteractedSet(required, target));
        return this;
    }

    public Mingled storageRaw(String storage_raw) {
        if (storage_raw != null) addPair(Mingled.consumeLava(), Mingled.block(storage_raw, id));
        return this;
    }
    public Mingled storageBlock(String storage_block) {
        if (storage_block != null) addPair(Mingled.fluid(id, 0,  storage_block), Mingled.consumeColdFluid());
        return this;
    }

    public Mingled addWaterAndLava() {
        addPair(Mingled.fluid(id, 0), Mingled.fluid("minecraft:water", 1, "minecraft:cobblestone"));
        addPair(Mingled.fluid(id, 1, "minecraft:cobblestone"), Mingled.fluid("minecraft:water", 2, "minecraft:air"));
        addPair(Mingled.fluid(id, 2), Mingled.fluid("minecraft:lava", 2, "minecraft:magma_block"));
        return this;
    }

    @SuppressWarnings("unused")
    public static InteractedPair block(String block) {
        return new InteractedPair(block);
    }

    @SuppressWarnings("unused")
    public static InteractedPair block(String block, String replace) {
        return new InteractedPair(block, replace);
    }

    @SuppressWarnings("unused")
    public static InteractedPair fluid(String fluid, int required) {
        return new InteractedPair(fluid, required);
    }

    @SuppressWarnings("unused")
    public static InteractedPair fluid(String fluid, int required, String replace) {
        return new InteractedPair(fluid, required, replace);
    }

    @SuppressWarnings("unused")
    public static InteractedPair isLava() {
        return new InteractedPair("minecraft:lava", 2);
    }

    @SuppressWarnings("unused")
    public static InteractedPair consumeLava() {
        return new InteractedPair("minecraft:lava", 0, "minecraft:obsidian");
    }

    @SuppressWarnings("unused")
    public static InteractedPair consumeFlowingLava() {
        return new InteractedPair("minecraft:lava", 1, "minecraft:magma_block");
    }

    @SuppressWarnings("unused")
    public static InteractedPair isWater() {
        return new InteractedPair("minecraft:water", 2);
    }

    @SuppressWarnings("unused")
    public static InteractedPair consumeWater() {
        return new InteractedPair("minecraft:water", 0, "minecraft:air");
    }

    @SuppressWarnings("unused")
    public static InteractedPair consumeFlowingWater() {
        return new InteractedPair("minecraft:water", 1, "minecraft:air");
    }

    @SuppressWarnings("unused")
    public static InteractedPair consumeColdFluid() {
        return new InteractedPair("@temperature<301", 0, "minecraft:air");
    }

}
