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
    public float damageOnTouch = 10f;
    public float powerUse = 2f;
    public float maxConnectDist = 10f * 8f; 

    // 远古色系：青蓝色
    public static final Color ancientColor = Color.valueOf("00e5ff");
    public static final Color ancientFront = ancientColor.cpy().lerp(Color.white, 0.45f);

    // 👇 完美复刻 NHFx.chainLightningFade 的闪电折痕特效
    public static final Effect lightningFx = new Effect(20f, 500f, e -> {
        if (!(e.data instanceof Position)) return;
        Position target = e.data();
        float tx = target.getX(), ty = target.getY();
        float dst = Mathf.dst(e.x, e.y, tx, ty);
        if (dst <= 0) return;

        float normx = (tx - e.x) / dst;
        float normy = (ty - e.y) / dst;
        
        int links = Mathf.ceil(dst / 20f); // 每20像素一段折痕
        float spacing = dst / links;
        
        Lines.stroke(3f * Mathf.curve(e.fout(), 0, 0.7f));
        Draw.color(ancientColor, Color.white, e.fout() * 0.6f);
        
        Lines.beginLine();
        Fill.circle(e.x, e.y, Lines.getStroke() / 2);
        Lines.linePoint(e.x, e.y);
        
        Rand rand = new Rand();
        rand.setSeed(e.id);
        
        float fin = Mathf.curve(e.fin(), 0, 0.5f); // 闪电蔓延动画
        int i;
        float nx = e.x, ny = e.y;
        
        for (i = 0; i < (int)(links * fin); i++) {
            if (i == links - 1) {
                nx = tx; ny = ty;
            } else {
                float len = (i + 1) * spacing;
                // 在 30度角以内随机偏移 (tan(30) ≈ 0.57)
                float offsetMag = spacing * 0.57f;
                float ox = rand.range(offsetMag);
                float oy = rand.range(offsetMag);
                nx = e.x + normx * len + ox;
                ny = e.y + normy * len + oy;
            }
            Lines.linePoint(nx, ny);
        }
        
        // 蔓延的最前端发光
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
                mindustry.entities.Units.nearby(x, y, size * 8f, size * 8f, u -> {
                    if (u.team != team && u.isGrounded()) {
                        u.damage(damageOnTouch);
                        float angle = Mathf.angle(x - u.x, y - u.y);
                        u.vel.add(Mathf.cosDeg(angle) * 3f, Mathf.sinDeg(angle) * 3f);
                    }
                });
            }
        }

        @Override
        public void draw() {
            if (power == null || power.status < 0.5f || shield <= 0) return;

            Draw.z(Layer.effect);
            // 👇 开启发光混合模式，这是新视界能量感的核心
            Draw.blend(Blending.additive);
            
            for (int i = 0; i < 4; i++) {
                Building other = nearby(i);
                if (other instanceof QuantumShieldWallBuild wall) {
   
