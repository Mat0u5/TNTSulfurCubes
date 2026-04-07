package net.mat0u5.tntsulfurcubes.mixin;

import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SulfurCubeArchetype;
import net.minecraft.world.entity.monster.cubemob.SulfurCube;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(SulfurCube.class)
public class SulfurCubeMixin {
	@Inject(at = @At("HEAD"), method = "isSwallowableItem", cancellable = true)
	private static void tntSwallowable(ItemStack itemStack, CallbackInfoReturnable<Boolean> cir) {
		if (itemStack.is(Items.TNT)) {
			cir.setReturnValue(true);
		}
	}

	@Inject(method = "matchingArchetypes", at = @At("RETURN"), cancellable = true)
	private void injectTNTArchetype(ItemStack stack, CallbackInfoReturnable<List<SulfurCubeArchetype>> cir) {
		if (!stack.is(Items.TNT)) return;

		SulfurCube self = (SulfurCube)(Object)this;

		List<SulfurCubeArchetype> result = new ArrayList<>(cir.getReturnValue());

		self.level().registryAccess()
				.lookupOrThrow(Registries.SULFUR_CUBE_ARCHETYPE)
				.stream()
				.filter(arch -> new ItemStack(Items.DIRT).is(arch.items()))
				.forEach(result::add);

		cir.setReturnValue(result);
	}

	@Inject(method = "hurtServer", at = @At("HEAD"))
	public void explodeOnExplosionDamage(ServerLevel par1, DamageSource source, float par3, CallbackInfoReturnable<Boolean> cir) {
		SulfurCube self = (SulfurCube)(Object)this;
		if (!(self.level() instanceof ServerLevel serverLevel)) return;
		if (source.is(DamageTypes.EXPLOSION)|| source.is(DamageTypes.PLAYER_EXPLOSION)) {
			serverLevel.explode(
					self,
					self.getX(), self.getY(), self.getZ(),
					4,
					Level.ExplosionInteraction.TNT
			);
			self.discard();
		}
	}
}