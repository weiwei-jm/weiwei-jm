package jm;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Angles;
import arc.math.Mathf;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.entities.Effect;
import mindustry.gen.Unit;
import mindustry.graphics.Layer;

public class JmSpearEntity extends Effect {
    public float rotation, size;
    public Unit target;
    public float damage;
    public float time;
    public float speed = 15f;
    public boolean hit;

    public static void create(Unit target, float x, float y, float rotation, float size, float damage) {
        JmSpearEntity e = new JmSpearEntity();
        e.target = target;
        e.rotation = rotation;
        e.size = size;
        e.damage = damage;
        e.time = 0f;
        e.hit = false;
        e.lifetime = 120f; // 最长存活 2 秒
        e.layer = Layer.effect + 2;
        e.at(x, y, rotation, Color.valueOf("ff0000"));
    }

    public void update(EffectContainer e) {
        time += Time.delta;
        if (target == null || !target.isValid() || hit) return;

        // 追踪目标
        float angTo = Angles.angle(e.x, e.y, target.x, target.y);
        rotation = Angles.moveToward(rotation, angTo, 10f * Time.delta);

        // 向前飞行
        float dx = Angles.trnsx(rotation, speed * Time.delta);
        float dy = Angles.trnsy(rotation, speed * Time.delta);
        e.x += dx;
        e.y += dy;

        // 判断是否击中目标
        if (Mathf.dst(e.x, e.y, target.x, target.y) < target.hitSize / 2f + size) {
            hit = true;
            target.damage(damage);
            // 原版击中特效
            mindustry.content.Fx.massiveExplosion.at(target.x, target.y);
            // 这里可以加点血雨或者碎片，为了纯原版先省了
        }
    }

    public void draw(EffectContainer e) {
        float fout = Mathf.clamp((lifetime - e.time) / 15f);
        if (fout <= 0f) return;
        Draw.color(Color.valueOf("ff0000"));
        Draw.alpha(fout);

        // 绘制长矛本体（用原版最基础的多边形模拟）
        Tmp.v1.trns(rotation, size * 3f);
        Tmp.v2.trns(rotation + 90, size);
        
        Fill.tri(e.x, e.y, e.x + Tmp.v1.x, e.y + Tmp.v1.y, e.x + Tmp.v2.x, e.y + Tmp.v2.y);
        Fill.tri(e.x, e.y, e.x + Tmp.v1.x, e.y + Tmp.v1.y, e.x - Tmp.v2.x, e.y - Tmp.v2.y);
        
        Draw.color(Color.white);
        Fill.circle(e.x, e.y, size * 0.5f);
        Draw.reset();
    }
}
