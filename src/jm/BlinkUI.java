package jm;

import arc.Core;
import arc.Events;
import arc.math.geom.Vec2;
import mindustry.Vars;
import mindustry.gen.Unit;
import mindustry.game.EventType;
import mindustry.entities.Units;
import mindustry.entities.abilities.Ability;

public class BlinkUI {
    public static boolean isAiming = false;

    public static void init() {
        // 每次进图重置状态
        Events.on(EventType.WorldLoadEvent.class, e -> {
            isAiming = false;
            BlinkAbility.effects.clear();
        });

        // 监听触摸事件：当我们处于跃迁瞄准状态时，拦截点击
        Events.run(EventType.Trigger.update, () -> {
            if (isAiming && Vars.player != null) {
                if (Core.input.isTouched()) {
                    Vec2 pos = Core.input.mouseWorld();
                    
                    if (Vars.control.input.selectedUnits != null) {
                        for (Unit unit : Vars.control.input.selectedUnits) {
                            for (Ability ability : unit.abilities) {
                                if (ability instanceof BlinkAbility blink) {
                                    blink.trigger(unit, pos.x, pos.y);
                                }
                            }
                        }
                    }
                    isAiming = false;
                    // 强制清空点击，防止单位自己走过去
                    Vars.control.input.isTouched = false; 
                }
            }
        });
    }

    // 这个方法会在玩家点击我们的“跃迁”UI按钮时被调用
    public static void startAiming() {
        if (Vars.control.input.selectedUnits != null && Vars.control.input.selectedUnits.size > 0) {
            Unit unit = Vars.control.input.selectedUnits.first();
            for (Ability ability : unit.abilities) {
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
