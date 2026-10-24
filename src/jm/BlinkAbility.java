package jm;

import arc.graphics.Color;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.Time;
import mindustry.entities.abilities.Ability;
import mindustry.gen.Unit;

public class BlinkAbility extends Ability {
    public float maxDistance = 100f * 8f; // 默认 100 格
    public float cooldown = 600f;         // 默认冷却 10 秒
    public Color effectColor = Color.valueOf("00e5ff"); // 默认特效色
    
    // 记录当前状态
    public transient float timer = 0f;
    public static Seq<BlinkEffect> effects = new Seq<>();

    public BlinkAbility() {
    }

    @Override
    public void update(Unit unit) {
        if (timer > 0) timer -= Time.delta;
    }

    public boolean canBlink() {
        return timer <= 0;
    }

    public void trigger(Unit unit, float targetX, float targetY) {
        if (!canBlink()) return;
        
        float dst = Mathf.dst(unit.x, unit.y, targetX, targetY);
        if (dst > maxDistance) {
            // 太远了，屏幕中间弹字
            mindustry.Vars.ui.showInfoFade("[red]太远了！[]");
            return;
        }

        float startX = unit.x;
        float startY = unit.y;
        float startRot = unit.rotation;

        unit.x = targetX;
        unit.y = targetY;
        unit.vel.setZero();
        timer = cooldown;

        Color c = effectColor != null ? effectColor : Color.white;
        effects.add(new BlinkEffect(startX, startY, targetX, targetY, startRot, unit.rotation, c, unit.type));
        
        if (unit == mindustry.Vars.player.unit()) {
            mindustry.Vars.renderer.shake(5f, 10f);
        }
    }
}
