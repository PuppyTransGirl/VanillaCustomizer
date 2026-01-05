package dev.lone.vanillacustomizer.customization.changes;

import beer.devs.fastnbt.nms.nbt.NItem;
import dev.lone.vanillacustomizer.ChangeSession;
import dev.lone.vanillacustomizer.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class LoreSetJson implements IChange
{
    private final List<String> linesJson;

    public LoreSetJson(List<String> linesJson)
    {
        this.linesJson = Utils.fixJsonFormatting(linesJson);
    }

    @Override
    public void apply(ChangeSession session)
    {
        NItem nbt = session.nbt();
        List<Object> loreNMS = new ArrayList<>();
        for (String line : linesJson)
            loreNMS.add(Utils.jsonToNMS(IChange.replacePlaceholders(session, line)));
        nbt.setLore(loreNMS);
        nbt.save();
    }
}
