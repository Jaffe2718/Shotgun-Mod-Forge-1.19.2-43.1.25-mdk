package mod.jaffe2718.shotgun.item;

import mod.jaffe2718.shotgun.entity.GrapeshotEntity;
import mod.jaffe2718.shotgun.init.EntityInit;
import mod.jaffe2718.shotgun.init.SoundInit;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Random;
import java.util.function.Predicate;

public class ShotgunItem extends CrossbowItem
{
    private static final float BASE_POWER = 10.0F;
    private static final float BASE_DAMAGE = 1.0F;

    public ShotgunItem()
    {
        super(new Item.Properties().tab(CreativeModeTab.TAB_COMBAT).durability(100));
    }

    public static boolean isLoaded(ItemStack itemStack) {
        CompoundTag compoundtag = itemStack.getTag();
        return compoundtag != null && compoundtag.getBoolean("Charged");
    }

    public static void setLoaded(ItemStack itemStack, boolean option) {
        CompoundTag compoundtag = itemStack.getOrCreateTag();
        compoundtag.putBoolean("Charged", option);
    }

    public static void setLoading(ItemStack itemStack, boolean option) {
        CompoundTag compoundtag = itemStack.getOrCreateTag();
        compoundtag.putBoolean("Loading", option);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand)
    {
        ItemStack itemstack = player.getItemInHand(hand);
        if (!player.isInWater()) {                                       // ???????????????
            if (isLoaded(itemstack)) {                                   // ????????????????????????????????????
                shotgunPerformShooting(world, player, itemstack);        // ????????????
                if(!player.isCreative()) {                               // ??????????????????
                    if (new Random().nextInt(5) >= itemstack.getEnchantmentLevel(Enchantments.UNBREAKING)){
                        ItemStack _ist = itemstack;
                        if(_ist.hurt(1, RandomSource.create(),null)) {
                            _ist.shrink(1);
                            _ist.setDamageValue(0);}
                    }
                }
                return InteractionResultHolder.consume(itemstack);
            }
            else if (!player.getProjectile(itemstack).isEmpty()) {       // ?????????
                if (!isLoaded(itemstack)) {                              // ???????????????????????????
                    setLoading(itemstack, true);
                    player.startUsingItem(hand);
                }
                return new InteractionResultHolder(InteractionResult.SUCCESS, player.getItemInHand(hand));
            } else {                                                     // ????????????
                return InteractionResultHolder.fail(itemstack);
            }
        } else {                                                         // ????????????
            world.playSound(player, player.getX(), player.getY(), player.getZ(), SoundInit.SHOTGUN_DAMP.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
            Vec3 view = player.getLookAngle();
            Random rd = new Random();
            for (int i = 0; i < 5; i++) {                                // ???????????????????????????
                world.addParticle(ParticleTypes.BUBBLE,
                        player.getX() + view.x,
                        player.getEyeY() - 0.1F + view.y,
                        player.getZ() + view.z,
                        view.x + rd.nextFloat() -0.5F,
                        view.y + rd.nextFloat() -0.5F,
                        view.z + rd.nextFloat() -0.5F);
                world.addParticle(ParticleTypes.BUBBLE_POP,
                        player.getX() + view.x,
                        player.getEyeY() - 0.1F + view.y,
                        player.getZ() + view.z,
                        view.x + rd.nextFloat() -0.5F,
                        view.y + rd.nextFloat() -0.5F,
                        view.z + rd.nextFloat() -0.5F);
            }
            player.swing(hand);
            player.getCooldowns().addCooldown(itemstack.getItem(), 10);
            return InteractionResultHolder.fail(itemstack);
        }

    }

    @Override
    public void onUseTick(Level world, LivingEntity livingEntity, ItemStack itemStack, int timeLeft)
    {
        if (timeLeft == getUseDuration(itemStack) - 1)
            world.playSound((Player) null, livingEntity, SoundInit.SHOTGUN_LOADING.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
        else if (timeLeft == 0)
            world.playSound((Player) null, livingEntity, SoundInit.SHOTGUN_CAN_LOAD.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
    }

    @Override
    public void releaseUsing(ItemStack itemStack, Level world, LivingEntity livingEntity, int timeLeft) {
        setLoading(itemStack, false);
        if (itemStack.is(this) && !isLoaded(itemStack) && timeLeft <= 0)
        {
            if (livingEntity instanceof Player _player && !is_creative(livingEntity)){ // ????????????
                //System.out.print("use gunpowder");
                _player.getInventory().clearOrCountMatchingItems(p -> Items.GUNPOWDER == p.getItem(),1,_player.inventoryMenu.getCraftSlots());
            }
            setLoaded(itemStack, true);
            world.playSound((Player) null, livingEntity, SoundInit.SHOTGUN_LOADED.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
        }
    }

    @Override
    public Predicate<ItemStack> getSupportedHeldProjectiles() {return GUNPOWER_ONLY;}

    @Override
    public Predicate<ItemStack> getAllSupportedProjectiles() {return GUNPOWER_ONLY;}

    @Override  // ????????????????????????
    public int getUseDuration(ItemStack itemStack) {return 30 - 4 * itemStack.getEnchantmentLevel(Enchantments.QUICK_CHARGE);}

    @Override  // ?????????????????????
    public UseAnim getUseAnimation(ItemStack p_40935_) {return UseAnim.CROSSBOW;}

    private static final Predicate<ItemStack> GUNPOWER_ONLY = (itemStack) -> {return itemStack.is(Tags.Items.GUNPOWDER);};

    private boolean is_creative(Entity _ent) {    // ???????????????????????????
        if (_ent instanceof ServerPlayer _serverPlayer)
        {
            return _serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
        }
        else if (_ent.level.isClientSide() && _ent instanceof Player _player)
        {
            return Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()) != null && Minecraft
                    .getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE;
        }
        else return false;
    }

    private void uncertaintyShoot(Entity _shootFrom, float power, float damage, int knockback, byte piercing, float uncertainty) {
        // ??????????????????????????????????????????????????????
        Level projectileLevel = _shootFrom.level;
        Projectile _entityToSpawn = new Object() {
            public Projectile getArrow(Level level, Entity entity, float damage, int knockback, byte piercing) {
                AbstractArrow entityToSpawn = new GrapeshotEntity(EntityInit.GRAPESHOT.get(), level);
                //System.out.print(entityToSpawn.getId());
                entityToSpawn.setOwner(entity);
                entityToSpawn.setBaseDamage(damage);
                entityToSpawn.setKnockback(knockback);
                entityToSpawn.setSilent(true);
                entityToSpawn.setPierceLevel(piercing);
                return entityToSpawn;
            }
        }.getArrow(projectileLevel, _shootFrom, damage, knockback, piercing);
        _entityToSpawn.setPos(_shootFrom.getX(), _shootFrom.getEyeY() - 0.1F, _shootFrom.getZ());
        _entityToSpawn.shoot(_shootFrom.getLookAngle().x, _shootFrom.getLookAngle().y, _shootFrom.getLookAngle().z, power, uncertainty);
        projectileLevel.addFreshEntity(_entityToSpawn);
    }

    public void shotgunPerformShooting(Level world, Player player, ItemStack itemStack) {
        //  ?????????????????????Server?????????
        Vec3 view = player.getLookAngle();
        byte piercing = (byte) (itemStack.getEnchantmentLevel(Enchantments.PIERCING) + 1);
        if (!world.isClientSide() && player instanceof ServerPlayer _player){
            for (int _index = 0; _index < 7 + itemStack.getEnchantmentLevel(Enchantments.PIERCING); _index++) {    // ????????????????????????????????????????????????????????????????????????
                uncertaintyShoot(player, BASE_POWER + 1.5F * itemStack.getEnchantmentLevel(Enchantments.PIERCING), BASE_DAMAGE, 1, piercing, 7.0F);
            }
        }
        world.playSound((Player) null, player, SoundInit.SHOTGUN_SHOT.get(), SoundSource.PLAYERS, 4F, 1F);
        setLoaded(itemStack, false);
        player.setXRot(player.getXRot() - 5.5F - 2.0F * (float) Math.random());        // ???????????????
        player.setYRot(player.getYRot() + ((float)Math.random() - 0.5F) * 6);          // ???????????????
        world.addParticle(ParticleTypes.LARGE_SMOKE,                                   // ??????????????????
                player.getX()+view.x,
                player.getEyeY()-0.1F+view.y,
                player.getZ()+view.z,
                0,0,0);
        if (itemStack.getEnchantmentLevel(Enchantments.MULTISHOT) != 0) {              // ??????????????????
            player.getCooldowns().addCooldown(itemStack.getItem(), 15);
            new Object() {                                                             // ???????????????????????????????????????????????????12tick?????????start
                private int ticks = 0;
                private float waitTicks;

                private Player user;
                private Vec3 _view;
                private LevelAccessor world;

                public void start(LevelAccessor world, Player _player, int waitTicks) {
                    this.waitTicks = waitTicks;
                    this.user = _player;
                    MinecraftForge.EVENT_BUS.register(this);
                    this.world = world;
                }

                @SubscribeEvent
                public void tick(TickEvent.ServerTickEvent event) {
                    if (event.phase == TickEvent.Phase.END) {
                        this.ticks += 1;
                        this._view = user.getLookAngle();
                        if (this.ticks == this.waitTicks)
                            run();
                    }
                }

                private void run() {
                    for (int index = 0; index < 7; index++) {
                        uncertaintyShoot(user, BASE_POWER, BASE_DAMAGE, 1, piercing, 7.0F);
                    }
                    user.playSound(SoundInit.SHOTGUN_SHOT.get(), 3.5F,1.0F);
                    user.setXRot(user.getXRot() - 5.5F - 2.0F * (float) Math.random());        // ???????????????
                    user.setYRot(user.getYRot() + ((float)Math.random() - 0.5F) * 6);          // ???????????????
                    world.addParticle(ParticleTypes.LARGE_SMOKE,                               // ??????????????????
                            user.getX()+_view.x,
                            user.getEyeY()-0.1F+_view.y,
                            user.getZ()+_view.z, 0,0,0);
                }
            }.start(world, player, 12);
        }
    }

}

