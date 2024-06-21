package zeh.mingle;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;

import static zeh.mingle.AllTags.NameSpace.MOD;

import java.util.Locale;

public class AllTags {

    public static String asId(String name) { return name.toLowerCase(Locale.ROOT); }

    public enum NameSpace {
        MOD(Mingle.ID, false, true),
        ;
        public final String id;
        public final boolean optionalDefault;
        public final boolean alwaysDatagenDefault;

        NameSpace(String id) {
            this(id, false, false);
        }

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

        AllBlockTags(NameSpace namespace, String path) {
            this(namespace, path, namespace.alwaysDatagenDefault);
        }

        AllBlockTags(NameSpace namespace, boolean alwaysDatagen) {
            this(namespace, null, alwaysDatagen);
        }

        AllBlockTags(NameSpace namespace, String path, boolean alwaysDatagen) {
            ResourceLocation id = ResourceLocation.fromNamespaceAndPath(namespace.id, path == null ? asId(name()) : path);
            tag = BlockTags.create(id);
            this.alwaysDatagen = alwaysDatagen;
        }

        @SuppressWarnings("deprecation")
        public boolean matches(Block block) {
            return block.builtInRegistryHolder()
                    .is(tag);
        }

        public boolean matches(ItemStack stack) {
            return stack != null && stack.getItem() instanceof BlockItem blockItem && matches(blockItem.getBlock());
        }

        public boolean matches(BlockState state) {
            return state.is(tag);
        }

        private static void init() {
        }

    }

    public enum AllItemTags {
        LAVA_RESISTANT
        ;

        public final TagKey<Item> tag;
        public final boolean alwaysDatagen;
        AllItemTags() {
            this(MOD);
        }
        AllItemTags(NameSpace namespace) {
            this(namespace, namespace.alwaysDatagenDefault);
        }
        AllItemTags(NameSpace namespace, String path) {
            this(namespace, path, namespace.alwaysDatagenDefault);
        }
        AllItemTags(NameSpace namespace, boolean alwaysDatagen) {
            this(namespace, null, alwaysDatagen);
        }
        AllItemTags(NameSpace namespace, String path, boolean alwaysDatagen) {
            ResourceLocation id = ResourceLocation.fromNamespaceAndPath(namespace.id, path == null ? asId(name()) : path);
            tag = ItemTags.create(id);
            this.alwaysDatagen = alwaysDatagen;
        }

        public boolean matches(ItemStack stack) {
            return stack.is(tag);
        }
        private static void init() {
        }
    }

    public enum AllFluidTags {
        FLUID_INTERACTIONS_EXCLUSION
        ;

        public final TagKey<Fluid> tag;
        public final boolean alwaysDatagen;

        AllFluidTags() {
            this(MOD);
        }

        AllFluidTags(NameSpace namespace) {
            this(namespace, namespace.alwaysDatagenDefault);
        }

        AllFluidTags(NameSpace namespace, String path) {
            this(namespace, path, namespace.alwaysDatagenDefault);
        }

        AllFluidTags(NameSpace namespace, boolean alwaysDatagen) {
            this(namespace, null, alwaysDatagen);
        }

        AllFluidTags(NameSpace namespace, String path, boolean alwaysDatagen) {
            ResourceLocation id = ResourceLocation.fromNamespaceAndPath(namespace.id, path == null ? asId(name()) : path);
            tag = FluidTags.create(id);
            this.alwaysDatagen = alwaysDatagen;
        }

        public boolean matches(FluidState state) {
            return state.is(tag);
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

