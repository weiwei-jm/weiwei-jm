package jm;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.gen.UnitEntity;
import mindustry.graphics.Layer;

public class SpaceRiftUnit extends UnitEntity {
    public float spawnTimer = 0f;
    public float spawnDuration = 180f; // 3秒

    public boolean isDying = false;
    public float deathTimer = 0f;
    public float deathDuration = 60f; // 1秒

    // 裂缝尺寸设定 (长是宽的10倍，宽是碰撞体积的一半)
    public float riftWidth = 16f; // 默认宽度，你可以根据 hitSize 动态改
    public float riftLength = riftWidth * 10f; 

    @Override
    public void update() {
        if (isDying) {
            deathTimer += Time.delta;
            if (deathTimer >= deathDuration) {
                super.remove(); // 彻底移除
            }
            return;
        }

        if (spawnTimer < spawnDuration) {
            spawnTimer += Time.delta;
            this.health = this.maxHealth; // 出生无敌
            this.vel.setZero(); // 不能移动
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
        if (isDying) {
            // 死亡动画：画在单位前面（先画单位，再画裂缝盖住它）
            float progress = deathTimer / deathDuration;
            
            // 画单位
            super.draw();
            
            // 画裂缝盖在单位上面（因为是后画的，黑色会遮挡单位）
            drawRift(progress, true);
            
        } else if (spawnTimer < spawnDuration) {
            // 出生动画：先画裂缝，再画单位（单位从裂缝中浮现）
            float progress = spawnTimer / spawnDuration;
            
            drawRift(progress, false);
            
            // 画单位，并控制透明度
            Draw.alpha(progress);
            super.draw();
            Draw.reset();
        } else {
            super.draw();
        }
    }

    private void drawRift(float progress, boolean isDeath) {
        // 裂缝的角度（垂直于单位的朝向）
        float angle = rotation + 90f;
        
        // 展开动画（0.3秒内完全撕开）
        float openFrac = Mathf.curve(progress, 0f, 0.3f);
        float currentLength = riftLength * openFrac;
        float currentWidth = riftWidth * openFrac;

        // 设置渲染层级到最上层
        Draw.z(Layer.effect + 1);
        
        // 1. 画黑色的长条裂缝
        Draw.color(Color.black);
        Fill.rect(x, y, currentLength, currentWidth, angle);
        
        // 2. 画队伍颜色描边
        Draw.color(team.color);
        Lines.stroke(3f);
        
        // 计算上下两条长边的四个顶点
        Tmp.v1.trns(angle + 90, currentWidth / 2f);
        Tmp.v2.trns(angle - 90, currentWidth / 2f);
        Tmp.v3.trns(angle, currentLength / 2f);
        
        // 画上边
        Lines.line(x + Tmp.v1.x - Tmp.v3.x, y + Tmp.v1.y - Tmp.v3.y, x + Tmp.v1.x + Tmp.v3.x, y + Tmp.v1.y + Tmp.v3.y);
        // 画下边
        Lines.line(x + Tmp.v2.x - Tmp.v3.x, y + Tmp.v2.y - Tmp.v3.y, x + Tmp.v2.x + Tmp.v3.x, y + Tmp.v2.y + Tmp.v3.y);
        
        // 画左右两个短边
        Lines.line(x + Tmp.v1.x + Tmp.v3.x, y + Tmp.v1.y + Tmp.v3.y, x + Tmp.v2.x + Tmp.v3.x, y + Tmp.v2.y + Tmp.v3.y);
        Lines.line(x + Tmp.v1.x - Tmp.v3.x, y + Tmp.v1.y - Tmp.v3.y, x + Tmp.v2.x - Tmp.v3.x, y + Tmp.v2.y - Tmp.v3.y);
        
        Draw.reset();
    }
    ｝
