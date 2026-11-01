package jm;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.type.Weapon; 
import mindustry.entities.units.WeaponMount;
import mindustry.gen.Healthc;
import mindustry.gen.Unit;
import mindustry.graphics.Layer;

public class JmExecutorWeapon extends Weapon {
    public JmExecutorWeapon() {
        mirror = false;
        x = y = 0f;
        noAttack = true;
        controllable = false;
        autoTarget = false;
    }

    @Override
    public void update(Unit unit, WeaponMount mount) {
        if (!(mount instanceof JmExecutorMount m)) return;

        if (m.active) {
            m.time += Time.delta;
            m.stageTime -= Time.delta;

            if (m.stageTime <= 0) {
                switch (m.stage) {
                    case 0 -> m.stageTime = 60f; 
                    case 1 -> {
                        if (mount.target != null) {
                            if (mount.target instanceof Healthc h && h.isValid()) {
                                for (int i = 0; i < 8; i++) {
                                    float ang = i * 45f + Mathf.random(-20f, 20f);
                                    float sx = unit.x + Angles.trnsx(ang, 60f);
                                    float sy = unit.y + Angles.trnsy(ang, 60f);
                                    JmSpearEffect.create((Unit) mount.target, sx, sy, mount.target.x(), mount.target.y());
                                }
                                h.damage(9999999f);
                            }
                        }
                        m.stageTime = 60f;
                    }
                    case 2 -> {
                        m.active = false;
                        m.stage = 0;
                        m.stageTime = 0f;
                    }
                }
                m.stage++;
            }
        } else {
            if (mount.target != null && mount.target instanceof Healthc h && h.isValid() && unit.within(mount.target, unit.type.maxRange)) {
                if (h.health() > 2000f) {
                    m.active = true;
                    m.stage = 0;
                    m.stageTime = 60f;
                } else {
                    h.damage(9999999f);
                    mindustry.content.Fx.massiveExplosion.at(h.x(), h.y());
                }
            }
        }
    }

    @Override
    public void draw(Unit unit, WeaponMount mount) {
        if (!(mount instanceof JmExecutorMount m)) return;
        
        if (m.active) {
            Draw.z(Layer.effect + 1);
            Draw.color(Color.valueOf("ff0000"));
            Lines.stroke(3f);
            Lines.circle(unit.x, unit.y, 150f + m.time / 2f);
            Lines.circle(unit.x, unit.y, 100f - m.time / 4f);
            Lines.poly(unit.x, unit.y, 6, 120f, m.time);
            Draw.reset();
        }
    }

    public static class JmExecutorMount extends WeaponMount {
        public float time, stageTime;
        public int stage;
        public boolean active;

        JmExecutorMount(Weapon w) {
            super(w);
        }
    }
}
