package net.mat0u5.tntsulfurcubes.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.cubemob.SulfurCube;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityMixin {

    @Unique
    private Vec3 tsc$preBounceVel = Vec3.ZERO;

    @Inject(method = "restituteMovementAfterCollisions", at = @At("HEAD"))
    private void capturePreBounce(BlockState effectState, boolean xCollision, boolean zCollision, Vec3 movement, CallbackInfo ci) {
        tsc$preBounceVel = ((Entity)(Object)this).getDeltaMovement();
    }

    @Inject(method = "restituteMovementAfterCollisions", at = @At("TAIL"))
    private void onBounce(BlockState effectState, boolean xCollision, boolean zCollision, Vec3 movement, CallbackInfo ci) {
        Entity self = (Entity)(Object)this;
        if (!(self instanceof SulfurCube cube) || !cube.hasBodyItem()) return;
        ItemStack item = cube.getBodyArmorItem();
        if (item == null) return;
        if (!item.is(Items.TNT)) return;
        if (!(self.level() instanceof ServerLevel serverLevel)) return;

        Vec3 pre = tsc$preBounceVel;
        Vec3 post = self.getDeltaMovement();

        double strength = pre.subtract(post).length();

        if (strength > 0.6) {
            serverLevel.explode(
                    self,
                    self.getX(), self.getY(), self.getZ(),
                    (float)(strength * 4),
                    Level.ExplosionInteraction.TNT
            );
            self.discard();
        }
    }
}
