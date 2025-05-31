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

	@Unique
	private static final Text RAIN_START = Text.literal("§b[云语铃音] §9开始下雨了...");
	@Unique
	private static final Text RAIN_STOP = Text.literal("§b[云语铃音] §a雨过天晴!");
	@Unique
	private static final Text THUNDER_START = Text.literal("§6[云语铃音] §c雷暴来袭! 注意安全!");
	@Unique
	private static final Text THUNDER_STOP = Text.literal("§6[云语铃音] §e雷暴已减弱为小雨");

	@Inject(method = "tick", at = @At("TAIL"))
	private void onWorldTick(CallbackInfo ci) {
		ClientWorld world = (ClientWorld)(Object)this;
		boolean isRaining = world.isRaining();
		boolean isThundering = world.isThundering();

		if (isThundering != lastThunderingState) {
			handleThunderChange(world, isThundering);
			lastThunderingState = isThundering;
		}
		else if (isRaining != lastRainingState) {
			handleRainChange(world, isRaining);
			lastRainingState = isRaining;
		}
	}

	@Unique
	private void handleRainChange(ClientWorld world, boolean isRaining) {
		ClientPlayerEntity player = MinecraftClient.getInstance().player;
		if (player != null) {
			player.sendMessage(isRaining ? RAIN_START : RAIN_STOP, false);
			player.playSound(
					isRaining ? CloudChime.RAIN_START_SOUND : CloudChime.RAIN_STOP_SOUND,
					0.8F,  // 音量
					0.9F + world.random.nextFloat() * 0.2F // 随机音调
			);
			System.out.println("[云语铃音] 天气变化: " + (isRaining ? "开始下雨" : "雨停转晴"));
		}
	}

	@Unique
	private void handleThunderChange(ClientWorld world, boolean isThundering) {
		ClientPlayerEntity player = MinecraftClient.getInstance().player;
		if (player != null) {
			player.sendMessage(isThundering ? THUNDER_START : THUNDER_STOP, false);
			player.playSound(
					CloudChime.THUNDER_SOUND,
					1.0F,  // 较大音量
					0.8F + world.random.nextFloat() * 0.4F // 随机音调
			);
			System.out.println("[云语铃音] 天气变化: " + (isThundering ? "雷暴开始" : "雷暴停止"));

			}
		}
	}
