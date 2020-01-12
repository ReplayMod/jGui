//#if FABRIC>=1
package de.johni0702.minecraft.gui.versions.callbacks;

import de.johni0702.minecraft.gui.utils.Event;

public interface PostRenderHudCallback {
    Event<PostRenderHudCallback> EVENT = Event.create((listeners) ->
            (partialTicks) -> {
                for (PostRenderHudCallback listener : listeners) {
                    listener.postRenderHud(partialTicks);
                }
            }
    );

    void postRenderHud(float partialTicks);
}
//#endif
