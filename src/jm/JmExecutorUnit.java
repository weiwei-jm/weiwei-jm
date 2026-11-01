package jm;

import arc.util.Time;
import mindustry.gen.UnitEntity;

public class JmExecutorUnit extends UnitEntity {
    public float trueHealth;
    public float trueMaxHealth;
    public float invFrames;
    public float lastDamage;

    @Override
    public void update() {
        updateValues();
        super.update();
    }

    @Override
    public void rawDamage(float amount) {
        if (Float.isNaN(amount) || amount > 5000000f) return; 
        if (invFrames <= 0f || amount > lastDamage) {
            float lam = amount;
            amount -= lastDamage;
            lastDamage = lam;
            amount = Math.min(amount, type.health / 220f);
            trueHealth -= amount;
            super.rawDamage(amount);
            trueHealth = health;
            invFrames = 15f;
        }
    }

    void updateValues() {
        if (!Float.isNaN(health)) trueHealth = Math.max(trueHealth, health);
        health = trueHealth;
        maxHealth = trueMaxHealth;
        
        if (trueHealth > 0) {
            elevation = 1f;
            dead = false;
        } else {
            elevation = 0f;
            dead = true;
        }
        if (invFrames > 0) {
            invFrames -= Time.delta;
            if (invFrames <= 0f) lastDamage = 0f;
        }
    }

    @Override
    public void setType(mindustry.type.UnitType type) {
        super.setType(type);
        trueMaxHealth = type.health;
    }

    @Override
    public void add() {
        if (!added) trueHealth = type.health;
        super.add();
    }
}
