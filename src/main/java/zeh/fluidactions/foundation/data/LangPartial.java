package zeh.fluidactions.foundation.data;

import com.google.gson.JsonElement;
import zeh.fluidactions.foundation.utility.FilesHelper;

public interface LangPartial {
    String getDisplayName();

    JsonElement provide();

    static JsonElement fromResource(String namespace, String fileName) {
        String path = "assets/" + namespace + "/lang/default/" + fileName + ".json";
        JsonElement element = FilesHelper.loadJsonResource(path);
        if (element == null)
            throw new IllegalStateException(String.format("Could not find default lang file: %s", path));
        return element;
    }
}