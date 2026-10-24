package jm;

import arc.graphics.Color;
import arc.util.Time;
import mindustry.content.StatusEffects;
import mindustry.gen.Sounds;
import mindustry.type.weather.ParticleWeather;
import mindustry.world.meta.Attribute;

public class QuantumStormWeather extends ParticleWeather {
    
    // 加上这个变量
    public mindustry.type.StatusEffect erosionStatus;

    // 改成接收两个参数
    public QuantumStormWeather(String name, mindustry.type.StatusEffect erosion) {
        super(name);
        this.erosionStatus = erosion;
        
        this.color = Color.valueOf("80c8ff"); 
        this.particleRegion = "particle"; 
        this.drawNoise = true;            
        this.useWindVector = true;        
        
        this.sizeMax = 15f;
        this.sizeMin = 5f;
        this.minAlpha = 0.4f;
        this.maxAlpha = 0.9f;
        
        this.density = 2000f;             
        this.baseSpeed = 8.5f;            
        
        this.attrs.set(Attribute.light, -0.2f); 
        this.opacityMultiplier = 0.8f;    
        
        this.duration = 10f * Time.toMinutes;
        this.force = 0.15f;
        
        this.sound = Sounds.wind;
        this.soundVol = 0.8f;
    }

    // 👇 重写 update 方法，让风暴刮风时对单位施加状态
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
