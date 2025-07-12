package lire.cloudchime.mixin.client;

import lire.cloudchime.CloudChime;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
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

	// 修改为状态初始化标志
	@Unique
	private boolean statesInitialized = false;

	@Unique
	private static final Text RAIN_START = Text.literal("§b[云语铃音] §9开始下雨了...");
	@Unique
	private static final Text RAIN_STOP = Text.literal("§b[云语铃音] §a雨过天晴!");
	@Unique
	private static final Text THUNDER_START = Text.literal("§6[云语铃音] §c雷暴来袭! 注意安全!");
	@Unique
	private static final Text THUNDER_STOP = Text.literal("§6[云语铃音] §e雷暴已减弱为小雨");

	// 添加当前天气状态消息
	@Unique
	private static final Text CURRENT_THUNDER = Text.literal("§6[云语铃音] §c当前正在雷暴中!");
	@Unique
	private static final Text CURRENT_RAIN = Text.literal("§b[云语铃音] §9当前正在下雨...");

	// 添加世界加载提示
	@Unique
	private static final Text WORLD_LOADED = Text.literal("§b[云语铃音] §f天气监测已启动");

	@Inject(method = "tick", at = @At("TAIL"))
	private void onWorldTick(CallbackInfo ci) {
		ClientWorld world = (ClientWorld)(Object)this;
		ClientPlayerEntity player = MinecraftClient.getInstance().player;

		// 玩家未就绪时跳过
		if (player == null) return;

		boolean isRaining = world.isRaining();
		boolean isThundering = world.isThundering();

		// 首次tick初始化状态并发送世界加载提示
		if (!statesInitialized) {
			sendWorldLoadedMessage(player);

			// 检测并发送当前天气状态（带音效）
			if (isThundering) {
				playThunderSound(player, world);
				player.sendMessage(CURRENT_THUNDER, false);
				System.out.println("[云语铃音] 当前天气: 雷暴");
			} else if (isRaining) {
				playRainStartSound(player, world);
				player.sendMessage(CURRENT_RAIN, false);
				System.out.println("[云语铃音] 当前天气: 下雨");
			}

			lastRainingState = isRaining;
			lastThunderingState = isThundering;
			statesInitialized = true;
			return;
		}

		// 优先检测雷暴变化
		if (isThundering != lastThunderingState) {
			handleThunderChange(world, isThundering);
			lastThunderingState = isThundering;
			lastRainingState = isRaining; // 同步更新下雨状态
			return; // 跳过下雨检测
		}

		// 检测普通下雨变化
		if (isRaining != lastRainingState) {
			handleRainChange(world, isRaining);
			lastRainingState = isRaining;
		}
	}

	@Unique
	private void sendWorldLoadedMessage(ClientPlayerEntity player) {
		player.sendMessage(WORLD_LOADED, false);
		System.out.println("[云语铃音] 世界加载完成，天气监测已启动");
	}

	@Unique
	private void handleRainChange(ClientWorld world, boolean isRaining) {
		ClientPlayerEntity player = MinecraftClient.getInstance().player;
		if (player != null) {
			player.sendMessage(isRaining ? RAIN_START : RAIN_STOP, false);

			// 根据天气变化播放不同音效
			if (isRaining) {
				playRainStartSound(player, world);
			} else {
				playRainStopSound(player, world);
			}

			System.out.println("[云语铃音] 天气变化: " + (isRaining ? "开始下雨" : "雨停转晴"));
		}
	}

	@Unique
	private void handleThunderChange(ClientWorld world, boolean isThundering) {
		ClientPlayerEntity player = MinecraftClient.getInstance().player;
		if (player != null) {
			player.sendMessage(isThundering ? THUNDER_START : THUNDER_STOP, false);

			// 雷暴变化时播放雷声音效
			playThunderSound(player, world);

			System.out.println("[云语铃音] 天气变化: " + (isThundering ? "雷暴开始" : "雷暴停止"));
		}
	}

	// ==== 音效播放方法 ====

	@Unique
	private void playRainStartSound(ClientPlayerEntity player, ClientWorld world) {
		player.playSound(
				CloudChime.RAIN_START_SOUND,
				0.8F,
				0.9F + world.random.nextFloat() * 0.2F
		);
	}

	@Unique
	private void playRainStopSound(ClientPlayerEntity player, ClientWorld world) {
		player.playSound(
				CloudChime.RAIN_STOP_SOUND,
				0.8F,
				0.9F + world.random.nextFloat() * 0.2F
		);
	}

	@Unique
	private void playThunderSound(ClientPlayerEntity player, ClientWorld world) {
		player.playSound(
				CloudChime.THUNDER_SOUND,
				1.0F,
				0.8F + world.random.nextFloat() * 0.4F
		);
	}
}