package jm;

import arc.math.Angles;
import arc.math.Mathf;
import arc.math.Rand;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.entities.units.AIController;
import mindustry.gen.Healthc;
import mindustry.gen.Unit;

public class JmExecutorAI extends AIController {
    private float stageTime = 0f;
    private int stage = 0;
    private Unit execTarget = null;
    private final Rand rand = new Rand();

    @Override
    public void updateUnit() {
        if (retarget()) target = findTarget();
        updateMovement();

        // 如果没目标就重置
        if (target == null || !(target instanceof Healthc h) || !h.isValid()) {
            stage = 0;
            stageTime = 0f;
            execTarget = null;
            return;
        }

        float dst = unit.dst(target);

        // 触发条件：敌人在 400 像素内，且还没进入处决
        if (dst < 400f && stage == 0) {
            if (h.health() > 2000f) {
                // 高血量敌人，开启处决阶段
                stage = 1;
                stageTime = 0f;
                execTarget = (Unit) target;
            } else {
                // 杂兵直接秒杀
                h.damage(9999999f);
                mindustry.content.Fx.massiveExplosion.at(h.x(), h.y());
            }
        }

        // 👇 处决动画展开逻辑
        if (stage > 0 && execTarget != null) {
            stageTime += Time.delta;

            // 第 1 阶段：准备动作（0 - 40帧）
            if (stage == 1) {
                unit.lookAt(execTarget);
                if (stageTime >= 40f) {
                    stage = 2;
                    stageTime = 0f;
                }
            }
            // 第 2 阶段：万剑穿刺（每 3 帧生成一把剑）
            else if (stage == 2) {
                if (stageTime >= 3f) {
                    stageTime = 0f;
                    rand.setSeed(Time.time);
                    
                    // 随机角度生成一把长矛
                    float ang = rand.random(360f);
                    float sx = unit.x + Angles.trnsx(ang, 60f);
                    float sy = unit.y + Angles.trnsy(ang, 60f);
                    
                    JmSpearEntity.create(execTarget, sx, sy, ang, rand.random(8f, 15f), 9999999f);
                    
                    // 稍微震一下屏
                    if (unit == mindustry.Vars.player.unit()) {
                        mindustry.Vars.renderer.shake(2f, 5f);
                    }
                }

                // 几秒后结束处决
                if (stageTime > 60f) {
                    stage = 0;
                    stageTime = 0f;
                    execTarget = null;
                }
            }
        }
    }

    @Override
    public void updateWeapons() {
        // 屏蔽原版武器
    }

    public mindustry.gen.Teamc findTarget() {
        return mindustry.entities.Units.closestEnemy(unit.team, unit.x, unit.y, 400f, u -> true);
    }

    @Override
    public boolean retarget() {
        return timer.get(timerTarget, 30);
    }
}
