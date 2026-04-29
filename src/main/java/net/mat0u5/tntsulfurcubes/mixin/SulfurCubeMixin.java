package net.mat0u5.tntsulfurcubes.mixin;

import net.mat0u5.tntsulfurcubes.IEntityBounce;
import net.minecraft.world.entity.monster.cubemob.SulfurCube;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SulfurCube.class)
public class SulfurCubeMixin {

	@Inject(method = "equipItem", at = @At("TAIL"))
	private void onEquip(ItemStack heldItem, CallbackInfoReturnable<Boolean> cir) {
		SulfurCube self = (SulfurCube)(Object)this;
		if (!cir.getReturnValue()) return; // equip failed, ignore
		if (!(self instanceof IEntityBounce accessor)) return;
		accessor.tsc$setDontExplodeTicks(25);
	}
}