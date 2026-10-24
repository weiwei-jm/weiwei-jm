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
import mindustry.graphics.Layer;

public class QuantumErosionStatus extends StatusEffect {
    
    private static final Rand rand = new Rand();
    private static boolean glitchInitialized = false;

    public QuantumErosionStatus() {
        super("quantum-erosion");
        this.color = Color.valueOf("00e5ff");
        this.damage = 2f;
        this.speedMultiplier = 0.8f;
        this.show = true;

        // 👇 在初始化时监听全局绘制事件，用来画高级的屏幕错位特效
        if (!glitchInitialized) {
            glitchInitialized = true;
            Events.run(EventType.Trigger.draw, () -> {
                // 如果玩家没有这个状态，就不画
                if (mindustry.Vars.player == null || mindustry.Vars.player.unit() == null) return;
                if (!mindustry.Vars.player.unit().hasEffect(this)) return;

                // 开启叠加发光模式
                Draw.blend(arc.graphics.Blending.additive);
                
                // 1. 屏幕随机撕裂方块 (RGB 色差故障)
                rand.setSeed((long) (Time.time / 2f)); // 每2帧变一次，制造闪烁感
                int glitchCount = rand.random(5, 15);
                for (int i = 0; i < glitchCount; i++) {
                    float x = Core.camera.position.x + rand.random(-Core.camera.width / 2f, Core.camera.width / 2f);
                    float y = Core.camera.position.y + rand.random(-Core.camera.height / 2f, Core.camera.height / 2f);
                    float w = rand.random(20f, 200f);
                    float h = rand.random(2f, 15f);

                    // 红色故障块（向左边偏一点）
                    Draw.color(Color.red, 0.2f);
                    Fill.rect(x - 5f, y, w, h);

                    // 蓝色故障块（向右边偏一点）
                    Draw.color(Color.valueOf("00e5ff"), 0.2f);
                    Fill.rect(x + 5f, y, w, h);
                }

                // 2. 全屏雷达扫描线 (错位感)
                Draw.color(Color.valueOf("00e5ff"), 0.05f);
                for (float y = Core.camera.position.y - Core.camera.height / 2f; y < Core.camera.position.y + Core.camera.height / 2f; y += 8f) {
                    Fill.rect(Core.camera.position.x, y, Core.camera.width, 2f);
                }

                Draw.blend();
                Draw.reset();
            });
        }
    }

    @Override
    public void update(Unit unit, StatusEntry entry) {
        super.update(unit, entry);
        
        // 如果中招的是玩家，强制修改相机位置制造“画面碎裂拉扯”的错觉
        if (unit == mindustry.Vars.player.unit()) {
            // 随机小幅度抖动
            if (Mathf.chance(0.3f)) {
                Core.camera.position.x += Mathf.random(-4f, 4f);
                Core.camera.position.y += Mathf.random(-2f, 2f);
            }
            // 偶尔来一次大范围拉扯
            if (Mathf.chance(0.05f)) {
                Core.camera.position.x += Mathf.random(-15f, 15f);
            }
        }
    }

    @Override
    public void draw(Unit unit, float time) {
        super.draw(unit, time);
        
        // 单位身上画几个蓝色的几何碎片，代表“量子侵蚀”
        rand.setSeed(unit.id);
        Draw.z(Layer.effect);
        Draw.color(color);
        for (int i = 0; i < 3; i++) {
            float ox = rand.random(-unit.hitSize, unit.hitSize);
            float oy = rand.random(-unit.hitSize, unit.hitSize);
            float size = rand.random(3f, 8f) * (1f - (time % 30f) / 30f); // 闪烁
            Lines.square(unit.x + ox, unit.y + oy, size, rand.random(360f));
        }
        Draw.reset();
    }
}
