package mod.jaffe2718.shotgun.entity;

import com.mojang.logging.LogUtils;
import mod.jaffe2718.shotgun.init.SoundInit;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;
import org.slf4j.Logger;

import java.util.Random;

//@OnlyIn(value = Dist.DEDICATED_SERVER, _interface = ItemSupplier.class)
public class GrapeshotEntity extends AbstractArrow implements ItemSupplier
{
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final float BASE_DAMAGE = 0.5F;

    private final SoundEvent SOUNDEVENT = SoundInit.GRAPESHOT_HIT.get();

    public AbstractArrow.Pickup pickup = AbstractArrow.Pickup.DISALLOWED;

    //private static final EntityDataAccessor<Integer> ID_EFFECT_COLOR = SynchedEntityData.defineId(EntityMo.class, EntityDataSerializers.INT);

    public GrapeshotEntity(EntityType<GrapeshotEntity> entityIn, double vx, double vy, double vz, Level world) {
        super(entityIn, vx, vy, vz, world);
    }

    public GrapeshotEntity(EntityType<GrapeshotEntity> entityIn, LivingEntity entity, Level world) {
        super(entityIn, entity, world);
    }

    public GrapeshotEntity(EntityType<GrapeshotEntity> entityIn, Level world) {
        super(entityIn, world);
    }

    @Override
    //@OnlyIn(Dist.CLIENT)
    public void tick()
    {
        if (this.inGround) this.discard();
        if (this.getOwner() != null){
            Entity owner = this.getOwner();
            double distance = Math.sqrt(Math.pow(this.getX() - owner.getX(), 2)
                                        + Math.pow(this.getY() - owner.getY(), 2)
                                        + Math.pow(this.getZ() - owner.getZ(), 2));
            if (distance > 25.0D && distance <= 75.0D) {    // 伤害衰减算法
                this.setBaseDamage(1.0D - distance * 0.01D);
            }else if (distance > 75.0D){   //  射程 75米
                this.level.broadcastEntityEvent(this, (byte)3);
                this.remove(Entity.RemovalReason.KILLED);
            }
        }
        //this.findHitEntity()
//        System.out.print(this.level.isClientSide());
//        Random random = new Random();
//        Vec3 v3 = this.getDeltaMovement();
//        for (int i=0; i<4; i++){
//            this.level.addParticle(ParticleTypes.ASH,
//                this.getX() + random.nextFloat(),this.getY()+ random.nextFloat(), this.getZ() + random.nextFloat(),
//                random.nextFloat() , random.nextFloat(), random.nextFloat());
//        }
        super.tick();
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected ItemStack getPickupItem() {
        return new ItemStack(Items.GUNPOWDER);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public ItemStack getItem() {
        return new ItemStack(Blocks.POLISHED_BLACKSTONE_BUTTON);
    }

    @Override
    public SoundEvent getDefaultHitGroundSoundEvent() {
        return this.SOUNDEVENT;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putDouble("damage", this.BASE_DAMAGE);
    }

    @Override
    protected void onHitBlock(BlockHitResult blockHitResult) {  // 当子弹撞到方块时执行
        super.onHitBlock(blockHitResult);
        this.setSilent(false);
        this.setSoundEvent(this.SOUNDEVENT);
        this.level.playSound((Player) null, this.getX(), this.getY(), this.getZ(), this.SOUNDEVENT, SoundSource.VOICE, 0.06F, 2F);
        if (!this.level.isClientSide) {
            this.level.broadcastEntityEvent(this, (byte)3);
            this.remove(Entity.RemovalReason.KILLED);
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult)
    {
        if (this.getOwner() != null){
            Entity owner = this.getOwner();
            double distance = Math.sqrt(Math.pow(this.getX() - owner.getX(), 2)
                    + Math.pow(this.getY() - owner.getY(), 2)
                    + Math.pow(this.getZ() - owner.getZ(), 2));
            if (distance > 25.0D && distance <= 75.0D) {    // 伤害衰减算法
                this.setBaseDamage(1.0D - distance * 0.01D);
            }
        }
        super.onHitEntity(entityHitResult);
        if (!this.level.isClientSide) {
            this.level.broadcastEntityEvent(this, (byte)3);
            this.remove(Entity.RemovalReason.KILLED);
        }
    }

    @Override
    protected void onHit(HitResult hitResult){
        Random rd = new Random();
        if (this.level instanceof ServerLevel _serverLevel) {
            Vec3 pos = hitResult.getLocation();
            if (hitResult instanceof BlockHitResult && !this.isInWater() && !this.isInLava()) {     // 击中墙壁就落灰
                for (int i = 0; i < 4; i++) {
                    _serverLevel.sendParticles(ParticleTypes.ASH,
                            pos.x + rd.nextFloat(), pos.y + rd.nextFloat(), pos.z + rd.nextFloat(),
                            0,
                            rd.nextFloat(), rd.nextFloat(), rd.nextFloat(), 0);
                    //_serverLevel.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK,).setPos(pos))
                }
            } else if (hitResult instanceof EntityHitResult entityHitResult && this.getOwner() != null) {   // 是否会心一击
                Entity owner = this.getOwner();
                double distance = Math.sqrt(Math.pow(owner.getX() - pos.x, 2)
                                            + Math.pow(owner.getY() - pos.y, 2)
                                            + Math.pow(owner.getZ() - pos.z, 2));
                if ((distance < 3.0D) && (rd.nextInt(70) == 19)){     // 3米以内，会心一击,伤害75（37.5心）, 单子弹概率1/70
                    Entity target = entityHitResult.getEntity();
                    target.hurt(DamageSource.arrow(this, owner), 75.0F);
                    for (int i = 0; i < 5; i++) {
                        _serverLevel.sendParticles(ParticleTypes.DAMAGE_INDICATOR,
                                target.getX() + 2.5 * rd.nextFloat(),
                                target.getY() + 2.5 * rd.nextFloat(),
                                target.getZ() + 2.5 * rd.nextFloat(),
                                0,
                                rd.nextFloat(), rd.nextFloat(), rd.nextFloat(), 0);
                        _serverLevel.sendParticles(ParticleTypes.FLAME,
                                target.getX() + 2.5 * rd.nextFloat(),
                                target.getY() + 2.5 * rd.nextFloat(),
                                target.getZ() + 2.5 * rd.nextFloat(),
                                0,
                                rd.nextFloat(), rd.nextFloat(), rd.nextFloat(), 0);
                    }
                }
            }
        }
        super.onHit(hitResult);
    }

    @Override
    protected void doPostHurtEffects(LivingEntity entity) {
        super.doPostHurtEffects(entity);
        entity.setArrowCount(entity.getArrowCount() - 1);
    }

    @Override
    public void shoot(double p_36775_, double p_36776_, double p_36777_, float p_36778_, float p_36779_) {
        super.shoot(p_36775_, p_36776_, p_36777_, p_36778_, p_36779_);
    }
}
