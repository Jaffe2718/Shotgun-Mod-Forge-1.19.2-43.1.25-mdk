package mod.jaffe2718.shotgun.item;

import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTab;

public class CarbideAlloyItem extends Item {
    public CarbideAlloyItem() {
        super(new Item.Properties().tab(CreativeModeTab.TAB_MISC).stacksTo(64).rarity(Rarity.COMMON));
    }
}
