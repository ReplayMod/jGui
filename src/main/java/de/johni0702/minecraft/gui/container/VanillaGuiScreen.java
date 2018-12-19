package de.johni0702.minecraft.gui.container;

import de.johni0702.minecraft.gui.function.Draggable;
import de.johni0702.minecraft.gui.function.Scrollable;
import de.johni0702.minecraft.gui.function.Typeable;
import de.johni0702.minecraft.gui.utils.lwjgl.ReadablePoint;
import de.johni0702.minecraft.gui.versions.MCVer;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;

//#if MC>=10800
//#if MC>=11300
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
//#else
//$$ import net.minecraftforge.fml.common.eventhandler.EventPriority;
//$$ import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
//#endif
import net.minecraftforge.fml.common.gameevent.TickEvent;
//#else
//$$ import cpw.mods.fml.common.eventhandler.Cancelable;
//$$ import cpw.mods.fml.common.eventhandler.Event;
//$$ import cpw.mods.fml.common.eventhandler.EventPriority;
//$$ import cpw.mods.fml.common.eventhandler.SubscribeEvent;
//$$ import cpw.mods.fml.common.gameevent.TickEvent;
//#endif

import java.io.IOException;

public class VanillaGuiScreen extends GuiScreen implements Draggable, Typeable, Scrollable {

    public static VanillaGuiScreen setup(net.minecraft.client.gui.GuiScreen originalGuiScreen) {
        VanillaGuiScreen gui = new VanillaGuiScreen(originalGuiScreen);
        gui.register();
        return gui;
    }

    private final net.minecraft.client.gui.GuiScreen mcScreen;
    private final EventHandler eventHandler = new EventHandler();

    public VanillaGuiScreen(net.minecraft.client.gui.GuiScreen mcScreen) {
        this.mcScreen = mcScreen;

        super.setBackground(Background.NONE);
    }

    // Needs to be called from or after GuiInitEvent.Post, will auto-unregister on any GuiOpenEvent
    public void register() {
        if (!eventHandler.active) {
            eventHandler.active = true;

            MinecraftForge.EVENT_BUS.register(eventHandler);

            getSuperMcGui().setWorldAndResolution(MCVer.getMinecraft(), mcScreen.width, mcScreen.height);
            // FIXME getSuperMcGui().initGui();
        }
    }

    public void display() {
        getMinecraft().displayGuiScreen(mcScreen);
        register();
    }

    @Override
    public net.minecraft.client.gui.GuiScreen toMinecraft() {
        return mcScreen;
    }

    @Override
    public void setBackground(Background background) {
        throw new UnsupportedOperationException("Cannot set background of vanilla gui screen.");
    }

    private net.minecraft.client.gui.GuiScreen getSuperMcGui() {
        return super.toMinecraft();
    }

    @Override
    public boolean mouseClick(ReadablePoint position, int button) {
        forwardMouseInput();
        return false;
    }

    @Override
    public boolean mouseDrag(ReadablePoint position, int button, long timeSinceLastCall) {
        forwardMouseInput();
        return false;
    }

    @Override
    public boolean mouseRelease(ReadablePoint position, int button) {
        forwardMouseInput();
        return false;
    }

    @Override
    public boolean scroll(ReadablePoint mousePosition, int dWheel) {
        forwardMouseInput();
        return false;
    }

    private void forwardMouseInput() {
        //#if MC>=10800
        /* FIXME
        try {
            mcScreen.handleMouseInput();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        */
        //#else
        //$$ mcScreen.handleMouseInput();
        //#endif
    }

    @Override
    public boolean typeKey(ReadablePoint mousePosition, int keyCode, char keyChar, boolean ctrlDown, boolean shiftDown) {
        //#if MC>=10800
        /* FIXME
        try {
            mcScreen.handleKeyboardInput();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        */
        //#else
        //$$ mcScreen.handleKeyboardInput();
        //#endif
        return false;
    }

    // Used when wrapping an already existing mc.GuiScreen
    //#if MC>=10800
    private
    //#else
    //$$ public
    //#endif
    class EventHandler {
        private boolean active;

        @SubscribeEvent(priority = EventPriority.LOWEST)
        public void onGuiClosed(GuiOpenEvent event) {
            MinecraftForge.EVENT_BUS.unregister(this);

            if (active) {
                active = false;
                getSuperMcGui().onGuiClosed();
            }
        }

        /* FIXME
        @SubscribeEvent
        public void onGuiRender(GuiScreenEvent.DrawScreenEvent.Post event) {
            getSuperMcGui().drawScreen(MCVer.getMouseX(event), MCVer.getMouseY(event), MCVer.getPartialTicks(event));
        }

        @SubscribeEvent
        public void tickOverlay(TickEvent.ClientTickEvent event) {
            if (event.phase == TickEvent.Phase.START) {
                getSuperMcGui().updateScreen();
            }
        }

        // Mouse/Keyboard events aren't supported in 1.7.10
        // so this requires a mixin in any mod making use of it
        // (see ReplayMod: GuiScreenMixin)
        @SubscribeEvent(priority = EventPriority.LOWEST)
        //#if MC>=10800
        public void onMouseInput(GuiScreenEvent.MouseInputEvent.Pre event) throws IOException {
        //#else
        //$$ public void onMouseInput(MouseInputEvent event) throws IOException {
        //#endif
            event.setCanceled(true);

            getSuperMcGui().handleMouseInput();

            //#if MC>=10800
            if (mcScreen.equals(getMinecraft().currentScreen)) {
                MinecraftForge.EVENT_BUS.post(new GuiScreenEvent.MouseInputEvent.Post(mcScreen));
            }
            //#endif
        }

        @SubscribeEvent(priority = EventPriority.LOWEST)
        //#if MC>=10800
        public void onKeyboardInput(GuiScreenEvent.KeyboardInputEvent.Pre event) throws IOException {
        //#else
        //$$ public void onKeyboardInput(KeyboardInputEvent event) throws IOException {
        //#endif
            event.setCanceled(true);

            getSuperMcGui().handleKeyboardInput();

            //#if MC>=10800
            if (mcScreen.equals(getMinecraft().currentScreen)) {
                MinecraftForge.EVENT_BUS.post(new GuiScreenEvent.KeyboardInputEvent.Post(mcScreen));
            }
            //#endif
        }
        */
    }
    //#if MC<=10710
    //$$ @Cancelable
    //$$ public static class MouseInputEvent extends Event {}
    //$$ @Cancelable
    //$$ public static class KeyboardInputEvent extends Event {}
    //#endif
}
