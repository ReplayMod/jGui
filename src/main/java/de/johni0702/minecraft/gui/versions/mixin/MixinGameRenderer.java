//#if FABRIC>=1
package de.johni0702.minecraft.gui.versions.mixin;

import de.johni0702.minecraft.gui.versions.callbacks.PostRenderHudCallback;
import de.johni0702.minecraft.gui.versions.callbacks.PostRenderScreenCallback;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class MixinGameRenderer {
    @Inject(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/hud/InGameHud;render(F)V",
                    shift = At.Shift.AFTER
            )
    )
    private void postRenderOverlay(float partialTicks, long nanoTime, boolean renderWorld, CallbackInfo ci) {
        PostRenderHudCallback.EVENT.invoker().postRenderHud(partialTicks);
    }

    @Inject(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screen/Screen;render(IIF)V",
                    shift = At.Shift.AFTER
            )
    )
    private void postRenderScreen(float partialTicks, long nanoTime, boolean renderWorld, CallbackInfo ci) {
        PostRenderScreenCallback.EVENT.invoker().postRenderScreen(partialTicks);
    }
}
//#endif
