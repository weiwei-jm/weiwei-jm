package jm;

import arc.graphics.Color;
import arc.math.Angles;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.gen.Unit;
import mindustry.graphics.Draw;
import mindustry.graphics.Layer;

public class JmSpearEffect extends Effect {
    public float sx, sy, tx, ty, length, time;
    public Unit target;

    public static void create(Unit unit, float sx, float sy, float tx, float ty) {
        JmSpearEffect e = new JmSpearEffect();
        e.sx = sx; e.sy = sy;
        e.tx = tx; e.ty = ty;
        e.target = unit;
        e.length = Mathf.dst(sx, sy, tx, ty);
        e.time = 0f;
        e.lifetime = 30f; // 0.5秒特效
        e.layer = Layer.effect + 1;
        e.at(sx, sy, 0, Color.valueOf("ff0000"));
    }

    @Override
    public void update(float x, float y, float rotation, Color color) {
        super.update(x, y, rotation, color);
        time += Time.delta;
        
        if (time > 10f && target != null && target.isValid() && time < 11f) {
            // 击中瞬间的特效
            Fx.massiveExplosion.at(target.x, target.y);
        }
    }

    @Override
    public void render(float x, float y, float rotation, Color color) {
        float fin = Mathf.clamp(time / 10f); 
        float fout = Mathf.clamp((lifetime - time) / 10f); 
        float currentLength = length * fin;
        
        if (currentLength <= 0) return;
        
        Draw.color(Color.valueOf("ff0000"));
        Draw.alpha(fout);
        
        // 画红色长剑本体
        float ang = Mathf.angle(sx, sy, tx, ty);
        mindustry.graphics.Lines.stroke(4f * fout);
        mindustry.graphics.Lines.line(sx, sy, sx + Angles.trnsx(ang, currentLength), sy + Angles.trnsy(ang, currentLength));
        
        float ex = sx + Angles.trnsx(ang, currentLength);
        float ey = sy + Angles.trnsy(ang, currentLength);
        Draw.color(Color.white);
        mindustry.graphics.Fill.poly(ex, ey, 4, 10f * fout, ang + 45f);
        
        Draw.reset();
    }
}
