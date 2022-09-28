package mod.jaffe2718.shotgun.init.modelinit;

import mod.jaffe2718.shotgun.MainClass;
import mod.jaffe2718.shotgun.init.ItemInit;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ShotgunModelOverrides {
    private static final RegistryObject<Item> SHOTGUN = ItemInit.SHOTGUN;

    @SubscribeEvent(priority = EventPriority.LOWEST)  //EventPriority.LOWEST 必须是最后注册
    public static void modelOverrides(FMLLoadCompleteEvent event)   // 运行不了就改public
    {                           // 注册模型覆盖的变量
        event.enqueueWork( () -> {
            ItemProperties.register(SHOTGUN.get(), new ResourceLocation(MainClass.MODID, "loading"),
                (itemStack, clientWorld, livingEntity, timeLeft) -> {
                    if (livingEntity != null) {
                        if (livingEntity.isUsingItem() && itemStack.is(SHOTGUN.get())) {
                            if (itemStack.getTag() == null || itemStack.getTag().getBoolean("Charged")) return 0;
                            else return 1.0F;
                        } else return 0;
                    } else return 0;
                });

            ItemProperties.register(SHOTGUN.get(), new ResourceLocation(MainClass.MODID, "load_process"), (itemStack, clientWorld, livingEntity, timeLeft) -> {
                if (livingEntity != null && livingEntity.isUsingItem() && itemStack.getTag() != null && itemStack.is(SHOTGUN.get())) {
                    return (float) (itemStack.getUseDuration() - livingEntity.getUseItemRemainingTicks()) / itemStack.getUseDuration();
                } else return 0;
            });

            ItemProperties.register(SHOTGUN.get(), new ResourceLocation(MainClass.MODID, "loaded"), (itemStack, clientWorld, livingEntity, timeLeft) -> {
                if (itemStack.getTag() != null && itemStack.is(SHOTGUN.get())) {
                    if (itemStack.getTag().getBoolean("Charged")) return 1.0F;
                    else return 0.0F;
                } else return 0;
            });
        });
    }
}
