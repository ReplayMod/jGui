//#if FABRIC>=1
package de.johni0702.minecraft.gui.versions.mixin;

import de.johni0702.minecraft.gui.versions.callbacks.RenderHudCallback;
import net.minecraft.client.gui.hud.InGameHud;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//#if MC>=12100
//$$ import net.minecraft.client.render.RenderTickCounter;
//#endif

//#if MC>=12000
//$$ import net.minecraft.client.gui.DrawContext;
//#else
import net.minecraft.client.util.math.MatrixStack;
//#endif

@Mixin(InGameHud.class)
public class Mixin_RenderHudCallback {
    @Inject(
            //#if MC>=12005
            //$$ method = "method_55807",
            //#else
            method = "render",
            //#endif
            //#if MC>=12002
            //$$ at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/DebugHud;shouldShowDebugHud()Z")
            //#else
            at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/client/options/GameOptions;debugEnabled:Z")
            //#endif
    )
    //#if MC>=12100
    //$$ private void renderOverlay(DrawContext stack, RenderTickCounter renderTickCounter, CallbackInfo ci) {
    //$$     float partialTicks = renderTickCounter.getTickDelta(true);
    //#elseif MC>=12000
    //$$ private void renderOverlay(DrawContext stack, float partialTicks, CallbackInfo ci) {
    //#elseif MC>=11600
    private void renderOverlay(MatrixStack stack, float partialTicks, CallbackInfo ci) {
    //#else
    //$$ private void renderOverlay(float partialTicks, CallbackInfo ci) {
    //$$     MatrixStack stack = new MatrixStack();
    //#endif
        RenderHudCallback.EVENT.invoker().renderHud(stack, partialTicks);
    }
}
//#endif
