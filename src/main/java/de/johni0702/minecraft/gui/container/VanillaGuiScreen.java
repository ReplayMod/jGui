package de.johni0702.minecraft.gui.container;

import de.johni0702.minecraft.gui.function.CharHandler;
import de.johni0702.minecraft.gui.function.CharInput;
import de.johni0702.minecraft.gui.function.Click;
import de.johni0702.minecraft.gui.function.Draggable;
import de.johni0702.minecraft.gui.function.KeyHandler;
import de.johni0702.minecraft.gui.function.KeyInput;
import de.johni0702.minecraft.gui.function.Scrollable;
import de.johni0702.minecraft.gui.function.Tickable;
import de.johni0702.minecraft.gui.utils.EventRegistrations;
import de.johni0702.minecraft.gui.utils.MouseUtils;
import de.johni0702.minecraft.gui.utils.lwjgl.Point;
import de.johni0702.minecraft.gui.utils.lwjgl.ReadablePoint;
import de.johni0702.minecraft.gui.versions.MCVer;
import de.johni0702.minecraft.gui.versions.callbacks.InitScreenCallback;
import de.johni0702.minecraft.gui.versions.callbacks.OpenGuiScreenCallback;
import de.johni0702.minecraft.gui.versions.callbacks.PostRenderScreenCallback;
import de.johni0702.minecraft.gui.versions.callbacks.PreTickCallback;

//#if MC>=12000
//$$ import net.minecraft.client.gui.DrawContext;
//#else
import net.minecraft.client.util.math.MatrixStack;
//#endif

//#if FABRIC>=1
import de.johni0702.minecraft.gui.versions.callbacks.KeyboardCallback;
import de.johni0702.minecraft.gui.versions.callbacks.MouseCallback;
//#elseif MC<=11202
//$$ import net.minecraftforge.client.event.GuiOpenEvent;
//$$ import net.minecraftforge.client.event.GuiScreenEvent;
//$$ import net.minecraftforge.fml.common.eventhandler.EventPriority;
//$$ import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
//#endif

//#if MC<10800
//$$ import cpw.mods.fml.common.eventhandler.Cancelable;
//$$ import cpw.mods.fml.common.eventhandler.Event;
//$$ import cpw.mods.fml.common.eventhandler.SubscribeEvent;
//#endif

//#if MC<11400
//$$ import net.minecraftforge.common.MinecraftForge;
//$$ import java.io.IOException;
//#endif

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;


public class VanillaGuiScreen extends GuiScreen implements Draggable, KeyHandler, CharHandler, Scrollable, Tickable {

    private static final Map<net.minecraft.client.gui.screen.Screen, VanillaGuiScreen> WRAPPERS =
            Collections.synchronizedMap(new WeakHashMap<>());

    public static VanillaGuiScreen wrap(net.minecraft.client.gui.screen.Screen originalGuiScreen) {
        VanillaGuiScreen gui = WRAPPERS.get(originalGuiScreen);
        if (gui == null) {
            WRAPPERS.put(originalGuiScreen, gui = new VanillaGuiScreen(originalGuiScreen));
            gui.register();
        }
        return gui;
    }

    // Use wrap instead and make sure to preserve the existing layout.
    // (or if you really want your own, inline this code)
    @Deprecated
    public static VanillaGuiScreen setup(net.minecraft.client.gui.screen.Screen originalGuiScreen) {
        VanillaGuiScreen gui = new VanillaGuiScreen(originalGuiScreen);
        gui.register();
        return gui;
    }

    private final net.minecraft.client.gui.screen.Screen mcScreen;
    private final EventHandler eventHandler = new EventHandler();

    public VanillaGuiScreen(net.minecraft.client.gui.screen.Screen mcScreen) {
        this.mcScreen = mcScreen;
        this.suppressVanillaKeys = true;

        super.setBackground(Background.NONE);
    }

    // Needs to be called from or after GuiInitEvent.Post, will auto-unregister on any GuiOpenEvent
    public void register() {
        if (!eventHandler.active) {
            eventHandler.active = true;

            eventHandler.register();

            //#if MC>=12111
            //$$ getSuperMcGui().init(mcScreen.width, mcScreen.height);
            //#else
            getSuperMcGui().init(MCVer.getMinecraft(), mcScreen.width, mcScreen.height);
            //#endif
        }
    }

