package jm;

import arc.Core;
import arc.Events;
import arc.graphics.Color;
import arc.scene.ui.layout.Table;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.gen.Unit;
import mindustry.ui.Styles;

public class BlinkUI {
    public static boolean isAiming = false;
    public static Table blinkButtonTable; // 我们的按钮容器

    public static void init() {
        // 每次进图重置状态，并创建按钮
        Events.on(EventType.WorldLoadEvent.class, e -> {
            isAiming = false;
            BlinkAbility.effects.clear();
            if (blinkButtonTable != null) {
                blinkButtonTable.remove();
                blinkButtonTable = null;
            }
        });

        // 监听原版的 UI 绘制事件，每帧检查
        Events.run(EventType.Trigger.uiDraw, () -> {
            // 如果玩家存在且选择了单位，且不在菜单里
            if (Vars.player != null && Vars.control.input.selectedUnits.size > 0 && !Vars.ui.chatfrag.shown()) {
                
                // 如果按钮还没创建，我们就创建它
                if (blinkButtonTable == null) {
                    blinkButtonTable = new Table();
                    
                    // 这里创建一个原版风格的按钮
                    blinkButtonTable.button("跃迁", Styles.defaultt, () -> {
                        // 点击按钮执行瞄准逻辑
                        startAiming();
                    }).size(80f, 40f);

                    // 把这个按钮加到原版的 HUD 层里
                    Vars.ui.hudGroup.addChild(blinkButtonTable);
                }

                // 动态调整按钮位置：放在屏幕右下角指挥面板的正上方
                // 因为不同手机分辨率不同，我们需要动态计算，这里放在右下角偏左一点的位置
                float x = Core.graphics.getWidth() - 250f * Vars.mobileKeyScale;
                float y = 150f * Vars.mobileKeyScale; // 指挥面板大概在这个高度以上
                
                blinkButtonTable.setPosition(x, y);
                blinkButtonTable.setColor(isAiming ? Color.yellow : Color.white); // 瞄准时变黄
                blinkButtonTable.visible = true;
            } else {
                // 如果没有选中单位，隐藏按钮
                if (blinkButtonTable != null) {
                    blinkButtonTable.visible = false;
                }
            }
        });

        // 监听点击事件（用于确认跃迁目标）
        Events.run(EventType.Trigger.update, () -> {
            if (isAiming && Vars.player != null && Vars.player.unit() != null) {
                if (Vars.control.input.isTouched()) {
                    float wx = Vars.control.input.mouseWorldX();
                    float wy = Vars.control.input.mouseWorldY();

                    if (Vars.control.input.selectedUnits != null) {
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
                    isAiming = false;
                    Vars.control.input.isTouched = false; // 拦截点击，不让单位走过去
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
