package jm;

import arc.graphics.Color;
import arc.graphics.Gl;
import arc.math.Mathf;
import arc.math.geom.Mat3D;
import arc.math.geom.Vec3;
import arc.util.Time;
import mindustry.graphics.Shaders;
import mindustry.graphics.g3d.PlanetMesh;
import mindustry.graphics.g3d.PlanetParams;
import mindustry.type.Planet;

public class DysonRingMesh extends PlanetMesh {
    static Mat3D mat = new Mat3D();
    public static final int ringSegments = 256;

    public final int seed;
    public final boolean glow;

    public Vec3 flipAngle = new Vec3(0, 1, 0); // 固定旋转轴
    public float flipValue = 0;
    public float flipSpeed = 0.2f;
    public float rotateSpeed = 0.5f;
    public float pulseSpeed = 1f;
    public Color ambientTint = Color.white;

    public DysonRingMesh(Planet planet, float radius, float height, int seed, Color color, Color color2, boolean glow) {
        super(planet, CylinderRingMeshBuilder.build(radius, height, ringSegments, color, color2), Shaders.unlit);
        this.seed = seed;
        this.glow = glow;
        if (glow) {
            ambientTint = color.cpy();
        }
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
        shader.setUniformMatrix4("u_trans", mat
                .setToTranslation(planet.position)
                .rotate(flipAngle, flipValue)
                .rotate(Vec3.Y, planet.getRotation() + relRot()).val);
        shader.apply();
        mesh.render(shader, Gl.triangles);
    }

    @Override
    public void preRender(PlanetParams params) {
        float alpha = params.planet == planet ? 1f - params.uiAlpha : 1f;
        if (glow) {
            alpha *= 0.62f + 0.38f * Mathf.sin(Time.globalTime * pulseSpeed / 32f + seed * 0.013f);
        }
        Shaders.unlit.bind();
        Shaders.unlit.alpha = alpha;
    }
}