    public void display() {
        getMinecraft().openScreen(mcScreen);
        register();
    }

    @Override
    public net.minecraft.client.gui.screen.Screen toMinecraft() {
        return mcScreen;
    }

    @Override
    public void setBackground(Background background) {
        throw new UnsupportedOperationException("Cannot set background of vanilla gui screen.");
    }

    private net.minecraft.client.gui.screen.Screen getSuperMcGui() {
        return super.toMinecraft();
    }

    @Override
    public boolean mouseClick(Click click) {
        //#if MC>=11400
        //#else
        //$$ eventHandler.mouseHandled = false;
        //#endif
        return false;
    }

    @Override
    public boolean mouseDrag(Click click) {
        //#if MC>=11400
        //#else
        //$$ eventHandler.mouseHandled = false;
        //#endif
        return false;
    }

    @Override
    public boolean mouseRelease(Click click) {
        //#if MC>=11400
        //#else
        //$$ eventHandler.mouseHandled = false;
        //#endif
        return false;
    }

    @Override
    public boolean scroll(ReadablePoint mousePosition, int dWheel) {
        //#if MC>=11400
        //#else
        //$$ eventHandler.mouseHandled = false;
        //#endif
        return false;
    }

    @Override
    public boolean handleKey(KeyInput keyInput) {
        //#if MC>=11400
        //#else
        //$$ eventHandler.keyHandled = false;
        //#endif
        return false;
    }

    @Override
    public boolean handleChar(CharInput charInput) {
        //#if MC>=11400
        //#else
        //$$ eventHandler.charHandled = false;
        //#endif
        return false;
    }

    @Override
    public void tick() {
        // TODO this is a workaround for ReplayMod#560 until we remove the inner mc screen
        //      see also the note in ReplayMod's GuiBackgroundProcesses
        // If this screen ever becomes the main screen, something has gone wrong.
        if (getSuperMcGui() == getMinecraft().currentScreen) {
            getMinecraft().openScreen(null);
        }
    }

