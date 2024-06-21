package zeh.mingle.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public record DataTest(Block planks, String aaaa) {

    public static final Codec<DataTest> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BuiltInRegistries.BLOCK.byNameCodec().fieldOf("planks").forGetter(a -> a.planks),
            Codec.STRING.fieldOf("aaaaa").forGetter(a -> a.aaaa)
    ).apply(instance, DataTest::new));


    public DataTest(Holder<Block> planks) {
        this(planks.value(), "a");
    }

    //private final Set<Block> blocks;
/*
    public DataTest(Block planks, String aa) {
        this.planks = planks;
        this.aaaa = aa;
        //this.blocks = new HashSet<>(List.of(this.planks));
    }*/

}
