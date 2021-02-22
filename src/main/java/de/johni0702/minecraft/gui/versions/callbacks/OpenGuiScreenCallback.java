package de.johni0702.minecraft.gui.versions.callbacks;

import de.johni0702.minecraft.gui.utils.Event;
import net.minecraft.client.gui.screen.Screen;

public interface OpenGuiScreenCallback {
    Event<OpenGuiScreenCallback> EVENT = Event.create((listeners) ->
            (screen) -> {
                for (OpenGuiScreenCallback listener : listeners) {
                    listener.openGuiScreen(screen);
                }
            }
    );

    void openGuiScreen(Screen screen);
}
