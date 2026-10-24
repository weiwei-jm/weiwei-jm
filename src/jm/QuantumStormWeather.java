package jm;

import arc.Core;
import arc.graphics.Blending; // 👈 新增：用于提亮画面
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

    // 👇 降低鲜明度：改用柔和的冰蓝色，不再是纯粹的 00e5ff
    public Color stormColor = Color.valueOf("a3d5ff"); 

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
        // 提亮画面：使用叠加发光模式
        Draw.blend(Blending.additive);
        
        Draw.color(stormColor);
        // 降低透明度，让它只提供一层淡淡的亮蓝色光晕，不遮挡视线
        Draw.alpha(0.03f * state.opacity()); 
        
        Fill.rect(Core.camera.position.x, Core.camera.position.y, Core.graphics.getWidth(), Core.graphics.getHeight());
        
        // 恢复默认混合模式
        Draw.blend();
        Draw.reset();
    }

    @Override
    public void drawUnder(WeatherState state) {
        if (Core.camera == null || state.intensity() <= 0f) return;

        // 提亮粒子：开启发光模式
        Draw.blend(Blending.additive);
        
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

            Draw.color(stormColor);
            // 粒子透明度可以高一点，因为叠加模式下会非常好看
            Draw.alpha(state.opacity() * 0.8f); 
            
            Lines.stroke(size * 0.3f);
            Lines.line(x, y, x - windX * 4f, y); 
            Fill.circle(x, y, size * 0.5f);
        }
        
        Draw.blend();
        Draw.reset();
    }
}
