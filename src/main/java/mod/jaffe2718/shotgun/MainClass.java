package mod.jaffe2718.shotgun;

import mod.jaffe2718.shotgun.init.EntityInit;
import mod.jaffe2718.shotgun.init.ItemInit;
import mod.jaffe2718.shotgun.init.SoundInit;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(MainClass.MODID)
public class MainClass {
    public static final String MODID = "shotgun";

    IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

    public MainClass()
    {
        SoundInit.REGISTRY.register(bus);
        EntityInit.REGISTRY.register(bus);
        ItemInit.REGISTRY.register(bus);
        //modelOverrides();
    }
}
