package de.johni0702.minecraft.gui.versions.forge;

import de.johni0702.minecraft.gui.utils.EventRegistrations;
import de.johni0702.minecraft.gui.versions.MatrixStack;
import de.johni0702.minecraft.gui.versions.callbacks.InitScreenCallback;
import de.johni0702.minecraft.gui.versions.callbacks.OpenGuiScreenCallback;
import de.johni0702.minecraft.gui.versions.callbacks.PostRenderScreenCallback;
import de.johni0702.minecraft.gui.versions.callbacks.PreTickCallback;
import de.johni0702.minecraft.gui.versions.callbacks.RenderHudCallback;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.List;

public class EventsAdapter extends EventRegistrations {
    public static GuiScreen getScreen(GuiScreenEvent event) {
        //#if MC>=10904
        return event.getGui();
        //#else
        //$$ return event.gui;
        //#endif
    }

    public static List<GuiButton> getButtonList(GuiScreenEvent.InitGuiEvent event) {
        //#if MC>=10904
        return event.getButtonList();
        //#else
        //$$ return event.buttonList;
        //#endif
    }

    @SubscribeEvent
    public void preGuiInit(GuiScreenEvent.InitGuiEvent.Pre event) {
        InitScreenCallback.Pre.EVENT.invoker().preInitScreen(getScreen(event));
    }

    @SubscribeEvent
    public void onGuiInit(GuiScreenEvent.InitGuiEvent.Post event) {
        InitScreenCallback.EVENT.invoker().initScreen(getScreen(event), getButtonList(event));
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onGuiClosed(GuiOpenEvent event) {
        OpenGuiScreenCallback.EVENT.invoker().openGuiScreen(
                //#if MC>=10904
                event.getGui()
                //#else
                //$$ event.gui
                //#endif
        );
    }

    public static float getPartialTicks(RenderGameOverlayEvent event) {
        //#if MC>=10904
        return event.getPartialTicks();
        //#else
        //$$ return event.partialTicks;
        //#endif
    }

    public static float getPartialTicks(GuiScreenEvent.DrawScreenEvent.Post event) {
        //#if MC>=10904
        return event.getRenderPartialTicks();
        //#else
        //$$ return event.renderPartialTicks;
        //#endif
    }

    @SubscribeEvent
    public void onGuiRender(GuiScreenEvent.DrawScreenEvent.Post event) {
        PostRenderScreenCallback.EVENT.invoker().postRenderScreen(new MatrixStack(), getPartialTicks(event));
    }

    // Even when event was cancelled cause Lunatrius' InGame-Info-XML mod cancels it and we don't actually care about
    // the event (i.e. the overlay text), just about when it's called.
    @SubscribeEvent(receiveCanceled = true)
    public void renderOverlay(RenderGameOverlayEvent.Text event) {
        RenderHudCallback.EVENT.invoker().renderHud(new MatrixStack(), getPartialTicks(event));
    }

    @SubscribeEvent
    public void tickOverlay(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            PreTickCallback.EVENT.invoker().preTick();
        }
    }
}
