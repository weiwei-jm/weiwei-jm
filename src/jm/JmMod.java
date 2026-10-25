package jm;

import arc.Core;
import arc.Events;
import arc.graphics.Color;
import mindustry.content.Planets;
import mindustry.game.EventType;
import mindustry.mod.Mod;
import mindustry.type.Planet;
import mindustry.type.StatusEffect;
import mindustry.graphics.g3d.MultiMesh;

public class JmMod extends Mod {

    public static String ModName = "jm";
    public static Planet myPlanet;
    
    public static StatusEffect erosionStatus;
    public static QuantumStormWeather quantumStorm;

    public JmMod() {
        super();
    }

    @Override
public void init() {
    // 进游戏打印测试
    Events.on(EventType.WorldLoadEvent.class, e -> {
        arc.util.Log.info("==== [测试探针] 世界加载事件触发！ ====");
        arc.util.Log.info("==== [测试探针] 开始强行给所有兵种注入跃迁能力 ====");
        
        // 遍历所有原版单位
        mindustry.content.UnitTypes.all.each(unitType -> {
            // 检查一下有没有加过，防止重复添加报错
            boolean has = false;
            for (mindustry.entities.abilities.Ability a : unitType.abilities) {
                if (a instanceof BlinkAbility) has = true;
            }
            if (!has) {
                unitType.abilities.add(new BlinkAbility());
                arc.util.Log.info("已给单位注入跃迁: " + unitType.localizedName);
            }
        });
        arc.util.Log.info("==== [测试探针] 注入完成！ ====");
    });

    // 监听指挥模式点击
    Events.run(EventType.Trigger.update, () -> {
        if (Vars.player != null && Vars.control.input.commandMode && Core.input.isTouched()) {
            Vec2 pos = Core.input.mouseWorld();
            if (Vars.control.input.selectedUnits != null) {
                for (Unit unit : Vars.control.input.selectedUnits) {
                    for (mindustry.entities.abilities.Ability ability : unit.abilities) {
                        if (ability instanceof BlinkAbility blink) {
                            blink.setDestination(pos.x, pos.y);
                            unit.vel.setZero(); 
                        }
                    }
                }
            }
        }
    });

    // 渲染特效
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

        erosionStatus = new QuantumErosionStatus();
        quantumStorm = new QuantumStormWeather("quantum-storm", erosionStatus);
        quantumStorm.localizedName = "量子风暴";

        myPlanet = new Planet("ring-world", Planets.sun, 1f, 2) {{
            localizedName = "[sky]环界"; 
            accessible = true;
            alwaysUnlocked = true;
            visible = true;
            
            meshLoader = () -> new JmPlanet(this);
            
            cloudMeshLoader = () -> new MultiMesh(
                new DysonRingMesh(this, 1.5f, 0.1f, 729, Color.valueOf("555555"), Color.valueOf("555555"), false),
                new DysonRingMesh(this, 1.8f, 0.1f, 2941, Color.valueOf("555555"), Color.valueOf("555555"), false),
                new DysonRingMesh(this, 2.1f, 0.1f, 3834, Color.valueOf("555555"), Color.valueOf("555555"), false)
            );
            
            atmosphereColor = Color.valueOf("00e5ff");
            atmosphereRadIn = 0.05f;
            atmosphereRadOut = 0.25f;
            iconColor = Color.valueOf("00e5ff");
            
            ruleSetter = r -> {
                r.waves = true;
                r.waveSpacing = 60f * 60f; 
            };
        }};
    }
}
