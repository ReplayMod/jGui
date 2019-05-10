//#if MC>=11400
package de.johni0702.minecraft.gui.versions.callbacks;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface KeyboardCallback {
    Event<KeyboardCallback> EVENT = EventFactory.createArrayBacked(
            KeyboardCallback.class,
            (listeners) -> new KeyboardCallback() {
                @Override
                public boolean keyPressed(int keyCode, int modifiers, int scanCode) {
                    for (KeyboardCallback listener : listeners) {
                        if (listener.keyPressed(keyCode, modifiers, scanCode)) {
                            return true;
                        }
                    }
                    return false;
                }

                @Override
                public boolean keyReleased(int keyCode, int modifiers, int scanCode) {
                    for (KeyboardCallback listener : listeners) {
                        if (listener.keyReleased(keyCode, modifiers, scanCode)) {
                            return true;
                        }
                    }
                    return false;
                }

                @Override
                public boolean charTyped(char charCode, int modifiers) {
                    for (KeyboardCallback listener : listeners) {
                        if (listener.charTyped(charCode, modifiers)) {
                            return true;
                        }
                    }
                    return false;
                }
            }
    );

    boolean keyPressed(int keyCode, int modifiers, int scanCode);
    boolean keyReleased(int keyCode, int modifiers, int scanCode);
    boolean charTyped(char keyChar, int modifiers);
}
//#endif
