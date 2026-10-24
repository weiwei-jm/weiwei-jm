package jm;

import arc.Events;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.gen.Unit;

public class BlinkUI {
    // 标记是否正在等待玩家点击目标点
    public static boolean isAiming = false;

    public static void init() {
        // 每次进入地图重置状态
        Events.on(EventType.WorldLoadEvent.class, e -> {
            isAiming = false;
            BlinkAbility.effects.clear();
        });

        // 👇 核心逻辑：拦截原版玩家点击屏幕的指令
        Events.run(EventType.Trigger.update, () -> {
            if (isAiming && Vars.player != null && Vars.player.unit() != null && Vars.control.input.isBuilding == false) {
                // 如果按下了左键（手机上是轻触）
                if (Vars.control.input.isTouched()) {
                    float wx = Vars.control.input.mouseWorldX();
                    float wy = Vars.control.input.mouseWorldY();

                    // 获取所有选中的单位，让它们跃迁
                    if (Vars.control.input.selectedUnits != null && Vars.control.input.selectedUnits.size > 0) {
                        for (Unit unit : Vars.control.input.selectedUnits) {
                            if (unit.abilities != null) {
                                for (mindustry.entities.abilities.Ability ability : unit.abilities) {
                                    if (ability instanceof BlinkAbility blink) {
                                        blink.trigger(unit, wx, wy);
                                    }
                                }
                            }
                        }
                    }
                    // 释放鼠标，退出跃迁瞄准状态
                    isAiming = false;
                    Vars.control.input.isTouched = false; // 强制取消原版的移动指令
                }
            }
        });
    }

    // 这个方法用来给单位绑定一个触发跃迁的快捷方式
    public static void startAiming() {
        if (Vars.control.input.selectedUnits != null && Vars.control.input.selectedUnits.size > 0) {
            Unit unit = Vars.control.input.selectedUnits.first();
            if (unit.abilities != null) {
                for (mindustry.entities.abilities.Ability ability : unit.abilities) {
                    if (ability instanceof BlinkAbility blink) {
                        if (blink.canBlink()) {
                            isAiming = true;
                            Vars.ui.showInfoFade("[accent]请点击目标地点[]");
                        }
                        return;
                    }
                }
            }
        }
    }
}
