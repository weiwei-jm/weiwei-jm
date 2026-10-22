package jm;

import arc.graphics.Color;
import mindustry.content.Planets;
import mindustry.mod.Mod;
import mindustry.type.Planet;
import mindustry.graphics.g3d.MultiMesh;

public class JmMod extends Mod {

    public static String ModName = "jsm";
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
            
            // 星球本体
            meshLoader = () -> new JmPlanet(this);
            
            // 👇 终极三重环（深灰色 111111）
            cloudMeshLoader = () -> new MultiMesh(
                new DysonRingMesh(this, 1.5f, 0.1f, 729, Color.valueOf("111111"), Color.valueOf("111111"), false),
                new DysonRingMesh(this, 1.8f, 0.1f, 2941, Color.valueOf("111111"), Color.valueOf("111111"), false),
                new DysonRingMesh(this, 2.1f, 0.1f, 3834, Color.valueOf("111111"), Color.valueOf("111111"), false)
            );
            
            atmosphereColor = Color.valueOf("00e5ff");
            atmosphereRadIn = 0.05f;
            atmosphereRadOut = 0.25f;
            
            ruleSetter = r -> {
                r.waves = true;
                r.waveSpacing = 60f * 60f; 
            };
        }};
    }
}
