package zeh.fluidactions.foundation.data;

import com.tterrag.registrate.AbstractRegistrate;
import net.minecraftforge.eventbus.api.IEventBus;
import org.jetbrains.annotations.NotNull;

public class Enroll extends AbstractRegistrate<Enroll> {

    protected Enroll(String modid) {
        super(modid);
    }

    public static Enroll create(String modid) {
        return new Enroll(modid);
    }

    @Override
    public @NotNull Enroll registerEventListeners(@NotNull IEventBus bus) {
        return super.registerEventListeners(bus);
    }

}