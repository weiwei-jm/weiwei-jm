package jm;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.content.Fx;
import mindustry.content.Items;
import mindustry.gen.Building;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.world.Block;
import mindustry.world.draw.DrawDefault;
import mindustry.world.meta.BuildVisibility; // 👈 引入沙盒可见

public class QuantumShieldWall extends Block {
    public float shieldCapacity = 2000f;
    public float shieldRegenTime = 240f; 
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
        
        // 👇 强行设置它在沙盒模式中可见，并在建造菜单中显示
        buildVisibility = BuildVisibility.sandboxOnly; 
        
        // 👇 给它一个原版的默认绘制器，这样它就不会隐形了
        drawer = new DrawDefault();
        
        requirements(Category.defense, ItemStack.with(Items.titanium, 100, Items.lead, 80, Items.silicon, 60));
    }

    public class QuantumShieldWallBuild extends Building {
        public float shield = shieldCapacity;
        public float shieldTimer = 0f;

        @Override
        public void update() {
            super.update();

            if (power == null || power.status < 0.5f) {
                shield = 0f;
                return;
            }

            if (shield < shieldCapacity) {
                shieldTimer += Time.delta;
                if (shieldTimer >= shieldRegenTime) {
                    shield = Math.min(shield + shieldCapacity / 10f, shieldCapacity);
                    shieldTimer = 0f;
                }
            }

            if (shield > 0) {
                mindustry.entities.Units.nearby(x, y, size * 8f, size * 8f, u -> {
                    if (u.team != team && u.isGrounded()) {
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
            super.draw(); // 必须调用父类的 draw，这样才能画出那个默认的方块底图
            if (power == null || power.status < 0.5f || shield <= 0) return;

            Draw.z(Layer.effect);
            for (int i = 0; i < 4; i++) {
                Building other = nearby(i);
                if (other instanceof QuantumShieldWallBuild wall) {
                    if (wall.shield > 0 && wall.power.status >= 0.5f) {
                        Draw.color(Color.valueOf("00e5ff"));
                        Lines.stroke(2f);
                        Lines.line(x, y, wall.x, wall.y);

                        if (Mathf.chance(0.05f)) {
                            float startX = x, startY = y;
                            float endX = wall.x, endY = wall.y;
                            
                            Draw.color(Pal.accent);
                            Lines.stroke(1.5f);
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