    // Used when wrapping an already existing mc.GuiScreen
    //#if MC>=10800
    private
    //#else
    //$$ public
    //#endif
    class EventHandler extends EventRegistrations
        //#if FABRIC>=1
        implements KeyboardCallback, MouseCallback
        //#endif
    {
        private boolean active;

        { on(OpenGuiScreenCallback.EVENT, screen -> onGuiClosed()); }
        private void onGuiClosed() {
            unregister();

            if (active) {
                active = false;
                getSuperMcGui().removed();
                WRAPPERS.remove(mcScreen, VanillaGuiScreen.this);
            }
        }

        { on(InitScreenCallback.Pre.EVENT, this::preGuiInit); }
        private void preGuiInit(net.minecraft.client.gui.screen.Screen screen) {
            if (screen == mcScreen && active) {
                active = false;
                unregister();
                getSuperMcGui().removed();
                WRAPPERS.remove(mcScreen, VanillaGuiScreen.this);
            }
        }

        { on(PostRenderScreenCallback.EVENT, this::onGuiRender); }
        //#if MC>=12000
        //$$ private void onGuiRender(DrawContext stack, float partialTicks) {
        //#if MC<12106
        //$$     stack.draw(); // flush any buffered changes before we draw using legacy primitives
        //#endif
        //#else
        private void onGuiRender(MatrixStack stack, float partialTicks) {
        //#endif
            Point mousePos = MouseUtils.getMousePos();
            getSuperMcGui().render(
                    //#if MC>=11600
                    stack,
                    //#endif
                    mousePos.getX(), mousePos.getY(), partialTicks);
        }

        { on(PreTickCallback.EVENT, this::tickOverlay); }
        private void tickOverlay() {
            //#if MC>=11400
            getSuperMcGui().tick();
            //#else
            //$$ getSuperMcGui().updateScreen();
            //#endif
        }

        //#if FABRIC>=1
        { on(MouseCallback.EVENT, this); }

        @Override
        public boolean mouseDown(Click click) {
            //#if MC>=12109
            //$$ return getSuperMcGui().mouseClicked(click.toMC(), false);
            //#else
            return getSuperMcGui().mouseClicked(click.x, click.y, click.button);
            //#endif
        }

        @Override
        public boolean mouseDrag(Click click, double dx, double dy) {
            //#if MC>=12109
            //$$ return getSuperMcGui().mouseDragged(click.toMC(), dx, dy);
            //#else
            return getSuperMcGui().mouseDragged(click.x, click.y, click.button, dx, dy);
            //#endif
        }

        @Override
        public boolean mouseUp(Click click) {
            //#if MC>=12109
            //$$ return getSuperMcGui().mouseReleased(click.toMC());
            //#else
            return getSuperMcGui().mouseReleased(click.x, click.y, click.button);
            //#endif
        }

        @Override
        public boolean mouseScroll(double x, double y, double horizontal, double vertical) {
            return getSuperMcGui().mouseScrolled(x, y,
                    //#if MC>=12002
                    //$$ horizontal,
                    //#endif
                    vertical);
        }

        { on(KeyboardCallback.EVENT, this); }

        @Override
        public boolean keyPressed(KeyInput keyInput) {
            //#if MC>=12109
            //$$ return getSuperMcGui().keyPressed(keyInput.toMC());
            //#else
            return getSuperMcGui().keyPressed(keyInput.key, keyInput.scancode, keyInput.modifiers);
            //#endif
        }

        @Override
        public boolean keyReleased(KeyInput keyInput) {
            //#if MC>=12109
            //$$ return getSuperMcGui().keyReleased(keyInput.toMC());
            //#else
            return getSuperMcGui().keyReleased(keyInput.key, keyInput.scancode, keyInput.modifiers);
            //#endif
        }

        @Override
        public boolean charTyped(CharInput charInput) {
            //#if MC>=12109
            //$$ return getSuperMcGui().charTyped(charInput.toMC());
            //#else
            return getSuperMcGui().charTyped(charInput.character, charInput.modifiers);
            //#endif
        }
        //#elseif MC<=11202
        //$$ private boolean mouseHandled;
        //$$ private boolean keyHandled;
        //$$ private boolean charHandled;
        //$$
        //$$ // Mouse/Keyboard events aren't supported in 1.7.10
        //$$ // so this requires a mixin in any mod making use of it
        //$$ // (see ReplayMod: GuiScreenMixin)
        //$$ @SubscribeEvent(priority = EventPriority.LOWEST)
        //#if MC>=10800
        //$$ public void onMouseInput(GuiScreenEvent.MouseInputEvent.Pre event) throws IOException {
        //#else
        //$$ public void onMouseInput(MouseInputEvent event) throws IOException {
        //#endif
        //$$     mouseHandled = true;
        //$$     getSuperMcGui().handleMouseInput();
        //$$     if (mouseHandled) {
        //$$         event.setCanceled(true);
        //$$     }
        //$$ }
        //$$
        //$$ @SubscribeEvent(priority = EventPriority.LOWEST)
        //#if MC>=10800
        //$$ public void onKeyboardInput(GuiScreenEvent.KeyboardInputEvent.Pre event) throws IOException {
        //#else
        //$$ public void onKeyboardInput(KeyboardInputEvent event) throws IOException {
        //#endif
        //$$     // Skip key-up events, we currently have no mechanism to properly handle them
        //$$     if (!org.lwjgl.input.Keyboard.getEventKeyState()) {
        //$$         return;
        //$$     }
        //$$     keyHandled = org.lwjgl.input.Keyboard.getEventKey() != 0;
        //$$     charHandled = org.lwjgl.input.Keyboard.getEventCharacter() != '\0';
        //$$     getSuperMcGui().handleKeyboardInput();
        //$$     if (keyHandled || charHandled) {
        //$$         event.setCanceled(true);
        //$$     }
        //$$ }
        //#endif
    }
    //#if MC<=10710
    //$$ @Cancelable
    //$$ public static class MouseInputEvent extends Event {}
    //$$ @Cancelable
    //$$ public static class KeyboardInputEvent extends Event {}
    //#endif
}
