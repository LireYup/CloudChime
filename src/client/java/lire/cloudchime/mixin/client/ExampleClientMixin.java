package lire.cloudchime.mixin.client;

import lire.cloudchime.config.ConfigManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
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
		// 模组禁用时跳过
		if (!ConfigManager.isModEnabled()) return;

		ClientWorld world = (ClientWorld)(Object)this;
		ClientPlayerEntity player = MinecraftClient.getInstance().player;
		if (player == null) return;

		boolean isRaining = world.isRaining();
		boolean isThundering = world.isThundering();

		// 首次tick初始化状态
		if (!statesInitialized) {
			handleWorldEnter(player, world, isRaining, isThundering);
			statesInitialized = true;
			return;
		}

		// 优先检测雷暴变化
		if (isThundering != lastThunderingState) {
			if (isThundering) {
				triggerEvent("thunder_start", player, world);
			} else {
				triggerEvent("thunder_stop", player, world);
			}
			lastThunderingState = isThundering;
			lastRainingState = isRaining; // 同步更新下雨状态
			return;
		}

		// 检测普通下雨变化
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
		// 发送世界加载提示
		player.sendMessage(Text.literal("§b[云语铃音] §f天气监测已启动"), false);

		// 检测并发送当前天气状态
		if (isThundering) {
			triggerEvent("enter_thundering", player, world);
		} else if (isRaining) {
			triggerEvent("enter_raining", player, world);
		}

		// 更新状态
		lastRainingState = isRaining;
		lastThunderingState = isThundering;
	}

	@Unique
	private void triggerEvent(String eventName, ClientPlayerEntity player, ClientWorld world) {
		// 检查事件是否启用
		if (!ConfigManager.isEventEnabled(eventName)) return;

		// 获取随机消息
		String message = ConfigManager.getRandomMessage(eventName);
		player.sendMessage(Text.literal(message), false);

		// 获取随机音效并播放
		String soundFile = ConfigManager.getRandomSound(eventName);
		if (soundFile != null) {
			playCustomSound(player, world, soundFile);
		}
	}

	@Unique
	private void playCustomSound(ClientPlayerEntity player, ClientWorld world, String soundFile) {
		// 去除文件扩展名
		String soundName = soundFile.replace(".ogg", "");
		SoundEvent sound = lire.cloudchime.sound.CustomSoundManager.getSound(soundName);

		if (sound == null) {
			System.err.println("[云语铃音] 音效未注册: " + soundName);
			return;
		}

		world.playSound(
				player,
				player.getX(), player.getY(), player.getZ(),
				sound,
				SoundCategory.AMBIENT,
				1.0F,
				0.8F + world.random.nextFloat() * 0.4F
		);
	}
}