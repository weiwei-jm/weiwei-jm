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
    public float spawnDuration = 180f;

    public boolean isDying = false;
    public float deathTimer = 0f;
    public float deathDuration = 60f;

    public float riftWidth = 16f; 
    public float riftLength = 160f; 

    @Override
    public void update() {
        if (isDying) {
            deathTimer += Time.delta;
            if (deathTimer >= deathDuration) {
                super.remove(); 
            }
            return;
        }

        if (spawnTimer < spawnDuration) {
            spawnTimer += Time.delta;
            this.health = this.maxHealth; 
            this.vel.setZero(); 
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
            float progress = deathTimer / deathDuration;
            super.draw();
            drawRift(progress, true);
        } else if (spawnTimer < spawnDuration) {
            float progress = spawnTimer / spawnDuration;
            drawRift(progress, false);
            Draw.alpha(progress);
            super.draw();
            Draw.reset();
        } else {
            super.draw();
        }
    }

    private void drawRift(float progress, boolean isDeath) {
        float angle = rotation + 90f;
        float openFrac = Mathf.curve(progress, 0f, 0.3f);
        float currentLength = riftLength * openFrac;
        float currentWidth = riftWidth * openFrac;

        Draw.z(Layer.effect + 1);
        Draw.color(Color.black);
        Fill.rect(x, y, currentLength, currentWidth, angle);
        
        Draw.color(team.color);
        Lines.stroke(3f);
        
        Tmp.v1.trns(angle + 90, currentWidth / 2f);
        Tmp.v2.trns(angle - 90, currentWidth / 2f);
        Tmp.v3.trns(angle, currentLength / 2f);
        
        Lines.line(x + Tmp.v1.x - Tmp.v3.x, y + Tmp.v1.y - Tmp.v3.y, x + Tmp.v1.x + Tmp.v3.x, y + Tmp.v1.y + Tmp.v3.y);
        Lines.line(x + Tmp.v2.x - Tmp.v3.x, y + Tmp.v2.y - Tmp.v3.y, x + Tmp.v2.x + Tmp.v3.x, y + Tmp.v2.y + Tmp.v3.y);
        Lines.line(x + Tmp.v1.x + Tmp.v3.x, y + Tmp.v1.y + Tmp.v3.y, x + Tmp.v2.x + Tmp.v3.x, y + Tmp.v2.y + Tmp.v3.y);
        Lines.line(x + Tmp.v1.x - Tmp.v3.x, y + Tmp.v1.y - Tmp.v3.y, x + Tmp.v2.x - Tmp.v3.x, y + Tmp.v2.y - Tmp.v3.y);
        
        Draw.reset();
    }
}
