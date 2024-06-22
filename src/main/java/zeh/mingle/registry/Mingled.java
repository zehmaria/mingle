package zeh.mingle.registry;

import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import zeh.mingle.Mingle;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Mingled {

    private final ResourceKey<Interaction> KEY;
    private Fluid fluid;
    private Block slag;
    private final List<InteractedWith> with;
    private final List<InteractedFrom> from;

    public Mingled(ResourceKey<Interaction> key) {
        this.KEY = key;
        this.with = new ArrayList<>();
        this.from = new ArrayList<>();
    }

    public void build(BootstrapContext<Interaction> context) {
        context.register(KEY,
                Mingled.set(
                        Objects.requireNonNull(this.fluid),
                        Objects.requireNonNull(this.slag),
                        Objects.requireNonNull(this.with),
                        Objects.requireNonNull(this.from)
                )
        );
    }

    public static Mingled createKey(String name) {
        return new Mingled(ResourceKey.create(Interaction.KEY, Mingle.as(name)));
    }

    public Mingled setFluid(Fluid fluid) {
        this.fluid = fluid;
        return this;
    }

    public Mingled setSlag(Block slag) {
        this.slag = slag;
        return this;
    }

    public Mingled addWith(InteractedWith add) {
        with.add(add);
        return this;
    }

    public Mingled addFrom(InteractedFrom add) {
        from.add(add);
        return this;
    }

    public static Interaction set(Fluid fluid, Block block, List<InteractedWith> with, List<InteractedFrom> from) {
        return new Interaction(fluid, block, with, from);
    }

    @SuppressWarnings("unused")
    public static InteractedWith with(boolean requireSource, InteractSet target) {
        return new InteractedWith(requireSource, target);
    }

    @SuppressWarnings("unused")
    public static InteractedWith with(boolean requireSource, InteractSet replace, InteractSet target) {
        return new InteractedWith(requireSource, replace, target);
    }

    @SuppressWarnings("unused")
    public static InteractedFrom from(InteractSet target, InteractSet fluid) {
        return new InteractedFrom(target, fluid);
    }


    @SuppressWarnings("unused")
    public static InteractSet block(String block) {
        return new InteractSet(block);
    }

    @SuppressWarnings("unused")
    public static InteractSet block(String block, InteractSet replace) {
        return new InteractSet(block, replace);
    }

    @SuppressWarnings("unused")
    public static InteractSet fluid(String fluid, boolean requireSource) {
        return new InteractSet(fluid, requireSource);
    }

    @SuppressWarnings("unused")
    public static InteractSet fluid(String fluid, boolean requireSource, InteractSet replace) {
        return new InteractSet(fluid, requireSource, replace);
    }

    @SuppressWarnings("unused")
    public static InteractSet consumeLava() {
        return new InteractSet("minecraft:lava", true, new InteractSet("minecraft:obsidian"));
    }

    @SuppressWarnings("unused")
    public static InteractSet consumeWater() {
        return new InteractSet("minecraft:water", true, new InteractSet("minecraft:air"));
    }

}
