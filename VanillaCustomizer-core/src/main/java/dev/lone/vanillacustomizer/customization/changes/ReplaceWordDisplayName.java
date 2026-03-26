package dev.lone.vanillacustomizer.customization.changes;

import beer.devs.fastnbt.nms.nbt.NItem;
import dev.lone.vanillacustomizer.ChangeSession;
import dev.lone.vanillacustomizer.utils.Comp;
import dev.lone.vanillacustomizer.utils.ConfigFile;

public class ReplaceWordDisplayName implements IChange
{
    private final String from;
    private final String to;

    public ReplaceWordDisplayName(String from, String to)
    {
        this.from = ConfigFile.convertColor(from);
        this.to = ConfigFile.convertColor(to);
    }

    @Override
    public void apply(ChangeSession session)
    {
        if(session.refreshMeta() == null)
            return;
        if(!session.refreshMeta().hasDisplayName())
            return;

        String name = session.refreshMeta().getDisplayName().replace(from, to);

        NItem nbt = session.nbt();
        nbt.setCustomName(Comp.legacyToJson(name));
        nbt.save();
    }
}
