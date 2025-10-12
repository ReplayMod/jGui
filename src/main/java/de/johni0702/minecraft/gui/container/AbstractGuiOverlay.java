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
import de.johni0702.minecraft.gui.versions.ScreenExt;
import de.johni0702.minecraft.gui.versions.callbacks.PreTickCallback;
import de.johni0702.minecraft.gui.versions.callbacks.RenderHudCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.crash.CrashException;

//#if MC>=12000
//$$ import net.minecraft.client.gui.DrawContext;
//#else
import net.minecraft.client.util.math.MatrixStack;
//#endif

//#if MC>=11400
import static de.johni0702.minecraft.gui.versions.MCVer.literalText;
//#endif

//#if MC>=11400
import net.minecraft.client.util.Window;
//#else
//$$ import org.lwjgl.input.Mouse;
//$$ import net.minecraft.client.gui.ScaledResolution;
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
                invokeAll(Loadable.class, Loadable::load);
                eventHandler.register();
            } else {
                invokeAll(Closeable.class, Closeable::close);
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
        return ((ScreenExt) userInputGuiScreen).doesPassEvents();
    }

    /**
     * Enable/Disable user input for this overlay while the mouse is visible.
     * User input are things like moving the player, attacking/interacting, key bindings but not input into the
     * GUI elements such as text fields.
     * Default for overlays is {@code true} whereas for normal GUI screens it is {@code false}.
     * @param allowUserInput {@code true} to allow user input, {@code false} to disallow it
     */
    public void setAllowUserInput(boolean allowUserInput) {
        ((ScreenExt) userInputGuiScreen).setPassEvents(allowUserInput);
    }

    private void updateUserInputGui() {
        MinecraftClient mc = getMinecraft();
        if (visible) {
            if (mouseVisible) {
                if (mc.currentScreen == null) {
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
            final GuiElement tooltip = forEach(GuiElement.class, e -> e.getTooltip(renderInfo));
            if (tooltip != null) {
                tooltip.layout(tooltip.getMinSize(), renderInfo);
            }
        }
    }

    @Override
    public void draw(GuiRenderer renderer, ReadableDimension size, RenderInfo renderInfo) {
        super.draw(renderer, size, renderInfo);
        if (mouseVisible && renderInfo.layer == getMaxLayer()) {
            final GuiElement tooltip = forEach(GuiElement.class, e -> e.getTooltip(renderInfo));
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

    private class EventHandler extends EventRegistrations {
        private EventHandler() {}

        { on(RenderHudCallback.EVENT, this::renderOverlay); }
        //#if MC>=12000
        //$$ private void renderOverlay(DrawContext stack, float partialTicks) {
        //#else
        private void renderOverlay(MatrixStack stack, float partialTicks) {
        //#endif
            updateUserInputGui();
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
            MinecraftGuiRenderer renderer = new MinecraftGuiRenderer(stack);
            for (int layer = 0; layer <= layers; layer++) {
                draw(renderer, screenSize, renderInfo.layer(layer));
            }
        }

        { on(PreTickCallback.EVENT, () -> invokeAll(Tickable.class, Tickable::tick)); }

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
            }
        }
    }

    protected class UserInputGuiScreen extends net.minecraft.client.gui.screen.Screen {

        //#if MC>=11400
        UserInputGuiScreen() {
            super(literalText(""));
        }
        //#endif

        {
            ((ScreenExt) this).setPassEvents(true);
        }

        //#if MC>=11400
        @Override
        //#if MC>=12109
        //$$ public boolean keyPressed(net.minecraft.client.input.KeyInput mcKeyInput) {
        //$$     KeyInput keyInput = new KeyInput(mcKeyInput);
        //#else
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            KeyInput keyInput = new KeyInput(keyCode, scanCode, modifiers);
        //#endif
            if (!invokeHandlers(KeyHandler.class, e -> e.handleKey(keyInput))) {
                //#if MC>=12109
                //$$ return super.keyPressed(mcKeyInput);
                //#else
                return super.keyPressed(keyCode, scanCode, modifiers);
                //#endif
            }
            return true;
        }

        @Override
        //#if MC>=12109
        //$$ public boolean charTyped(net.minecraft.client.input.CharInput mcCharInput) {
        //$$     CharInput charInput = new CharInput(mcCharInput);
        //#else
        public boolean charTyped(char keyChar, int modifiers) {
            CharInput charInput = new CharInput(keyChar, modifiers);
        //#endif
            if (!invokeHandlers(CharHandler.class, e -> e.handleChar(charInput))) {
                //#if MC>=12109
                //$$ return super.charTyped(mcCharInput);
                //#else
                return super.charTyped(keyChar, modifiers);
                //#endif
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
        //$$     if (keyCode != 0) invokeHandlers(KeyHandler.class, e -> e.handleKey(new KeyInput(keyCode)));
        //$$     if (typedChar != '\0') invokeHandlers(CharHandler.class, e -> e.handleChar(new CharInput(typedChar)));
        //$$     if (closeable) {
        //$$         super.keyTyped(typedChar, keyCode);
        //$$     }
        //$$ }
        //#endif

        @Override
        //#if MC>=12109
        //$$ public boolean mouseClicked(net.minecraft.client.gui.Click mcClick, boolean doubled) {
        //$$     Click click = new Click(mcClick);
        //$$     return
        //#elseif MC>=11400
        public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
            Click click = new Click(mouseX, mouseY, mouseButton);
            return
        //#else
        //$$ protected void mouseClicked(int mouseX, int mouseY, int mouseButton)
                //#if MC>=10800
                //$$ throws IOException
                //#endif
        //$$ {
        //$$     Click click = new Click(mouseX, mouseY, mouseButton);
        //#endif
            invokeHandlers(Clickable.class, e -> e.mouseClick(click));
        }

        @Override
        //#if MC>=12109
        //$$ public boolean mouseReleased(net.minecraft.client.gui.Click mcClick) {
        //$$     Click click = new Click(mcClick);
        //$$     return
        //#elseif MC>=11400
        public boolean mouseReleased(double mouseX, double mouseY, int mouseButton) {
            Click click = new Click(mouseX, mouseY, mouseButton);
            return
        //#else
        //$$ protected void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        //$$     Click click = new Click(mouseX, mouseY, mouseButton);
        //#endif
            invokeHandlers(Draggable.class, e -> e.mouseRelease(click));
        }

        @Override
        //#if MC>=12109
        //$$ public boolean mouseDragged(net.minecraft.client.gui.Click mcClick, double deltaX, double deltaY) {
        //$$     Click click = new Click(mcClick);
        //$$     return
        //#elseif MC>=11400
        public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double deltaX, double deltaY) {
            Click click = new Click(mouseX, mouseY, mouseButton);
            return
        //#else
        //$$ protected void mouseClickMove(int mouseX, int mouseY, int mouseButton, long timeSinceLastClick) {
        //$$     Click click = new Click(mouseX, mouseY, mouseButton);
        //#endif
            invokeHandlers(Draggable.class, e -> e.mouseDrag(click));
        }

        @Override
        //#if MC>=11400
        public void tick() {
        //#else
        //$$ public void updateScreen() {
        //#endif
            invokeAll(Tickable.class, Tickable::tick);
        }

        //#if MC>=11400
        @Override
        public boolean mouseScrolled(
                //#if MC>=11400
                double mouseX,
                double mouseY,
                //#endif
                //#if MC>=12002
                //$$ double dWheelHorizontal,
                //#endif
                double dWheel
        ) {
            //#if MC>=11400
            Point mouse = new Point((int) mouseX, (int) mouseY);
            //#else
            //$$ Point mouse = MouseUtils.getMousePos();
            //#endif
            int wheel = (int) (dWheel * 120);
            return invokeHandlers(Scrollable.class, e -> e.scroll(mouse, wheel));
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
        //$$         Point mouse = MouseUtils.getMousePos();
        //$$         int wheel = Mouse.getEventDWheel();
        //$$         invokeHandlers(Scrollable.class, e -> e.scroll(mouse, wheel));
        //$$     }
        //$$ }
        //#endif

        //#if MC>=11400
        @Override
        public void onClose() {
            if (closeable) {
                super.onClose();
            }
        }
        //#endif

        @Override
        //#if MC>=11400
        public void removed() {
        //#else
        //$$ public void onGuiClosed() {
        //#endif
            if (closeable) {
                mouseVisible = false;
            }
        }

        //#if MC>=12002
        //$$ @Override
        //$$ public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        //$$ }
        //#endif

        public AbstractGuiOverlay<T> getOverlay() {
            return AbstractGuiOverlay.this;
        }
    }
}
