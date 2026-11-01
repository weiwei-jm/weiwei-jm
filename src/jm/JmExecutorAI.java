package jm;

import mindustry.entities.units.AIController;
import mindustry.gen.Unit;
import mindustry.gen.Healthc;

public class JmExecutorAI extends AIController {
    @Override
    public void updateUnit() {
        if (retarget()) target = findTarget();
        updateMovement();
    }

    @Override
    public void updateWeapons() {
        for (mindustry.entities.units.WeaponMount mount : unit.mounts) {
            if (mount.weapon instanceof JmExecutorWeapon) {
                if (mount instanceof JmExecutorWeapon.JmExecutorMount m) {
                    if (m.active) {
                        mount.shoot = true;
                        mount.rotate = true;
                        if (target != null && target instanceof Healthc h && h.isValid()) {
                            mount.target = target;
                            mount.aimX = target.x();
                            mount.aimY = target.y();
                            unit.isShooting = true;
                        }
                    } else {
                        mount.target = target;
                        mount.shoot = target != null;
                    }
                }
            }
        }
    }

    public mindustry.gen.Teamc findTarget() {
        // 使用原版正确的 Teamc 包路径
        return mindustry.entities.Units.closestEnemy(unit.team, unit.x, unit.y, unit.type.maxRange, u -> true);
    }

    @Override
    public boolean retarget() {
        return timer.get(timerTarget, 30);
    }
}
