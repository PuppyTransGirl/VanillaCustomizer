package dev.lone.vanillacustomizer.customization.changes;

import beer.devs.fastnbt.nms.nbt.NItem;
import dev.lone.vanillacustomizer.ChangeSession;
import dev.lone.vanillacustomizer.utils.Utils;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class LoreInsertJson implements IChange
{
    private final List<String> linesJson;
    private final int index;

    public LoreInsertJson(List<String> linesJson, int index)
    {
        this.linesJson = Utils.fixJsonFormatting(linesJson);
        this.index = index;
    }

    @Override
    public void apply(ChangeSession session)
    {
        NItem nbt = session.nbt();

        List<Object> newLinesNMS = new ArrayList<>();
        for (String lineJson : linesJson)
            newLinesNMS.add(Utils.jsonToNMS(IChange.replacePlaceholders(session, lineJson)));

        @Nullable List<Object> loreNMS = nbt.getLoreCopy();
        if(loreNMS == null)
        {
            loreNMS = new ArrayList<>(newLinesNMS);
        }
        else
        {
            if (index < loreNMS.size())
                loreNMS.addAll(index, newLinesNMS);
            else
                loreNMS.addAll(newLinesNMS);
        }

        nbt.setLore(loreNMS);
        nbt.save();
    }
}
