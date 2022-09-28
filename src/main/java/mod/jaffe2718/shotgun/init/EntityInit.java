package mod.jaffe2718.shotgun.init;

import mod.jaffe2718.shotgun.MainClass;
import mod.jaffe2718.shotgun.entity.GrapeshotEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

//@Mod.EventBusSubscriber(modid = MainClass.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EntityInit
{
    public static final DeferredRegister<EntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MainClass.MODID);
    public static final RegistryObject<EntityType<GrapeshotEntity>> GRAPESHOT = REGISTRY.register("grapeshot_entity",
            () -> EntityType.Builder.<GrapeshotEntity>of(GrapeshotEntity::new, MobCategory.MISC)
                    .sized(0.75F, 0.75F)
                    .setShouldReceiveVelocityUpdates(true)
                    .updateInterval(1)
                    .setTrackingRange(64)
                    .noSave()
                    .build(new ResourceLocation(MainClass.MODID, "grapeshot").toString()));
}
