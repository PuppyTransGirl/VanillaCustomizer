package dev.lone.vanillacustomizer.customization.changes;

import beer.devs.fastnbt.nms.nbt.NItem;
import dev.lone.vanillacustomizer.ChangeSession;
import dev.lone.vanillacustomizer.utils.Comp;
import dev.lone.vanillacustomizer.utils.ConfigFile;
import dev.lone.vanillacustomizer.utils.Utils;
import net.kyori.adventure.text.Component;

import java.util.ArrayList;
import java.util.List;

public class LoreSet implements IChange
{
    private final List<String> lines;

    public LoreSet(List<String> lines)
    {
        this.lines = ConfigFile.getColored(lines);
    }

    @Override
    public void apply(ChangeSession session)
    {
        NItem nbt = session.nbt();
        List<Object> loreNMS = new ArrayList<>();
        for (String line : lines)
        {
            String json = Comp.legacyToJson(IChange.replacePlaceholders(session, line));
            Component component = Utils.jsonToComponent(json);
            Object nms = Comp.componentToNms(component);
            loreNMS.add(nms);
        }
        nbt.setLore(loreNMS);
        nbt.save();
    }
}
