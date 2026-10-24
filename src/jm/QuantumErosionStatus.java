package jm;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import mindustry.gen.Unit;
import mindustry.type.StatusEffect;

public class QuantumErosionStatus extends StatusEffect {
    
    public QuantumErosionStatus() {
        super("quantum-erosion");
        this.color = Color.valueOf("00e5ff");
        this.damage = 2f; // 每秒受到 2 点伤害（不伤建筑，因为这是加在单位身上的）
        this.speedMultiplier = 0.8f;
        this.show = true;
    }

    @Override
    public void update(Unit unit) {
        super.update(unit);
        
        // 👇 核心：如果感染的是玩家自己的单位，让屏幕产生“错位”和“震动”
        if (unit == mindustry.Vars.player.unit()) {
            // 模拟屏幕撕裂和模糊震动
            Core.camera.position.x += Mathf.random(-3f, 3f);
            Core.camera.position.y += Mathf.random(-3f, 3f);
            
            // 画一层蓝色的屏幕色差（视觉污染）
            if (!Core.settings.getBool("lowQuality")) {
                mindustry.Vars.renderer.effects.add(new mindustry.entities.Effect(15f, e -> {
                    Draw.color(Color.valueOf("00e5ff"));
                    Draw.alpha(0.15f);
                    mindustry.graphics.Drawf.rect(e.x, e.y, Core.graphics.getWidth(), Core.graphics.getHeight());
                }));
            }
        }
    }

    @Override
    public void draw(Unit unit) {
        // 在单位身上画几个蓝色的错位方块
        super.draw(unit);
        Draw.color(color);
        Lines.stroke(2f);
        Lines.square(unit.x + Mathf.random(-5f, 5f), unit.y + Mathf.random(-5f, 5f), unit.hitSize * 0.8f, Mathf.random(360f));
    }
}
