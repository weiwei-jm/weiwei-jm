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
        // ⚠️ 关键修复：如果不调用这行，原版会在小地图偷偷再画一次，导致特效闪烁！
        super.draw(unit, time); 

        // 每个单位使用自己的专属随机种子
        rand.setSeed(unit.id + (long)(Time.time / 3f)); // 每3帧刷新一次位置，产生强烈的“错位闪烁”感
        
        Draw.color(color);
        Draw.blend(arc.graphics.Blending.additive); // 开启叠加发光

        // 随机范围基于单位的体积 (hitSize 的一半就是半径)
        float radius = unit.hitSize / 2f;

        // 1. 随机方形碎片 (大小和位置完全随机)
        for (int i = 0; i < 6; i++) {
            float ox = rand.random(-radius, radius);
            float oy = rand.random(-radius, radius);
            float size = rand.random(2f, 6f); // 方块大小
            float rot = rand.random(360f);    // 随机角度
            
            // 一半实心，一半空心，增加层次感
            if (i % 2 == 0) {
                Fill.square(unit.x + ox, unit.y + oy, size, rot);
            } else {
                Lines.stroke(1.5f);
                Lines.square(unit.x + ox, unit.y + oy, size, rot);
            }
        }

        // 2. 随机三角形碎片 (像玻璃碴子一样)
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
        Draw.reset();
    }
}
