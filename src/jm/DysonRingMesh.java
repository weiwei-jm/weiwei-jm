package jm;
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
        super(planet, CylinderRingMeshBuilder.build(radius, height, ringSegments, color, color2), Shaders.unlit);
        this.seed = seed;
        this.glow = glow;
        
        rand.setSeed(seed);
        flipValue = rand.random(-180, 180);
        flipAngle.setToRandomDirection(rand); // 随机倾斜轴
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

        preRender(params);
        shader.bind();
        shader.setUniformMatrix4("u_proj", projection.val);
        
        // 硬币翻滚 + 自身旋转
        shader.setUniformMatrix4("u_trans", mat
                .setToTranslation(planet.position)
                .rotate(flipAngle, flipRot())
                .rotate(Vec3.Y, planet.getRotation() + relRot()).val);
        shader.apply();
        mesh.render(shader, Gl.triangles);
    }

    @Override
    public void preRender(PlanetParams params) {
        Shaders.unlit.bind();
    }
}
