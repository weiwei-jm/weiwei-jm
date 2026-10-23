package jm;

import arc.Events;
import arc.graphics.Color;
import arc.math.Mathf;
import arc.struct.Seq;
import mindustry.entities.Units;
import mindustry.game.EventType;

public class BlackHoleManager {
    public static final Seq<BlackHoleRenderer> holes = new Seq<>();
    private static boolean initialized = false;

    public static void init() {
        if (initialized) return;
        initialized = true;
        
        Events.run(EventType.Trigger.draw, () -> {
            for (int i = holes.size - 1; i >= 0; i--) {
                BlackHoleRenderer hole = holes.get(i);
                
                // 让黑洞自己变大变小
                hole.update();
                
                if (!hole.active) {
                    hole.dispose();
                    holes.remove(i);
                    continue;
                }

                // 只要黑洞活着，就产生吸力！
                // 影响范围：4格(32像素) + 当前半径
                float pullRadius = 32f + hole.currentRadius;
                
                Units.nearby(hole.x - pullRadius, hole.y - pullRadius, pullRadius * 2, pullRadius * 2, unit -> {
                    float dst = Mathf.dst(hole.x, hole.y, unit.x, unit.y);
                    if (dst < pullRadius) {
                        if (dst < hole.currentRadius * 0.5f) {
                            unit.kill(); // 碰到核心直接死
                        } else {
                            // 被吸过去
                            float pullForce = 1.5f;
                            unit.x += (hole.x - unit.x) / dst * pullForce;
                            unit.y += (hole.y - unit.y) / dst * pullForce;
                        }
                    }
                });

                // 渲染特效
                if (hole.active) {
                    hole.begin();
                    arc.Core.batch.flush(); 
                    hole.end();
                }
            }
        });
    }

    // 给炮塔调用的接口
    public static void create(float x, float y, float maxRadius, float life, float growthSpeed, Color color) {
        init();
        BlackHoleRenderer hole = new BlackHoleRenderer();
        hole.x = x;
        hole.y = y;
        hole.maxRadius = maxRadius;
        hole.life = life;
        hole.growthSpeed = growthSpeed;
        hole.ringColor.set(color);
        hole.active = true;
        holes.add(hole);
    }
}
