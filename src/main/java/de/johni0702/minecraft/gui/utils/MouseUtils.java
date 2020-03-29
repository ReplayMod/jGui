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

import de.johni0702.minecraft.gui.utils.lwjgl.Point;
import de.johni0702.minecraft.gui.versions.MCVer;
import net.minecraft.client.MinecraftClient;

//#if MC>=11400
import net.minecraft.client.util.Window;
//#else
//$$ import net.minecraft.client.gui.ScaledResolution;
//$$ import org.lwjgl.input.Mouse;
//#endif

public class MouseUtils {
    private static final MinecraftClient mc = MCVer.getMinecraft();

    public static Point getMousePos() {
        //#if MC>=11400
        int mouseX = (int) mc.mouse.getX();
        int mouseY = (int) mc.mouse.getY();
        Window mainWindow = MCVer.newScaledResolution(mc);
        mouseX = (int) Math.round((double) mouseX * mainWindow.getScaledWidth() / mainWindow.getWidth());
        mouseY = (int) Math.round((double) mouseY * mainWindow.getScaledHeight() / mainWindow.getHeight());
        //#else
        //$$ Point scaled = getScaledDimensions();
        //$$ int width = scaled.getX();
        //$$ int height = scaled.getY();
        //$$
        //$$ int mouseX = (Mouse.getX() * width / mc.displayWidth);
        //$$ int mouseY = (height - Mouse.getY() * height / mc.displayHeight);
        //#endif

        return new Point(mouseX, mouseY);
    }

    public static Point getScaledDimensions() {
        //#if MC>=11400
        Window
        //#else
        //$$ ScaledResolution
        //#endif
                res = MCVer.newScaledResolution(mc);
        return new Point(res.getScaledWidth(), res.getScaledHeight());
    }
}
