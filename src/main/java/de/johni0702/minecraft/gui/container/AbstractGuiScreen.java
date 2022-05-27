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
import de.johni0702.minecraft.gui.element.GuiLabel;
import de.johni0702.minecraft.gui.function.*;
import de.johni0702.minecraft.gui.utils.MouseUtils;
import de.johni0702.minecraft.gui.utils.lwjgl.Dimension;
import de.johni0702.minecraft.gui.utils.lwjgl.Point;
import de.johni0702.minecraft.gui.utils.lwjgl.ReadableDimension;
import de.johni0702.minecraft.gui.utils.lwjgl.ReadablePoint;
import de.johni0702.minecraft.gui.versions.MCVer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.crash.CrashException;

//#if MC>=11400
import de.johni0702.minecraft.gui.versions.MCVer.Keyboard;
import net.minecraft.text.Text;
import static de.johni0702.minecraft.gui.versions.MCVer.literalText;
//#else
//$$ import org.lwjgl.input.Keyboard;
//$$ import org.lwjgl.input.Mouse;
//#endif

//#if MC>=10800
import com.mojang.blaze3d.platform.GlStateManager;

//#if MC<11400
//$$ import java.io.IOException;
//#endif
//#endif

public abstract class AbstractGuiScreen<T extends AbstractGuiScreen<T>> extends AbstractGuiContainer<T> {

    private final MinecraftGuiScreen wrapped = new MinecraftGuiScreen();

    private Dimension screenSize;

    private Background background = Background.DEFAULT;

    private boolean enabledRepeatedKeyEvents = true;

    private GuiLabel title;

    protected boolean suppressVanillaKeys;

    public net.minecraft.client.gui.screen.Screen toMinecraft() {
        return wrapped;
    }

    @Override
    public void layout(ReadableDimension size, RenderInfo renderInfo) {
        if (size == null) {
            size = screenSize;
        }
        if (renderInfo.layer == 0) {
            if (title != null) {
                title.layout(title.getMinSize(), renderInfo);
            }
        }
        super.layout(size, renderInfo);
        if (renderInfo.layer == getMaxLayer()) {
            final GuiElement tooltip = forEach(GuiElement.class, e -> e.getTooltip(renderInfo));
            if (tooltip != null) {
                tooltip.layout(tooltip.getMinSize(), renderInfo);
            }
        }
    }

