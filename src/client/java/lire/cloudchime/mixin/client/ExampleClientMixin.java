package lire.cloudchime.mixin.client;

import lire.cloudchime.config.ConfigManager;
import lire.cloudchime.sound.CustomSoundManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.World; // 确保导入 World 类
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(ClientWorld.class)
public class ExampleClientMixin {
	@Unique
	private boolean lastRainingState = false;
	@Unique
	private boolean lastThunderingState = false;
	@Unique
	private boolean statesInitialized = false;

	@Inject(method = "tick", at = @At("TAIL"))
	private void onWorldTick(CallbackInfo ci) {
		if (!ConfigManager.isModEnabled()) return;
		ClientWorld world = (ClientWorld)(Object)this;
		ClientPlayerEntity player = MinecraftClient.getInstance().player;
		if (player == null) return;
		boolean isRaining = world.isRaining();
		boolean isThundering = world.isThundering();
		if (!statesInitialized) {
			handleWorldEnter(player, world, isRaining, isThundering);
			statesInitialized = true;
			lastRainingState = isRaining;
			lastThunderingState = isThundering;
			return;
		}
		if (isThundering != lastThunderingState) {
			if (isThundering) {
				triggerEvent("thunder_start", player, world);
			} else {
				triggerEvent("thunder_stop", player, world);
			}
			lastThunderingState = isThundering;
			lastRainingState = isThundering || isRaining;
			return;
		}
		if (isRaining != lastRainingState) {
			if (isRaining) {
				triggerEvent("rain_start", player, world);
			} else {
				triggerEvent("rain_stop", player, world);
			}
			lastRainingState = isRaining;
		}
	}

	@Unique
	private void handleWorldEnter(ClientPlayerEntity player, ClientWorld world,
								  boolean isRaining, boolean isThundering) {
		// 修改：仅在主世界（Overworld）发送问候消息
		RegistryKey<World> dimensionKey = world.getRegistryKey();
		if (dimensionKey != null && dimensionKey.equals(World.OVERWORLD)) {
			// 根据配置决定是否发送问候消息
			if (ConfigManager.isGreetingEnabled()) {
				String greetingMessage = ConfigManager.getGreetingMessage();
				player.sendMessage(Text.literal(greetingMessage), false);
			}
		}
		// 原有逻辑保持不变：根据天气触发事件 (这部分事件触发本身没有限制维度，如果需要也可以加上判断)
		if (isThundering) {
			triggerEvent("enter_thundering", player, world);
		} else if (isRaining) {
			triggerEvent("enter_raining", player, world);
		}
	}

	@Unique
	private void triggerEvent(String eventName, ClientPlayerEntity player, ClientWorld world) {
		if (!ConfigManager.isEventEnabled(eventName)) return;
		String message = ConfigManager.getRandomMessage(eventName);
		player.sendMessage(Text.literal(message), false);
		String soundFile = ConfigManager.getRandomSound(eventName);
		if (soundFile != null) {
			playCustomSound(player, world, soundFile);
		}
	}

	@Unique
	private void playCustomSound(ClientPlayerEntity player, ClientWorld world, String soundFile) {
		String soundName = soundFile.replace(".ogg", "");
		SoundEvent sound = CustomSoundManager.getSound(soundName);
		if (sound == null) {
			System.err.println("[云语铃音] 音效未注册: " + soundName);
			return;
		}
		System.out.println("[云语铃音] 播放音效: " + soundName);
		// 使用1.21.7的正确API播放声音
		world.playSoundFromEntityClient(
				player, // 声音来源实体
				sound,  // 音效事件
				SoundCategory.MASTER, // 音量类别
				1.0F,   // 音量
				0.8F + world.random.nextFloat() * 0.4F // 音调
		);
		System.out.println("[云语铃音] 音效播放请求已发送");
	}
}