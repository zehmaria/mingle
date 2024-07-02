package zeh.mingle.fluids;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import zeh.mingle.Mingle;
import zeh.mingle.foundation.utility.Color;

import java.util.function.Function;

public class Attributes {

    public final String name;
    public int color = 0xffffffff;
    public int fog = 0xffffffff;
    public boolean isOre = false;
    public ResourceLocation still = Mingle.as("block/fluid/molten_still");
    public ResourceLocation flow = Mingle.as("block/fluid/molten_flow");
    public final ResourceLocation overlay = Mingle.as("textures/block/fluid/molten_slag_overlay.png");
    public Function<BaseFlowingFluid.Properties, BaseFlowingFluid> source = MoltenFlowingFluid.Source::new;
    public Function<BaseFlowingFluid.Properties, BaseFlowingFluid> flowing = MoltenFlowingFluid.Flowing::new;

    public Attributes(String name) {
        this.name = name;
    }

    public static Attributes of(String name) {
        return new Attributes(name);
    }

    @SuppressWarnings("unused")
    public Attributes color(int color) {
        this.color = color;
        this.fog = Color.mixColors(color, 0xff222222, 0.2f);
        return this;
    }

    @SuppressWarnings("unused")
    public Attributes fog(int fog) {
        this.fog = fog;
        return this;
    }

    @SuppressWarnings("unused")
    public Attributes isOre(boolean isOre) {
        this.isOre = isOre;
        return this;
    }

    @SuppressWarnings("unused")
    public Attributes source(ResourceLocation still) {
        this.still = still;
        return this;
    }

    @SuppressWarnings("unused")
    public Attributes flowing(ResourceLocation flow) {
        this.flow = flow;
        return this;
    }

    @SuppressWarnings("unused")
    public Attributes source(ResourceLocation still, Function<BaseFlowingFluid.Properties, BaseFlowingFluid> source) {
        this.still = still;
        this.source = source;
        return this;
    }

    @SuppressWarnings("unused")
    public Attributes flowing(ResourceLocation flow, Function<BaseFlowingFluid.Properties, BaseFlowingFluid> flowing) {
        this.flow = flow;
        this.flowing = flowing;
        return this;
    }

    @SuppressWarnings("unused")
    public Attributes source(Function<BaseFlowingFluid.Properties, BaseFlowingFluid> source) {
        this.source = source;
        return this;
    }

    @SuppressWarnings("unused")
    public Attributes flowing(Function<BaseFlowingFluid.Properties, BaseFlowingFluid> flowing) {
        this.flowing = flowing;
        return this;
    }

}