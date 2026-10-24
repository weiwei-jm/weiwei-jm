package jm;

import arc.Core;
import arc.Events;
import arc.graphics.Color;
import arc.math.geom.Vec2;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.gen.Unit;
import mindustry.graphics.Layer;

public class BlinkUI {
    public static boolean isAiming = false;

    public static void init() {
        Events.on(EventType.WorldLoadEvent.class, e -> {
            isAiming = false;
            BlinkAbility.effects.clear();
        });

        // 手机端画按钮
        Events.run(EventType.Trigger.draw, () -> {
            if (Vars.player != null && Vars.control.input.selectedUnits.size > 0) {
                Draw.z(Layer.max);
                Draw.color(Color.white);
                // 在屏幕正中间偏下画个提示，因为手机UI很难精确适配
                mindustry.graphics.Drawf.rect(Core.graphics.getWidth() / 2f - 50, 100, 100, 50); 
                // 注：这是测试用的临时位置，如果需要完美按钮，还需进一步写 UI 控件
            }
        });

        Events.run(EventType.Trigger.update, () -> {
            if (isAiming && Vars.player != null) {
                if (Core.input.isTouched()) {
                    Vec2 pos = Core.input.mouseWorld();
                    if (Vars.control.input.selectedUnits != null) {
                        for (Unit unit : Vars.control.input.selectedUnits) {
                            for (mindustry.entities.abilities.Ability ability : unit.abilities) {
                                if (ability instanceof BlinkAbility blink) {
                                    blink.trigger(unit, pos.x, pos.y);
                                }
                            }
                        }
                    }
                    isAiming = false;
                }
            }
        });
    }

    public static void startAiming() {
        if (Vars.control.input.selectedUnits != null && Vars.control.input.selectedUnits.size > 0) {
            Unit unit = Vars.control.input.selectedUnits.first();
            for (mindustry.entities.abilities.Ability ability : unit.abilities) {
                if (ability instanceof BlinkAbility blink) {
                    if (blink.canBlink()) {
                        isAiming = true;
                        Vars.ui.showInfoFade("[accent]请点击目标地点[]");
                    } else {
                        Vars.ui.showInfoFade("[red]跃迁冷却中！[]");
                    }
                    return;
                }
            }
        }
    }
}
