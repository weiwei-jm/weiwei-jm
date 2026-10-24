package jm;

import arc.graphics.Color;
import mindustry.content.Planets;
import mindustry.mod.Mod;
import mindustry.type.Planet;
import mindustry.graphics.g3d.MultiMesh;
import mindustry.type.Weather.WeatherEntry;
import mindustry.type.StatusEffect;

public class JmMod extends Mod {

    public static String ModName = "jm";
    public static Planet myPlanet;
    
    public static StatusEffect erosionStatus;
    public static QuantumStormWeather quantumStorm;

    public JmMod() {
        super();
    }

    @Override
    public void loadContent() {
        super.loadContent();

        // ==========================================
        // 1. 注册你的自定义星球 (带三个环的蓝白星球)
        // ==========================================
        myPlanet = new Planet("[sky]RingWorld", Planets.sun, 1f, 2) {{
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
            iconColor = Color.valueOf("00e5ff"); // 图标改为蓝色
            
            ruleSetter = r -> {
                r.waves = true;
                r.waveSpacing = 60f * 60f; 
            };
        }};


        // ==========================================
        // 2. 注册状态和天气 (量子风暴)
        // ==========================================
        erosionStatus = new QuantumErosionStatus();
        quantumStorm = new QuantumStormWeather("quantum-storm", erosionStatus);


        // ==========================================
        // 3. 将天气挂载到原版的 蓝巨星(Blue Giant)
        // ==========================================
        Planets.blueGiant.visible = true;
        Planets.blueGiant.accessible = true;
        Planets.blueGiant.alwaysUnlocked = true;
        Planets.blueGiant.iconColor = Color.valueOf("00e5ff");
        
        Planets.blueGiant.ruleSetter = r -> {
            r.weather.add(new WeatherEntry(quantumStorm));
            r.weather.each(w -> w.always = true); 
        };
    }
    }
