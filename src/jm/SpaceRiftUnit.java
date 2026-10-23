package jm;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.scene.ui.layout.Scl;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.gen.Unit;
import mindustry.graphics.Layer;

public class SpaceRiftUnit extends Unit {
    public float spawnTimer = 0f;
    public float spawnDuration = 180f; // 3秒

    public boolean isDying = false;
    public float deathTimer = 0f;
    public float deathDuration = 60f; // 1秒

    // 裂缝尺寸设定
    public float riftWidth = hitSize / 2f; // 宽 = 碰撞体积的一半
    public float riftLength = riftWidth * 10f; // 长 = 宽的10倍

    @Override
    public void update() {
        if (isDying) {
            deathTimer += Time.delta;
            if (deathTimer >= deathDuration) {
                this.remove(); 
            }
            return;
        }

        if (spawnTimer < spawnDuration) {
            spawnTimer += Time.delta;
            this.health = this.maxHealth; // 出生无敌
            this.vel.setZero(); // 不能移动
            this.elevation = 0;
            return; 
        }

        super.update();
    }

    @Override
    public void kill() {
        if (!isDying && spawnTimer >= spawnDuration) {
            isDying = true;
            deathTimer = 0f;
            this.health = 1; 
            this.vel.setZero();
        }
    }

    @Override
    public void draw() {
        float progress;
        boolean hidingCenter;
        
        if (isDying) {
            progress = deathTimer / deathDuration;
            hidingCenter = true; // 死亡时，只显示裂缝外的部分，或者相反
        } else if (spawnTimer < spawnDuration) {
            progress = spawnTimer / spawnDuration;
            hidingCenter = false; // 出生时，只显示裂缝中间的部分
        } else {
            super.draw();
            return;
        }

        // 裂缝的角度（垂直于单位的朝向）
        float angle = rotation + 90f;
        
        // 裂缝的展开动画（0.3秒内完全撕开）
        float openFrac = Mathf.curve(progress, 0f, 0.3f);
        float currentLength = riftLength * openFrac;
        float currentWidth = riftWidth * openFrac;

        // 画裂缝的黑色背景和描边
        drawRiftBackground(x, y, angle, currentLength, currentWidth);

        // 裁剪并画单位
        arc.graphics.g2d.ScissorStack.push(x, y, angle, currentLength, currentWidth, hidingCenter);
        
        // 因为裁剪框的中心在单位上，我们需要把单位挪一下，才能做出“挤出来”或“吸进去”的效果
        if (isDying) {
            // 死亡：单位往后退（往裂缝反方向移动）
            Tmp.v1.trns(angle + 90, progress * hitSize * 1.5f);
            this.x += Tmp.v1.x; this.y += Tmp.v1.y;
            Draw.alpha(1f - progress * 0.5f); // 慢慢变暗
        } else {
            // 出生：单位往前挤（从裂缝中心往外冲）
            Tmp.v1.trns(angle - 90, (1f - progress) * hitSize * 1.5f);
            this.x += Tmp.v1.x; this.y += Tmp.v1.y;
            Draw.alpha(progress);
        }

        // 让单位画在最上层，不被裂缝挡住
        Draw.z(Layer.effect + 1);
        super.draw();

        // 恢复坐标（很重要，不然单位会瞬移）
        if (isDying) {
            this.x -= Tmp.v1.x; this.y -= Tmp.v1.y;
        } else {
            this.x -= Tmp.v1.x; this.y -= Tmp.v1.y;
        }
        
        arc.graphics.g2d.ScissorStack.pop();
        Draw.reset();
    }

    private void drawRiftBackground(float cx, float cy, float angle, float length, float width) {
        Draw.z(Layer.effect);
        Draw.color(Color.black);
        // 画中间的黑色矩形（即裂缝）
        Fill.rect(cx, cy, length, width, angle);
        
        // 画队伍颜色的描边
        Lines.stroke(3f);
        Draw.color(team.color);
        // 上下两条长边
        Lines.line(cx + Mathf.cosDeg(angle+90)*width/2 - Mathf.cosDeg(angle)*length/2, 
                   cy + Mathf.sinDeg(angle+90)*width/2 - Mathf.sinDeg(angle)*length/2,
                   cx + Mathf.cosDeg(angle+90)*width/2 + Mathf.cosDeg(angle)*length/2, 
                   cy + Mathf.sinDeg(angle+90)*width/2 + Mathf.sinDeg(angle)*length/2);
        Lines.line(cx - Mathf.cosDeg(angle+90)*width/2 - Mathf.cosDeg(angle)*length/2, 
                   cy - Mathf.sinDeg(angle+90)*width/2 - Mathf.sinDeg(angle)*length/2,
                   cx - Mathf.cosDeg(angle+90)*width/2 + Mathf.cosDeg(angle)*length/2, 
                   cy - Mathf.sinDeg(angle+90)*width/2 + Mathf.sinDeg(angle)*length/2);
        // 左右两条短边
        Lines.line(cx + Mathf.cosDeg(angle)*length/2 + Mathf.cosDeg(angle+90)*width/2, 
                   cy + Mathf.sinDeg(angle)*length/2 + Mathf.sinDeg(angle+90)*width/2,
                   cx + Mathf.cosDeg(angle)*length/2 - Mathf.cosDeg(angle+90)*width/2, 
                   cy + Mathf.sinDeg(angle)*length/2 - Mathf.sinDeg(angle+90)*width/2);
        Lines.line(cx - Mathf.cosDeg(angle)*length/2 + Mathf.cosDeg(angle+90)*width/2, 
                   cy - Mathf.sinDeg(angle)*length/2 + Mathf.sinDeg(angle+90)*width/2,
                   cx - Mathf.cosDeg(angle)*length/2 - Mathf.cosDeg(angle+90)*width/2, 
                   cy - Mathf.sinDeg(angle)*length/2 - Mathf.sinDeg(angle+90)*width/2);
        Draw.reset();
    }
}
