package mod.jaffe2718.shotgun.init;

import mod.jaffe2718.shotgun.MainClass;
import mod.jaffe2718.shotgun.item.CarbideAlloyItem;
import mod.jaffe2718.shotgun.item.ShotgunItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;


public class ItemInit {
    public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, MainClass.MODID);

    public static final RegistryObject<Item> SHOTGUN = REGISTRY.register("shotgun", ShotgunItem::new);// () -> new ShotgunItem(new Item.Properties()));
    public static final RegistryObject<Item> CARBIDEAllOY = REGISTRY.register("carbide_alloy", CarbideAlloyItem::new);
}
