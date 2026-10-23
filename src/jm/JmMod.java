package jm;

import arc.graphics.Color;
import mindustry.content.Planets;
import mindustry.mod.Mod;
import mindustry.type.Planet;
import mindustry.graphics.g3d.MultiMesh;

// 引入实体注册相关的类
import mindustry.gen.EntityMapping;
import mindustry.gen.Entityc;

public class JmMod extends Mod {

    public static String ModName = "jm";
    public static Planet myPlanet;

    public JmMod() {
        super();
        
        // 👇 核心：在原版解析 JSON 之前，把我们写的单位类注册进引擎！
        try {
            // 注册到游戏的实体字典里
            EntityMapping.register("space-rift-unit", SpaceRiftUnit::new);
            // 注册实体类的 ID
            EntityMapping.idMap.put(SpaceRiftUnit.class, 128); // 随便给个不冲突的ID
        } catch (Exception e) {
            arc.util.Log.err("注册 SpaceRiftUnit 失败", e);
        }
    }

    @Override
    public void loadContent() {
        super.loadContent();

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