    @Override
    public void draw(GuiRenderer renderer, ReadableDimension size, RenderInfo renderInfo) {
        if (renderInfo.layer == 0) {
            switch (background) {
                case NONE:
                    break;
                case DEFAULT:
                    //#if MC>=11600
                    wrapped.renderBackground(renderer.getMatrixStack());
                    //#else
                    //$$ wrapped.renderBackground();
                    //#endif
                    break;
                case TRANSPARENT:
                    int top = 0xc0_10_10_10, bottom = 0xd0_10_10_10;
                    renderer.drawRect(0, 0, size.getWidth(), size.getHeight(), top, top, bottom, bottom);
                    break;
                case DIRT:
                    //#if MC>=11600
                    wrapped.renderBackgroundTexture(0);
                    //#else
                    //$$ wrapped.renderDirtBackground(0);
                    //#endif
                    break;
            }
            if (title != null) {
                ReadableDimension titleSize = title.getMinSize();
                int x = screenSize.getWidth() / 2 - titleSize.getWidth() / 2;
                OffsetGuiRenderer eRenderer = new OffsetGuiRenderer(renderer, new Point(x, 10), new Dimension(0, 0));
                title.draw(eRenderer, titleSize, renderInfo);
            }
        }
        super.draw(renderer, size, renderInfo);
        if (renderInfo.layer == getMaxLayer()) {
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

    public void setEnabledRepeatedKeyEvents(boolean enableRepeatKeyEvents) {
        this.enabledRepeatedKeyEvents = enableRepeatKeyEvents;
        if (wrapped.active) {
            Keyboard.enableRepeatEvents(enableRepeatKeyEvents);
        }
    }

    public void display() {
        getMinecraft().openScreen(toMinecraft());
    }

    public Background getBackground() {
        return this.background;
    }

    public boolean isEnabledRepeatedKeyEvents() {
        return this.enabledRepeatedKeyEvents;
    }

    public GuiLabel getTitle() {
        return this.title;
    }

    public void setBackground(Background background) {
        this.background = background;
    }

    public void setTitle(GuiLabel title) {
        this.title = title;
    }

    protected class MinecraftGuiScreen extends net.minecraft.client.gui.screen.Screen {
        private boolean active;

        //#if MC>=11400
        protected MinecraftGuiScreen() {
            super(null);
        }

        @Override
        public Text getTitle() {
            GuiLabel title = AbstractGuiScreen.this.title;
            return literalText(title == null ? "" : title.getText());
        }
        //#endif

        @Override
        //#if MC>=11600
        public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        //#else
        //#if MC>=11400
        //$$ public void render(int mouseX, int mouseY, float partialTicks) {
        //#else
        //$$ public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        //#endif
        //$$     MatrixStack stack = new MatrixStack();
        //#endif
            // The Forge loading screen apparently leaves one of the textures of the GlStateManager in an
            // incorrect state which can cause the whole screen to just remain white. This is a workaround.
            //#if MC>=10800 && MC<11400
            //$$ GlStateManager.disableTexture2D();
            //$$ GlStateManager.enableTexture2D();
            //#endif

            int layers = getMaxLayer();
            RenderInfo renderInfo = new RenderInfo(partialTicks, mouseX, mouseY, 0);
            for (int layer = 0; layer <= layers; layer++) {
                layout(screenSize, renderInfo.layer(layer));
            }
            MinecraftGuiRenderer renderer = new MinecraftGuiRenderer(stack);
            for (int layer = 0; layer <= layers; layer++) {
                draw(renderer, screenSize, renderInfo.layer(layer));
            }
        }

        //#if MC>=11400
        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            Point mouse = MouseUtils.getMousePos();
            boolean ctrlDown = hasControlDown();
            boolean shiftDown = hasShiftDown();
            if (!invokeHandlers(Typeable.class, e -> e.typeKey(mouse, keyCode, '\0', ctrlDown, shiftDown))) {
                if (suppressVanillaKeys) {
                    return false;
                }
                return super.keyPressed(keyCode, scanCode, modifiers);
            }
            return true;
        }

        @Override
        public boolean charTyped(char keyChar, int scanCode) {
            Point mouse = MouseUtils.getMousePos();
            boolean ctrlDown = hasControlDown();
            boolean shiftDown = hasShiftDown();
            if (!invokeHandlers(Typeable.class, e -> e.typeKey(mouse, 0, keyChar, ctrlDown, shiftDown))) {
                if (suppressVanillaKeys) {
                    return false;
                }
                return super.charTyped(keyChar, scanCode);
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
        //$$     Point mouse = MouseUtils.getMousePos();
        //$$     boolean ctrlDown = isCtrlKeyDown();
        //$$     boolean shiftDown = isShiftKeyDown();
        //$$     if (!invokeHandlers(Typeable.class, e -> e.typeKey(mouse, keyCode, typedChar, ctrlDown, shiftDown))) {
        //$$         if (suppressVanillaKeys) {
        //$$             return;
        //$$         }
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
            invokeHandlers(Clickable.class, e -> e.mouseClick(new Point(mouseX, mouseY), mouseButton));
        }

        @Override
        //#if MC>=11400
        public boolean mouseReleased(double mouseXD, double mouseYD, int mouseButton) {
            int mouseX = (int) Math.round(mouseXD), mouseY = (int) Math.round(mouseYD);
            return
        //#else
        //$$ protected void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        //#endif
            invokeHandlers(Draggable.class, e -> e.mouseRelease(new Point(mouseX, mouseY), mouseButton));
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
            invokeHandlers(Draggable.class, e -> e.mouseDrag(new Point(mouseX, mouseY), mouseButton, timeSinceLastClick));
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

        @Override
        //#if MC>=11400
        public void removed() {
        //#else
        //$$ public void onGuiClosed() {
        //#endif
            invokeAll(Closeable.class, Closeable::close);
            active = false;
            if (enabledRepeatedKeyEvents) {
                Keyboard.enableRepeatEvents(false);
            }
        }

        @Override
        //#if MC>=11400
        public void init() {
        //#else
        //$$ public void initGui() {
        //#endif
            active = false;
            if (enabledRepeatedKeyEvents) {
                Keyboard.enableRepeatEvents(true);
            }
            screenSize = new Dimension(width, height);
            invokeAll(Loadable.class, Loadable::load);
        }

        public T getWrapper() {
            return AbstractGuiScreen.this.getThis();
        }
    }

    public enum Background {
        NONE, DEFAULT, TRANSPARENT, DIRT;
    }
}
