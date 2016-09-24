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
package de.johni0702.minecraft.gui.utils;

import de.johni0702.minecraft.gui.GuiRenderer;
import de.johni0702.minecraft.gui.function.Focusable;

import java.util.Arrays;
import java.util.HashSet;

import static com.google.common.base.Preconditions.checkArgument;

public class Utils {
    /**
     * Interval (in ms) within which two clicks have to be made on the same element to be counted as a double click.
     */
    public static final int DOUBLE_CLICK_INTERVAL = 250;

    /**
     * Link the specified focusable compontents in the order they're supplied.
     * @param focusables Focusables to link
     * @see Focusable#setNext(Focusable)
     * @see Focusable#setPrevious(Focusable)
     */
    public static void link(Focusable... focusables) {
        checkArgument(new HashSet<>(Arrays.asList(focusables)).size() == focusables.length, "focusables must be unique and not null");
        for (int i = 0; i < focusables.length; i++) {
            Focusable next = focusables[(i + 1) % focusables.length];
            focusables[i].setNext(next);
            next.setPrevious(focusables[i]);
        }
    }

    public static void drawDynamicRect(GuiRenderer renderer, int width, int height, int u, int v, int uWidth, int vHeight,
                                int topBorder, int bottomBorder, int leftBorder, int rightBorder) {
        int textureBodyHeight = vHeight - topBorder - bottomBorder;
        int textureBodyWidth = uWidth - leftBorder - rightBorder;
        // Left and right borders
        for (int pass = 0; pass < 2; pass++) {
            int x = pass == 0 ? 0 : width - rightBorder;
            int textureX = pass == 0 ? u : u + uWidth - rightBorder;
            // Border
            for (int y = topBorder; y < height - bottomBorder; y += textureBodyHeight) {
                int segmentHeight = Math.min(textureBodyHeight, height - bottomBorder - y);
                renderer.drawTexturedRect(x, y, textureX, v + topBorder, leftBorder, segmentHeight);
            }
            // Top corner
            renderer.drawTexturedRect(x, 0, textureX, v, leftBorder, topBorder);
            // Bottom corner
            renderer.drawTexturedRect(x, height - bottomBorder, textureX, v + vHeight - bottomBorder,
                    leftBorder, bottomBorder);
        }

        for (int x = leftBorder; x < width - rightBorder; x += textureBodyWidth) {
            int segmentWidth = Math.min(textureBodyWidth, width - rightBorder - x);
            int textureX = u + leftBorder;

            // Content
            for (int y = topBorder; y < height - bottomBorder; y += textureBodyHeight) {
                int segmentHeight = Math.min(textureBodyHeight, height - bottomBorder - y);
                renderer.drawTexturedRect(x, y, textureX, v + topBorder, segmentWidth, segmentHeight);
            }

            // Top border
            renderer.drawTexturedRect(x, 0, textureX, v, segmentWidth, topBorder);
            // Bottom border
            renderer.drawTexturedRect(x, height - bottomBorder, textureX, v + vHeight - bottomBorder,
                    segmentWidth, bottomBorder);
        }
    }
}
