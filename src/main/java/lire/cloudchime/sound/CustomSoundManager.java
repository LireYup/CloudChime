package lire.cloudchime.sound; // 修正包名

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class CustomSoundManager {
    private static final Map<String, SoundEvent> CUSTOM_SOUNDS = new HashMap<>();

    public static void registerSound(String soundName) {
        // 使用工厂方法创建Identifier
        Identifier id = Identifier.of("cloudchime", soundName);
        SoundEvent sound = SoundEvent.of(id);
        Registry.register(Registries.SOUND_EVENT, id, sound);
        CUSTOM_SOUNDS.put(soundName, sound);
    }

    public static SoundEvent getSound(String soundName) {
        return CUSTOM_SOUNDS.get(soundName);
    }

    public static void loadSoundsFromDirectory(Path soundsDir) {
        if (!Files.isDirectory(soundsDir)) {
            System.err.println("[云语铃音] 音效目录不存在: " + soundsDir);
            return;
        }

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(soundsDir, "*.ogg")) {
            for (Path soundFile : stream) {
                String fileName = soundFile.getFileName().toString();
                // 去除扩展名
                String soundName = fileName.substring(0, fileName.length() - 4);
                registerSound(soundName);
                System.out.println("[云语铃音] 注册自定义音效: " + soundName);
            }
        } catch (IOException e) {
            System.err.println("[云语铃音] 加载自定义音效失败: " + e.getMessage());
        }
    }
}