package lire.cloudchime.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_DIR = FabricLoader.getInstance().getConfigDir().resolve("cloudchime");
    private static final Path MAIN_CONFIG = CONFIG_DIR.resolve("cloudchime.json");
    private static final Path EVENTS_DIR = CONFIG_DIR.resolve("events");
    private static final Path SOUNDS_DIR = CONFIG_DIR.resolve("sounds");

    private static MainConfig mainConfig;
    private static final Map<String, EventConfig> eventConfigs = new HashMap<>();
    private static final Random random = new Random();

    public static void init() {
        try {
            // 确保目录存在
            Files.createDirectories(CONFIG_DIR);
            Files.createDirectories(EVENTS_DIR);
            Files.createDirectories(SOUNDS_DIR);

            // 加载或创建主配置
            if (Files.exists(MAIN_CONFIG)) {
                mainConfig = GSON.fromJson(new FileReader(MAIN_CONFIG.toFile()), MainConfig.class);
            } else {
                mainConfig = new MainConfig();
                saveMainConfig();
            }

            // 首次启动时提取默认资源
            if (mainConfig.first_launch) {
                extractDefaultResources();
                mainConfig.first_launch = false;
                saveMainConfig();
            }

            // 加载所有事件配置
            loadEventConfigs();

        } catch (IOException e) {
            System.err.println("[云语铃音] 配置初始化失败: " + e.getMessage());
            mainConfig = new MainConfig(); // 使用默认配置
        }
    }

    private static void extractDefaultResources() throws IOException {
        // 提取默认音效 - 使用新文件名
        extractResource("/assets/cloudchime/sounds/small_alarm.ogg", SOUNDS_DIR.resolve("small_alarm.ogg"));
        extractResource("/assets/cloudchime/sounds/cheer.ogg", SOUNDS_DIR.resolve("cheer.ogg"));
        extractResource("/assets/cloudchime/sounds/big_alarm.ogg", SOUNDS_DIR.resolve("big_alarm.ogg"));

        // 提取默认事件配置
        String[] eventFiles = {
                "enter_raining.json", "enter_thundering.json",
                "rain_start.json", "rain_stop.json",
                "thunder_start.json", "thunder_stop.json"
        };

        for (String file : eventFiles) {
            extractResource("/assets/cloudchime/default_configs/" + file, EVENTS_DIR.resolve(file));
        }
    }

    private static void extractResource(String resourcePath, Path targetPath) throws IOException {
        try (InputStream in = ConfigManager.class.getResourceAsStream(resourcePath);
             OutputStream out = Files.newOutputStream(targetPath)) {
            if (in == null) {
                throw new IOException("资源不存在: " + resourcePath);
            }
            in.transferTo(out);
        }
    }

    private static void loadEventConfigs() throws IOException {
        File[] eventFiles = EVENTS_DIR.toFile().listFiles((dir, name) -> name.endsWith(".json"));
        if (eventFiles == null) return;

        for (File file : eventFiles) {
            String eventName = file.getName().replace(".json", "");
            try (FileReader reader = new FileReader(file)) {
                eventConfigs.put(eventName, GSON.fromJson(reader, EventConfig.class));
            }
        }
    }

    private static void saveMainConfig() throws IOException {
        try (FileWriter writer = new FileWriter(MAIN_CONFIG.toFile())) {
            GSON.toJson(mainConfig, writer);
        }
    }

    public static boolean isModEnabled() {
        return mainConfig.enabled;
    }

    public static boolean isEventEnabled(String eventName) {
        return mainConfig.enabled &&
                mainConfig.events.getOrDefault(eventName, false) &&
                eventConfigs.containsKey(eventName);
    }

    public static String getRandomMessage(String eventName) {
        EventConfig config = eventConfigs.get(eventName);
        if (config == null || config.messages == null || config.messages.length == 0) {
            return "§c[云语铃音] 未配置消息";
        }
        return config.messages[random.nextInt(config.messages.length)];
    }

    public static String getRandomSound(String eventName) {
        EventConfig config = eventConfigs.get(eventName);
        if (config == null || config.sounds == null || config.sounds.length == 0) {
            return null;
        }
        return config.sounds[random.nextInt(config.sounds.length)];
    }

    public static Path getSoundsDir() {
        return SOUNDS_DIR;
    }

    // 配置类定义
    public static class MainConfig {
        public boolean enabled = true;
        public boolean first_launch = true;
        public Map<String, Boolean> events = new HashMap<>();

        public MainConfig() {
            // 默认启用所有事件
            events.put("enter_raining", true);
            events.put("enter_thundering", true);
            events.put("rain_start", true);
            events.put("rain_stop", true);
            events.put("thunder_start", true);
            events.put("thunder_stop", true);
        }
    }

    public static class EventConfig {
        public String[] messages;
        public String[] sounds;
    }
}