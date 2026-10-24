package jm;

import arc.Core;
import arc.Events;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.math.Rand;
import arc.util.Time;
import mindustry.gen.Unit;
import mindustry.type.StatusEffect;
import mindustry.entities.units.StatusEntry;
import mindustry.game.EventType;

public class QuantumErosionStatus extends StatusEffect {
    
    private static final Rand rand = new Rand();
    private static boolean glitchInitialized = false;

    public QuantumErosionStatus() {
        super("quantum-erosion");
        this.localizedName = "量子侵蚀"; // 中文名
        this.color = Color.valueOf("00e5ff");
        this.damage = 2f;
        this.speedMultiplier = 0.8f;
        this.show = true;

        // 全屏故障特效（保留之前的）
        if (!glitchInitialized) {
            glitchInitialized = true;
            Events.run(EventType.Trigger.draw, () -> {
                if (mindustry.Vars.player == null || mindustry.Vars.player.unit() == null) return;
                if (!mindustry.Vars.player.unit().hasEffect(this)) return;

                Draw.blend(arc.graphics.Blending.additive);
                rand.setSeed((long) (Time.time / 2f)); 
                int glitchCount = rand.random(5, 15);
                for (int i = 0; i < glitchCount; i++) {
                    float x = Core.camera.position.x + rand.random(-Core.camera.width / 2f, Core.camera.width / 2f);
                    float y = Core.camera.position.y + rand.random(-Core.camera.height / 2f, Core.camera.height / 2f);
                    float w = rand.random(20f, 200f);
                    float h = rand.random(2f, 15f);

                    Draw.color(Color.red, 0.2f);
                    Fill.rect(x - 5f, y, w, h);

                    Draw.color(Color.valueOf("00e5ff"), 0.2f);
                    Fill.rect(x + 5f, y, w, h);
                }
                Draw.blend();
                Draw.reset();
            });
        }
    }

    @Override
    public void update(Unit unit, StatusEntry entry) {
        super.update(unit, entry);
        
        // 玩家屏幕抖动
        if (unit == mindustry.Vars.player.unit()) {
            if (Mathf.chance(0.3f)) {
                Core.camera.position.x += Mathf.random(-4f, 4f);
                Core.camera.position.y += Mathf.random(-2f, 2f);
            }
            if (Mathf.chance(0.05f)) {
                Core.camera.position.x += Mathf.random(-15f, 15f);
            }
        }
    }

@Override
public void draw(Unit unit, float time) {
    // 调用原版方法保证不闪烁
    super.draw(unit, time); 

    // 👇 核心修改 1：强制拔高图层到 155.1，盖住所有单位、墙体甚至大部分 UI
    Draw.z(155.1f);

    rand.setSeed(unit.id + (long)(Time.time / 3f)); 
    
    Draw.color(color);
    Draw.blend(arc.graphics.Blending.additive); 

    float radius = unit.hitSize / 2f;

    for (int i = 0; i < 6; i++) {
        float ox = rand.random(-radius, radius);
        float oy = rand.random(-radius, radius);
        float size = rand.random(2f, 6f); 
        float rot = rand.random(360f);    
        
        if (i % 2 == 0) {
            Fill.square(unit.x + ox, unit.y + oy, size, rot);
        } else {
            Lines.stroke(1.5f);
            Lines.square(unit.x + ox, unit.y + oy, size, rot);
        }
    }

    for (int i = 0; i < 5; i++) {
        float ox = rand.random(-radius * 1.2f, radius * 1.2f);
        float oy = rand.random(-radius * 1.2f, radius * 1.2f);
        float size = rand.random(3f, 8f);
        float rot = rand.random(360f);
        
        Fill.tri(
            unit.x + ox + Mathf.cosDeg(rot) * size, unit.y + oy + Mathf.sinDeg(rot) * size,
            unit.x + ox + Mathf.cosDeg(rot + 120) * size, unit.y + oy + Mathf.sinDeg(rot + 120) * size,
            unit.x + ox + Mathf.cosDeg(rot + 240) * size, unit.y + oy + Mathf.sinDeg(rot + 240) * size
        );
    }

    Draw.blend();
    
    // 👇 核心修改 2：画完之后一定要 Reset，把图层还给后面的渲染，否则整局游戏会卡死
    Draw.z(mindustry.graphics.Layer.effect); 
    Draw.reset();
                              }
}
