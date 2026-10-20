package jm;

import arc.Events;
import arc.util.Reflect;
import arc.util.Log;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.graphics.MenuRenderer;
import mindustry.mod.Mod;

public class JmMod extends Mod {

    // 在这里定义你的模组名称
    public static String ModName = "jsm";
    
    public JmMod() {
        super();
        // 构造函数，模组初始化时最先调用
    }

    @Override
    public void init() {
        // 监听游戏加载完毕事件，执行菜单替换
        Events.on(EventType.ClientLoadEvent.class, e -> {
            try {
                // 获取原版菜单渲染器
                MenuRenderer originalRenderer = Reflect.get(Vars.ui.menufrag, "renderer");
                // 创建你的文字渲染器并替换
                TextMenuRenderer textRenderer = new TextMenuRenderer(originalRenderer);
                Reflect.set(Vars.ui.menufrag, "renderer", textRenderer);
            } catch (Exception ex) {
                Log.err("菜单文字模组加载失败", ex);
            }
        });
    }

    @Override
    public void loadContent() {
        super.loadContent();
        // 如果你以后想加物品、方块，就在这里写： JmItems.load(); 等等
    }
}
