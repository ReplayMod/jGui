//#if FABRIC>=1
package de.johni0702.minecraft.gui.versions.mixin;

import de.johni0702.minecraft.gui.versions.callbacks.KeyboardCallback;
import net.minecraft.client.Keyboard;
import net.minecraft.client.gui.Element;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Group;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//#if MC>=11700
//$$ import net.minecraft.client.gui.screen.Screen;
//#else
import net.minecraft.client.gui.ParentElement;
//#endif

@Mixin(Keyboard.class)
public class MixinKeyboardListener {
    @Inject(
            method = "method_1454",
            //#if MC>=11700
            //$$ at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;keyPressed(III)Z"),
            //#else
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/ParentElement;keyPressed(III)Z"),
            //#endif
            cancellable = true
    )
    //#if MC>=11700
    //#if MC>=11903
    //$$ static
    //#endif
    //$$ private void keyPressed(int i, Screen screen, boolean[] bls, int keyCode, int scanCode, int modifiers, CallbackInfo ci) {
    //#else
    private void keyPressed(int i, boolean[] bls, ParentElement element, int keyCode, int scanCode, int modifiers, CallbackInfo ci) {
    //#endif
        if (KeyboardCallback.EVENT.invoker().keyPressed(keyCode, scanCode, modifiers)) {
            bls[0] = true;
            ci.cancel();
        }
    }

    @Inject(
            method = "method_1454",
            //#if MC>=11700
            //$$ at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;keyReleased(III)Z"),
            //#else
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/ParentElement;keyReleased(III)Z"),
            //#endif
            cancellable = true
    )
    //#if MC>=11700
    //#if MC>=11903
    //$$ static
    //#endif
    //$$ private void keyReleased(int i, Screen screen, boolean[] bls, int keyCode, int scanCode, int modifiers, CallbackInfo ci) {
    //#else
    private void keyReleased(int i, boolean[] bls, ParentElement element, int keyCode, int scanCode, int modifiers, CallbackInfo ci) {
    //#endif
        if (KeyboardCallback.EVENT.invoker().keyReleased(keyCode, scanCode, modifiers)) {
            bls[0] = true;
            ci.cancel();
        }
    }

    @Inject(method = "method_1458", at = @At("HEAD"), cancellable = true)
    @Group(min = 1, max = 1, name = "replaymod-jgui-charTyped-int")
    private static void charTyped(Element element, int keyChar, int modifiers, CallbackInfo ci) {
        if (KeyboardCallback.EVENT.invoker().charTyped((char) keyChar, modifiers)) {
            ci.cancel();
        }
    }

    @Inject(method = "lambda$onCharEvent$5", at = @At("HEAD"), cancellable = true, remap = false)
    @Group(min = 1, max = 1, name = "replaymod-jgui-charTyped-int")
    private void charTypedOptifine(int keyChar, int modifiers, Element element, CallbackInfo ci) {
        if (KeyboardCallback.EVENT.invoker().charTyped((char) keyChar, modifiers)) {
            ci.cancel();
        }
   }

   @Inject(method = "method_1473", at = @At("HEAD"), cancellable = true)
   @Group(min = 1, max = 1, name = "replaymod-jgui-charTyped-char")
   private static void charTyped(Element element, char keyChar, int modifiers, CallbackInfo ci) {
       if (KeyboardCallback.EVENT.invoker().charTyped(keyChar, modifiers)) {
           ci.cancel();
       }
   }

    @Inject(method = "lambda$onCharEvent$6", at = @At("HEAD"), cancellable = true, remap = false)
    @Group(min = 1, max = 1, name = "replaymod-jgui-charTyped-char")
    private void charTypedOptifine(char keyChar, int modifiers, Element element, CallbackInfo ci) {
        if (KeyboardCallback.EVENT.invoker().charTyped(keyChar, modifiers)) {
            ci.cancel();
        }
    }
}
//#endif
