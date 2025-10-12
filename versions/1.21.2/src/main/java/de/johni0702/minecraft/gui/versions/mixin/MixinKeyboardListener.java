package de.johni0702.minecraft.gui.versions.mixin;

import de.johni0702.minecraft.gui.function.CharInput;
import de.johni0702.minecraft.gui.function.KeyInput;
import de.johni0702.minecraft.gui.versions.callbacks.KeyboardCallback;
import net.minecraft.client.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public class MixinKeyboardListener {
    @Inject(
            method = "onKey",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;keyPressed(III)Z"),
            cancellable = true
    )
    private void keyPressed(long window, int keyCode, int scanCode, int action, int modifiers, CallbackInfo ci) {
        if (KeyboardCallback.EVENT.invoker().keyPressed(new KeyInput(keyCode, scanCode, modifiers))) {
            ci.cancel();
        }
    }

    @Inject(
            method = "onKey",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;keyReleased(III)Z"),
            cancellable = true
    )
    private void keyReleased(long window, int keyCode, int scanCode, int action, int modifiers, CallbackInfo ci) {
        if (KeyboardCallback.EVENT.invoker().keyReleased(new KeyInput(keyCode, scanCode, modifiers))) {
            ci.cancel();
        }
    }

    @Inject(method = "onChar", at = @At("HEAD"), cancellable = true)
    private void charTyped(long window, int codePoint, int modifiers, CallbackInfo ci) {
        if (Character.isBmpCodePoint(codePoint)) {
            if (KeyboardCallback.EVENT.invoker().charTyped(new CharInput((char) codePoint, modifiers))) ci.cancel();
        } else if (Character.isValidCodePoint(codePoint)) {
            if (KeyboardCallback.EVENT.invoker().charTyped(new CharInput(Character.highSurrogate(codePoint), modifiers))) ci.cancel();
            if (KeyboardCallback.EVENT.invoker().charTyped(new CharInput(Character.lowSurrogate(codePoint), modifiers))) ci.cancel();
        }
    }
}
