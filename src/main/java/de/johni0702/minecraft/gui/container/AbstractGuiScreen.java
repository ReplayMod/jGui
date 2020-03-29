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
import lombok.Getter;
import lombok.Setter;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.crash.CrashException;

//#if MC>=11400
import de.johni0702.minecraft.gui.versions.MCVer.Keyboard;
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

    @Getter
    @Setter
    private Background background = Background.DEFAULT;

    @Getter
    private boolean enabledRepeatedKeyEvents = true;

    @Getter
    @Setter
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
            final GuiElement tooltip = forEach(GuiElement.class).getTooltip(renderInfo);
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
                    wrapped.renderBackground();
                    break;
                case TRANSPARENT:
                    int top = 0xc0_10_10_10, bottom = 0xd0_10_10_10;
                    renderer.drawRect(0, 0, size.getWidth(), size.getHeight(), top, top, bottom, bottom);
                    break;
                case DIRT:
                    wrapped.renderDirtBackground(0);
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

    public void setEnabledRepeatedKeyEvents(boolean enableRepeatKeyEvents) {
        this.enabledRepeatedKeyEvents = enableRepeatKeyEvents;
        if (wrapped.active) {
            Keyboard.enableRepeatEvents(enableRepeatKeyEvents);
        }
    }

    public void display() {
        getMinecraft().openScreen(toMinecraft());
    }

    protected class MinecraftGuiScreen extends net.minecraft.client.gui.screen.Screen {
        private MinecraftGuiRenderer renderer;
        private boolean active;

        //#if MC>=11400
        protected MinecraftGuiScreen() {
            super(null);
        }

        @Override
        public String getNarrationMessage() {
            return title == null ? "" : title.getString();
        }
        //#endif

        @Override
        //#if MC>=11400
        public void render(int mouseX, int mouseY, float partialTicks) {
        //#else
        //$$ public void drawScreen(int mouseX, int mouseY, float partialTicks) {
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
            for (int layer = 0; layer <= layers; layer++) {
                draw(renderer, screenSize, renderInfo.layer(layer));
            }
        }

        //#if MC>=11400
        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            if (!forEach(Typeable.class).typeKey(MouseUtils.getMousePos(), keyCode, '\0', hasControlDown(), hasShiftDown())) {
                if (suppressVanillaKeys) {
                    return false;
                }
                return super.keyPressed(keyCode, scanCode, modifiers);
            }
            return true;
        }

        @Override
        public boolean charTyped(char keyChar, int modifiers) {
            if (!forEach(Typeable.class).typeKey(MouseUtils.getMousePos(), 0, keyChar, hasControlDown(), hasShiftDown())) {
                if (suppressVanillaKeys) {
                    return false;
                }
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
        //$$     if (!forEach(Typeable.class).typeKey(
        //$$             MouseUtils.getMousePos(), keyCode, typedChar, isCtrlKeyDown(), isShiftKeyDown())) {
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
            forEach(Closeable.class).close();
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
            renderer = new MinecraftGuiRenderer(MCVer.newScaledResolution(this.minecraft));
            forEach(Loadable.class).load();
        }

        public T getWrapper() {
            return AbstractGuiScreen.this.getThis();
        }
    }

    public enum Background {
        NONE, DEFAULT, TRANSPARENT, DIRT;
    }
}
