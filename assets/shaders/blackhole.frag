package jm;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.Gl;
import arc.graphics.gl.FrameBuffer;
import arc.graphics.gl.Shader;
import arc.util.Disposable;
import mindustry.core.Time;

public class BlackHoleRenderer implements Disposable {
    private final FrameBuffer buffer;
    private final Shader shader;
    public float x, y;
    
    // 动态大小和寿命控制
    public float currentRadius = 0f; // 当前半径
    public float maxRadius = 40f;    // 最大半径（5格 = 40像素）
    public float growthSpeed = 0.5f; // 增长速度（每帧变大多少）
    
    public float life = 300f;        // 总存活时间（比如 300 帧，约 5 秒）
    public float currentLife = 0f;   // 当前已经存活的时间
    public boolean active = false;

    public Color ringColor = Color.red.cpy();

    public BlackHoleRenderer() {
        buffer = new FrameBuffer();
        shader = new Shader(
            Core.files.internal("shaders/default.vert").readString(),
            Core.files.internal("shaders/blackhole.frag").readString()
        );
    }

    // 更新大小和寿命
    public void update() {
        if (!active) return;
        
        currentLife += Time.delta();
        
        // 如果还在寿命内
        if (currentLife < life) {
            // 增长到最大
            if (currentRadius < maxRadius) {
                currentRadius += growthSpeed * Time.delta() / 60f; // 根据帧率平滑增长
                if (currentRadius > maxRadius) currentRadius = maxRadius;
            }
        } else {
            // 寿命到了，开始缩小
            currentRadius -= growthSpeed * Time.delta() / 60f;
            if (currentRadius <= 0) {
                currentRadius = 0;
                active = false; // 彻底消失
            }
        }
    }

    public void begin() {
        if (!active || currentRadius <= 0) return;
        buffer.resize(Core.graphics.getWidth(), Core.graphics.getHeight());
        buffer.begin();
    }

    public void end() {
        if (!active || currentRadius <= 0) return;
        buffer.end();
        
        shader.bind();
        float cx = (x / Core.graphics.getWidth());
        float cy = 1f - (y / Core.graphics.getHeight());
        
        shader.setUniformf("u_center", cx, cy);
        // 传入当前的动态半径
        shader.setUniformf("u_radius", currentRadius / Core.graphics.getWidth());
        shader.setUniformf("u_color", ringColor.r, ringColor.g, ringColor.b, ringColor.a);
        
        buffer.blit(shader);
        shader.unbind();
    }

    @Override
    public void dispose() {
        buffer.dispose();
        shader.dispose();
    }
}
