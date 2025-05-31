package lire.cloudchime;

import net.fabricmc.api.ModInitializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class CloudChime implements ModInitializer {
    public static final SoundEvent RAIN_START_SOUND = SoundEvent.of(Identifier.of("cloudchime", "rain_start"));
    public static final SoundEvent RAIN_STOP_SOUND = SoundEvent.of(Identifier.of("cloudchime", "rain_stop"));
    public static final SoundEvent THUNDER_SOUND = SoundEvent.of(Identifier.of("cloudchime", "thunder"));

    @Override
    public void onInitialize() {
        Registry.register(Registries.SOUND_EVENT, Identifier.of("cloudchime", "rain_start"), RAIN_START_SOUND);
        Registry.register(Registries.SOUND_EVENT, Identifier.of("cloudchime", "rain_stop"), RAIN_STOP_SOUND);
        Registry.register(Registries.SOUND_EVENT, Identifier.of("cloudchime", "thunder"), THUNDER_SOUND);
        System.out.println("[云语铃音] 模组已加载!");
    }
}