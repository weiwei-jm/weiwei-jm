package jm;

import arc.Core;
import arc.Events;
import arc.graphics.Color;
import arc.math.geom.Vec2;
import mindustry.content.Planets;
import mindustry.game.EventType;
import mindustry.gen.Unit;
import mindustry.mod.Mod;
import mindustry.type.Planet;
import mindustry.type.StatusEffect;
import mindustry.graphics.g3d.MultiMesh;
import mindustry.Vars;

public class JmMod extends Mod {

    public static String ModName = "jm";
    public static Planet myPlanet;
    public static StatusEffect erosionStatus;
    public static QuantumStormWeather quantumStorm;
    public static QuantumShieldWall quantumShieldWall; // 👈 新增的防御塔

    public JmMod() {
        super();
    }

    @Override
    public void init() {
        // 1. 给原版单位加跃迁能力（方便测试）
        Events.on(EventType.WorldLoadEvent.class, e -> {
            mindustry.content.UnitTypes.dagger.abilities.add(new BlinkAbility());
            mindustry.content.UnitTypes.flare.abilities.add(new BlinkAbility());
        });

        // 2. 指挥模式点击触发跃迁
        Events.run(EventType.Trigger.update, () -> {
            if (Vars.player != null && Vars.control != null && Vars.control.input.commandMode) {
                if (Core.input.isTouched()) {
                    if (Core.scene.hasMouse()) return;
                    Vec2 pos = Core.input.mouseWorld();
                    if (Vars.control.input.selectedUnits != null) {
                        for (Unit unit : Vars.control.input.selectedUnits) {
                            for (mindustry.entities.abilities.Ability ability : unit.abilities) {
                                if (ability instanceof BlinkAbility blink) {
                                    blink.trigger(unit, pos.x, pos.y);
                                    if (unit.controller() instanceof mindustry.ai.types.CommandAI ai) {
                                        ai.targetPos = null;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        });

        // 3. 渲染跃迁特效
        Events.run(EventType.Trigger.draw, () -> {
            for (int i = BlinkAbility.effects.size - 1; i >= 0; i--) {
                BlinkEffect effect = BlinkAbility.effects.get(i);
                effect.update();
                if (effect.isDead()) {
                    BlinkAbility.effects.remove(i);
                } else {
                    effect.draw();
                }
            }
        });

        // 👇 4. 核心发射动画（已彻底删除） 👈
    }

@Override
public void loadContent() {
    super.loadContent();

    // 注册量子风暴状态和天气
    erosionStatus = new QuantumErosionStatus();
    quantumStorm = new QuantumStormWeather("quantum-storm", erosionStatus);
    quantumStorm.localizedName = "量子风暴";

    // 注册环界星球
    myPlanet = new Planet("ring-world", Planets.sun, 1f, 2) {{
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
        iconColor = Color.valueOf("00e5ff");
        
        ruleSetter = r -> {
            r.waves = true;
            r.waveSpacing = 60f * 60f; 
        };
    }};

    // 👇 核心修改：实例化并强行注册到游戏方块库中
    quantumShieldWall = new QuantumShieldWall("quantum-shield-wall");
    Vars.content.blocks().add(quantumShieldWall);
                }
