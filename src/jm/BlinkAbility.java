package jm;

import arc.graphics.Color;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.Time;
import mindustry.entities.abilities.Ability;
import mindustry.gen.Unit;
import mindustry.Vars;

public class BlinkAbility extends Ability {
    // 默认最大传送距离 100 格（1格=8像素，所以800像素）
    public float maxDistance = 800f; 
    public float cooldown = 120f; // 测试用，冷却短一点，2秒一次
    public Color effectColor = Color.valueOf("00e5ff"); 
    
    public transient float timer = 0f;
    public static Seq<BlinkEffect> effects = new Seq<>();
    
    // 你要到达的目的地坐标
    public float destX, destY;
    public boolean hasDestination = false;

    public BlinkAbility() {
    }

    // 外部调用，设置目的地
    public void setDestination(float x, float y) {
        this.destX = x;
        this.destY = y;
        this.hasDestination = true;
    }

    @Override
    public void update(Unit unit) {
        if (timer > 0) timer -= Time.delta;

        // 如果有目的地，且冷却好了
        if (hasDestination && canBlink()) {
            float dst = Mathf.dst(unit.x, unit.y, destX, destY);
            
            // 计算触发阈值（最大距离的 1%）
            float threshold = maxDistance * 0.01f;
            
            // 如果最大传送距离小于 100（即 800像素），那么阈值强行设为 1 格（8像素）
            if (maxDistance < 800f) {
                threshold = 8f;
            }

            // 如果当前离目的地超过阈值，直接跃迁过去
            if (dst > threshold) {
                // 跃迁逻辑：不能超过最大距离，如果超过了就传送到途中的最大距离处
                float moveDst = Math.min(dst, maxDistance);
                float angle = Mathf.angle(destX - unit.x, destY - unit.y);
                
                float tx = unit.x + Mathf.cosDeg(angle) * moveDst;
                float ty = unit.y + Mathf.sinDeg(angle) * moveDst;

                trigger(unit, tx, ty);

                // 如果已经到达目的地附近，清除目的地
                if (dst <= threshold) {
                    hasDestination = false;
                }
            }
        }
    }

    public boolean canBlink() {
        return timer <= 0;
    }

    public void trigger(Unit unit, float targetX, float targetY) {
        if (!canBlink()) return;
        
        float startX = unit.x;
        float startY = unit.y;
        float startRot = unit.rotation;

        unit.x = targetX;
        unit.y = targetY;
        unit.vel.setZero();
        timer = cooldown;

        Color c = effectColor != null ? effectColor : Color.white;
        effects.add(new BlinkEffect(startX, startY, targetX, targetY, startRot, unit.rotation, c, unit.type));
        
        if (unit == Vars.player.unit()) {
            Vars.renderer.shake(5f, 10f);
        }
    }
}
