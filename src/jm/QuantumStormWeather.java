package jm;

import arc.graphics.Color;
import arc.util.Time;
import mindustry.gen.Sounds;
import mindustry.type.weather.ParticleWeather;
import mindustry.world.meta.Attribute;

public class QuantumStormWeather extends ParticleWeather {
    
    public mindustry.type.StatusEffect erosionStatus;

    public QuantumStormWeather(String name, mindustry.type.StatusEffect erosion) {
        super(name);
        this.erosionStatus = erosion;
        
        // 1. 粒子颜色（飞行的小碎块）
        this.color = Color.valueOf("80c8ff"); 
        this.particleRegion = "particle"; 
        this.useWindVector = true;        
        
        // ==========================================
        // 👇 2. 核心修改：雾的颜色改成蓝色
        // ==========================================
        this.drawNoise = true; // 开启全屏雾气
        // 把原先默认的白色底雾，改成深一点的幽蓝色 (如 1a5fb4 或 0044cc)
        this.noiseColor = Color.valueOf("1a5fb4"); 
        this.opacityMultiplier = 0.8f; // 雾气的浓度
        this.noiseScale = 800f; // 雾的纹理大小
        // ==========================================
        
        this.sizeMax = 15f;
        this.sizeMin = 5f;
        this.minAlpha = 0.4f;
        this.maxAlpha = 0.9f;
        
        this.density = 2000f;             
        this.baseSpeed = 8.5f;            
        
        this.attrs.set(Attribute.light, -0.2f); 
        
        this.duration = 10f * Time.toMinutes;
        this.force = 0.15f;
        
        this.sound = Sounds.wind;
        this.soundVol = 0.8f;
    }

    @Override
    public void update(mindustry.gen.WeatherState state) {
        super.update(state);
        if (arc.math.Mathf.chance(0.1f)) {
            mindustry.entities.Units.nearby(arc.Core.camera.position.x - 500, arc.Core.camera.position.y - 500, 1000, 1000, unit -> {
                if (unit.isValid() && arc.math.Mathf.chance(0.15f)) {
                    unit.apply(erosionStatus, 180f);
                }
            });
        }
    }
}
