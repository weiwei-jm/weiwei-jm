package jm;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.math.Rand;
import arc.util.Time;
import mindustry.entities.Units;
import mindustry.gen.WeatherState;
import mindustry.type.Weather;
import mindustry.world.meta.Attribute;

public class QuantumStormWeather extends Weather {
    private final Rand rand = new Rand();
    public mindustry.type.StatusEffect erosionStatus;

    public QuantumStormWeather(String name, mindustry.type.StatusEffect erosion) {
        super(name);
        this.erosionStatus = erosion;
        this.duration = 10f * 60f * 60f;
        this.opacityMultiplier = 0.6f;
        this.attrs.set(Attribute.light, -0.2f); 
    }

    @Override
    public void update(WeatherState state) {
        super.update(state);
        if (Mathf.chance(0.1f)) {
            Units.nearby(Core.camera.position.x - 500, Core.camera.position.y - 500, 1000, 1000, unit -> {
                if (unit.isValid() && Mathf.chance(0.15f)) {
                    unit.apply(erosionStatus, 180f);
                }
            });
        }
    }

    @Override
    public void drawOver(WeatherState state) {
        Draw.color(Color.valueOf("00e5ff"));
        Draw.alpha(0.05f * state.opacity());
        // 👇 用原版底层自带的 Fill.rect 覆盖全屏，绝对不报错
        Fill.rect(Core.camera.position.x, Core.camera.position.y, Core.graphics.getWidth(), Core.graphics.getHeight());
        Draw.reset();
    }

    @Override
    public void drawUnder(WeatherState state) {
        if (Core.camera == null || state.intensity() <= 0f) return;

        rand.setSeed(0);
        int count = (int) (200 * state.intensity());

        for (int i = 0; i < count; i++) {
            float size = rand.random(2f, 6f);
            float speed = rand.random(4f, 8f);
            
            float y = Core.camera.position.y + rand.random(-Core.camera.height / 2f, Core.camera.height / 2f);
            float x;
            float windX;

            if (rand.chance(0.5f)) {
                x = Core.camera.position.x - Core.camera.width / 2f + Mathf.mod(rand.random(0f, 1000f) + Time.time * speed, Core.camera.width);
                windX = speed;
            } else {
                x = Core.camera.position.x + Core.camera.width / 2f - Mathf.mod(rand.random(0f, 1000f) + Time.time * speed, Core.camera.width);
                windX = -speed;
            }

            Draw.color(Color.valueOf("00e5ff"));
            Draw.alpha(state.opacity());
            Lines.stroke(size * 0.3f);
            Lines.line(x, y, x - windX * 4f, y); 
            Fill.circle(x, y, size * 0.5f);
        }
        Draw.reset();
    }
}
