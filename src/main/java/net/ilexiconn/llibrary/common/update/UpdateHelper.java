package net.ilexiconn.llibrary.common.update;

import com.google.common.collect.Lists;
import net.ilexiconn.llibrary.common.json.JsonFactory;
import net.ilexiconn.llibrary.common.json.container.JsonModUpdate;
import net.ilexiconn.llibrary.common.web.WebHelper;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ModContainer;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Helper class to register a mod for automatic update checking.
 *
 * @author FiskFille
 * @author iLexiconn
 * @since 0.1.0
 */
public class UpdateHelper {
    public static ArrayList<JsonModUpdate> modList = Lists.newArrayList();

    /**
     * Register the main mod class for automatic update checking.
     * <p/>
     * Example pastebin version file:
     * <p/>
     * { "newestVersion": "9000", "versions": { "0.1.0": [ "Initial release" ], "9000": [ "Added more awesomeness" ] }, "updateUrl": "http://ilexiconn.net", "iconUrl": "http://ilexiconn.net/llibrary/data/llibrary_64.png" }
     *
     * @param mod the main mod instance
     * @param url the updater file
     * @throws java.io.IOException
     */
    public static void registerUpdateChecker(Object mod, String url) throws IOException {
        registerUpdateChecker(mod, new String[]{url});
    }

    /**
     * Register the main mod class for automatic update checking.
     * <p/>
     * Example pastebin version file:
     * <p/>
     * { "newestVersion": "9000", "versions": { "0.1.0": [ "Initial release" ], "9000": [ "Added more awesomeness" ] }, "updateUrl": "http://ilexiconn.net", "iconUrl": "http://ilexiconn.net/llibrary/data/llibrary_64.png" }
     *
     * @param mod  the main mod instance
     * @param urls the updater file
     * @throws java.io.IOException
     */
    public static void registerUpdateChecker(Object mod, String[] urls) throws IOException {
        JsonModUpdate json = JsonFactory.getGson().fromJson(WebHelper.downloadTextFile(urls), JsonModUpdate.class);
        Class<?> modClass = mod.getClass();

        if (json == null) {
            return;
        }

        if (!modClass.isAnnotationPresent(Mod.class)) {
            return;
        }

        Mod annotation = modClass.getAnnotation(Mod.class);

        ModContainer container = null;
        for (ModContainer c : Loader.instance().getModList()) {
            if (c.getModId().equals(annotation.modid())) {
                container = c;
                break;
            }
        }

        json.modContainer = container;
        json.modid = annotation.modid();
        json.currentVersion = annotation.version();
        json.name = annotation.name();
        json.thumbnail = WebHelper.downloadImage(json.getIconUrl());

        modList.add(json);
    }

    /**
     * Register the main mod class for automatic update checking.
     * <p/>
     * Example pastebin version file:
     * <p/>
     * { "newestVersion": "9000", "versions": { "0.1.0": [ "Initial release" ], "9000": [ "Added more awesomeness" ] }, "updateUrl": "http://ilexiconn.net", "iconUrl": "http://ilexiconn.net/llibrary/data/llibrary_64.png" }
     *
     * @param mod the modcontainer instance
     * @param url the updater file
     * @throws java.io.IOException
     */
    public static void registerUpdateChecker(ModContainer mod, String url) throws IOException {
        registerUpdateChecker(mod, new String[]{url});
    }

    /**
     * Register the main mod class for automatic update checking.
     * <p/>
     * Example pastebin version file:
     * <p/>
     * { "newestVersion": "9000", "versions": { "0.1.0": [ "Initial release" ], "9000": [ "Added more awesomeness" ] }, "updateUrl": "http://ilexiconn.net", "iconUrl": "http://ilexiconn.net/llibrary/data/llibrary_64.png" }
     *
     * @param mod  the modcontainer instance
     * @param urls the updater file
     * @throws java.io.IOException
     */
    public static void registerUpdateChecker(ModContainer mod, String[] urls) throws IOException {
        JsonModUpdate json = JsonFactory.getGson().fromJson(WebHelper.downloadTextFile(urls), JsonModUpdate.class);

        if (json == null) {
            return;
        }

        json.modContainer = mod;
        json.modid = mod.getModId();
        json.currentVersion = mod.getVersion();
        json.name = mod.getName();
        json.thumbnail = WebHelper.downloadImage(json.getIconUrl());

        modList.add(json);
    }

    public static JsonModUpdate getModContainerById(String modid) {
        for (JsonModUpdate mod : modList) {
            if (mod.modid.equals(modid)) {
                return mod;
            }
        }

        return null;
    }
}
