package xyz.gaminered.cloudflared_connector.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.client.network.CookieStorage;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.gaminered.cloudflared_connector.CloudflaredConnector;
import xyz.gaminered.cloudflared_connector.access.IServerInfoMixin;
import xyz.gaminered.cloudflared_connector.util.CloudflaredHandler;

@Mixin(ConnectScreen.class)
public abstract class ConnectScreenMixin {
	@Shadow public abstract void setStatus(Text status);

	@Inject(method = "connect(Lnet/minecraft/client/MinecraftClient;Lnet/minecraft/client/network/ServerAddress;Lnet/minecraft/client/network/ServerInfo;Lnet/minecraft/client/network/CookieStorage;)V", at = @At("HEAD"))
	private void connectHead(MinecraftClient client, ServerAddress address, ServerInfo info, CookieStorage cookieStorage, CallbackInfo ci) {
		if (!((IServerInfoMixin) info).cloudflared$getUseCloudflared()) return;
		this.setStatus(Text.literal("Cloudflared立ち上げ中"));
	}
}

@Mixin(targets = "net.minecraft.client.gui.screen.multiplayer.ConnectScreen$1")
abstract class ConnectScreenConnectThreadMixin {
	@Shadow @Final ServerInfo field_40415;
	@Shadow @Final MinecraftClient field_33738;
	@Shadow @Final ConnectScreen field_2416;
	@Shadow @Final ServerAddress field_33737;
	@Shadow @Final CookieStorage field_48396;

	@Inject(method = "run", at = @At("HEAD"), cancellable = true)
	private void runHead(CallbackInfo ci) {
		if (((IServerInfoMixin) this.field_40415).cloudflared$getUseCloudflared()) {
			if (!CloudflaredHandler.isCloudflaredInstalled()) {
				this.field_33738.execute(() -> this.field_33738.setScreen(new DisconnectedScreen(this.field_2416.parent, this.field_2416.failureErrorMessage, Text.literal("Cloudflared is not installed"))));
				ci.cancel();
			}

			Process cloudflaredProcess = CloudflaredHandler.accessTcpTunnel(this.field_40415.address.split(":", 2)[0], this.field_33737.getPort());
			if (cloudflaredProcess == null) {
				this.field_33738.execute(() -> this.field_33738.setScreen(new DisconnectedScreen(this.field_2416.parent, this.field_2416.failureErrorMessage, Text.literal("Cloudflared立ち上がらなかった"))));
				ci.cancel();
			}

			CloudflaredConnector.setCloudflaredProcess(cloudflaredProcess);

			if (this.field_48396 == null) this.field_2416.setStatus(Text.translatable("connect.connecting"));
			else this.field_2416.setStatus(Text.translatable("connect.transferring"));
		}
	}

	@Inject(method = "run", at = @At("RETURN"))
	private void runReturn(CallbackInfo ci) {
		if (this.field_2416.connectingCancelled && CloudflaredConnector.getCloudflaredProcess() != null) {
			CloudflaredConnector.getCloudflaredProcess().destroy();
			CloudflaredConnector.setCloudflaredProcess(null);
		}
	}

	@Inject(method = "run", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;execute(Ljava/lang/Runnable;)V"))
	private void runDisconnected(CallbackInfo ci) {
		if (CloudflaredConnector.getCloudflaredProcess() == null) return;
		CloudflaredConnector.getCloudflaredProcess().destroy();
		CloudflaredConnector.setCloudflaredProcess(null);
	}
}
