package dev.lone.vanillacustomizer.customization.changes;

import beer.devs.fastnbt.nms.nbt.NItem;
import dev.lone.vanillacustomizer.ChangeSession;
import dev.lone.vanillacustomizer.utils.Utils;

public class RenamerJson implements IChange
{
    private final String json;

    public RenamerJson(String json)
    {
        // This should validate if the json is valid and throw an exception if not.
        // NOTE: test if it is actually the case.
        Utils.jsonToComponent(json);

        this.json = Utils.fixJsonFormatting(json);
    }

    @Override
    public void apply(ChangeSession session)
    {
        NItem nbt = session.nbt();
        String json = IChange.replacePlaceholders(session, this.json);
        nbt.setCustomName(json);
        nbt.save();
    }
}
