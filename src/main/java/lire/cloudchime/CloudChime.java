package lire.cloudchime;

import lire.cloudchime.config.ConfigManager;
import lire.cloudchime.sound.CustomSoundManager;
import net.fabricmc.api.ModInitializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class CloudChime implements ModInitializer {
    public static final SoundEvent SMALL_ALARM_SOUND = SoundEvent.of(Identifier.of("cloudchime", "small_alarm"));
    public static final SoundEvent CHEER_SOUND = SoundEvent.of(Identifier.of("cloudchime", "cheer"));
    public static final SoundEvent BIG_ALARM_SOUND = SoundEvent.of(Identifier.of("cloudchime", "big_alarm"));

    @Override
    public void onInitialize() {
        ConfigManager.init();

        // 注册内置音效
        Registry.register(Registries.SOUND_EVENT, Identifier.of("cloudchime", "small_alarm"), SMALL_ALARM_SOUND);
        Registry.register(Registries.SOUND_EVENT, Identifier.of("cloudchime", "cheer"), CHEER_SOUND);
        Registry.register(Registries.SOUND_EVENT, Identifier.of("cloudchime", "big_alarm"), BIG_ALARM_SOUND);

        // 将内置音效添加到音效管理器
        CustomSoundManager.registerSound("small_alarm", SMALL_ALARM_SOUND);
        CustomSoundManager.registerSound("cheer", CHEER_SOUND);
        CustomSoundManager.registerSound("big_alarm", BIG_ALARM_SOUND);

        System.out.println("[云语铃音] 注册内置音效: small_alarm, cheer, big_alarm");

        // 加载自定义音效
        CustomSoundManager.loadSoundsFromDirectory(ConfigManager.getSoundsDir());

        System.out.println("[云语铃音] 模组已加载!");
    }
}