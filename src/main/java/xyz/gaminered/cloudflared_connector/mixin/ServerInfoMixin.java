package xyz.gaminered.cloudflared_connector.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.gaminered.cloudflared_connector.access.IServerInfoMixin;

@Mixin(ServerInfo.class)
public class ServerInfoMixin implements IServerInfoMixin {
	@Unique
	private boolean isUseCloudflared;

	@Override
	public boolean cloudflared$getUseCloudflared() {
		return this.isUseCloudflared;
	}

	@Override
	public void cloudflared$setUseCloudflared(boolean isUse) {
		this.isUseCloudflared = isUse;
	}

	@Inject(method = "toNbt", at = @At("RETURN"))
	private void toNbtReturn(CallbackInfoReturnable<NbtCompound> cir, @Local NbtCompound nbtCompound) {
		if (this.isUseCloudflared) nbtCompound.putBoolean("useCloudflared", true);
	}

	@Inject(method = "fromNbt", at = @At("RETURN"))
	private static void fromNbtReturn(NbtCompound root, CallbackInfoReturnable<ServerInfo> cir, @Local ServerInfo serverInfo) {
		if (root.contains("useCloudflared", NbtElement.BYTE_TYPE)) {
			((IServerInfoMixin) serverInfo).cloudflared$setUseCloudflared(root.getBoolean("useCloudflared"));
		}
	}

	@Inject(method = "copyFrom", at = @At("RETURN"))
	private void copyFromReturn(ServerInfo serverInfo, CallbackInfo ci) {
		this.isUseCloudflared = ((IServerInfoMixin) serverInfo).cloudflared$getUseCloudflared();
	}
}
