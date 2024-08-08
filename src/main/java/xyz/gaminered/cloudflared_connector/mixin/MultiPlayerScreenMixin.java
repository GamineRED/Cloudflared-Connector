package xyz.gaminered.cloudflared_connector.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.option.ServerList;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import xyz.gaminered.cloudflared_connector.access.IServerInfoMixin;
import xyz.gaminered.cloudflared_connector.access.IServerListMixin;

@Mixin(MultiplayerScreen.class)
public class MultiPlayerScreenMixin extends Screen {
	protected MultiPlayerScreenMixin() {super(Text.empty());}

	@Shadow private ServerInfo selectedEntry;

	@ModifyArg(method = "connect(Lnet/minecraft/client/network/ServerInfo;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/multiplayer/ConnectScreen;connect(Lnet/minecraft/client/gui/screen/Screen;Lnet/minecraft/client/MinecraftClient;Lnet/minecraft/client/network/ServerAddress;Lnet/minecraft/client/network/ServerInfo;ZLnet/minecraft/client/network/CookieStorage;)V"), index = 2)
	private ServerAddress modifyAddress(ServerAddress address, @Local(argsOnly = true) ServerInfo serverInfo) {
		return ((IServerInfoMixin) serverInfo).cloudflared$getUseCloudflared() ? new ServerAddress("127.0.0.1", address.getPort()) : address;
	}

	@Redirect(method = "directConnect", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/ServerList;get(Ljava/lang/String;)Lnet/minecraft/client/network/ServerInfo;", ordinal = 0))
	private ServerInfo getServerinfo(ServerList serverList, String address) {
		return ((IServerListMixin) serverList).cloudflared$get(address, ((IServerInfoMixin) this.selectedEntry).cloudflared$getUseCloudflared());
	}
}
