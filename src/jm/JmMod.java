package jm;

import arc.graphics.Color;
import mindustry.content.Planets;
import mindustry.mod.Mod;
import mindustry.type.Planet;

public class JmMod extends Mod {

    public static String ModName = "jm";
    public static Planet myPlanet;

    public JmMod() {
        super();
    }

    @Override
    public void loadContent() {
        super.loadContent();

        myPlanet = new Planet("jm-planet", Planets.sun, 1f, 2) {{
            localizedName = "JM 星球";
            accessible = true;
            alwaysUnlocked = true;
            visible = true;
            
            // 1. 星球本体
            meshLoader = () -> new JmPlanet(this);
            
            // 2. 星球外环（使用我们改写的 DysonRingMesh，灰色）
            cloudMeshLoader = () -> new DysonRingMesh(this, 2.0f, 0.1f, 729, Color.valueOf("888888"), Color.valueOf("cccccc"), false);
            
            // 3. 大气层颜色
            atmosphereColor = Color.valueOf("00e5ff");
            atmosphereRadIn = 0.05f;
            atmosphereRadOut = 0.25f;
            
            // 4. 游戏规则
            ruleSetter = r -> {
                r.waves = true;
                r.waveSpacing = 60f * 60f; 
            };
        }};
    }
}
