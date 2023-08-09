package zeh.fluidactions.foundation.data;

import com.google.common.base.Supplier;
import com.google.gson.JsonElement;
import zeh.fluidactions.FluidActions;
import zeh.fluidactions.foundation.utility.Lang;

public enum AllLangPartials implements LangPartial {

    //ADVANCEMENTS("Advancements", AllAdvancements::provideLangEntries),
    INTERFACE("UI & Messages"),
    //SUBTITLES("Subtitles", AllSoundEvents::provideLangEntries),
    TOOLTIPS("Item Descriptions"),

    ;

    private final String displayName;
    private final Supplier<JsonElement> provider;

    private AllLangPartials(String displayName) {
        this.displayName = displayName;
        String fileName = Lang.asId(name());
        this.provider = () -> LangPartial.fromResource(FluidActions.ID, fileName);
    }

    private AllLangPartials(String displayName, Supplier<JsonElement> provider) {
        this.displayName = displayName;
        this.provider = provider;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public JsonElement provide() {
        return provider.get();
    }

}