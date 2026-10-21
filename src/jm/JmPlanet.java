package jm;

import arc.graphics.Color;
import arc.math.geom.Vec3;
import arc.util.noise.Simplex;
import mindustry.graphics.Shaders;
import mindustry.graphics.g3d.HexMesher;
import mindustry.graphics.g3d.MeshBuilder;
import mindustry.graphics.g3d.PlanetMesh;
import mindustry.graphics.g3d.PlanetParams;
import mindustry.type.Planet;

public class JmPlanet extends PlanetMesh {
    public JmPlanet(Planet planet) {
        super(planet, MeshBuilder.buildHex(new HexMesher() {
            @Override
            public float getHeight(Vec3 position) {
                return 0; // 纯平面星球
            }

            @Override
            public void getColor(Vec3 position, Color out) {
                // 用噪声生成随机地形颜色
                double height = Math.pow(Simplex.noise3d(0, 3, 0.5, 8.0, position.x, position.y, position.z), 2.0) * 1.5;
                if (height > 0.6) {
                    out.set(Color.valueOf("ffffff")); // 高地：纯白色
                } else if (height > 0.3) {
                    out.set(Color.valueOf("00e5ff")); // 中段：亮蓝色
                } else {
                    out.set(Color.valueOf("0055ff")); // 低洼：深蓝色
                }
            }
        }, 6, planet.radius, 0.2f), Shaders.unlit);
    }

    @Override
    public void preRender(PlanetParams params) {
        Shaders.planet.planet = planet;
        Shaders.planet.lightDir.set(planet.solarSystem.position).sub(planet.position).rotate(Vec3.Y, planet.getRotation()).nor();
        Shaders.planet.ambientColor.set(planet.solarSystem.lightColor);
    }
}
