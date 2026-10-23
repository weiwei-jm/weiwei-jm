package jm;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.util.Disposable;
import arc.util.Time; // 👈 引用正确的 Time 类

public class BlackHoleRenderer implements Disposable {
    public float x, y;
    public float currentRadius = 0f;
    public float maxRadius = 40f;
    public float growthSpeed = 0.5f;
    public float life = 300f;
    public float currentLife = 0f;
    public boolean active = false;
    public Color ringColor = Color.yellow.cpy();

    public void update() {
        if (!active) return;
        
        // 👇 改用 Time.delta
        currentLife += Time.delta;
        
        if (currentLife < life) {
            if (currentRadius < maxRadius) {
                // 👇 直接用 Time.delta 计算
                currentRadius += growthSpeed * (Time.delta / 60f);
                if (currentRadius > maxRadius) currentRadius = maxRadius;
            }
        } else {
            // 👇 直接用 Time.delta 计算
            currentRadius -= growthSpeed * (Time.delta / 60f);
            if (currentRadius <= 0) {
                currentRadius = 0;
                active = false;
            }
        }
    }

    public void draw() {
        if (!active || currentRadius <= 0) return;
        Draw.color(Color.black);
        Fill.circle(x, y, currentRadius);
        Draw.color(ringColor);
        Lines.stroke(3f);
        Lines.circle(x, y, currentRadius);
        Draw.reset();
    }

    @Override
    public void dispose() {}
}
