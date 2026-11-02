package jm;

import arc.math.Angles;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.entities.units.AIController;
import mindustry.gen.Healthc;
import mindustry.gen.Unit;

public class JmExecutorAI extends AIController {
    
    private float attackTimer = 0f; // 👈 自己写一个计时器
    private int hits = 0; 

    @Override
    public void updateUnit() {
        if (retarget()) target = findTarget();
        updateMovement();

        if (target == null || !(target instanceof Healthc h) || !h.isValid()) {
            attackTimer = 0f;
            hits = 0;
            return;
        }

        float dst = unit.dst(target);

        if (dst < 300f) {
            unit.lookAt(target);
            
            // 👇 每次 update 加上时间，超过 10 帧就触发一次攻击
            attackTimer += Time.delta;
            if (attackTimer >= 10f) {
                attackTimer = 0f;
                
                if (h.health() > 2000f) {
                    hits++;
                    
                    if (hits <= 3) {
                        mindustry.content.Fx.chainLightning.at(target.x(), target.y(), 0, arc.graphics.Color.red);
                    } else {
                        for (int i = 0; i < 8; i++) {
                            float ang = i * 45f + Mathf.random(-20f, 20f);
                            float sx = unit.x + Angles.trnsx(ang, 60f);
                            float sy = unit.y + Angles.trnsy(ang, 60f);
                            
                            JmSpearEntity.create((Unit) target, sx, sy, ang, 12f, 9999999f);
                        }
                        h.damage(9999999f);
                        hits = 0;
                        mindustry.Vars.renderer.shake(5f, 10f);
                    }
                } else {
                    h.damage(9999999f);
                    mindustry.content.Fx.massiveExplosion.at(h.x(), h.y());
                }
            }
        } else {
            hits = 0;
            attackTimer = 0f;
        }
    }

    @Override
    public void updateWeapons() {
    }

    public mindustry.gen.Teamc findTarget() {
        return mindustry.entities.Units.closestEnemy(unit.team, unit.x, unit.y, 400f, u -> true);
    }

    @Override
    public boolean retarget() {
        return timer.get(timerTarget, 30);
    }
}
