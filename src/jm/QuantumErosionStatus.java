package jm;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import mindustry.gen.Unit;
import mindustry.type.StatusEffect;
import mindustry.entities.units.StatusEntry;

public class QuantumErosionStatus extends StatusEffect {
    
    public QuantumErosionStatus() {
        super("quantum-erosion");
        this.color = Color.valueOf("00e5ff");
        this.damage = 2f;
        this.speedMultiplier = 0.8f;
        this.show = true;
    }

    @Override
    public void update(Unit unit, StatusEntry entry) {
        super.update(unit, entry); // 这样写参数就全了，不会报错
        
        if (unit == mindustry.Vars.player.unit()) {
            Core.camera.position.x += Mathf.random(-3f, 3f);
            Core.camera.position.y += Mathf.random(-3f, 3f);
        }
    }

    @Override
    public void draw(Unit unit, float time) {
        super.draw(unit, time);
        Draw.color(color);
        Lines.stroke(2f);
        Lines.square(unit.x + Mathf.random(-5f, 5f), unit.y + Mathf.random(-5f, 5f), unit.hitSize * 0.8f, Mathf.random(360f));
        Draw.reset();
    }
}
