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
package de.johni0702.minecraft.gui.element;

import de.johni0702.minecraft.gui.GuiRenderer;
import de.johni0702.minecraft.gui.RenderInfo;
import de.johni0702.minecraft.gui.container.GuiContainer;
import de.johni0702.minecraft.gui.function.Clickable;
import de.johni0702.minecraft.gui.function.Draggable;
import de.johni0702.minecraft.gui.utils.lwjgl.Dimension;
import de.johni0702.minecraft.gui.utils.lwjgl.Point;
import de.johni0702.minecraft.gui.utils.lwjgl.ReadableDimension;
import de.johni0702.minecraft.gui.utils.lwjgl.ReadablePoint;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.resource.language.I18n;

//#if MC>=12106
//$$ import net.minecraft.client.gl.RenderPipelines;
//#endif

//#if MC>=12102
//$$ import net.minecraft.client.render.RenderLayer;
//#endif

//#if MC>=12002
//$$ import net.minecraft.util.Identifier;
//$$ import static de.johni0702.minecraft.gui.versions.MCVer.identifier;
//#endif

// TODO: Currently assumes a height of 20
public abstract class AbstractGuiSlider<T extends AbstractGuiSlider<T>> extends AbstractGuiElement<T> implements Clickable, Draggable, IGuiSlider<T> {
    //#if MC>=12002
    //$$ protected static final Identifier TEXTURE = identifier("widget/slider");
    //$$ protected static final Identifier HANDLE_TEXTURE = identifier("widget/slider_handle");
    //#endif

    private Runnable onValueChanged;

    private int value;
    private int steps;

    private String text = "";

    private boolean dragging;

    public AbstractGuiSlider() {
    }

    public AbstractGuiSlider(GuiContainer container) {
        super(container);
    }

    @Override
    protected ReadableDimension calcMinSize() {
        return new Dimension(0, 0);
    }

    @Override
    public boolean mouseClick(ReadablePoint position, int button) {
        Point pos = new Point(position);
        if (getContainer() != null) {
            getContainer().convertFor(this, pos);
        }

        if (isMouseHovering(pos) && isEnabled()) {
            updateValue(pos);
            dragging = true;
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseDrag(ReadablePoint position, int button, long timeSinceLastCall) {
        if (dragging) {
            Point pos = new Point(position);
            if (getContainer() != null) {
                getContainer().convertFor(this, pos);
            }
            updateValue(pos);
        }
        return dragging;
    }

    @Override
    public boolean mouseRelease(ReadablePoint position, int button) {
        if (dragging) {
            dragging = false;
            Point pos = new Point(position);
            if (getContainer() != null) {
                getContainer().convertFor(this, pos);
            }
            updateValue(pos);
            return true;
        } else {
            return false;
        }
    }

    protected boolean isMouseHovering(ReadablePoint pos) {
        return pos.getX() > 0 && pos.getY() > 0
                && pos.getX() < getLastSize().getWidth() && pos.getY() < getLastSize().getHeight();
    }

    @Override
    public void draw(GuiRenderer renderer, ReadableDimension size, RenderInfo renderInfo) {
        super.draw(renderer, size, renderInfo);

        int width = size.getWidth();
        int height = size.getHeight();

        //#if MC>=12002
        //$$ ReadablePoint offset = renderer.getOpenGlOffset();
        //#else
        renderer.bindTexture(GuiButton.WIDGETS_TEXTURE);
        //#endif

        // Draw background
        //#if MC>=12106
        //$$ renderer.getContext().drawGuiTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, offset.getX(), offset.getY(), width, height);
        //#elseif MC>=12102
        //$$ renderer.getContext().drawGuiTexture(RenderLayer::getGuiTextured, TEXTURE, offset.getX(), offset.getY(), width, height);
        //$$ renderer.getContext().draw();
        //#elseif MC>=12002
        //$$ renderer.getContext().drawGuiTexture(TEXTURE, offset.getX(), offset.getY(), width, height);
        //#else
        renderer.drawTexturedRect(0, 0, 0, 46, width / 2, height);
        renderer.drawTexturedRect(width / 2, 0, 200 - width / 2, 46, width / 2, height);
        //#endif

        // Draw slider
        int sliderX = (width - 8) * value / steps;
        //#if MC>=12106
        //$$ renderer.getContext().drawGuiTexture(RenderPipelines.GUI_TEXTURED, HANDLE_TEXTURE, offset.getX() + sliderX, offset.getY(), 8, 20);
        //#elseif MC>=12102
        //$$ renderer.getContext().drawGuiTexture(RenderLayer::getGuiTextured, HANDLE_TEXTURE, offset.getX() + sliderX, offset.getY(), 8, 20);
        //$$ renderer.getContext().draw();
        //#elseif MC>=12002
        //$$ renderer.getContext().drawGuiTexture(HANDLE_TEXTURE, offset.getX() + sliderX, offset.getY(), 8, 20);
        //#else
        renderer.drawTexturedRect(sliderX, 0, 0, 66, 4, 20);
        renderer.drawTexturedRect(sliderX + 4, 0, 196, 66, 4, 20);
        //#endif

        // Draw text
        int color = 0xe0e0e0;
        if (!isEnabled()) {
            color = 0xa0a0a0;
        } else if (isMouseHovering(new Point(renderInfo.mouseX, renderInfo.mouseY))) {
            color = 0xffffa0;
        }
        renderer.drawCenteredString(width / 2, height / 2 - 4, color, text);
    }

    protected void updateValue(ReadablePoint position) {
        if (getLastSize() == null) {
            return;
        }
        int width = getLastSize().getWidth() - 8;
        int pos = Math.max(0, Math.min(width, position.getX() - 4));
        setValue(steps * pos / width);
    }

    public void onValueChanged() {
        if (onValueChanged != null) {
            onValueChanged.run();
        }
    }

    @Override
    public T setText(String text) {
        this.text = text;
        return getThis();
    }

    @Override
    public T setI18nText(String text, Object... args) {
        return setText(I18n.translate(text, args));
    }

    @Override
    public T setValue(int value) {
        this.value = value;
        onValueChanged();
        return getThis();
    }

    @Override
    public int getValue() {
        return value;
    }

    @Override
    public int getSteps() {
        return steps;
    }

    @Override
    public T setSteps(int steps) {
        this.steps = steps;
        return getThis();
    }

    @Override
    public T onValueChanged(Runnable runnable) {
        this.onValueChanged = runnable;
        return getThis();
    }
}
