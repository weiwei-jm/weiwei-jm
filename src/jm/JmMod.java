package jm;

import arc.graphics.Color;
import mindustry.Vars;
import mindustry.content.Planets;
import mindustry.mod.Mod;
import mindustry.type.Planet;
import mindustry.graphics.g3d.MultiMesh;
import mindustry.game.EventType;
import arc.Events;

public class JmMod extends Mod {

    public static String ModName = "jm";
    public static Planet myPlanet;

    public JmMod() {
        super();
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
        
        // 👇 核心：只要你加载任何地图，就在玩家身边召唤它！
        Events.on(EventType.WorldLoadEvent.class, e -> {
            // 直接用 Java 创建
            SpaceRiftUnit unit = SpaceRiftUnit.create();
            // 借用匕首的贴图和数据
            unit.type = mindustry.content.UnitTypes.dagger;
            // 阵营设为玩家阵营
            unit.team = Vars.player.team;
            // 生成在玩家位置偏上一点
            unit.x = Vars.player.x;
            unit.y = Vars.player.y + 40f; 
            // 让它出场！（这会触发3秒裂缝动画）
            unit.add();
        });
    }
}
