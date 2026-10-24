package jm;

import arc.Core;
import arc.Events; // 👈 引入事件
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.math.Rand;
import arc.util.Time;
import mindustry.entities.Units;
import mindustry.game.EventType;
import mindustry.gen.WeatherState;
import mindustry.type.Weather;
import mindustry.world.meta.Attribute;

public class QuantumStormWeather extends Weather {
    private final Rand rand = new Rand();
    public mindustry.type.StatusEffect erosionStatus;

    // 柔和的发光冰蓝色
    public Color stormColor = Color.valueOf("80c8ff"); 

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

    // ⚠️ 放弃官方的 drawUnder 和 drawOver，用我们自己的全局事件强制盖在最上层！
    public void init() {
        Events.run(EventType.Trigger.draw, () -> {
            // 检查当前地图里是否正在刮这个风暴
            boolean active = mindustry.Vars.state.rules.weather.contains(w -> w.weather == this);
            if (!active) return;
            
            float intensity = 1f; // 风暴强度

            // 1. 拔高图层：直接画在顶层，甚至盖住大部分UI
            Draw.z(155.1f);
            Draw.blend(Blending.additive);

            // 2. 画全屏发光遮罩
            Draw.color(stormColor);
            Draw.alpha(0.08f * intensity); 
            Fill.rect(Core.camera.position.x, Core.camera.position.y, Core.graphics.getWidth(), Core.graphics.getHeight());

            // 3. 画粒子（固定数量 300 个，无论怎么缩放都不会变多）
            rand.setSeed(0);
            int count = 300; 

            for (int i = 0; i < count; i++) {
                float size = rand.random(2f, 6f);
                float speed = rand.random(4f, 8f);
                
                // 使用固定数值来生成相对屏幕的随机坐标
                float yOffset = rand.random(-Core.camera.height / 2f, Core.camera.height / 2f);
                float y = Core.camera.position.y + yOffset;
                
                float x;
                float windX;

                if (rand.chance(0.5f)) {
                    // 向右飞
                    float offsetX = Mathf.mod(rand.random(0f, 1000f) + Time.time * speed, Core.camera.width);
                    x = Core.camera.position.x - Core.camera.width / 2f + offsetX;
                    windX = speed;
                } else {
                    // 向左飞
                    float offsetX = Mathf.mod(rand.random(0f, 1000f) + Time.time * speed, Core.camera.width);
                    x = Core.camera.position.x + Core.camera.width / 2f - offsetX;
                    windX = -speed;
                }

                Draw.color(stormColor);
                Draw.alpha(intensity); // 拉满亮度
                
                Lines.stroke(size * 0.3f);
                Lines.line(x, y, x - windX * 4f, y); 
                Fill.circle(x, y, size * 0.5f);
            }
            
            Draw.blend();
            Draw.reset();
        });
    }
                                              }
