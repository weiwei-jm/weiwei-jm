package jm;

import arc.math.Angles;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.entities.units.AIController;
import mindustry.gen.Healthc;
import mindustry.gen.Unit;

public class JmExecutorAI extends AIController {
    private float execTimer = 0f;
    private int hits = 0; // 记录打了几次，用来模拟原版的处决前摇

    @Override
    public void updateUnit() {
        // 1. 原版基础索敌
        if (retarget()) target = findTarget();
        updateMovement();

        // 2. 如果没目标，重置状态
        if (target == null || !(target instanceof Healthc h) || !h.isValid()) {
            execTimer = 0f;
            hits = 0;
            return;
        }

        float dst = unit.dst(target);

        // 3. 进入攻击范围（原版尖刀射程很短，我们放宽到 300 像素）
        if (dst < 300f) {
            unit.lookAt(target);
            
            // 每次到 10 帧就攻击一次
            if (unit.timer(1, 10f)) {
                
                // 👇 核心：如果敌人血量极高（Boss），触发万剑归宗！
                if (h.health() > 2000f) {
                    hits++;
                    
                    // 前 3 次攻击作为前摇，爆出红色法阵特效
                    if (hits <= 3) {
                        mindustry.content.Fx.chainLightning.at(target.x(), target.y(), 0, arc.graphics.Color.red);
                    } else {
                        // 第 4 次，直接释放万剑归宗！
                        for (int i = 0; i < 8; i++) {
                            float ang = i * 45f + Mathf.random(-20f, 20f);
                            float sx = unit.x + Angles.trnsx(ang, 60f);
                            float sy = unit.y + Angles.trnsy(ang, 60f);
                            
                            // 调用你之前的实体类
                            JmSpearEntity.create((Unit) target, sx, sy, ang, 12f, 9999999f);
                        }
                        // 造成真实伤害
                        h.damage(9999999f);
                        hits = 0; // 重置
                        mindustry.Vars.renderer.shake(5f, 10f);
                    }
                } else {
                    // 👇 如果是小兵，直接秒杀
                    h.damage(9999999f);
                    mindustry.content.Fx.massiveExplosion.at(h.x(), h.y());
                }
            }
        } else {
            // 离得太远，重置攻击段数
            hits = 0;
        }
    }

    @Override
    public void updateWeapons() {
        // 彻底禁用原版武器，防止它去用原本的小机枪平A
    }

    public mindustry.gen.Teamc findTarget() {
        // 索敌范围提升到 400 像素
        return mindustry.entities.Units.closestEnemy(unit.team, unit.x, unit.y, 400f, u -> true);
    }

    @Override
    public boolean retarget() {
        return timer.get(timerTarget, 30);
    }
}
