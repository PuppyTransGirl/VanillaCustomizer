package dev.lone.vanillacustomizer.customization.changes;

import beer.devs.fastnbt.nms.nbt.NItem;
import dev.lone.vanillacustomizer.ChangeSession;

public class ReplaceCustomModelData implements IChange
{
    private final int id;

    public ReplaceCustomModelData(int id)
    {
        this.id = id;
    }

    @Override
    public void apply(ChangeSession session)
    {
        NItem nbt = session.nbt();
        nbt.setInt("CustomModelData", id);

        session.saveNbt();
    }
}
