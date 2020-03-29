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
import de.johni0702.minecraft.gui.utils.lwjgl.Dimension;
import de.johni0702.minecraft.gui.utils.lwjgl.Point;
import de.johni0702.minecraft.gui.utils.lwjgl.ReadableDimension;
import de.johni0702.minecraft.gui.versions.MCVer;
import lombok.Getter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.Identifier;

//#if MC>=11400
import net.minecraft.client.sound.PositionedSoundInstance;
//#else
//$$ import net.minecraft.client.audio.PositionedSoundRecord;
//#endif

//#if MC>=10904
import net.minecraft.sound.SoundEvents;
import net.minecraft.sound.SoundEvent;
//#endif

//#if MC>=10800
import static com.mojang.blaze3d.platform.GlStateManager.*;
//#endif
import static de.johni0702.minecraft.gui.versions.MCVer.*;

import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;

public abstract class AbstractGuiButton<T extends AbstractGuiButton<T>> extends AbstractGuiClickable<T> implements Clickable, IGuiButton<T> {
    protected static final Identifier BUTTON_SOUND = new Identifier("gui.button.press");
    protected static final Identifier WIDGETS_TEXTURE = new Identifier("textures/gui/widgets.png");

    //#if MC>=10904
    @Getter
    private SoundEvent sound = SoundEvents.UI_BUTTON_CLICK;
    //#endif

    @Getter
    private String label;

    public AbstractGuiButton() {
    }

    public AbstractGuiButton(GuiContainer container) {
        super(container);
    }

    @Override
    public void draw(GuiRenderer renderer, ReadableDimension size, RenderInfo renderInfo) {
        super.draw(renderer, size, renderInfo);

        renderer.bindTexture(WIDGETS_TEXTURE);
        color4f(1, 1, 1, 1);

        byte texture = 1;
        int color = 0xe0e0e0;
        if (!isEnabled()) {
            texture = 0;
            color = 0xa0a0a0;
        } else if (isMouseHovering(new Point(renderInfo.mouseX, renderInfo.mouseY))) {
            texture = 2;
            color = 0xffffa0;
        }

        enableBlend();
        blendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        int textureY = 46 + texture * 20;
        int halfWidth = size.getWidth() / 2;
        int secondHalfWidth = size.getWidth() - halfWidth;

        renderer.drawTexturedRect(0, 0, 0, textureY, halfWidth, size.getHeight());
        renderer.drawTexturedRect(halfWidth, 0, 200 - secondHalfWidth, textureY, secondHalfWidth, size.getHeight());
        renderer.drawCenteredString(halfWidth, (size.getHeight() - 8) / 2, color, label, true);
    }

    @Override
    public ReadableDimension calcMinSize() {
        TextRenderer fontRenderer = MCVer.getFontRenderer();
        return new Dimension(fontRenderer.getStringWidth(label), 20);
    }

    @Override
    public void onClick() {
        playClickSound(getMinecraft());
        super.onClick();
    }

    public static void playClickSound(MinecraftClient mc) {
    //#if MC>=10904
        playClickSound(mc, SoundEvents.UI_BUTTON_CLICK);
    }
    public static void playClickSound(MinecraftClient mc, SoundEvent sound) {
    //#endif
        //#if MC>=11400
        mc.getSoundManager().play(PositionedSoundInstance.master(sound, 1.0F));
        //#else
        //#if MC>=10904
        //$$ mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(sound, 1.0F));
        //#else
        //#if MC>=10800
        //$$ mc.getSoundHandler().playSound(PositionedSoundRecord.create(BUTTON_SOUND, 1.0F));
        //#else
        //$$ mc.getSoundHandler().playSound(PositionedSoundRecord.createPositionedSoundRecord(BUTTON_SOUND, 1.0F));
        //#endif
        //#endif
        //#endif
    }

    @Override
    public T setLabel(String label) {
        this.label = label;
        return getThis();
    }

    //#if MC>=10904
    @Override
    public T setSound(SoundEvent sound) {
        this.sound = sound;
        return getThis();
    }
    //#endif

    @Override
    public T setI18nLabel(String label, Object... args) {
        return setLabel(I18n.translate(label, args));
    }
}
