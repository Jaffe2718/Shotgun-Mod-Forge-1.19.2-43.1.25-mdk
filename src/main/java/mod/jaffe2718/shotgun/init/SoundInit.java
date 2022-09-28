package mod.jaffe2718.shotgun.init;

import mod.jaffe2718.shotgun.MainClass;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = MainClass.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SoundInit {
    public static final DeferredRegister<SoundEvent> REGISTRY = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, MainClass.MODID);

    public static final RegistryObject<SoundEvent> GRAPESHOT_HIT = load_sound("grapeshot_hit");
    public static final RegistryObject<SoundEvent> SHOTGUN_CAN_LOAD = load_sound("shotgun_can_load");
    public static final RegistryObject<SoundEvent> SHOTGUN_LOADED = load_sound("shotgun_loaded");
    public static final RegistryObject<SoundEvent> SHOTGUN_LOADING = load_sound("shotgun_loading");
    public static final RegistryObject<SoundEvent> SHOTGUN_SHOT = load_sound("shotgun_shot");

    private static RegistryObject<SoundEvent> load_sound(String id) {
        return REGISTRY.register(id, () -> new SoundEvent(new ResourceLocation(MainClass.MODID, id)));
    }
}
