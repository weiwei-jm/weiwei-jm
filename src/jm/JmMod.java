package jm;

import arc.graphics.Color;
import mindustry.content.Planets;
import mindustry.mod.Mod;
import mindustry.type.Planet;
import mindustry.graphics.g3d.MeshBuilder;

public class JmMod extends Mod {

    public static String ModName = "jsm";
    public static Planet myPlanet;

    public JmMod() {
        super();
    }

    @Override
    public void loadContent() {
        super.loadContent();

        // 注册你的星球
        myPlanet = new Planet("jm-planet", Planets.sun, 1f, 2) {{
            localizedName = "JM 星球";
            accessible = true;
            alwaysUnlocked = true;
            visible = true;
            
            // 星球外观
            meshLoader = () -> new JmPlanet(this);
            
            // 给星球加一个灰色的环
            cloudMeshLoader = () -> MeshBuilder.buildRing(this.radius * 1.5f, 0.2f);
            
            // 设置大气层颜色（亮蓝色）
            atmosphereColor = Color.valueOf("00e5ff");
            atmosphereRadIn = 0.05f;
            atmosphereRadOut = 0.25f;
            
            // 游戏规则（无需战役区块）
            ruleSetter = r -> {
                r.waves = true;
                r.waveSpacing = 60f * 60f; 
            };
        }};
    }
}
