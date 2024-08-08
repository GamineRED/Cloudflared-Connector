package xyz.gaminered.cloudflared_connector.util;

import org.jetbrains.annotations.Nullable;
import xyz.gaminered.cloudflared_connector.CloudflaredConnector;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class CloudflaredHandler {
	private static final String CLOUDFLARED = "cloudflared";

	public static boolean isCloudflaredInstalled() {
		try {
			return new ProcessBuilder(CLOUDFLARED, "-v").start().waitFor() == 0;
		} catch (InterruptedException | IOException e) {
			return false;
		}
	}

	static public @Nullable Process accessTcpTunnel(String tunnelUrl, int usePort) {
		try {
			Process process = new ProcessBuilder("cloudflared", "access", "tcp", "--hostname", tunnelUrl, "--url", "127.0.0.1:" + usePort).start();
			BufferedReader bufferedReader = process.errorReader();

			boolean hasError = false;
			do {
				String line = bufferedReader.readLine();
				if (!line.contains("INF")) {
					CloudflaredConnector.LOGGER.warn("Cloudflared log: {}", line);
					hasError = true;
				}
				process.waitFor(200, TimeUnit.MILLISECONDS);
			} while (bufferedReader.ready());

			if (hasError) {
				process.destroy();
				return null;
			}

			bufferedReader.close();
			return process;
		} catch (IOException | InterruptedException e) {
			return null;
		}
	}
}
