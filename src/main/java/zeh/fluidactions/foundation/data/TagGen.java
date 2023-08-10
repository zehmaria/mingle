package zeh.fluidactions.foundation.data;

import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateTagsProvider;
import com.tterrag.registrate.util.nullness.NonNullFunction;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

import zeh.fluidactions.AllTags.AllBlockTags;
import zeh.fluidactions.AllTags.AllItemTags;
import zeh.fluidactions.AllTags.AllFluidTags;
import zeh.fluidactions.AllTags;
import zeh.fluidactions.FluidActions;

public class TagGen {
    public static <T extends Block, P> NonNullFunction<BlockBuilder<T, P>, BlockBuilder<T, P>> axeOrPickaxe() {
        return b -> b.tag(BlockTags.MINEABLE_WITH_AXE)
                .tag(BlockTags.MINEABLE_WITH_PICKAXE);
    }

    public static <T extends Block, P> NonNullFunction<BlockBuilder<T, P>, BlockBuilder<T, P>> axeOnly() {
        return b -> b.tag(BlockTags.MINEABLE_WITH_AXE);
    }

    public static <T extends Block, P> NonNullFunction<BlockBuilder<T, P>, BlockBuilder<T, P>> pickaxeOnly() {
        return b -> b.tag(BlockTags.MINEABLE_WITH_PICKAXE);
    }

    public static <T extends Block, P> NonNullFunction<BlockBuilder<T, P>, ItemBuilder<BlockItem, BlockBuilder<T, P>>> tagBlockAndItem(
            String... path) {
        return b -> {
            for (String p : path)
                b.tag(AllTags.forgeBlockTag(p));
            ItemBuilder<BlockItem, BlockBuilder<T, P>> item = b.item();
            for (String p : path)
                item.tag(AllTags.forgeItemTag(p));
            return item;
        };
    }

    public static void datagen() {
        FluidActions.REGISTRATE.addDataGenerator(ProviderType.BLOCK_TAGS, TagGen::genBlockTags);
        FluidActions.REGISTRATE.addDataGenerator(ProviderType.ITEM_TAGS, TagGen::genItemTags);
        FluidActions.REGISTRATE.addDataGenerator(ProviderType.FLUID_TAGS, TagGen::genFluidTags);
    }

    private static void genBlockTags(RegistrateTagsProvider<Block> prov) {

        // VALIDATE
        for (AllBlockTags tag : AllBlockTags.values()) {
            if (tag.alwaysDatagen) {
                prov.getOrCreateRawBuilder(tag.tag);
            }
        }
    }

    private static void genItemTags(RegistrateTagsProvider<Item> prov) {

        prov.tag(AllItemTags.LAVA_RESISTANT.tag)
                .add(Items.NETHERITE_AXE)
                .add(Items.NETHERITE_HOE)
                .add(Items.NETHERITE_SWORD)
                .add(Items.NETHERITE_PICKAXE)
                .add(Items.NETHERITE_SHOVEL)

                .add(Items.NETHERITE_BLOCK)
                .add(Items.NETHERITE_INGOT)
                .add(Items.NETHERITE_SCRAP)

                .add(Items.NETHERITE_HELMET)
                .add(Items.NETHERITE_CHESTPLATE)
                .add(Items.NETHERITE_LEGGINGS)
                .add(Items.NETHERITE_BOOTS);
        
        // VALIDATE
        for (AllItemTags tag : AllItemTags.values()) {
            if (tag.alwaysDatagen) {
                prov.getOrCreateRawBuilder(tag.tag);
            }
        }
    }

    private static void genFluidTags(RegistrateTagsProvider<Fluid> prov) {

        // VALIDATE
        for (AllFluidTags tag : AllFluidTags.values()) {
            if (tag.alwaysDatagen) {
                prov.getOrCreateRawBuilder(tag.tag);
            }
        }
    }

}
