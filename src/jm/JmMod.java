package jm;

import arc.graphics.Color;
import mindustry.content.Planets;
import mindustry.mod.Mod;
import mindustry.type.Planet;
import mindustry.graphics.g3d.MultiMesh;
import mindustry.Vars;
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
        
        // 👇 进游戏自动生成一个测试单位
        Events.on(EventType.WorldLoadEvent.class, e -> {
            // 强制让游戏加载这个类
            SpaceRiftUnit unit = new SpaceRiftUnit();
            // 必须给它指定一个原版类型，否则不会显示
            unit.type = mindustry.content.UnitTypes.dagger;
            // 设置阵营为你所在的队伍
            unit.team = Vars.player.team;
            // 生成在玩家位置
            unit.x = Vars.player.x;
            unit.y = Vars.player.y + 40f; 
            // 加入游戏世界
            unit.add();
        });
    }
}
