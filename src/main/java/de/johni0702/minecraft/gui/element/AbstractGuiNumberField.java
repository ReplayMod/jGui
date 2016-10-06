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
import de.johni0702.minecraft.gui.container.GuiContainer;
import lombok.Getter;

import java.util.Locale;
import java.util.regex.Pattern;

// TODO: This is suboptimal e.g. if there are trailing zeros, they stay (should be fixed after TextField is done w/o MC)
public abstract class AbstractGuiNumberField<T extends AbstractGuiNumberField<T>>
        extends AbstractGuiTextField<T> implements IGuiNumberField<T> {

    private int precision;
    private volatile Pattern precisionPattern;

    @Getter
    private Double minValue;

    @Getter
    private Double maxValue;

    private boolean validateOnFocusChange = false;

    public AbstractGuiNumberField() {
    }

    public AbstractGuiNumberField(GuiContainer container) {
        super(container);
    }

    {
        setValue(0);
    }

    @Override
    public T setText(String text) {
        if (!isTextValid(text, !validateOnFocusChange)) {
            throw new IllegalArgumentException(text + " is not a valid number!");
        }
        return super.setText(text);
    }

    @Override
    public T setValidateOnFocusChange(boolean validateOnFocusChange) {
        this.validateOnFocusChange = validateOnFocusChange;
        return getThis();
    }

    private boolean isSemiZero(String text) {
        return text.isEmpty() || "-".equals(text);
    }

    private boolean isTextValid(String text, boolean validateRange) {
        // Allow empty text to be equal to 0
        if (validateOnFocusChange && isSemiZero(text)) {
            // but only if 0 is in range
            return !validateRange || valueInRange(0);
        }
        try {
            if (precision == 0) {
                int val = Integer.parseInt(text);
                return !validateRange || valueInRange(val);
            } else {
                double val = Double.parseDouble(text);
                return !validateRange || (valueInRange(val) && precisionPattern.matcher(text).matches());
            }
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean valueInRange(double value) {
        return (minValue == null || value >= minValue) && (maxValue == null || value <= maxValue);
    }

    @Override
    protected void onTextChanged(String from) {
        if (isTextValid(getText(), !validateOnFocusChange)) {
            super.onTextChanged(from);
        } else {
            setText(from);
        }
    }

    @Override
    public byte getByte() {
        if (validateOnFocusChange && isSemiZero(getText())) {
            return 0;
        }
        return Byte.parseByte(getText());
    }

    @Override
    public short getShort() {
        if (validateOnFocusChange && isSemiZero(getText())) {
            return 0;
        }
        return Short.parseShort(getText());
    }

    @Override
    public int getInteger() {
        if (validateOnFocusChange && isSemiZero(getText())) {
            return 0;
        }
        return Integer.parseInt(getText());
    }

    @Override
    public long getLong() {
        if (validateOnFocusChange && isSemiZero(getText())) {
            return 0;
        }
        return Long.parseLong(getText());
    }

    @Override
    public float getFloat() {
        if (validateOnFocusChange && isSemiZero(getText())) {
            return 0;
        }
        return Float.parseFloat(getText());
    }

    @Override
    public double getDouble() {
        if (validateOnFocusChange && isSemiZero(getText())) {
            return 0;
        }
        return Double.parseDouble(getText());
    }

    @Override
    public T setValue(int value) {
        setText(Integer.toString(value));
        return getThis();
    }

    @Override
    public T setValue(double value) {
        setText(String.format(Locale.ROOT, "%." + precision + "f", value));
        return getThis();
    }

    @Override
    public T setPrecision(int precision) {
        Preconditions.checkArgument(precision >= 0, "precision must not be negative");
        precisionPattern = Pattern.compile(String.format("-?[0-9]*+((\\.[0-9]{0,%d})?)||(\\.)?", precision));
        this.precision = precision;
        return getThis();
    }

    @Override
    public T setMinValue(Double minValue) {
        this.minValue = minValue;
        return getThis();
    }

    @Override
    public T setMaxValue(Double maxValue) {
        this.maxValue = maxValue;
        return getThis();
    }

    @Override
    public T setMinValue(int minValue) {
        return setMinValue((double) minValue);
    }

    @Override
    public T setMaxValue(int maxValue) {
        return setMaxValue((double) maxValue);
    }

    private double clampToBounds() {
        double d = getDouble();
        if (getMinValue() != null && d < getMinValue()) {
            return getMinValue();
        }
        if (getMaxValue() != null && d > getMaxValue()) {
            return getMaxValue();
        }
        return d;
    }

    @Override
    protected void onFocusChanged(boolean focused) {
        setValue(clampToBounds());
        super.onFocusChanged(focused);
    }
}
