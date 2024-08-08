package xyz.gaminered.cloudflared_connector.access;

import net.minecraft.client.network.ServerInfo;

public interface IServerListMixin {
	ServerInfo cloudflared$get(String address, boolean useCloudflared);
}
