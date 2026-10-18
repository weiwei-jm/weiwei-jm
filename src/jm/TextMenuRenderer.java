package jm;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Drawf;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.graphics.MenuRenderer;

public class TextMenuRenderer extends MenuRenderer {
    private final MenuRenderer original;

    public TextMenuRenderer(MenuRenderer original) {
        this.original = original;
    }

    @Override
    public void render() {
        if (original != null) {
            original.render();
        }

        Draw.reset();

        String text = "mj你快回来吧";
        float scale = 1f + Mathf.sin(Time.time * 0.002f) * 0.2f;
        float x = Core.graphics.getWidth() / 2f + 80f;
        float y = Core.graphics.getHeight() / 2f + 40f;
        float rotation = -15f;

        Drawf.text(text, x, y, rotation, Color.white, scale, 0);

        Draw.reset();
    }

    @Override
    public void dispose() {
    }
}
