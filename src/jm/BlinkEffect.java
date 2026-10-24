package jm;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.type.UnitType;

public class BlinkEffect {
    public float sx, sy, ex, ey;
    public float sRot, eRot;
    public Color color;
    public UnitType type;
    public float life = 30f; // 存活 0.5 秒（30帧）
    public float maxLife = 30f;

    public BlinkEffect(float sx, float sy, float ex, float ey, float sRot, float eRot, Color color, UnitType type) {
        this.sx = sx; this.sy = sy;
        this.ex = ex; this.ey = ey;
        this.sRot = sRot; this.eRot = eRot;
        this.color = color.cpy();
        this.type = type;
    }

    public void update() {
        life -= Time.delta;
    }

    public boolean isDead() {
        return life <= 0;
    }

    public void draw() {
        float fin = life / maxLife;
        
        // 遍历单位的每一个引擎（推进器）
        for (UnitType.UnitEngine engine : type.engines) {
            // 计算传送前引擎位置
            Tmp.v1.trns(sRot - 90f, engine.x, -engine.y).add(sx, sy);
            float startEx = Tmp.v1.x, startEy = Tmp.v1.y;

            // 计算传送后引擎位置
            Tmp.v1.trns(eRot - 90f, engine.x, -engine.y).add(ex, ey);
            float endEx = Tmp.v1.x, endEy = Tmp.v1.y;

            // 画连线
            Draw.color(color);
            Lines.stroke(2f * fin);
            Lines.line(startEx, startEy, endEx, endEy);

            // 在一格范围内随机撒圆点
            for (int i = 0; i < 4; i++) {
                float rX = Mathf.random(startEx, endEx);
                float rY = Mathf.random(startEy, endEy);
                float ox = Mathf.range(8f);
                float oy = Mathf.range(8f);
                
                Draw.color(color);
                Draw.alpha(fin);
                Fill.circle(rX + ox, rY + oy, 2f * fin);
            }
        }
    }
}
