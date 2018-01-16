package de.johni0702.minecraft.gui.container;

import de.johni0702.minecraft.gui.function.Draggable;
import de.johni0702.minecraft.gui.function.Scrollable;
import de.johni0702.minecraft.gui.function.Typeable;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.util.ReadablePoint;

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

            getSuperMcGui().setWorldAndResolution(mcScreen.mc, mcScreen.width, mcScreen.height);
            getSuperMcGui().initGui();
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
        try {
            mcScreen.handleMouseInput();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean typeKey(ReadablePoint mousePosition, int keyCode, char keyChar, boolean ctrlDown, boolean shiftDown) {
        try {
            mcScreen.handleKeyboardInput();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    // Used when wrapping an already existing mc.GuiScreen
    private class EventHandler {
        private boolean active;

        @SubscribeEvent(priority = EventPriority.LOWEST)
        public void onGuiClosed(GuiOpenEvent event) {
            MinecraftForge.EVENT_BUS.unregister(this);

            if (active) {
                active = false;
                getSuperMcGui().onGuiClosed();
            }
        }

        @SubscribeEvent
        public void onGuiRender(GuiScreenEvent.DrawScreenEvent.Post event) {
            getSuperMcGui().drawScreen(event.getMouseX(), event.getMouseY(), event.getRenderPartialTicks());
        }

        @SubscribeEvent
        public void tickOverlay(TickEvent.ClientTickEvent event) {
            if (event.phase == TickEvent.Phase.START) {
                getSuperMcGui().updateScreen();
            }
        }

        @SubscribeEvent(priority = EventPriority.LOWEST)
        public void onMouseInput(GuiScreenEvent.MouseInputEvent.Pre event) throws IOException {
            event.setCanceled(true);

            getSuperMcGui().handleMouseInput();

            if (mcScreen.equals(getMinecraft().currentScreen)) {
                MinecraftForge.EVENT_BUS.post(new GuiScreenEvent.MouseInputEvent.Post(mcScreen));
            }
        }

        @SubscribeEvent(priority = EventPriority.LOWEST)
        public void onKeyboardInput(GuiScreenEvent.KeyboardInputEvent.Pre event) throws IOException {
            event.setCanceled(true);

            getSuperMcGui().handleKeyboardInput();

            if (mcScreen.equals(getMinecraft().currentScreen)) {
                MinecraftForge.EVENT_BUS.post(new GuiScreenEvent.KeyboardInputEvent.Post(mcScreen));
            }
        }
    }
}
