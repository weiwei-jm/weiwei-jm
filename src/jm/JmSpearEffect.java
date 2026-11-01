package jm;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.entities.Effect;
import mindustry.gen.Unit;
import mindustry.graphics.Layer;

public class JmSpearEffect extends Effect {
    public float sx, sy, tx, ty, length;
    public Unit target;
    public float time;

    public static void create(Unit unit, float sx, float sy, float tx, float ty) {
        JmSpearEffect e = new JmSpearEffect();
        e.sx = sx; e.sy = sy;
        e.tx = tx; e.ty = ty;
        e.target = unit;
        e.length = Mathf.dst(sx, sy, tx, ty);
        e.lifetime = 30f; 
        e.layer = Layer.effect + 1;
        e.at(sx, sy, 0, Color.valueOf("ff0000"));
    }

    @Override
    public void update(float x, float y) {
        time += Time.delta;
        if (time > 10f && target != null && target.isValid() && time < 11f) {
            mindustry.content.Fx.massiveExplosion.at(target.x, target.y);
        }
    }

    @Override
    public void render(float x, float y) {
        float fin = Mathf.clamp(time / 10f); 
        float fout = Mathf.clamp((lifetime - time) / 10f); 
        float currentLength = length * fin;
        
        if (currentLength <= 0) return;
        
        Draw.color(Color.valueOf("ff0000"));
        Draw.alpha(fout);
        
        float ang = Angles.angle(sx, sy, tx, ty);
        Lines.stroke(4f * fout);
        Lines.line(sx, sy, sx + Angles.trnsx(ang, currentLength), sy + Angles.trnsy(ang, currentLength));
        
        float ex = sx + Angles.trnsx(ang, currentLength);
        float ey = sy + Angles.trnsy(ang, currentLength);
        Draw.color(Color.white);
        Fill.poly(ex, ey, 4, 10f * fout, ang + 45f);
        
        Draw.reset();
    }
}
