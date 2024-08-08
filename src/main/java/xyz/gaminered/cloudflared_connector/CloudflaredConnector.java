package xyz.gaminered.cloudflared_connector;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CloudflaredConnector {
	public static final String MOD_ID = "cloudflared-connector";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static @Nullable Process cloudflaredProcess;
	public static @Nullable Process getCloudflaredProcess() {
		return CloudflaredConnector.cloudflaredProcess;
	}
	public static void setCloudflaredProcess(@Nullable Process cloudflaredProcess) {
		CloudflaredConnector.cloudflaredProcess = cloudflaredProcess;
	}
}
