package dev.lone.vanillacustomizer.customization.changes;

import beer.devs.fastnbt.nms.nbt.NItem;
import dev.lone.vanillacustomizer.ChangeSession;
import dev.lone.vanillacustomizer.utils.ConfigFile;
import dev.lone.vanillacustomizer.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class LoreInsert implements IChange
{
    private final List<String> lines;
    private final int index;

    public LoreInsert(List<String> lines, int index)
    {
        this.lines = ConfigFile.getColored(lines);
        this.index = index;
    }

    @Override
    public void apply(ChangeSession session)
    {
        NItem nbt = session.nbt();
        List<Object> loreNMS = nbt.getLoreCopy();
        if (loreNMS == null)
        {
            loreNMS = new ArrayList<>();
            for (String line : lines)
                loreNMS.add(Utils.jsonToNMS(IChange.replacePlaceholders(session, line)));
        }
        else
        {
            // If the index is correctly inside the already existing lore range I can put the new lines there.
            if (index < loreNMS.size())
            {
                int i = index;
                for (String line : lines)
                {
                    loreNMS.add(i, Utils.jsonToNMS(IChange.replacePlaceholders(session, line)));
                    i++;
                }
            }
            else // If it's out of bounds I just append at the end.
            {
                for (String line : lines)
                    loreNMS.add(Utils.jsonToNMS(IChange.replacePlaceholders(session, line)));
            }
        }

        nbt.setLore(loreNMS);
        nbt.save();
    }

    public static void putLine(ChangeSession session, int index, String line)
    {
        NItem nbt = session.nbt();
        List<Object> loreNMS = nbt.getLoreCopy();
        if (loreNMS == null)
        {
            loreNMS = new ArrayList<>();
            loreNMS.add(Utils.jsonToNMS(line));
        }
        else
        {
            loreNMS.add(index, Utils.jsonToNMS(line));
        }

        nbt.setLore(loreNMS);
        nbt.save();
    }
}
