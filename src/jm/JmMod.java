package jm;

import arc.graphics.Color;
import mindustry.content.Planets;
import mindustry.mod.Mod;
import mindustry.type.Planet;
import mindustry.graphics.g3d.MultiMesh;

public class JmMod extends Mod {

    public static String ModName = "jm";
    public static Planet myPlanet;

    public JmMod() {
        super();
    }

    @Override
    public void loadContent() {
        super.loadContent();

        // 强制加载类，防止安卓报 ClassNotFound
        try {
            Class.forName("jm.SpaceRiftUnit");
        } catch (ClassNotFoundException e) {
            arc.util.Log.err("找不到 SpaceRiftUnit 类！");
        }

        // 注册星球
        myPlanet = new Planet("jm-planet", Planets.sun, 1f, 2) {{
            localizedName = "JM 星球";
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
            
            ruleSetter = r -> {
                r.waves = true;
                r.waveSpacing = 60f * 60f; 
            };
        }};
    }
}
