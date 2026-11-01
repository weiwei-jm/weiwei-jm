package jm;

import mindustry.entities.units.AIController;
import mindustry.gen.Unit;

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
                        if (target != null && target.isValid()) {
                            mount.target = target;
                            mount.aimX = target.x();
                            mount.aimY = target.y();
                            unit.isShooting = true;
                        }
                    } else {
                        // 平时也锁定敌人，用于清杂兵
                        mount.target = target;
                        mount.shoot = target != null;
                    }
                }
            }
        }
    }

    public mindustry.entities.units.Teamc findTarget() {
        // 优先寻找高血量敌人
        return mindustry.entities.Units.closestEnemy(unit.team, unit.x, unit.y, unit.type.maxRange, u -> true);
    }

    @Override
    public boolean retarget() {
        return timer.get(timerTarget, 30);
    }
}
