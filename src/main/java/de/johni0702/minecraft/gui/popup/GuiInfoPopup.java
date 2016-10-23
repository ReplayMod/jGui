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
package de.johni0702.minecraft.gui.popup;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import de.johni0702.minecraft.gui.container.GuiContainer;
import de.johni0702.minecraft.gui.container.GuiPanel;
import de.johni0702.minecraft.gui.element.GuiButton;
import de.johni0702.minecraft.gui.element.GuiElement;
import de.johni0702.minecraft.gui.element.GuiLabel;
import de.johni0702.minecraft.gui.function.Typeable;
import de.johni0702.minecraft.gui.layout.VerticalLayout;
import de.johni0702.minecraft.gui.utils.Colors;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.lwjgl.input.Keyboard;
import org.lwjgl.util.Dimension;
import org.lwjgl.util.ReadablePoint;

public class GuiInfoPopup extends AbstractGuiPopup<GuiInfoPopup> implements Typeable {
    public static GuiInfoPopup open(GuiContainer container, String...info) {
        GuiElement[] labels = new GuiElement[info.length];
        for (int i = 0; i < info.length; i++) {
            labels[i] = new GuiLabel().setI18nText(info[i]).setColor(Colors.BLACK);
        }
        return open(container, labels);
    }

    public static GuiInfoPopup open(GuiContainer container, GuiElement... info) {
        GuiInfoPopup popup = new GuiInfoPopup(container).setBackgroundColor(Colors.DARK_TRANSPARENT);
        popup.getInfo().addElements(new VerticalLayout.Data(0.5), info);
        popup.open();
        return popup;
    }

    private final SettableFuture<Void> future = SettableFuture.create();

    @Getter
    private final GuiButton closeButton = new GuiButton().setSize(150, 20).onClick(() -> {
        close();
        future.set(null);
    }).setI18nLabel("gui.back");

    @Getter
    private final GuiPanel info = new GuiPanel().setMinSize(new Dimension(320, 50))
            .setLayout(new VerticalLayout(VerticalLayout.Alignment.TOP).setSpacing(2));

    {
        popup.setLayout(new VerticalLayout().setSpacing(10))
                .addElements(new VerticalLayout.Data(0.5), info, closeButton);
    }

    @Getter
    @Setter
    @Accessors(chain = true)
    private int layer;

    public GuiInfoPopup(GuiContainer container) {
        super(container);
    }

    public GuiInfoPopup setCloseLabel(String label) {
        closeButton.setLabel(label);
        return this;
    }

    public GuiInfoPopup setCloseI18nLabel(String label, Object...args) {
        closeButton.setI18nLabel(label, args);
        return this;
    }

    public ListenableFuture<Void> getFuture() {
        return future;
    }

    @Override
    protected GuiInfoPopup getThis() {
        return this;
    }

    @Override
    public boolean typeKey(ReadablePoint mousePosition, int keyCode, char keyChar, boolean ctrlDown, boolean shiftDown) {
        if (keyCode == Keyboard.KEY_ESCAPE) {
            closeButton.onClick();
            return true;
        }
        return false;
    }
}
