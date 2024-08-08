package xyz.gaminered.cloudflared_connector.access;

public interface IServerInfoMixin {
	boolean cloudflared$getUseCloudflared();
	void cloudflared$setUseCloudflared(boolean isUse);
}
