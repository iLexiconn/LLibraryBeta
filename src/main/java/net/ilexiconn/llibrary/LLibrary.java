package net.ilexiconn.llibrary;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import net.ilexiconn.llibrary.common.ServerProxy;
import net.ilexiconn.llibrary.common.content.ContentHelper;
import net.ilexiconn.llibrary.common.content.IContentHandler;
import net.ilexiconn.llibrary.common.content.InitializationState;
import net.ilexiconn.llibrary.common.crash.SimpleCrashReport;
import net.ilexiconn.llibrary.common.log.LoggerHelper;
import net.ilexiconn.llibrary.common.message.MessageLLibraryAnimation;
import net.ilexiconn.llibrary.common.message.MessageLLibraryAnimationAction;
import net.ilexiconn.llibrary.common.message.MessageLLibraryIntemittentAnimation;
import net.ilexiconn.llibrary.common.update.UpdateHelper;
import net.minecraft.crash.CrashReport;
import java.util.Map;

@Mod(modid = "llibrary", name = "LLibrary", version = LLibrary.VERSION, guiFactory = "net.ilexiconn.llibrary.client.gui.GuiLLibraryConfigFactory", dependencies = "required-after:Forge")
public class LLibrary {
    @Mod.Instance("llibrary")
    public static LLibrary instance;
    @SidedProxy(serverSide = "net.ilexiconn.llibrary.common.ServerProxy", clientSide = "net.ilexiconn.llibrary.client.ClientProxy")
    public static ServerProxy proxy;
    public static LoggerHelper logger = new LoggerHelper("llibrary");
    public static SimpleNetworkWrapper networkWrapper;

    public static final String VERSION = "0.7.0-develop";

    @Mod.EventHandler
    private void preInit(FMLPreInitializationEvent event) {
        networkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel("llibrary");
        networkWrapper.registerMessage(MessageLLibraryIntemittentAnimation.class, MessageLLibraryIntemittentAnimation.class, 0, Side.CLIENT);
        networkWrapper.registerMessage(MessageLLibraryAnimation.class, MessageLLibraryAnimation.class, 1, Side.CLIENT);
        networkWrapper.registerMessage(MessageLLibraryAnimationAction.class, MessageLLibraryAnimationAction.class, 2, Side.CLIENT);

        proxy.preInit(event.getSuggestedConfigurationFile());
    }

    @Mod.EventHandler
    private void init(FMLInitializationEvent event) {
        for (Map.Entry<InitializationState, IContentHandler> contentHandlerEntry : ContentHelper.getTimedHandlers().entrySet()) {
            if (contentHandlerEntry.getKey() == InitializationState.INIT) {
                ContentHelper.init(true, contentHandlerEntry.getValue());
            }
        }
    }

    @Mod.EventHandler
    private void postInit(FMLPostInitializationEvent event) {
        proxy.postInit();

        for (Map.Entry<InitializationState, IContentHandler> contentHandlerEntry : ContentHelper.getTimedHandlers().entrySet()) {
            if (contentHandlerEntry.getKey() == InitializationState.POSTINIT) {
                ContentHelper.init(true, contentHandlerEntry.getValue());
            }
        }
    }

    @Mod.EventHandler
    public void messageReceived(FMLInterModComms.IMCEvent event) {
        for (FMLInterModComms.IMCMessage message : event.getMessages()) {
            if (message.key.equalsIgnoreCase("update-checker") && message.isStringMessage()) {
                try {
                    ModContainer modContainer = null;
                    for (ModContainer mod : Loader.instance().getModList()) {
                        if (mod.getModId().equals(message.getSender())) {
                            modContainer = mod;
                        }
                    }
                    if (modContainer == null) {
                        logger.error(SimpleCrashReport.makeCrashReport(new NullPointerException(), ""));
                    }
                    UpdateHelper.registerUpdateChecker(modContainer, message.getStringValue());
                } catch (Exception e) {
                    LLibrary.logger.info(CrashReport.makeCrashReport(e, "Failed to register update checker for mod " + message.getSender()).getCompleteReport());
                }
            }
        }
    }
}
