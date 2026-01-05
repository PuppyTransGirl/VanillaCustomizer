package dev.lone.vanillacustomizer.customization.changes;

import beer.devs.fastnbt.nms.nbt.NItem;
import dev.lone.vanillacustomizer.ChangeSession;

public class ProtectNbtData implements IChange
{
    @Override
    public void apply(ChangeSession session)
    {
        NItem nbt = session.nbt();
        nbt.remove("PublicBukkitValues");
        nbt.remove("itemsadder");

        session.saveNbt();
    }
}
