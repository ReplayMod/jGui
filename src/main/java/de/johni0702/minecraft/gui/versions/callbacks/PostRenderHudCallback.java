//#if MC>=11400
package de.johni0702.minecraft.gui.versions.callbacks;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface PostRenderHudCallback {
    Event<PostRenderHudCallback> EVENT = EventFactory.createArrayBacked(
            PostRenderHudCallback.class,
            (listeners) -> (partialTicks) -> {
                for (PostRenderHudCallback listener : listeners) {
                    listener.postRenderHud(partialTicks);
                }
            }
    );

    void postRenderHud(float partialTicks);
}
//#endif
