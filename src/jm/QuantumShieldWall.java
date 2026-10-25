package jm;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.content.Fx;
import mindustry.entities.Lightning;
import mindustry.gen.Building;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.world.Block;
import mindustry.world.meta.BuildVisibility;
import mindustry.world.meta.Stat;
import mindustry.content.Items;

public class QuantumShieldWall extends Block {
    public float shieldCapacity = 2000f;
    public float shieldRegenTime = 240f; // 4秒（60帧/秒）
    public float damageOnTouch = 10f;
    public float powerUse = 2f;

    public QuantumShieldWall(String name) {
        super(name);
        update = true;
        solid = true;
        destructible = true;
        hasPower = true;
        canOverdrive = false;
        consumePower(powerUse / 60f);
        
        size = 2;
        health = 3200;
        armor = 10f;
        category = Category.defense;
        requirements(Category.defense, ItemStack.with(Items.titanium, 100, Items.lead, 80, Items.silicon, 60));
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.add(Stat.health, shieldCapacity / 10, Stat.UnitType.none); // 简单显示一下盾量
    }

    public class QuantumShieldWallBuild extends Building {
        public float shield = shieldCapacity;
        public float shieldTimer = 0f;

        @Override
        public void update() {
            super.update();

            // 没电就重置护盾
            if (power == null || power.status < 0.5f) {
                shield = 0f;
                return;
            }

            // 护盾回复
            if (shield < shieldCapacity) {
                shieldTimer += Time.delta;
                if (shieldTimer >= shieldRegenTime) {
                    shield = Math.min(shield + shieldCapacity / 10f, shieldCapacity);
                    shieldTimer = 0f;
                }
            }

            // 检测并伤害碰触护盾的敌方陆军单位
            if (shield > 0) {
                mindustry.entities.Units.nearby(x, y, size * 8f, size * 8f, u -> {
                    if (u.team != team && u.isGrounded()) {
                        // 造成伤害并击退
                        u.damage(damageOnTouch);
                        float angle = Mathf.angle(x - u.x, y - u.y);
                        u.vel.add(Mathf.cosDeg(angle) * 3f, Mathf.sinDeg(angle) * 3f);
                        Fx.chainLightning.at(u.x, u.y, 0, Pal.accent);
                    }
                });
            }
        }

        @Override
        public void draw() {
            super.draw();
            if (power == null || power.status < 0.5f || shield <= 0) return;

            // 画连接线
            Draw.z(Layer.effect);
            for (int i = 0; i < 4; i++) {
                Building other = nearby(i);
                if (other instanceof QuantumShieldWallBuild wall) {
                    if (wall.shield > 0 && wall.power.status >= 0.5f) {
                        // 绘制两个方块面对面的连线
                        float midX = (x + wall.x) / 2f;
                        float midY = (y + wall.y) / 2f;
                        
                        // 连线
                        Draw.color(Color.valueOf("00e5ff"));
                        Lines.stroke(2f);
                        Lines.line(x, y, wall.x, wall.y);

                        // 随机闪电路径
                        if (Mathf.chance(0.05f)) {
                            // 在原版里我们可以直接画两个折线点
                            float startX = x;
                            float startY = y;
                            float endX = wall.x;
                            float endY = wall.y;
                            
                            Draw.color(Pal.accent);
                            Lines.stroke(1.5f);
                            // 简单的折线闪电算法
                            float lastX = startX, lastY = startY;
                            for (int j = 1; j <= 5; j++) {
                                float t = j / 5f;
                                float px = Mathf.lerp(startX, endX, t) + Mathf.range(8f);
                                float py = Mathf.lerp(startY, endY, t) + Mathf.range(8f);
                                Lines.line(lastX, lastY, px, py);
                                lastX = px;
                                lastY = py;
                            }
                            Lines.line(lastX, lastY, endX, endY);
                        }
                    }
                }
            }
            Draw.reset();
        }
    }
                }
