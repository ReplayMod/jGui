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

import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.List;

public class StringUtils {
    public static String[] splitStringInMultipleRows(String string, int maxWidth) {
        if(string == null) return new String[0];
        Minecraft mc = Minecraft.getMinecraft();
        List<String> rows = new ArrayList<>();
        String remaining = string;
        while(remaining.length() > 0) {
            String[] split = remaining.split(" ");
            String b = "";
            for(String sp : split) {
                b += sp + " ";
                if (mc.fontRenderer.getStringWidth(b.trim()) > maxWidth) {
                    b = b.substring(0, b.trim().length() - (sp.length()));
                    break;
                }
            }
            String trimmed = b.trim();
            rows.add(trimmed);
            try {
                remaining = remaining.substring(trimmed.length() + 1);
            } catch(Exception e) {
                break;
            }
        }

        return rows.toArray(new String[rows.size()]);
    }
}
