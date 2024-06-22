package zeh.mingle;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

import java.util.Locale;

import static zeh.mingle.AllTags.NameSpace.MOD;

public class AllTags {

    public static String asId(String name) {
        return name.toLowerCase(Locale.ROOT);
    }

    public enum NameSpace {
        MOD(Mingle.ID, false, true),
        ;

        public final String id;
        public final boolean optionalDefault;
        public final boolean alwaysDatagenDefault;

        NameSpace(String id, boolean optionalDefault, boolean alwaysDatagenDefault) {
            this.id = id;
            this.optionalDefault = optionalDefault;
            this.alwaysDatagenDefault = alwaysDatagenDefault;
        }
    }

    public enum AllBlockTags {
        ;

        public final TagKey<Block> tag;
        public final boolean alwaysDatagen;

        AllBlockTags() {
            this(MOD);
        }

        AllBlockTags(NameSpace namespace) {
            this(namespace, namespace.alwaysDatagenDefault);
        }

        AllBlockTags(NameSpace namespace, boolean alwaysDatagen) {
            ResourceLocation id = ResourceLocation.fromNamespaceAndPath(namespace.id, asId(name()));
            tag = BlockTags.create(id);
            this.alwaysDatagen = alwaysDatagen;
        }

        private static void init() {
        }
    }

    public enum AllItemTags {
        LAVA_RESISTANT;

        public final TagKey<Item> tag;
        public final boolean alwaysDatagen;

        AllItemTags() {
            this(MOD);
        }

        AllItemTags(NameSpace namespace) {
            this(namespace, namespace.alwaysDatagenDefault);
        }

        AllItemTags(NameSpace namespace, boolean alwaysDatagen) {
            ResourceLocation id = ResourceLocation.fromNamespaceAndPath(namespace.id, asId(name()));
            tag = ItemTags.create(id);
            this.alwaysDatagen = alwaysDatagen;
        }

        private static void init() {
        }
    }

    public enum AllFluidTags {
        WASTE_MIXING;

        public final TagKey<Fluid> tag;
        public final boolean alwaysDatagen;

        AllFluidTags() {
            this(MOD);
        }

        AllFluidTags(NameSpace namespace) {
            this(namespace, namespace.alwaysDatagenDefault);
        }

        AllFluidTags(NameSpace namespace, boolean alwaysDatagen) {
            ResourceLocation id = ResourceLocation.fromNamespaceAndPath(namespace.id, asId(name()));
            tag = FluidTags.create(id);
            this.alwaysDatagen = alwaysDatagen;
        }

        private static void init() {
        }
    }

    public static void init() {
        AllBlockTags.init();
        AllItemTags.init();
        AllFluidTags.init();
    }

}

