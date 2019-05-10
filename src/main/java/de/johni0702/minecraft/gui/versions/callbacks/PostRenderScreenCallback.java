//#if MC>=11400
package de.johni0702.minecraft.gui.versions.callbacks;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface PostRenderScreenCallback {
    Event<PostRenderScreenCallback> EVENT = EventFactory.createArrayBacked(
            PostRenderScreenCallback.class,
            (listeners) -> (partialTicks) -> {
                for (PostRenderScreenCallback listener : listeners) {
                    listener.postRenderScreen(partialTicks);
                }
            }
    );

    void postRenderScreen(float partialTicks);
}
//#endif
