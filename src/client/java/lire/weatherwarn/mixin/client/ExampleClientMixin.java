package lire.weatherwarn.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientWorld.class)
public class ExampleClientMixin {
	private boolean lastRainingState = false;
	private static final Text RAIN_START = Text.literal("§b[天气预警] §c开始下雨了！");
	private static final Text RAIN_STOP = Text.literal("§b[天气预警] §a雨停了！");

	@Inject(method = "tick", at = @At("TAIL"))
	private void onWorldTick(CallbackInfo ci) {
		ClientWorld world = (ClientWorld)(Object)this;
		boolean isRaining = world.isRaining();

		if (isRaining != lastRainingState) {
			if (MinecraftClient.getInstance().player != null) {
				MinecraftClient.getInstance().player.sendMessage(
						isRaining ? RAIN_START : RAIN_STOP,
						false
				);
			}
			System.out.println("[天气预警] 检测到天气变化: " + (isRaining ? "下雨" : "晴天"));
			lastRainingState = isRaining;
		}
	}
}