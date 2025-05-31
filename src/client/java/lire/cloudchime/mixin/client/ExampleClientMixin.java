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
	private static final Text RAIN_START = Text.literal("§b[天气预警] §c开始下雨了！");
	@Unique
	private static final Text RAIN_STOP = Text.literal("§b[天气预警] §a雨停了！");

	@Inject(method = "tick", at = @At("TAIL"))
	private void onWorldTick(CallbackInfo ci) {
		ClientWorld world = (ClientWorld)(Object)this;
		boolean isRaining = world.isRaining();

		if (isRaining != lastRainingState) {
			ClientPlayerEntity player = MinecraftClient.getInstance().player;
			if (player != null) {

				player.sendMessage(isRaining ? RAIN_START : RAIN_STOP, false);


				player.playSound(
						isRaining ?
								CloudChime.RAIN_START_SOUND :
								CloudChime.RAIN_STOP_SOUND,
						1.0F,
						1.0F
				);
			}
			System.out.println("[天气预警] 检测到天气变化: " + (isRaining ? "下雨" : "晴天"));
			lastRainingState = isRaining;
		}
	}
}