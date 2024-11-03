package de.johni0702.minecraft.gui.versions.mixin;

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
        if (KeyboardCallback.EVENT.invoker().keyPressed(keyCode, scanCode, modifiers)) {
            ci.cancel();
        }
    }

    @Inject(
            method = "onKey",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;keyReleased(III)Z"),
            cancellable = true
    )
    private void keyReleased(long window, int keyCode, int scanCode, int action, int modifiers, CallbackInfo ci) {
        if (KeyboardCallback.EVENT.invoker().keyReleased(keyCode, scanCode, modifiers)) {
            ci.cancel();
        }
    }

    @Inject(method = "onChar", at = @At("HEAD"), cancellable = true)
    private void charTyped(long window, int codePoint, int modifiers, CallbackInfo ci) {
        if (Character.isBmpCodePoint(codePoint)) {
            if (KeyboardCallback.EVENT.invoker().charTyped((char) codePoint, modifiers)) ci.cancel();
        } else if (Character.isValidCodePoint(codePoint)) {
            if (KeyboardCallback.EVENT.invoker().charTyped(Character.highSurrogate(codePoint), modifiers)) ci.cancel();
            if (KeyboardCallback.EVENT.invoker().charTyped(Character.lowSurrogate(codePoint), modifiers)) ci.cancel();
        }
    }
}
