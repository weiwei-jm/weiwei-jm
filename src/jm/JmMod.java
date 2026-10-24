@package jm;

import arc.Core;
import arc.Events;
import arc.Input;
import arc.graphics.Color;
import mindustry.content.Planets;
import mindustry.game.EventType;
import mindustry.mod.Mod;
import mindustry.type.Planet;
import mindustry.type.Weather.WeatherEntry;
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
    public void init() {
        // 1. 初始化跃迁 UI
        BlinkUI.init();

        // 2. 渲染跃迁特效 (连接线、圆点)
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

        // 3. 绑定快捷键：按 T 键启动跃迁瞄准
        Events.run(EventType.Trigger.update, () -> {
            if (Core.input.keyTap(Input.Keys.t)) {
                BlinkUI.startAiming();
            }
        });
    }

    @Override
    public void loadContent() {
        super.loadContent();

        // ==========================================
        // 注册状态和天气
        // ==========================================
        erosionStatus = new QuantumErosionStatus();
        quantumStorm = new QuantumStormWeather("quantum-storm", erosionStatus);
        quantumStorm.localizedName = "量子风暴";

        // ==========================================
        // 注册你的自定义星球 [sky]环界
        // ==========================================
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
                // 如果想强制每次进图都刮风暴，可以把下面两行解除注释
                // r.weather.add(new WeatherEntry(quantumStorm));
                // r.weather.each(w -> w.always = true);
            };
        }};
    }
}
