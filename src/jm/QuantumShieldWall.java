package jm;

import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.math.Rand;
import arc.math.geom.Position;
import arc.math.geom.Vec2;
import arc.util.Time;
import mindustry.content.Fx;
import mindustry.content.Items;
import mindustry.entities.Effect;
import mindustry.gen.Building;
import mindustry.graphics.Layer;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.world.Block;
import mindustry.world.meta.BuildVisibility;

public class QuantumShieldWall extends Block {
    public float shieldCapacity = 2000f;
    public float shieldRegenTime = 240f; 
    public float damageOnTouch = 100f; // 碰一下极其致命
    public float powerUse = 2f;
    public float maxConnectDist = 10f * 8f; 

    public static final Color ancientColor = Color.valueOf("00e5ff");
    public static final Color ancientFront = ancientColor.cpy().lerp(Color.white, 0.45f);

    public static final Effect lightningFx = new Effect(20f, 500f, e -> {
        if (!(e.data instanceof Position)) return;
        Position target = e.data();
        float tx = target.getX(), ty = target.getY();
        float dst = Mathf.dst(e.x, e.y, tx, ty);
        if (dst <= 0) return;

        float normx = (tx - e.x) / dst;
        float normy = (ty - e.y) / dst;
        
        int links = Mathf.ceil(dst / 20f); 
        float spacing = dst / links;
        
        Lines.stroke(3f * Mathf.curve(e.fout(), 0, 0.7f));
        Draw.color(ancientColor, Color.white, e.fout() * 0.6f);
        
        Lines.beginLine();
        Fill.circle(e.x, e.y, Lines.getStroke() / 2);
        Lines.linePoint(e.x, e.y);
        
        Rand rand = new Rand();
        rand.setSeed(e.id);
        
        float fin = Mathf.curve(e.fin(), 0, 0.5f); 
        int i;
        float nx = e.x, ny = e.y;
        
        for (i = 0; i < (int)(links * fin); i++) {
            if (i == links - 1) {
                nx = tx; ny = ty;
            } else {
                float len = (i + 1) * spacing;
                float offsetMag = spacing * 0.57f;
                float ox = rand.range(offsetMag);
                float oy = rand.range(offsetMag);
                nx = e.x + normx * len + ox;
                ny = e.y + normy * len + oy;
            }
            Lines.linePoint(nx, ny);
        }
        
        if (i < links) {
            float f = Mathf.clamp(fin * links % 1);
            float len = (i + 1) * spacing;
            float ox = rand.range(spacing * 0.57f);
            float oy = rand.range(spacing * 0.57f);
            float px = e.x + normx * len + ox;
            float py = e.y + normy * len + oy;
            Lines.linePoint(px, py);
            Fill.circle(px, py, Lines.getStroke());
        }
        Lines.endLine();
    });

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
        buildVisibility = BuildVisibility.sandboxOnly; 
        
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
                mindustry.entities.Units.nearby(x, y, maxConnectDist, maxConnectDist, u -> {
                    if (u.team != team && u.isGrounded()) {
                        // 检查敌人是不是刚好在我们的十字连线上
                        for (int i = 0; i < 4; i++) {
                            Building other = nearby(i);
                            if (other instanceof QuantumShieldWallBuild wall && wall.shield > 0) {
                                float dst = Mathf.dst(x, y, wall.x, wall.y);
                                boolean sameX = Math.abs(this.x - wall.x) < 1f;
                                boolean sameY = Math.abs(this.y - wall.y) < 1f;
                                
                                if (dst <= maxConnectDist && (sameX || sameY)) {
                                    // 计算敌人到线段的距离
                                    float lineDst = Mathf.dstLine(u.x, u.y, x, y, wall.x, wall.y);
                                    
                                    // 如果敌人撞在了光束上（距离线小于 12 像素）
                                    if (lineDst < 12f) {
                                        u.damageContinuous(damageOnTouch); // 持续受到高额伤害
                                        
                                        // 纯视觉效果，不影响移动
                                        if (Mathf.chance(0.2f)) {
                                            Fx.chainLightning.at(u.x, u.y, 0, ancientColor);
                                        }
                                    }
                                }
                            }
                        }
                    }
                });
            }
        }

        @Override
        public void draw() {
            if (power == null || power.status < 0.5f || shield <= 0) return;

            Draw.z(Layer.effect);
            Draw.blend(Blending.additive);
            
            mindustry.gen.Groups.build.each(b -> {
                if (b != this && b instanceof QuantumShieldWallBuild wall && b.team == this.team) {
                    if (wall.shield > 0 && wall.power.status >= 0.5f) {
                        float dst = Mathf.dst(x, y, wall.x, wall.y);
                        boolean sameX = Math.abs(this.x - wall.x) < 1f;
                        boolean sameY = Math.abs(this.y - wall.y) < 1f;
                        
                        if (dst <= maxConnectDist && (sameX || sameY)) {
                            if (this.id < wall.id) {
                                Draw.color(ancientColor, 0.3f);
                                Lines.stroke(8f); 
                                Lines.line(x, y, wall.x, wall.y);
                                
                                Draw.color(ancientFront);
                                Lines.stroke(3f); 
                                Lines.line(x, y, wall.x, wall.y);

                                Draw.color(ancientColor);
                                Fill.circle(x, y, 6f);
                                Fill.circle(wall.x, wall.y, 6f);
                                Draw.color(Color.white);
                                Fill.circle(x, y, 3f);
                                Fill.circle(wall.x, wall.y, 3f);

                                if (Mathf.chance(0.15f)) {
                                    lightningFx.at(x, y, 0, ancientColor, new Vec2(wall.x, wall.y));
                                }
                            }
                        }
                    }
                }
            });
            
            Draw.blend(); 
            Draw.reset();
        }
    }
}
