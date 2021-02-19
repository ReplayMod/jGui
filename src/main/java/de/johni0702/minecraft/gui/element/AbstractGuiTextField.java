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

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import de.johni0702.minecraft.gui.GuiRenderer;
import de.johni0702.minecraft.gui.RenderInfo;
import de.johni0702.minecraft.gui.container.GuiContainer;
import de.johni0702.minecraft.gui.function.Clickable;
import de.johni0702.minecraft.gui.function.Focusable;
import de.johni0702.minecraft.gui.function.Tickable;
import de.johni0702.minecraft.gui.function.Typeable;
import de.johni0702.minecraft.gui.utils.Consumer;
import de.johni0702.minecraft.gui.utils.lwjgl.Color;
import de.johni0702.minecraft.gui.utils.lwjgl.Dimension;
import de.johni0702.minecraft.gui.utils.lwjgl.Point;
import de.johni0702.minecraft.gui.utils.lwjgl.ReadableColor;
import de.johni0702.minecraft.gui.utils.lwjgl.ReadableDimension;
import de.johni0702.minecraft.gui.utils.lwjgl.ReadablePoint;
import de.johni0702.minecraft.gui.versions.MCVer;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resource.language.I18n;

//#if MC>=11400
import net.minecraft.SharedConstants;
//#else
//$$ import net.minecraft.util.ChatAllowedCharacters;
//$$ import org.lwjgl.input.Keyboard;
//#endif

import static de.johni0702.minecraft.gui.utils.Utils.clamp;
import static de.johni0702.minecraft.gui.versions.MCVer.*;

