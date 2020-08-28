//#if FABRIC>=1
package de.johni0702.minecraft.gui.versions.mixin;

import de.johni0702.minecraft.gui.versions.callbacks.KeyboardCallback;
import net.minecraft.client.Keyboard;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ParentElement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public class MixinKeyboardListener {
    @Inject(
            method = "method_1454",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/ParentElement;keyPressed(III)Z"),
            cancellable = true
    )
    private void keyPressed(int i, boolean[] bls, ParentElement element, int keyCode, int scanCode, int modifiers, CallbackInfo ci) {
        if (KeyboardCallback.EVENT.invoker().keyPressed(keyCode, scanCode, modifiers)) {
            bls[0] = true;
            ci.cancel();
        }
    }

    @Inject(
            method = "method_1454",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/ParentElement;keyReleased(III)Z"),
            cancellable = true
    )
    private void keyReleased(int i, boolean[] bls, ParentElement element, int keyCode, int scanCode, int modifiers, CallbackInfo ci) {
        if (KeyboardCallback.EVENT.invoker().keyReleased(keyCode, scanCode, modifiers)) {
            bls[0] = true;
            ci.cancel();
        }
    }

    @Inject(method = "method_1458", at = @At("HEAD"), cancellable = true)
    private static void charTyped(Element element, int keyChar, int modifiers, CallbackInfo ci) {
        if (KeyboardCallback.EVENT.invoker().charTyped((char) keyChar, modifiers)) {
            ci.cancel();
        }
   }

   @Inject(method = "method_1473", at = @At("HEAD"), cancellable = true)
   private static void charTyped(Element element, char keyChar, int modifiers, CallbackInfo ci) {
       if (KeyboardCallback.EVENT.invoker().charTyped(keyChar, modifiers)) {
           ci.cancel();
       }
   }
}
//#endif
