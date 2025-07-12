package lire.cloudchime;

import lire.cloudchime.config.ConfigManager;
import lire.cloudchime.sound.CustomSoundManager;
import net.fabricmc.api.ModInitializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class CloudChime implements ModInitializer {
    // 添加缺失的音效常量
    public static final SoundEvent RAIN_START_SOUND = SoundEvent.of(Identifier.of("cloudchime", "rain_start"));
    public static final SoundEvent RAIN_STOP_SOUND = SoundEvent.of(Identifier.of("cloudchime", "rain_stop"));
    public static final SoundEvent THUNDER_SOUND = SoundEvent.of(Identifier.of("cloudchime", "thunder"));

    @Override
    public void onInitialize() {
        ConfigManager.init();

        // 注册内置音效
        Registry.register(Registries.SOUND_EVENT, Identifier.of("cloudchime", "rain_start"), RAIN_START_SOUND);
        Registry.register(Registries.SOUND_EVENT, Identifier.of("cloudchime", "rain_stop"), RAIN_STOP_SOUND);
        Registry.register(Registries.SOUND_EVENT, Identifier.of("cloudchime", "thunder"), THUNDER_SOUND);

        // 加载自定义音效
        CustomSoundManager.loadSoundsFromDirectory(ConfigManager.getSoundsDir());

        System.out.println("[云语铃音] 模组已加载!");
    }
}