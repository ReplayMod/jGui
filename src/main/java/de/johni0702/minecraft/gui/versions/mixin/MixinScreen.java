//#if FABRIC>=1
package de.johni0702.minecraft.gui.versions.mixin;

import de.johni0702.minecraft.gui.versions.callbacks.InitScreenCallback;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

//#if MC>=11700
//$$ import com.google.common.collect.Collections2;
//$$ import net.minecraft.client.gui.Element;
//#endif

// Increased priority so we can consider existing third-party buttons when choosing the position for our button
@Mixin(value = Screen.class, priority = 1100)
public class MixinScreen {

    //#if MC>=11700
    //$$ @Shadow @Final private List<Element> children;
    //#else
    @Shadow
    protected @Final List<AbstractButtonWidget> buttons;
    //#endif

    //#if MC>=12111
    //$$ @Inject(method = "init(II)V", at = @At("HEAD"))
    //#else
    @Inject(method = "init(Lnet/minecraft/client/MinecraftClient;II)V", at = @At("HEAD"))
    //#endif
    private void preInit(CallbackInfo ci) {
        firePreInit();
    }

    //#if MC>=12111
    //$$ @Inject(method = "init(II)V", at = @At("TAIL"))
    //#else
    @Inject(method = "init(Lnet/minecraft/client/MinecraftClient;II)V", at = @At("TAIL"))
    //#endif
    private void init(CallbackInfo ci) {
        firePostInit();
    }

    //#if MC>=11904
    //$$ @Inject(method = "resize", at = @At("HEAD"))
    //$$ private void preResize(CallbackInfo ci) {
    //$$     firePreInit();
    //$$ }
    //$$
    //$$ @Inject(method = "resize", at = @At("TAIL"))
    //$$ private void resize(CallbackInfo ci) {
    //$$     firePostInit();
    //$$ }
    //#endif

    @Unique
    private void firePreInit() {
        InitScreenCallback.Pre.EVENT.invoker().preInitScreen((Screen) (Object) this);
    }

    @Unique
    private void firePostInit() {
        InitScreenCallback.EVENT.invoker().initScreen(
                (Screen) (Object) this,
                //#if MC>=11700
                //$$ Collections2.transform(Collections2.filter(this.children, it -> it instanceof ClickableWidget), it -> (ClickableWidget) it)
                //#else
                buttons
                //#endif
        );
    }
}
//#endif
