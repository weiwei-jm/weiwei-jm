package jm;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.math.Rand;
import arc.util.Time;
import mindustry.Vars;
import mindustry.entities.Units;
import mindustry.gen.WeatherState;
import mindustry.type.Weather;
import mindustry.world.meta.Attribute;

public class QuantumStormWeather extends Weather {
    private final Rand rand = new Rand();
    
    // 你的量子侵蚀状态
    public mindustry.type.StatusEffect erosionStatus;

    public QuantumStormWeather(String name, mindustry.type.StatusEffect erosion) {
        super(name);
        this.erosionStatus = erosion;
        this.duration = 10f * 60f * 60f; // 持续 10 分钟
        this.opacityMultiplier = 0.6f;
        this.attrs.set(Attribute.light, -0.2f); 
    }

    @Override
    public void update(WeatherState state) {
        super.update(state);
        // 每 10 帧触发一次天气伤害判定（降低性能消耗）
        if (Mathf.chance(0.1f)) {
            // 让场上所有单位有 15% 的概率中招（包括玩家）
            Units.nearby(Core.camera.position.x - 500, Core.camera.position.y - 500, 1000, 1000, unit -> {
                if (unit.isValid() && Mathf.chance(0.15f)) {
                    unit.apply(erosionStatus, 180f); // 施加 3 秒（180帧）的量子侵蚀
                }
            });
        }
    }

    @Override
    public void drawOver(WeatherState state) {
        // 让天气看起来更压抑
        Draw.color(Color.valueOf("00e5ff"));
        Draw.alpha(0.05f * state.opacity());
        mindustry.graphics.Drawf.rect(Core.camera.position.x, Core.camera.position.y, Core.graphics.getWidth(), Core.graphics.getHeight());
        Draw.reset();
    }

    @Override
    public void drawUnder(WeatherState state) {
        // 这里的逻辑：屏幕左边往右飞，屏幕右边往左飞
        if (Core.camera == null || state.intensity() <= 0f) return;

        rand.setSeed(0);
        int count = (int) (200 * state.intensity()); // 粒子数量

        for (int i = 0; i < count; i++) {
            float size = rand.random(2f, 6f);
            float speed = rand.random(4f, 8f); // 飞行的速度
            
            // 粒子在屏幕边缘的 Y 轴位置
            float y = Core.camera.position.y + rand.random(-Core.camera.height / 2f, Core.camera.height / 2f);
            float x;
            float windX;

            // 决定是从左边飞还是右边飞
            if (rand.chance(0.5f)) {
                // 左边出生，向右飞
                x = Core.camera.position.x - Core.camera.width / 2f + Mathf.mod(rand.random(0f, 1000f) + Time.time * speed, Core.camera.width);
                windX = speed;
            } else {
                // 右边出生，向左飞
                x = Core.camera.position.x + Core.camera.width / 2f - Mathf.mod(rand.random(0f, 1000f) + Time.time * speed, Core.camera.width);
                windX = -speed;
            }

            // 根据速度拉伸成线条（像激光或者高速粒子）
            Draw.color(Color.valueOf("00e5ff"));
            Draw.alpha(state.opacity());
            Lines.stroke(size * 0.3f);
            Lines.line(x, y, x - windX * 4f, y); 
            Fill.circle(x, y, size * 0.5f);
        }
        Draw.reset();
    }
          }
