package jm;

import arc.graphics.Color;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.Time;
import mindustry.entities.abilities.Ability;
import mindustry.gen.Unit;
import mindustry.Vars;

public class BlinkAbility extends Ability {
    // 最大传送距离：默认 100 格（800像素）
    public float maxDistance = 800f; 
    public float cooldown = 60f; // 冷却调短一点（1秒），方便测试
    public Color effectColor = Color.valueOf("00e5ff"); 
    
    public transient float timer = 0f;
    public static Seq<BlinkEffect> effects = new Seq<>();
    
    public float destX, destY;
    public boolean hasDestination = false;

    public BlinkAbility() {
    }

    public void setDestination(float x, float y) {
        this.destX = x;
        this.destY = y;
        this.hasDestination = true;
    }

    @Override
    public void update(Unit unit) {
        if (timer > 0) timer -= Time.delta;

        if (hasDestination && canBlink()) {
            float dst = Mathf.dst(unit.x, unit.y, destX, destY);
            
            // 计算阈值：最大距离的 1%
            float threshold = maxDistance * 0.01f;
            
            // 如果最大距离小于 100 格（800像素），阈值强制设为 1 格（8像素）
            if (maxDistance < 800f) threshold = 8f;

            // 距离超过阈值时跃迁
            if (dst > threshold) {
                float moveDst = Math.min(dst, maxDistance);
                float angle = Mathf.angle(destX - unit.x, destY - unit.y);
                
                float tx = unit.x + Mathf.cosDeg(angle) * moveDst;
                float ty = unit.y + Mathf.sinDeg(angle) * moveDst;

                trigger(unit, tx, ty);

                // 如果已经传送到了目的地附近，清除目标（避免原地抽搐）
                if (Mathf.dst(tx, ty, destX, destY) < threshold) {
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
        unit.vel.setZero(); // 传送后瞬间停下，不保留惯性
        timer = cooldown;

        Color c = effectColor != null ? effectColor : Color.white;
        effects.add(new BlinkEffect(startX, startY, targetX, targetY, startRot, unit.rotation, c, unit.type));
        
        if (unit == Vars.player.unit()) {
            Vars.renderer.shake(5f, 10f);
        }
    }
                    }
