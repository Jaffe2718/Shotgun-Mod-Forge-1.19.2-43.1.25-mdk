package mod.jaffe2718.shotgun.entity;

import com.mojang.logging.LogUtils;
import mod.jaffe2718.shotgun.init.SoundInit;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;
import org.slf4j.Logger;

@OnlyIn(value = Dist.CLIENT, _interface = ItemSupplier.class)
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
    @OnlyIn(Dist.CLIENT)
    public void tick()
    {
        if (this.inGround) this.discard();
        if (this.getOwner() != null){     //  射程 75米
            Entity owner = this.getOwner();
            if (Math.sqrt(Math.pow(this.getX()-owner.getX(),2) + Math.pow(this.getY()-owner.getY(),2) + Math.pow(this.getZ()-owner.getZ(),2)) > 75.0D){
                this.level.broadcastEntityEvent(this, (byte)3);
                this.remove(Entity.RemovalReason.KILLED);
                //System.out.print("Kill");
            }
        }
        //this.level.addParticle();
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
        if (this.getOwner() != null){
            this.getOwner().level.addParticle(ParticleTypes.SCRAPE,this.getX(),this.getY(),this.getZ(),0,0,0);
        }
        if (!this.level.isClientSide) {
            this.level.broadcastEntityEvent(this, (byte)3);
            this.remove(Entity.RemovalReason.KILLED);
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult)
    {
        super.onHitEntity(entityHitResult);
        if (!this.level.isClientSide) {
            this.level.broadcastEntityEvent(this, (byte)3);
            this.remove(Entity.RemovalReason.KILLED);
        }
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
