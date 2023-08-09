package zeh.fluidactions;

import static zeh.fluidactions.FluidActions.REGISTRATE;
import static zeh.fluidactions.foundation.data.ModelGen.customItemModel;
//import zeh.jaca.content.processing.charcoal.*;


public class AllBlocks {

    static { REGISTRATE.useCreativeTab(AllCreativeModeTabs.MAIN_TAB); }


    /*
    public static final BlockEntry<CharcoalBurnerBlock> CHARCOAL_BURNER =
            REGISTRATE.block("charcoal_burner", CharcoalBurnerBlock::new)
                    .initialProperties(SharedProperties::stone)
                    .properties(p -> p.mapColor(MapColor.COLOR_GRAY))
                    .properties(p -> p.sound(SoundType.NETHERITE_BLOCK))
                    .properties(p -> p.lightLevel(CharcoalBurnerBlock::getLight))
                    .transform(pickaxeOnly())
                    .addLayer(() -> RenderType::cutoutMipped)
                    .tag(AllTags.AllBlockTags.FAN_TRANSPARENT.tag)
                    .blockstate((c, p) -> {
                        p.getVariantBuilder(c.getEntry())
                                .forAllStatesExcept(state -> {
                                    return ConfiguredModel.builder().modelFile(p.models()
                                            .getExistingFile(p.modLoc("block/" + (state.getValue(CharcoalBurnerBlock.LIT) == false
                                                    ? "charcoal_burner_off"
                                                    : "charcoal_burner"))))
                                            .build();
                                }, CharcoalBurnerBlock.EMPOWERED, CharcoalBurnerBlock.HEAT_LEVEL, CharcoalBurnerBlock.FUELED, CharcoalBurnerBlock.FACING);
                    })
                    .item()
                    .transform(customItemModel("charcoal_burner_off"))
                    .register();*/

    public static void register() {}

}
