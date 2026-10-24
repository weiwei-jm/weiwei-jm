package jm;

import arc.Core;
import arc.Events;
import arc.math.geom.Vec2;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.gen.Unit;

public class BlinkUI {
    public static boolean isAiming = false;

    public static void init() {
        Events.on(EventType.WorldLoadEvent.class, e -> {
            isAiming = false;
            BlinkAbility.effects.clear();
        });

        // 监听更新事件：如果处于瞄准状态，且玩家点击了屏幕
        Events.run(EventType.Trigger.update, () -> {
            if (isAiming && Vars.player != null) {
                if (Core.input.isTouched()) {
                    // 修复了获取世界坐标的方法
                    Vec2 pos = Core.input.mouseWorld();
                    
                    if (Vars.control.input.selectedUnits != null) {
                        for (Unit unit : Vars.control.input.selectedUnits) {
                            if (unit.abilities != null) {
                                for (mindustry.entities.abilities.Ability ability : unit.abilities) {
                                    if (ability instanceof BlinkAbility blink) {
                                        blink.trigger(unit, pos.x, pos.y);
                                    }
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
            if (unit.abilities != null) {
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
                                }
