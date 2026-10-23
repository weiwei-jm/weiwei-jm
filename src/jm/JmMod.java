package jm;

import arc.graphics.Color;
import mindustry.content.Planets;
import mindustry.mod.Mod;
import mindustry.type.Planet;
import mindustry.graphics.g3d.MultiMesh;
import mindustry.gen.EntityMapping;

public class JmMod extends Mod {

    public static String ModName = "jm";
    public static Planet myPlanet;

    public JmMod() {
        super();
        
        // 将类强行注册到实体映射表，防止手机端找不到
        try {
            EntityMapping.map.put("space-rift-unit", SpaceRiftUnit::new);
        } catch (Exception e) {
            arc.util.Log.err("注册单位类失败", e);
        }
    }

    @Override
    public void loadContent() {
        super.loadContent();

        // 注册星球...
        myPlanet = new Planet("jm-planet", Planets.sun, 1f, 2) {{
            localizedName = "[blue]蓝日";
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
