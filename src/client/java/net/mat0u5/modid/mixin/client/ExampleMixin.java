package net.mat0u5.modid.mixin.client;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class ExampleMixin {
	@Inject(at = @At("HEAD"), method = "setLevel")
	private void init(CallbackInfo info) {
		System.out.println("[ExampleMixin] Client joined level");
	}
}