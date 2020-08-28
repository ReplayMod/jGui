//#if FABRIC>=1
package de.johni0702.minecraft.gui.versions.callbacks;

import de.johni0702.minecraft.gui.utils.Event;

public interface KeyboardCallback {
    Event<KeyboardCallback> EVENT = Event.create((listeners) ->
            new KeyboardCallback() {
                @Override
                public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
                    for (KeyboardCallback listener : listeners) {
                        if (listener.keyPressed(keyCode, scanCode, modifiers)) {
                            return true;
                        }
                    }
                    return false;
                }

                @Override
                public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
                    for (KeyboardCallback listener : listeners) {
                        if (listener.keyReleased(keyCode, scanCode, modifiers)) {
                            return true;
                        }
                    }
                    return false;
                }

                @Override
                public boolean charTyped(char charCode, int scanCode) {
                    for (KeyboardCallback listener : listeners) {
                        if (listener.charTyped(charCode, scanCode)) {
                            return true;
                        }
                    }
                    return false;
                }
            }
    );

    boolean keyPressed(int keyCode, int scanCode, int modifiers);
    boolean keyReleased(int keyCode, int scanCode, int modifiers);
    boolean charTyped(char keyChar, int scanCode);
}
//#endif
