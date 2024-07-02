package zeh.mingle;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.*;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import zeh.mingle.blocks.SlagBlock;
import zeh.mingle.fluids.Attributes;
import zeh.mingle.fluids.MoltenFluidType;
import zeh.mingle.fluids.MoltenWasteFlowingFluid;

import java.util.Arrays;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public final class AllFluids {

    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(Registries.FLUID, Mingle.ID);
    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(NeoForgeRegistries.FLUID_TYPES, Mingle.ID);
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Mingle.ID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Mingle.ID);

    public static void register(IEventBus bus) {
        FLUIDS.register(bus);
        FLUID_TYPES.register(bus);
        BLOCKS.register(bus);
        ITEMS.register(bus);
        for (AllFluids.INSTANCE entry : AllFluids.INSTANCE.values()) {
            if (!entry.init()) return; // Makes sure the fluids are registered at the right time.
        }
    }

    private static final DispenseItemBehavior BUCKET_DISPENSE_BEHAVIOR = new DefaultDispenseItemBehavior() {
        @NotNull
        @Override
        public ItemStack execute(@NotNull BlockSource source, @NotNull ItemStack stack) {
            Level world = source.level();
            DispensibleContainerItem bucket = (DispensibleContainerItem) stack.getItem();
            BlockPos pos = source.pos().relative(source.state().getValue(DispenserBlock.FACING));
            if (bucket.emptyContents(null, world, pos, null, stack)) {
                bucket.checkExtraContent(null, world, stack, pos);
                return new ItemStack(Items.BUCKET);
            }
            return super.execute(source, stack);
        }
    };

    public static void registerBucketDispenserBehavior() {
        for (Holder<Item> bucket : ITEMS.getEntries()) {
            DispenserBlock.registerBehavior(bucket.value(), BUCKET_DISPENSE_BEHAVIOR);
        }
    }

    public enum INSTANCE {
        WASTE(Attributes.of("molten_waste").fog(0xff3a2e2e)
                .source(Mingle.as("block/fluid/molten_waste_still"), MoltenWasteFlowingFluid.Source::new)
                .flowing(Mingle.as("block/fluid/molten_waste_flow"), MoltenWasteFlowingFluid.Flowing::new)),

        IRON(Attributes.of("molten_iron").color(0xff910000).isOre(true)),
        GOLD(Attributes.of("molten_gold").color(0xffF5BD02).isOre(true)),
        COPPER(Attributes.of("molten_copper").color(0xffDE5244).isOre(true)),

        ANDESITE(Attributes.of("molten_andesite").color(0xffa2a3a3)),
        SOUL(Attributes.of("molten_soul").color(0xff41230e)),
        ENDER(Attributes.of("molten_ender").color(0xff034830)),
        SLIME(Attributes.of("molten_slime").color(0xff70dc70)),
        QUARTZ(Attributes.of("molten_quartz").color(0xffF5F5DC)),
        GLASS(Attributes.of("molten_glass").color(0xffF6B26B)),
        NETHERITE(Attributes.of("molten_netherite").color(0xff3b2921)),
        DEBRIS(Attributes.of("molten_debris").color(0xff88774f)),

        // GENERAL
        ANDESITE_ALLOY(Attributes.of("molten_andesite_alloy").color(0xff757575)),
        BRASS(Attributes.of("molten_brass").color(0xffbf8e55)),
        REFINED_GLOWSTONE(Attributes.of("molten_refined_glowstone").color(0xffd49710)),
        REFINED_OBSIDIAN(Attributes.of("molten_refined_obsidian").color(0xff272a3e)),
        BRONZE(Attributes.of("molten_bronze").color(0xff986441)),
        STEEL(Attributes.of("molten_steel").color(0xff71797E)),
        ELECTRUM(Attributes.of("molten_electrum").color(0xffCC5500)),
        CONSTANTAN(Attributes.of("molten_constantan").color(0xff884739)),
        INVAR(Attributes.of("molten_invar").color(0xff5c7c7b)),

        ZINC(Attributes.of("molten_zinc").color(0xff809a7d).isOre(true)),
        OSMIUM(Attributes.of("molten_osmium").color(0xff587470).isOre(true)),
        TIN(Attributes.of("molten_tin").color(0xffa7afaf).isOre(true)),
        LEAD(Attributes.of("molten_lead").color(0xff58546d).isOre(true)),
        URANIUM(Attributes.of("molten_uranium").color(0xff4F7942).isOre(true)),
        ALUMINUM(Attributes.of("molten_aluminum").color(0xff919d9c).isOre(true)),
        NICKEL(Attributes.of("molten_nickel").color(0xffb3bba9).isOre(true)),
        SILVER(Attributes.of("molten_silver").color(0xff8dadad).isOre(true)),

        // AD-ASTRA
        DESH(Attributes.of("molten_desh").color(0xffd38b4c).isOre(true)),
        OSTRUM(Attributes.of("molten_ostrum").color(0xff7c5156).isOre(true)),
        CALORITE(Attributes.of("molten_calorite").color(0xff904748).isOre(true)),

        // BOTANIA
        MANASTEEL(Attributes.of("molten_manasteel").color(0xff006afc)),
        ELEMENTIUM(Attributes.of("molten_elementium").color(0xffea3298)),
        TERRASTEEL(Attributes.of("molten_terrasteel").color(0xff3fca00)),
        SPIRIT(Attributes.of("molten_spirit").color(0xff7ea37e)),
        ESSENCE(Attributes.of("molten_essence").color(0xff6ea31e));

        public final DeferredBlock<Block> SLAG_BLOCK;
        public final DeferredItem<Item> SLAG_ITEM;
        public final DeferredHolder<Fluid, FlowingFluid> STILL;
        public final DeferredHolder<Fluid, FlowingFluid> FLOWING;
        public final DeferredBlock<LiquidBlock> BLOCK;
        public final DeferredItem<BucketItem> BUCKET_ITEM;
        public final Supplier<FluidType> FLUID_TYPE;
        public BaseFlowingFluid.Properties PROPS;

        public final String id;
        public final String slagId;
        public final String name;
        public final String lang;
        public final String slag;
        public final int color;
        public final int fog;
        public final String storage_raw;
        public final String storage_block;
        public final boolean isOre;
        public final TagKey<Fluid> tag;

        INSTANCE(Attributes attributes) {
            this.name = attributes.name;
            this.color = attributes.color;
            this.fog = attributes.fog;
            this.isOre = attributes.isOre;

            this.id = Mingle.ID + ":" + name;
            this.slag = name + "_slag";
            this.slagId = Mingle.ID + ":" + this.slag;
            this.tag = TagKey.create(BuiltInRegistries.FLUID.key(), ResourceLocation.fromNamespaceAndPath("c", name));
            this.lang = Arrays.stream(name.split("_"))
                    .map(String::toLowerCase).map(StringUtils::capitalize).collect(Collectors.joining(" "));

            if (isOre) {
                this.storage_raw = "#c:storage_blocks/raw_" + name.replace("molten_", "");
                this.storage_block = "#c:storage_blocks/" + name.replace("molten_", "");
            } else {
                this.storage_block = null;
                this.storage_raw = null;
            }

            this.SLAG_BLOCK = BLOCKS.register(this.slag,
                    () -> new SlagBlock(color, Block.Properties.ofFullCopy(Blocks.GLASS)
                            .lightLevel((data) -> 5)
                            .instabreak().noLootTable().replaceable()
                            .noOcclusion().isSuffocating((s, w, p) -> false).isViewBlocking((s, w, p) -> false)
                            .isValidSpawn((s, r, p, e) -> false)
                    ));
            this.SLAG_ITEM = ITEMS.register(this.slag, () -> new BlockItem(SLAG_BLOCK.get(), new Item.Properties()));

            this.FLUID_TYPE = FLUID_TYPES.register(name + "_type",
                    () -> new MoltenFluidType(FluidType.Properties.create().density(4500).viscosity(9000).temperature(1000)
                            .canDrown(true).lightLevel(12).motionScale(0.00233).canSwim(false)
                            .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY_LAVA)
                            .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_LAVA)
                            .sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH),
                            attributes.still, attributes.flow, attributes.flow, attributes.overlay, color, attributes.fog));
            this.STILL = FLUIDS.register(name, () -> attributes.source.apply(PROPS));
            this.FLOWING = FLUIDS.register("flowing_" + name, () -> attributes.flowing.apply(PROPS));
            this.BLOCK = BLOCKS.register(name, () -> new LiquidBlock(
                    STILL.get(),
                    BlockBehaviour.Properties.of().noCollission().strength(100.0F).noLootTable()
                            .replaceable().pushReaction(PushReaction.DESTROY)
                            .lightLevel((data) -> 15).liquid()));
            this.BUCKET_ITEM = ITEMS.register(name + "_bucket", () -> new BucketItem(STILL.get(),
                    new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));
            this.PROPS = new BaseFlowingFluid.Properties(FLUID_TYPE, STILL, FLOWING)
                    .explosionResistance(100f).block(BLOCK).bucket(BUCKET_ITEM);
        }

        public boolean init() {
            return true;
        }
    }

}
