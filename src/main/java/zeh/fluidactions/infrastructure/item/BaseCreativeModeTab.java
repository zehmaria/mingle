package zeh.fluidactions.infrastructure.item;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

public class BaseCreativeModeTab extends CreateCreativeModeTab {
    public BaseCreativeModeTab() {
        super("base");
    }

    @Override
    public @NotNull ItemStack makeIcon() {
        return Items.BUCKET.getDefaultInstance();
    }
}