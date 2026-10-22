// 带太阳光影的环
package jm;

import arc.graphics.Color;
import arc.graphics.Gl;
import arc.math.Mathf;
import arc.math.Rand;
import arc.math.geom.Mat3D;
import arc.math.geom.Vec3;
import arc.util.Time;
import mindustry.graphics.Shaders;
import mindustry.graphics.g3d.PlanetMesh;
import mindustry.graphics.g3d.PlanetParams;
import mindustry.type.Planet;

public class DysonRingMesh extends PlanetMesh {
    static Mat3D mat = new Mat3D();
    static Rand rand = new Rand();
    public static final int ringSegments = 256;

    public final int seed;
    public final boolean glow;

    public Vec3 flipAngle = new Vec3();
    public float flipValue = 0;
    public float flipSpeed = 0f;
    public float rotateSpeed = 0.5f;
    public Color ambientTint;

    public DysonRingMesh(Planet planet, float radius, float height, int seed, Color color, Color color2, boolean glow) {
        // 使用原版的 clouds 着色器（完美支持光影，且不会闪退）
        super(planet, CylinderRingMeshBuilder.build(radius, height, ringSegments, color, color2), Shaders.clouds);
        this.seed = seed;
        this.glow = glow;
        
        rand.setSeed(seed);
        flipValue = rand.random(-180, 180);
        flipAngle.setToRandomDirection(rand); 
        flipSpeed = rand.random(0.25f, 0.55f); 
        rotateSpeed = rand.random(0.6f, 2.2f);
        
        if (glow) {
            ambientTint = color.cpy();
        }
    }

    public float flipRot() {
        return Time.globalTime * flipSpeed / 40f + flipValue;
    }

    public float relRot() {
        return Time.globalTime * rotateSpeed / 40f;
    }

    @Override
    public void render(PlanetParams params, Mat3D projection, Mat3D transform) {
        if (params.planet == planet && Mathf.zero(1f - params.uiAlpha, 0.01f)) return;

        // 关键：计算光影必须在 bind 之前
        preRender(params);
        
        shader.bind();
        shader.setUniformMatrix4("u_proj", projection.val);
        shader.setUniformMatrix4("u_trans", mat
                .setToTranslation(planet.position)
                .rotate(flipAngle, flipRot())
                .rotate(Vec3.Y, planet.getRotation() + relRot()).val);
        shader.apply();
        mesh.render(shader, Gl.triangles);
    }

    @Override
    public void preRender(PlanetParams params) {
        // 告诉着色器当前是哪颗星球（太阳方向会自动根据星球位置计算）
        Shaders.clouds.planet = planet;
        // 计算太阳光的方向
        Shaders.clouds.lightDir.set(planet.solarSystem.position).sub(planet.position)
                .rotate(flipAngle, flipRot())
                .rotate(Vec3.Y, planet.getRotation() + relRot())
                .nor();
        
        // 设置环境光和透明度
        Shaders.clouds.ambientColor.set(planet.solarSystem.lightColor).lerp(planet.atmosphereColor, 0.12f);
        Shaders.clouds.alpha = params.planet == planet ? 1f - params.uiAlpha : 1f;
    }
}
