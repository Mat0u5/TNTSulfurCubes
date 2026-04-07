package net.mat0u5.tntsulfurcubes.mixin;

import net.mat0u5.tntsulfurcubes.IEntityBounce;
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
public class EntityMixin implements IEntityBounce {

    @Unique
    private Vec3 tsc$preBounceVel = Vec3.ZERO;
    @Unique
    private int tsc$ticksDontExplode = 0;
    @Unique
    private int tsc$explodeFuse = -1;

    @Override
    public void tsc$setDontExplodeTicks(int ticks) {
        tsc$ticksDontExplode = ticks;
    }
    @Override
    public void tsc$setFuse(int ticks) {
        tsc$explodeFuse = ticks;
    }

    @Override
    public int tsc$getFuse() {
        return tsc$explodeFuse;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tick(CallbackInfo ci) {
        Entity self = (Entity)(Object)this;
        if (self.isRemoved()) return;
        if (!(self instanceof SulfurCube cube) || !cube.hasBodyItem()) return;
        ItemStack item = cube.getBodyArmorItem();
        if (item == null) return;
        if (!item.is(Items.TNT)) return;
        if (!(self.level() instanceof ServerLevel serverLevel)) return;

        if (tsc$ticksDontExplode > 0) {
            tsc$ticksDontExplode--;
        }
        if (tsc$explodeFuse > 0) {
            tsc$explodeFuse--;
            if (tsc$explodeFuse == 0) {
                self.discard();
                serverLevel.explode(
                        self,
                        self.getX(), self.getY(), self.getZ(),
                        4,
                        Level.ExplosionInteraction.TNT
                );
            }
        }
    }

    @Inject(method = "restituteMovementAfterCollisions", at = @At("HEAD"))
    private void capturePreBounce(BlockState effectState, boolean xCollision, boolean zCollision, Vec3 movement, CallbackInfo ci) {
        tsc$preBounceVel = ((Entity)(Object)this).getDeltaMovement();
    }

    @Inject(method = "restituteMovementAfterCollisions", at = @At("TAIL"))
    private void onBounce(BlockState effectState, boolean xCollision, boolean zCollision, Vec3 movement, CallbackInfo ci) {
        if (tsc$ticksDontExplode > 0) return;
        Entity self = (Entity)(Object)this;
        if (self.isRemoved()) return;
        if (!(self instanceof SulfurCube cube) || !cube.hasBodyItem()) return;
        ItemStack item = cube.getBodyArmorItem();
        if (item == null) return;
        if (!item.is(Items.TNT)) return;
        if (!(self.level() instanceof ServerLevel serverLevel)) return;

        Vec3 pre = tsc$preBounceVel;
        Vec3 post = self.getDeltaMovement();

        double strength = pre.subtract(post).length();

        if (strength > 0.6) {
            self.discard();
            serverLevel.explode(
                    self,
                    self.getX(), self.getY(), self.getZ(),
                    (float)(Math.min(2, strength) * 4),
                    Level.ExplosionInteraction.TNT
            );
        }
    }
}
