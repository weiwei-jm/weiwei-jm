package jm;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.gen.Unit;

public class SpaceRiftUnit extends Unit {
    // 控制出场动画的时间（比如 60 帧 = 1 秒）
    public float spawnTimer = 0f;
    public float spawnDuration = 60f;

    @Override
    public void update() {
        super.update();
        // 时间不断增加，直到等于设定时间
        if (spawnTimer < spawnDuration) {
            spawnTimer += Time.delta;
        }
    }

    @Override
    public void draw() {
        // 如果还在出场动画中，单位不能移动/攻击（可选）
        if (spawnTimer < spawnDuration) {
            // 计算动画进度 (0.0 到 1.0)
            float progress = spawnTimer / spawnDuration;
            
            // 画“空间裂缝”和“遮挡”
            drawSpawnEffect(progress);
            
            // 画单位本身，并用 progress 控制透明度
            Draw.alpha(progress); 
            super.draw();
            Draw.reset();
        } else {
            // 出场完毕，正常画单位
            super.draw();
        }
    }

    private void drawSpawnEffect(float progress) {
        // 获取当前单位的队伍颜色
        Color teamColor = team.color;
        
        // 菱形展开的半径动画（随着时间变大，然后慢慢消失）
        float r = Mathf.lerp(0f, 50f, Mathf.curve(progress, 0f, 0.5f));
        
        // 如果是最后 20% 的时间，特效开始变透明
        if (progress > 0.8f) {
            Draw.alpha(1f - (progress - 0.8f) / 0.2f);
        } else {
            Draw.alpha(1f);
        }

        // 1. 画中间的黑色核心区域
        Draw.color(Color.black);
        Fill.circle(x, y, r * 0.8f); // 用圆来盖住中间的单位

        // 2. 画四周的黑色菱形
        Draw.color(Color.black);
        for (int i = 0; i < 4; i++) {
            float angle = i * 90f; // 上下左右四个方向
            float length = r * 1.5f;
            Tmp.v1.trns(angle, length);
            // 这里画简单的菱形（用原版的三角形拼凑）
            float baseWidth = r * 0.3f;
            float bx = x + Tmp.v1.x * 0.5f;
            float by = y + Tmp.v1.y * 0.5f;
            
            // 画黑色菱形
            Fill.tri(x, y, 
                     bx + Mathf.cosDeg(angle + 90) * baseWidth, by + Mathf.sinDeg(angle + 90) * baseWidth,
                     bx, by + length * 0.5f);
            Fill.tri(x, y, 
                     bx, by + length * 0.5f,
                     bx + Mathf.cosDeg(angle - 90) * baseWidth, by + Mathf.sinDeg(angle - 90) * baseWidth);
        }

        // 3. 画队伍颜色的描边
        Lines.stroke(3f); // 线宽
        Draw.color(teamColor); // 队伍色
        
        // 描边黑圆
        Lines.circle(x, y, r * 0.8f);
        
        // 描边外围的菱形
        for (int i = 0; i < 4; i++) {
            float angle = i * 90f;
            float length = r * 1.5f;
            Tmp.v1.trns(angle, length);
            
            float baseWidth = r * 0.3f;
            float bx = x + Tmp.v1.x * 0.5f;
            float by = y + Tmp.v1.y * 0.5f;
            
            // 用线条画菱形边框
            Lines.line(x, y, 
                       bx + Mathf.cosDeg(angle + 90) * baseWidth, by + Mathf.sinDeg(angle + 90) * baseWidth);
            Lines.line(bx + Mathf.cosDeg(angle + 90) * baseWidth, by + Mathf.sinDeg(angle + 90) * baseWidth,
                       bx, by + length * 0.5f);
            Lines.line(bx, by + length * 0.5f,
                       bx + Mathf.cosDeg(angle - 90) * baseWidth, by + Mathf.sinDeg(angle - 90) * baseWidth);
            Lines.line(bx + Mathf.cosDeg(angle - 90) * baseWidth, by + Mathf.sinDeg(angle - 90) * baseWidth,
                       x, y);
        }
        
        Draw.reset();
    }
}
