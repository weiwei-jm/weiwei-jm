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

        // 1. 注册你的自定义星球
        myPlanet = new Planet("ring-world", Planets.sun, 1f, 2) {{
            localizedName = "[sky]环界"; // 👈 中文名
            // 英文名会自动使用注册名 "ring-world"，如果要强制英文名可以加：
            // name = "[sky]RingWorld"; 
            
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

        // 2. 注册状态和天气
        erosionStatus = new QuantumErosionStatus();
        quantumStorm = new QuantumStormWeather("quantum-storm", erosionStatus);

        // 3. 挂载到原版蓝巨星（原版变量名叫 Planets.blue）
        Planets.blue.visible = true;
        Planets.blue.accessible = true;
        Planets.blue.alwaysUnlocked = true;
        Planets.blue.iconColor = Color.valueOf("00e5ff");
        
        Planets.blue.ruleSetter = r -> {
            r.weather.add(new WeatherEntry(quantumStorm));
            r.weather.each(w -> w.always = true); 
        };
    }
}
