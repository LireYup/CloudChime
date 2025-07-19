package lire.cloudchime.sound;

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
import java.util.Set;

public class CustomSoundManager {
    private static final Map<String, SoundEvent> SOUND_REGISTRY = new HashMap<>();
    // 内置音效白名单
    private static final Set<String> BUILTIN_SOUNDS = Set.of(
            "small_alarm", "cheer", "big_alarm"
    );

    public static void registerSound(String soundName, SoundEvent soundEvent) {
        SOUND_REGISTRY.put(soundName, soundEvent);
    }

    public static SoundEvent getSound(String soundName) {
        return SOUND_REGISTRY.get(soundName);
    }

    public static void loadSoundsFromDirectory(Path soundsDir) {
        if (!Files.isDirectory(soundsDir)) {
            System.err.println("[云语铃音] 音效目录不存在: " + soundsDir);
            return;
        }

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(soundsDir, "*.ogg")) {
            for (Path soundFile : stream) {
                String fileName = soundFile.getFileName().toString();
                String soundName = fileName.substring(0, fileName.length() - 4); // 移除.ogg扩展名

                // 跳过内置音效文件
                if (BUILTIN_SOUNDS.contains(soundName)) {
                    System.out.println("[云语铃音] 跳过内置音效文件: " + soundName);
                    continue;
                }

                // 注册自定义音效
                Identifier id = Identifier.of("cloudchime", soundName);
                SoundEvent sound = SoundEvent.of(id);
                Registry.register(Registries.SOUND_EVENT, id, sound);
                SOUND_REGISTRY.put(soundName, sound);
                System.out.println("[云语铃音] 注册自定义音效: " + soundName);
            }
        } catch (IOException e) {
            System.err.println("[云语铃音] 加载自定义音效失败: " + e.getMessage());
        }
    }
}