package jm;

import arc.Core;
import arc.Events;
import arc.graphics.Color;
import arc.math.geom.Vec2;
import mindustry.content.Planets;
import mindustry.game.EventType;
import mindustry.gen.Unit;
import mindustry.mod.Mod;
import mindustry.type.Planet;
import mindustry.type.StatusEffect;
import mindustry.graphics.g3d.MultiMesh;
import mindustry.Vars;

public class JmMod extends Mod {

    public static String ModName = "jm";
    public static Planet myPlanet;
    public static StatusEffect erosionStatus;
    public static QuantumStormWeather quantumStorm;

    public JmMod() {
        super();
    }

    @Override
@Override
public void init() {
    // 1. 进游戏给匕首加能力
    Events.on(EventType.WorldLoadEvent.class, e -> {
        mindustry.content.UnitTypes.dagger.abilities.add(new BlinkAbility());
        mindustry.content.UnitTypes.flare.abilities.add(new BlinkAbility());
    });

    // 2. 核心监听：原版指挥模式点击事件
    Events.run(EventType.Trigger.update, () -> {
        // 如果玩家在指挥模式，且按下了鼠标左键 / 手指触摸了屏幕
        if (Vars.player != null && Vars.control != null && Vars.control.input.commandMode) {
            if (Core.input.isTouched()) {
                // 👇 加上这个判断，防止点到了 UI 面板时也触发瞬移
                if (Core.scene.hasMouse()) return;

                Vec2 pos = Core.input.mouseWorld();

                if (Vars.control.input.selectedUnits != null) {
                    for (Unit unit : Vars.control.input.selectedUnits) {
                        for (mindustry.entities.abilities.Ability ability : unit.abilities) {
                            if (ability instanceof BlinkAbility blink) {
                                // 直接触发瞬移！把目的地传给单位
                                blink.trigger(unit, pos.x, pos.y);
                                
                                // 拦截原版的移动指令，避免它又跑又闪
                                if (unit.controller() instanceof mindustry.ai.types.CommandAI ai) {
                                    ai.targetPos = null;
                                }
                            }
                        }
                    }
                }
            }
        }
    });

    // 3. 渲染特效
    Events.run(EventType.Trigger.draw, () -> {
        for (int i = BlinkAbility.effects.size - 1; i >= 0; i--) {
            BlinkEffect effect = BlinkAbility.effects.get(i);
            effect.update();
            if (effect.isDead()) {
                BlinkAbility.effects.remove(i);
            } else {
                effect.draw();
            }
        }
    });
}

    @Override
    public void loadContent() {
        super.loadContent();
        // 这里保留你之前的星球和天气代码，不用动...
    }
                        }
