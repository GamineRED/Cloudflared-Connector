package xyz.gaminered.cloudflared_connector.mixin;

import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.option.ServerList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xyz.gaminered.cloudflared_connector.access.IServerInfoMixin;
import xyz.gaminered.cloudflared_connector.access.IServerListMixin;

import java.util.List;

@Mixin(ServerList.class)
public class ServerListMixin implements IServerListMixin {
	@Shadow @Final private List<ServerInfo> servers;

	@Shadow @Final private List<ServerInfo> hiddenServers;

	@Override
	public ServerInfo cloudflared$get(String address, boolean useCloudflared) {
		for (ServerInfo serverInfo : this.servers) {
			if (serverInfo.address.equals(address) && ((IServerInfoMixin) serverInfo).cloudflared$getUseCloudflared() == useCloudflared) {
				return serverInfo;
			}
		}

		for (ServerInfo serverInfo : this.hiddenServers) {
			if (serverInfo.address.equals(address) && ((IServerInfoMixin) serverInfo).cloudflared$getUseCloudflared() == useCloudflared) {
				return serverInfo;
			}
		}

		return null;
	}
}
