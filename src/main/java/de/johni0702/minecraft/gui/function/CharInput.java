/*
 * This file is part of jGui API, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2025 johni0702 <https://github.com/johni0702>
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
package de.johni0702.minecraft.gui.function;

//#if MC>=12006
//$$ import net.minecraft.util.StringHelper;
//#elseif MC>=11400
import net.minecraft.SharedConstants;
//#else
//$$ import net.minecraft.util.ChatAllowedCharacters;
//#endif

public class CharInput implements InputWithModifiers {
    //#if MC>=12109
    //$$ public final int codepoint;
    //#else
    public final char character;
    //#endif
    public final int modifiers;

    //#if MC>=12109
    //$$ public CharInput(int codepoint, int modifiers) {
    //$$     this.codepoint = codepoint;
    //$$     this.modifiers = modifiers;
    //$$ }
    //#else
    public CharInput(char character, int modifiers) {
        this.character = character;
        this.modifiers = modifiers;
    }
    //#endif

    //#if MC>=12109
    //$$ public CharInput(net.minecraft.client.input.CharInput mcCharInput) {
    //$$     this(mcCharInput.codepoint(), mcCharInput.modifiers());
    //$$ }
    //$$ public net.minecraft.client.input.CharInput toMC() {
    //$$     return new net.minecraft.client.input.CharInput(codepoint, modifiers);
    //$$ }
    //#endif

    //#if MC<11300
    //$$ public CharInput(char character) {
    //$$     this(character, InputWithModifiers.currentModifiers());
    //$$ }
    //#endif

    @Override
    public int modifiers() {
        return modifiers;
    }

    public boolean isValidChar() {
        //#if MC>=12109
        //$$ return isValidChar((char) codepoint);
        //#else
        return isValidChar(character);
        //#endif
    }

    public String asString() {
        //#if MC>=12109
        //$$ return new String(new int[]{codepoint}, 0, 1);
        //#else
        return Character.toString(character);
        //#endif
    }

    public static boolean isValidChar(char c) {
        //#if MC>=12006
        //$$ return StringHelper.isValidChar(c);
        //#elseif MC>=11400
        return SharedConstants.isValidChar(c);
        //#else
        //$$ return ChatAllowedCharacters.isAllowedCharacter(c);
        //#endif
    }
}
