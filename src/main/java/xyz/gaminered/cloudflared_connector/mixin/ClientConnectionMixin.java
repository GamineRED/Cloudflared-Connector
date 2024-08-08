package xyz.gaminered.cloudflared_connector.mixin;

import net.minecraft.network.ClientConnection;
import net.minecraft.network.DisconnectionInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.gaminered.cloudflared_connector.CloudflaredConnector;

@Mixin(ClientConnection.class)
public class ClientConnectionMixin {
	@Inject(method = "disconnect(Lnet/minecraft/network/DisconnectionInfo;)V", at = @At("TAIL"))
	private void disconnectTail(DisconnectionInfo disconnectionInfo, CallbackInfo ci) {
		Process cloudflaredProcess = CloudflaredConnector.getCloudflaredProcess();
		if (cloudflaredProcess == null) return;
		cloudflaredProcess.destroy();
		CloudflaredConnector.setCloudflaredProcess(null);
	}
}
