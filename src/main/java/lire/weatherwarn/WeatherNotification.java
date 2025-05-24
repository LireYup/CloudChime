package lire.weatherwarn;

import net.fabricmc.api.ModInitializer;

public class WeatherNotification implements ModInitializer {
    @Override
    public void onInitialize() {
        System.out.println("[Weather Warn] Enabled");
    }
}