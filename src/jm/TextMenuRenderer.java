package jm;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.util.Time;
import arc.math.Mathf;
import mindustry.graphics.Drawf;
import mindustry.graphics.MenuRenderer;

public class TextMenuRenderer extends MenuRenderer {
    private final MenuRenderer original;

    public TextMenuRenderer(MenuRenderer original) {
        this.original = original;
    }

    @Override
    public void render() {
        // 先渲染原版菜单（Logo、按钮等）
        if (original != null) {
            original.render();
        }

        Draw.reset();

        // 你的文字内容，你在这里随便改
        String text = "mj你快回来吧";
        
        // 动画：正弦波控制大小，实现变大变小的效果
        float scale = 1f + Mathf.sin(Time.time * 0.002f) * 0.2f;
        
        // 文字位置：屏幕中心偏右下一点
        float x = Core.graphics.getWidth() / 2f + 80f;
        float y = Core.graphics.getHeight() / 2f + 40f;
        
        // 倾斜角度（负数向右倾斜）
        float rotation = -15f;

        // 使用 Mindustry 官方的 Drawf 画字
        Drawf.text(text, x, y, rotation, Color.white, scale, true);

        Draw.reset();
    }

    @Override
    public void dispose() {
        // 没有需要释放的资源
    }
}
