/*
 * This file is part of jGui API, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2016 johni0702 <https://github.com/johni0702>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package de.johni0702.minecraft.gui.container;

import de.johni0702.minecraft.gui.GuiRenderer;
import de.johni0702.minecraft.gui.MinecraftGuiRenderer;
import de.johni0702.minecraft.gui.OffsetGuiRenderer;
import de.johni0702.minecraft.gui.RenderInfo;
import de.johni0702.minecraft.gui.element.GuiElement;
import de.johni0702.minecraft.gui.function.*;
import de.johni0702.minecraft.gui.utils.EventRegistrations;
import de.johni0702.minecraft.gui.utils.MouseUtils;
import de.johni0702.minecraft.gui.utils.lwjgl.Dimension;
import de.johni0702.minecraft.gui.utils.lwjgl.Point;
import de.johni0702.minecraft.gui.utils.lwjgl.ReadableDimension;
import de.johni0702.minecraft.gui.utils.lwjgl.ReadablePoint;
import de.johni0702.minecraft.gui.versions.MCVer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.crash.CrashException;

//#if MC>=11400
import de.johni0702.minecraft.gui.versions.callbacks.PreTickCallback;
import net.minecraft.text.LiteralText;
//#endif

//#if MC>=11400
import net.minecraft.client.util.Window;
//#else
//$$ import org.lwjgl.input.Mouse;
//$$ import net.minecraft.client.gui.ScaledResolution;
//#endif

//#if FABRIC>=1
import de.johni0702.minecraft.gui.versions.callbacks.PostRenderHudCallback;
//#else
//$$ import net.minecraftforge.client.event.RenderGameOverlayEvent;
//#if MC>=10800
//#if MC>=11400
//$$ import net.minecraftforge.eventbus.api.SubscribeEvent;
//#else
//$$ import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
//#endif
//#if MC>=11400
//#else
//$$ import net.minecraftforge.fml.common.gameevent.TickEvent;
//#endif
//#else
//$$ import cpw.mods.fml.common.eventhandler.SubscribeEvent;
//$$ import cpw.mods.fml.common.gameevent.TickEvent;
//#endif
//#endif

//#if MC>=10800 && MC<11400
//$$ import java.io.IOException;
//#endif

public abstract class AbstractGuiOverlay<T extends AbstractGuiOverlay<T>> extends AbstractGuiContainer<T> {

    private final UserInputGuiScreen userInputGuiScreen = new UserInputGuiScreen();
    private final EventHandler eventHandler = new EventHandler();
    private boolean visible;
    private Dimension screenSize;
    private boolean mouseVisible;
    private boolean closeable = true;

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        if (this.visible != visible) {
            if (visible) {
                forEach(Loadable.class).load();
                eventHandler.register();
            } else {
                forEach(Closeable.class).close();
                eventHandler.unregister();
            }
            updateUserInputGui();
        }
        this.visible = visible;
    }

    public boolean isMouseVisible() {
        return mouseVisible;
    }

    public void setMouseVisible(boolean mouseVisible) {
        this.mouseVisible = mouseVisible;
        updateUserInputGui();
    }

    public boolean isCloseable() {
        return closeable;
    }

    public void setCloseable(boolean closeable) {
        this.closeable = closeable;
    }

    /**
     * @see #setAllowUserInput(boolean)
     */
    public boolean isAllowUserInput() {
        return userInputGuiScreen.passEvents;
    }

    /**
     * Enable/Disable user input for this overlay while the mouse is visible.
     * User input are things like moving the player, attacking/interacting, key bindings but not input into the
     * GUI elements such as text fields.
     * Default for overlays is {@code true} whereas for normal GUI screens it is {@code false}.
     * @param allowUserInput {@code true} to allow user input, {@code false} to disallow it
     * @see net.minecraft.client.gui.screen.Screen#passEvents
     */
    public void setAllowUserInput(boolean allowUserInput) {
        userInputGuiScreen.passEvents = allowUserInput;
    }

    private void updateUserInputGui() {
        MinecraftClient mc = getMinecraft();
        if (visible) {
            if (mouseVisible) {
                if (mc.currentScreen != userInputGuiScreen) {
                    mc.openScreen(userInputGuiScreen);
                }
            } else {
                if (mc.currentScreen == userInputGuiScreen) {
                    mc.openScreen(null);
                }
            }
        }
    }

    @Override
    public void layout(ReadableDimension size, RenderInfo renderInfo) {
        if (size == null) {
            size = screenSize;
        }
        super.layout(size, renderInfo);
        if (mouseVisible && renderInfo.layer == getMaxLayer()) {
            final GuiElement tooltip = forEach(GuiElement.class).getTooltip(renderInfo);
            if (tooltip != null) {
                tooltip.layout(tooltip.getMinSize(), renderInfo);
            }
        }
    }

    @Override
    public void draw(GuiRenderer renderer, ReadableDimension size, RenderInfo renderInfo) {
        super.draw(renderer, size, renderInfo);
        if (mouseVisible && renderInfo.layer == getMaxLayer()) {
            final GuiElement tooltip = forEach(GuiElement.class).getTooltip(renderInfo);
            if (tooltip != null) {
                final ReadableDimension tooltipSize = tooltip.getMinSize();
                int x, y;
                if (renderInfo.mouseX + 8 + tooltipSize.getWidth() < screenSize.getWidth()) {
                    x = renderInfo.mouseX + 8;
                } else {
                    x = screenSize.getWidth() - tooltipSize.getWidth() - 1;
                }
                if (renderInfo.mouseY + 8 + tooltipSize.getHeight() < screenSize.getHeight()) {
                    y = renderInfo.mouseY + 8;
                } else {
                    y = screenSize.getHeight() - tooltipSize.getHeight() - 1;
                }
                final ReadablePoint position = new Point(x, y);
                try {
                    OffsetGuiRenderer eRenderer = new OffsetGuiRenderer(renderer, position, tooltipSize);
                    tooltip.draw(eRenderer, tooltipSize, renderInfo);
                } catch (Exception ex) {
                    CrashReport crashReport = CrashReport.create(ex, "Rendering Gui Tooltip");
                    renderInfo.addTo(crashReport);
                    CrashReportSection category = crashReport.addElement("Gui container details");
                    MCVer.addDetail(category, "Container", this::toString);
                    MCVer.addDetail(category, "Width", () -> "" + size.getWidth());
                    MCVer.addDetail(category, "Height", () -> "" + size.getHeight());
                    category = crashReport.addElement("Tooltip details");
                    MCVer.addDetail(category, "Element", tooltip::toString);
                    MCVer.addDetail(category, "Position", position::toString);
                    MCVer.addDetail(category, "Size", tooltipSize::toString);
                    throw new CrashException(crashReport);
                }
            }
        }
    }

    @Override
    public ReadableDimension getMinSize() {
        return screenSize;
    }

    @Override
    public ReadableDimension getMaxSize() {
        return screenSize;
    }

    //#if MC>=10800
    private
    //#else
    //$$ public
    //#endif
    class EventHandler extends EventRegistrations {
        private MinecraftGuiRenderer renderer;

        private EventHandler() {}

        //#if FABRIC>=1
        { on(PostRenderHudCallback.EVENT, this::renderOverlay); }
        private void renderOverlay(float partialTicks) {
        //#else
        //$$ @SubscribeEvent
        //$$ public void renderOverlay(RenderGameOverlayEvent.Post event) {
        //$$     if (MCVer.getType(event) != RenderGameOverlayEvent.ElementType.ALL) return;
        //$$     float partialTicks = MCVer.getPartialTicks(event);
        //#endif
            updateRenderer();
            int layers = getMaxLayer();
            int mouseX = -1, mouseY = -1;
            if (mouseVisible) {
                Point mouse = MouseUtils.getMousePos();
                mouseX = mouse.getX();
                mouseY = mouse.getY();
            }
            RenderInfo renderInfo = new RenderInfo(partialTicks, mouseX, mouseY, 0);
            for (int layer = 0; layer <= layers; layer++) {
                layout(screenSize, renderInfo.layer(layer));
            }
            for (int layer = 0; layer <= layers; layer++) {
                draw(renderer, screenSize, renderInfo.layer(layer));
            }
        }

        //#if MC>=11400
        { on(PreTickCallback.EVENT, () -> forEach(Tickable.class).tick()); }
        //#else
        //$$ @SubscribeEvent
        //$$ public void tickOverlay(TickEvent.ClientTickEvent event) {
        //$$     if (event.phase == TickEvent.Phase.START) {
        //$$         forEach(Tickable.class).tick();
        //$$     }
        //$$ }
        //#endif

        private void updateRenderer() {
            MinecraftClient mc = getMinecraft();
            //#if MC>=11400
            Window
            //#else
            //$$ ScaledResolution
            //#endif
                    res = MCVer.newScaledResolution(mc);
            if (screenSize == null
                    || screenSize.getWidth() != res.getScaledWidth()
                    || screenSize.getHeight() != res.getScaledHeight()) {
                screenSize = new Dimension(res.getScaledWidth(), res.getScaledHeight());
                renderer = new MinecraftGuiRenderer(res);
            }
        }
    }

    protected class UserInputGuiScreen extends net.minecraft.client.gui.screen.Screen {

        //#if MC>=11400
        UserInputGuiScreen() {
            super(new LiteralText(""));
        }
        //#endif

        {
            this.passEvents = true;
        }

        //#if MC>=11400
        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            if (!forEach(Typeable.class).typeKey(MouseUtils.getMousePos(), keyCode, '\0', hasControlDown(), hasShiftDown())) {
                return super.keyPressed(keyCode, scanCode, modifiers);
            }
            return true;
        }

        @Override
        public boolean charTyped(char keyChar, int modifiers) {
            if (!forEach(Typeable.class).typeKey(MouseUtils.getMousePos(), 0, keyChar, hasControlDown(), hasShiftDown())) {
                return super.charTyped(keyChar, modifiers);
            }
            return true;
        }
        //#else
        //$$ @Override
        //$$ protected void keyTyped(char typedChar, int keyCode)
                //#if MC>=10800
                //$$ throws IOException
                //#endif
        //$$ {
        //$$     forEach(Typeable.class).typeKey(MouseUtils.getMousePos(), keyCode, typedChar, isCtrlKeyDown(), isShiftKeyDown());
        //$$     if (closeable) {
        //$$         super.keyTyped(typedChar, keyCode);
        //$$     }
        //$$ }
        //#endif

        @Override
        //#if MC>=11400
        public boolean mouseClicked(double mouseXD, double mouseYD, int mouseButton) {
            int mouseX = (int) Math.round(mouseXD), mouseY = (int) Math.round(mouseYD);
            return
        //#else
        //$$ protected void mouseClicked(int mouseX, int mouseY, int mouseButton)
                //#if MC>=10800
                //$$ throws IOException
                //#endif
        //$$ {
        //#endif
            forEach(Clickable.class).mouseClick(new Point(mouseX, mouseY), mouseButton);
        }

        @Override
        //#if MC>=11400
        public boolean mouseReleased(double mouseXD, double mouseYD, int mouseButton) {
            int mouseX = (int) Math.round(mouseXD), mouseY = (int) Math.round(mouseYD);
            return
        //#else
        //$$ protected void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        //#endif
            forEach(Draggable.class).mouseRelease(new Point(mouseX, mouseY), mouseButton);
        }

        @Override
        //#if MC>=11400
        public boolean mouseDragged(double mouseXD, double mouseYD, int mouseButton, double deltaX, double deltaY) {
            int mouseX = (int) Math.round(mouseXD), mouseY = (int) Math.round(mouseYD);
            long timeSinceLastClick = 0;
            return
        //#else
        //$$ protected void mouseClickMove(int mouseX, int mouseY, int mouseButton, long timeSinceLastClick) {
        //#endif
            forEach(Draggable.class).mouseDrag(new Point(mouseX, mouseY), mouseButton, timeSinceLastClick);
        }

        @Override
        //#if MC>=11400
        public void tick() {
        //#else
        //$$ public void updateScreen() {
        //#endif
            forEach(Tickable.class).tick();
        }

        //#if MC>=11400
        @Override
        public boolean mouseScrolled(
                //#if MC>=11400
                double mouseX,
                double mouseY,
                //#endif
                double dWheel
        ) {
            dWheel *= 120;
            return forEach(Scrollable.class).scroll(
                    //#if MC>=11400
                    new Point((int) mouseX, (int) mouseY),
                    //#else
                    //$$ MouseUtils.getMousePos(),
                    //#endif
                    (int) dWheel
            );
        }
        //#else
        //$$ @Override
        //$$ public void handleMouseInput()
                //#if MC>=10800
                //$$ throws IOException
                //#endif
        //$$ {
        //$$     super.handleMouseInput();
        //$$     if (Mouse.hasWheel() && Mouse.getEventDWheel() != 0) {
        //$$         forEach(Scrollable.class).scroll(MouseUtils.getMousePos(), Mouse.getEventDWheel());
        //$$     }
        //$$ }
        //#endif

        @Override
        //#if MC>=11400
        public void removed() {
        //#else
        //$$ public void onGuiClosed() {
        //#endif
            mouseVisible = false;
        }

        public AbstractGuiOverlay<T> getOverlay() {
            return AbstractGuiOverlay.this;
        }
    }
}
