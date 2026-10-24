package jm;

import arc.graphics.Color;
import arc.util.Time;
import mindustry.content.StatusEffects;
import mindustry.gen.Sounds;
import mindustry.type.weather.ParticleWeather;
import mindustry.world.meta.Attribute;

public class QuantumStormWeather extends ParticleWeather {
    public QuantumStormWeather(String name) {
        super(name);
        
        // ==========================================
        // 核心外观配置（借用原版粒子系统）
        // ==========================================
        this.color = Color.valueOf("80c8ff"); // 柔和发光冰蓝色
        this.particleRegion = "particle"; // 使用原版粒子贴图
        this.drawNoise = true;            // 开启全屏光晕遮罩
        this.useWindVector = true;        // 开启风向
        
        // 尺寸与透明度
        this.sizeMax = 15f;
        this.sizeMin = 5f;
        this.minAlpha = 0.4f;
        this.maxAlpha = 0.9f;
        
        // 粒子密度与速度
        this.density = 2000f;             // 越高越密集
        this.baseSpeed = 8.5f;            // 横向飞行速度（拉高做出风暴感）
        
        // 图层与亮度
        this.attrs.set(Attribute.light, -0.2f); 
        this.opacityMultiplier = 0.8f;    // 大幅拉高遮罩亮度
        
        // 持续时间与力度
        this.duration = 10f * Time.toMinutes;
        this.force = 0.15f;
        
        // 音效
        this.sound = Sounds.wind;
        this.soundVol = 0.8f;
    }
}
