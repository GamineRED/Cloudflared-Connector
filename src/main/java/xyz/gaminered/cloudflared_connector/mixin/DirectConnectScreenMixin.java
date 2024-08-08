package xyz.gaminered.cloudflared_connector.mixin;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.DirectConnectScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.gaminered.cloudflared_connector.access.IServerInfoMixin;

@Mixin(DirectConnectScreen.class)
public class DirectConnectScreenMixin extends Screen {
	@Shadow @Final private ServerInfo serverEntry;
	@Unique private boolean isCloudflaredEnable = false;

	private DirectConnectScreenMixin() {super(Text.empty());}

	@Unique
	private static Text getCloudflaredButtonText(boolean isCloudflaredEnable) {
		return Text.translatable(isCloudflaredEnable ? "options.on" : "options.off");
	}

	@Inject(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/multiplayer/DirectConnectScreen;addDrawableChild(Lnet/minecraft/client/gui/Element;)Lnet/minecraft/client/gui/Element;", ordinal = 0))
	private void initTail(CallbackInfo ci) {
		addDrawableChild(new TextWidget(this.width / 2 - 100, this.height / 4 + 96 - 12, 100, 20, Text.literal("Use Cloudflared"), this.textRenderer));

		addDrawableChild(
				ButtonWidget.builder(getCloudflaredButtonText(this.isCloudflaredEnable), button -> button.setMessage(getCloudflaredButtonText(this.isCloudflaredEnable = !this.isCloudflaredEnable)))
						.dimensions(this.width / 2, this.height / 4 + 96 - 12, 100, 20)
						.build());
	}

	@Inject(method = "saveAndClose", at = @At(value = "HEAD"))
	private void saveAndCloseGetTextAssign(CallbackInfo ci) {
		((IServerInfoMixin) this.serverEntry).cloudflared$setUseCloudflared(this.isCloudflaredEnable);
	}
}
