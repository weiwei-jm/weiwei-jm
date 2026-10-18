package jm;

import arc.Events;
import arc.util.Reflect;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.graphics.MenuRenderer;
import mindustry.mod.Mod;

public class JmMod extends Mod {
    private MenuRenderer originalRenderer;
    private TextMenuRenderer textRenderer;

    @Override
    public void init() {
        Events.on(EventType.ClientLoadEvent.class, e -> {
            try {
                originalRenderer = Reflect.get(Vars.ui.menufrag, "renderer");
                textRenderer = new TextMenuRenderer(originalRenderer);
                Reflect.set(Vars.ui.menufrag, "renderer", textRenderer);
            } catch (Exception ex) {
                arc.util.Log.err("Failed to apply menu text", ex);
            }
        });
    }
}
