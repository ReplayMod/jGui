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
import de.johni0702.minecraft.gui.utils.lwjgl.Color;
import de.johni0702.minecraft.gui.utils.lwjgl.Dimension;
import de.johni0702.minecraft.gui.utils.lwjgl.ReadableColor;
import de.johni0702.minecraft.gui.utils.lwjgl.ReadableDimension;
import de.johni0702.minecraft.gui.versions.MCVer;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.Identifier;

public abstract class AbstractGuiCheckbox<T extends AbstractGuiCheckbox<T>>
        extends AbstractGuiClickable<T> implements IGuiCheckbox<T> {
    protected static final Identifier BUTTON_SOUND = new Identifier("gui.button.press");
    protected static final ReadableColor BOX_BACKGROUND_COLOR = new Color(46, 46, 46);

    private String label;

    private boolean checked;

    public AbstractGuiCheckbox() {
    }

    public AbstractGuiCheckbox(GuiContainer container) {
        super(container);
    }

    @Override
    public void draw(GuiRenderer renderer, ReadableDimension size, RenderInfo renderInfo) {
        super.draw(renderer, size, renderInfo);

        int color = 0xe0e0e0;
        if (!isEnabled()) {
            color = 0xa0a0a0;
        }

        int boxSize = size.getHeight();
        renderer.drawRect(0, 0, boxSize, boxSize, ReadableColor.BLACK);
        renderer.drawRect(1, 1, boxSize - 2, boxSize - 2, BOX_BACKGROUND_COLOR);

        if(isChecked()) {
            renderer.drawCenteredString(boxSize / 2 + 1, 1, color, "x", true);
        }

        renderer.drawString(boxSize + 2, 2, color, label);
    }

    @Override
    public ReadableDimension calcMinSize() {
        TextRenderer fontRenderer = MCVer.getFontRenderer();
        int height = fontRenderer.fontHeight + 2;
        int width = height + 2 + fontRenderer.getWidth(label);
        return new Dimension(width, height);
    }

    @Override
    public ReadableDimension getMaxSize() {
        return getMinSize();
    }

    @Override
    public void onClick() {
        AbstractGuiButton.playClickSound(getMinecraft());
        setChecked(!isChecked());
        super.onClick();
    }

    @Override
    public T setLabel(String label) {
        this.label = label;
        return getThis();
    }

    @Override
    public T setI18nLabel(String label, Object... args) {
        return setLabel(I18n.translate(label, args));
    }

    @Override
    public T setChecked(boolean checked) {
        this.checked = checked;
        return getThis();
    }

    public String getLabel() {
        return this.label;
    }

    public boolean isChecked() {
        return this.checked;
    }
}