public abstract class AbstractGuiTextField<T extends AbstractGuiTextField<T>>
        extends AbstractGuiElement<T> implements Clickable, Tickable, Typeable, IGuiTextField<T> {
    private static final ReadableColor BORDER_COLOR = new Color(160, 160, 160);
    private static final ReadableColor CURSOR_COLOR = new Color(240, 240, 240);
    private static final int BORDER = 4;

    // Focus
    private boolean focused;
    private Focusable next, previous;

    // Content
    private int maxLength = 32;

    private String text = "";

    private int cursorPos;
    private int selectionPos;

    private String hint;

    // Rendering
    private int currentOffset;
    private int blinkCursorTick;
    private ReadableColor textColorEnabled = new Color(224, 224, 224);
    private ReadableColor textColorDisabled = new Color(112, 112, 112);
    private ReadableDimension size = new Dimension(0, 0); // Size of last render

    private Consumer<String> textChanged;
    private Consumer<Boolean> focusChanged;
    private Runnable onEnter;

    public AbstractGuiTextField() {
    }

    public AbstractGuiTextField(GuiContainer container) {
        super(container);
    }

    @Override
    public T setText(String text) {
        if (text.length() > maxLength) {
            text = text.substring(0, maxLength);
        }
        this.text = text;
        selectionPos = cursorPos = text.length();
        return getThis();
    }

    @Override
    public T setI18nText(String text, Object... args) {
        return setText(I18n.translate(text, args));
    }

    @Override
    public T setMaxLength(int maxLength) {
        Preconditions.checkArgument(maxLength >= 0, "maxLength must not be negative");
        this.maxLength = maxLength;
        if (text.length() > maxLength) {
            setText(text);
        }
        return getThis();
    }

    @Override
    public String deleteText(int from, int to) {
        Preconditions.checkArgument(from <= to, "from must not be greater than to");
        Preconditions.checkArgument(from >= 0, "from must be greater than zero");
        Preconditions.checkArgument(to < text.length(), "to must be less than test.length()");

        String deleted = text.substring(from, to + 1);
        text = text.substring(0, from) + text.substring(to + 1);
        return deleted;
    }

    @Override
    public int getSelectionFrom() {
        return cursorPos > selectionPos ? selectionPos : cursorPos;
    }

    @Override
    public int getSelectionTo() {
        return cursorPos > selectionPos ? cursorPos : selectionPos;
    }

    @Override
    public String getSelectedText() {
        return text.substring(getSelectionFrom(), getSelectionTo());
    }

    @Override
    public String deleteSelectedText() {
        if (cursorPos == selectionPos) {
            return ""; // Nothing selected
        }
        int from = getSelectionFrom();
        String deleted = deleteText(from, getSelectionTo() - 1);
        cursorPos = selectionPos = from;
        return deleted;
    }

    /**
     * Update current text offset to make sure the cursor is always visible.
     */
    private void updateCurrentOffset() {
        currentOffset = Math.min(currentOffset, cursorPos);
        String line = text.substring(currentOffset, cursorPos);
        TextRenderer fontRenderer = MCVer.getFontRenderer();
        int currentWidth = fontRenderer.getWidth(line);
        if (currentWidth > size.getWidth() - 2*BORDER) {
            currentOffset = cursorPos - fontRenderer.trimToWidth(line, size.getWidth() - 2*BORDER, true).length();
        }
    }

    @Override
    public T writeText(String append) {
        for (char c : append.toCharArray()) {
            writeChar(c);
        }
        return getThis();
    }

    @Override
    public T writeChar(char c) {
        //#if MC>=11400
        if (!SharedConstants.isValidChar(c)) {
        //#else
        //$$ if (!ChatAllowedCharacters.isAllowedCharacter(c)) {
        //#endif
            return getThis();
        }

        deleteSelectedText();

        if (text.length() >= maxLength) {
            return getThis();
        }

        text = text.substring(0, cursorPos) + c + text.substring(cursorPos);
        selectionPos = ++cursorPos;

        return getThis();
    }

    @Override
    public T deleteNextChar() {
        if (cursorPos < text.length()) {
            text = text.substring(0, cursorPos) + text.substring(cursorPos + 1);
        }
        selectionPos = cursorPos;
        return getThis();
    }

    /**
     * Return the amount of characters to the next word (excluding).
     * If this is the last word in the line, return the amount of characters remaining to till the end.
     * Everything except the Space character is considered part of a word.
     * @return Length in characters
     */
    protected int getNextWordLength() {
        int length = 0;
        boolean inWord = true;
        for (int i = cursorPos; i < text.length(); i++) {
            if (inWord) {
                if (text.charAt(i) == ' ') {
                    inWord = false;
                }
            } else {
                if (text.charAt(i) != ' ') {
                    return length;
                }
            }
            length++;
        }
        return length;
    }

    @Override
    public String deleteNextWord() {
        int worldLength = getNextWordLength();
        if (worldLength > 0) {
            return deleteText(cursorPos, cursorPos + worldLength - 1);
        }
        return "";
    }

    @Override
    public T deletePreviousChar() {
        if (cursorPos > 0) {
            text = text.substring(0, cursorPos - 1) + text.substring(cursorPos);
            selectionPos = --cursorPos;
        }
        return getThis();
    }

    /**
     * Return the amount of characters to the previous word (including).
     * If this is the first word in the line, return the amount of characters till the start.
     * Everything except the Space character is considered part of a word.
     * @return Length in characters
     */
    protected int getPreviousWordLength() {
        int length = 0;
        boolean inWord = false;
        for (int i = cursorPos - 1; i >= 0; i--) {
            if (inWord) {
                if (text.charAt(i) == ' ') {
                    return length;
                }
            } else {
                if (text.charAt(i) != ' ') {
                    inWord = true;
                }
            }
            length++;
        }
        return length;
    }

    @Override
    public String deletePreviousWord() {
        int worldLength = getPreviousWordLength();
        String deleted = "";
        if (worldLength > 0) {
            deleted = deleteText(cursorPos - worldLength, cursorPos - 1);
            selectionPos = cursorPos -= worldLength;
        }
        return deleted;
    }

    @Override
    public T setCursorPosition(int pos) {
        Preconditions.checkArgument(pos >= 0 && pos <= text.length());
        selectionPos = cursorPos = pos;
        return getThis();
    }

    @Override
    protected ReadableDimension calcMinSize() {
        return new Dimension(0, 0);
    }

    @Override
    public boolean mouseClick(ReadablePoint position, int button) {
        if (getContainer() != null) {
            getContainer().convertFor(this, (Point) (position = new Point(position)));
        }
        boolean hovering = isMouseHovering(position);

        if (hovering && isFocused() && button == 0) {
            updateCurrentOffset();
            int mouseX = position.getX() - BORDER;
            TextRenderer fontRenderer = MCVer.getFontRenderer();
            String text = this.text.substring(currentOffset);
            int textX = fontRenderer.trimToWidth(text, mouseX).length() + currentOffset;
            setCursorPosition(textX);
            return true;
        }

        setFocused(hovering);
        // Do not yet return true to allow focusables later in the event chain to be notified of the focus change
        return false;
    }

    protected boolean isMouseHovering(ReadablePoint pos) {
        return pos.getX() > 0 && pos.getY() > 0
                && pos.getX() < size.getWidth() && pos.getY() < size.getHeight();
    }

    @Override
    public T setFocused(boolean isFocused) {
        if (isFocused && !this.focused) {
            this.blinkCursorTick = 0; // Restart blinking to indicate successful focus
        }
        if (this.focused != isFocused) {
            this.focused = isFocused;
            onFocusChanged(this.focused);
        }
        return getThis();
    }

    @Override
    public T setNext(Focusable next) {
        this.next = next;
        return getThis();
    }

    @Override
    public T setPrevious(Focusable previous) {
        this.previous = previous;
        return getThis();
    }

    @Override
    public void draw(GuiRenderer renderer, ReadableDimension size, RenderInfo renderInfo) {
        this.size = size;
        updateCurrentOffset();
        super.draw(renderer, size, renderInfo);

        int width = size.getWidth(), height = size.getHeight();
        TextRenderer fontRenderer = MCVer.getFontRenderer();
        int posY = height / 2 - fontRenderer.fontHeight / 2;

        // Draw black rect once pixel smaller than gray rect
        renderer.drawRect(0, 0, width, height, isFocused() ? ReadableColor.WHITE : BORDER_COLOR);
        renderer.drawRect(1, 1, width - 2, height - 2, ReadableColor.BLACK);

        if (text.isEmpty() && !isFocused() && !Strings.isNullOrEmpty(hint)) {
            // Draw hint
            String text = fontRenderer.trimToWidth(hint, width - 2*BORDER);
            renderer.drawString(BORDER, posY, textColorDisabled, text);
        } else {
            // Draw text
            String renderText = text.substring(currentOffset);
            renderText = fontRenderer.trimToWidth(renderText, width - 2*BORDER);
            ReadableColor color = isEnabled() ? textColorEnabled : textColorDisabled;
            int lineEnd = renderer.drawString(BORDER, height / 2 - fontRenderer.fontHeight / 2, color, renderText);

            // Draw selection
            int from = getSelectionFrom();
            int to = getSelectionTo();
            String leftStr = renderText.substring(0, clamp(from - currentOffset, 0, renderText.length()));
            String rightStr = renderText.substring(clamp(to - currentOffset, 0, renderText.length()));
            int left = BORDER + fontRenderer.getWidth(leftStr);
            int right = lineEnd - fontRenderer.getWidth(rightStr) - 1;
            renderer.invertColors(right, height - 2, left, 2);

            // Draw cursor
            if (blinkCursorTick / 6 % 2 == 0 && focused) {
                String beforeCursor = renderText.substring(0, cursorPos - currentOffset);
                int posX = BORDER + fontRenderer.getWidth(beforeCursor);
                if (cursorPos == text.length()) {
                    renderer.drawString(posX, posY, CURSOR_COLOR, "_", true);
                } else {
                    renderer.drawRect(posX, posY - 1, 1, 1 + fontRenderer.fontHeight, CURSOR_COLOR);
                }
            }
        }
    }

    @Override
    public boolean typeKey(ReadablePoint mousePosition, int keyCode, char keyChar, boolean ctrlDown, boolean shiftDown) {
        if (!this.focused) {
            return false;
        }

        if (keyCode == Keyboard.KEY_TAB) {
            Focusable other = shiftDown ? previous : next;
            if (other != null) {
                setFocused(false);
                other.setFocused(true);
                // If the other field is a text field, by default select all its text (saves a Ctrl+A)
                if (other instanceof AbstractGuiTextField) {
                    AbstractGuiTextField<?> field = (AbstractGuiTextField<?>) other;
                    field.cursorPos = 0;
                    field.selectionPos = field.text.length();
                }
            }
            return true;
        }

        if (keyCode == Keyboard.KEY_RETURN) {
            onEnter();
            return true;
        }

        String textBefore = text;
        try {
            if (Screen.hasControlDown()) {
                switch (keyCode) {
                    case Keyboard.KEY_A: // Select all
                        cursorPos = 0;
                        selectionPos = text.length();
                        return true;
                    case Keyboard.KEY_C: // Copy
                        MCVer.setClipboardString(getSelectedText());
                        return true;
                    case Keyboard.KEY_V: // Paste
                        if (isEnabled()) {
                            writeText(MCVer.getClipboardString());
                        }
                        return true;
                    case Keyboard.KEY_X: // Cut
                        if (isEnabled()) {
                            MCVer.setClipboardString(deleteSelectedText());
                        }
                        return true;
                }
            }

            boolean words = Screen.hasControlDown();
            boolean select = Screen.hasShiftDown();
            switch (keyCode) {
                case Keyboard.KEY_HOME:
                    cursorPos = 0;
                    break;
                case Keyboard.KEY_END:
                    cursorPos = text.length();
                    break;
                case Keyboard.KEY_LEFT:
                    if (cursorPos != 0) {
                        if (words) {
                            cursorPos -= getPreviousWordLength();
                        } else {
                            cursorPos--;
                        }
                    }
                    break;
                case Keyboard.KEY_RIGHT:
                    if (cursorPos != text.length()) {
                        if (words) {
                            cursorPos += getNextWordLength();
                        } else {
                            cursorPos++;
                        }
                    }
                    break;
                case Keyboard.KEY_BACK:
                    if (isEnabled()) {
                        if (getSelectedText().length() > 0) {
                            deleteSelectedText();
                        } else if (words) {
                            deletePreviousWord();
                        } else {
                            deletePreviousChar();
                        }
                    }
                    return true;
                case Keyboard.KEY_DELETE:
                    if (isEnabled()) {
                        if (getSelectedText().length() > 0) {
                            deleteSelectedText();
                        } else if (words) {
                            deleteNextWord();
                        } else {
                            deleteNextChar();
                        }
                    }
                    return true;
                default:
                    if (isEnabled()) {
                        if (keyChar == '\r') {
                            keyChar = '\n';
                        }
                        writeChar(keyChar);
                    }
                    return true;
            }

            if (!select) {
                selectionPos = cursorPos;
            }
            return true;
        } finally {
            if (!textBefore.equals(text)) {
                onTextChanged(textBefore);
            }
        }
    }

    @Override
    public void tick() {
        blinkCursorTick++;
    }

    /**
     * Called when the user presses the Enter/Return key while this text field is focused.
     */
    protected void onEnter() {
        if (onEnter != null) {
            onEnter.run();
        }
    }

    /**
     * Called when the text has changed due to user input.
     */
    protected void onTextChanged(String from) {
        if (textChanged != null) {
            textChanged.consume(from);
        }
    }

    /**
     * Called when the element has been focused or unfocused
     */
    protected void onFocusChanged(boolean focused) {
        if (focusChanged != null) {
            focusChanged.consume(focused);
        }
    }

    @Override
    public T onEnter(Runnable onEnter) {
        this.onEnter = onEnter;
        return getThis();
    }

    @Override
    public T onTextChanged(Consumer<String> textChanged) {
        this.textChanged = textChanged;
        return getThis();
    }

    @Override
    public T onFocusChange(Consumer<Boolean> focusChanged) {
        this.focusChanged = focusChanged;
        return getThis();
    }

    @Override
    public T setHint(String hint) {
        this.hint = hint;
        return getThis();
    }

    @Override
    public T setI18nHint(String hint, Object... args) {
        return setHint(I18n.translate(hint));
    }

    @Override
    public ReadableColor getTextColor() {
        return textColorEnabled;
    }

    @Override
    public T setTextColor(ReadableColor textColor) {
        this.textColorEnabled = textColor;
        return getThis();
    }

    @Override
    public ReadableColor getTextColorDisabled() {
        return textColorDisabled;
    }

    @Override
    public T setTextColorDisabled(ReadableColor textColorDisabled) {
        this.textColorDisabled = textColorDisabled;
        return getThis();
    }

    public boolean isFocused() {
        return this.focused;
    }

    public Focusable getNext() {
        return this.next;
    }

    public Focusable getPrevious() {
        return this.previous;
    }

    public int getMaxLength() {
        return this.maxLength;
    }

    public String getText() {
        return this.text;
    }

    public String getHint() {
        return this.hint;
    }
}
