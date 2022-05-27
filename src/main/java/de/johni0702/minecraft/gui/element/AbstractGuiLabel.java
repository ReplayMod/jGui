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
import de.johni0702.minecraft.gui.utils.lwjgl.Dimension;
import de.johni0702.minecraft.gui.utils.lwjgl.ReadableColor;
import de.johni0702.minecraft.gui.utils.lwjgl.ReadableDimension;
import de.johni0702.minecraft.gui.versions.MCVer;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.resource.language.I18n;

import java.util.List;

//#if MC>=11600
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.text.Style;

import static de.johni0702.minecraft.gui.versions.MCVer.literalText;
//#endif

public abstract class AbstractGuiLabel<T extends AbstractGuiLabel<T>> extends AbstractGuiElement<T> implements IGuiLabel<T> {
    private String text = "";

    private ReadableColor color = ReadableColor.WHITE, disabledColor = ReadableColor.GREY;

    public AbstractGuiLabel() {
    }

    public AbstractGuiLabel(GuiContainer container) {
        super(container);
    }

    @Override
    public void draw(GuiRenderer renderer, ReadableDimension size, RenderInfo renderInfo) {
        super.draw(renderer, size, renderInfo);
        TextRenderer fontRenderer = MCVer.getFontRenderer();
        //#if MC>=11600
        List<String> lines = fontRenderer.getTextHandler().wrapLines(literalText(text), size.getWidth(), Style.EMPTY).stream()
                .map(it -> it.visit(Optional::of)).filter(Optional::isPresent).map(Optional::get)
                .collect(Collectors.toList());
        //#else
        //$$ @SuppressWarnings("unchecked")
        //$$ List<String> lines = fontRenderer.wrapStringToWidthAsList(text, size.getWidth());
        //#endif
        int y = 0;
        for (String line : lines) {
            renderer.drawString(0, y, isEnabled() ? color : disabledColor, line);
            y+=fontRenderer.fontHeight;
        }
    }

    @Override
    public ReadableDimension calcMinSize() {
        TextRenderer fontRenderer = MCVer.getFontRenderer();
        return new Dimension(fontRenderer.getWidth(text), fontRenderer.fontHeight);
    }

    @Override
    public ReadableDimension getMaxSize() {
        return getMinSize();
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
    public T setColor(ReadableColor color) {
        this.color = color;
        return getThis();
    }

    @Override
    public T setDisabledColor(ReadableColor disabledColor) {
        this.disabledColor = disabledColor;
        return getThis();
    }

    public String getText() {
        return this.text;
    }

    public ReadableColor getColor() {
        return this.color;
    }

    public ReadableColor getDisabledColor() {
        return this.disabledColor;
    }
}
