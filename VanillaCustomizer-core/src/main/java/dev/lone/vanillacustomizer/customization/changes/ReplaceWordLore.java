package dev.lone.vanillacustomizer.customization.changes;

import beer.devs.fastnbt.nms.nbt.NItem;
import dev.lone.vanillacustomizer.ChangeSession;
import dev.lone.vanillacustomizer.utils.Comp;
import dev.lone.vanillacustomizer.utils.ConfigFile;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;

import java.util.List;

public class ReplaceWordLore implements IChange
{
    TextReplacementConfig textReplacement;

    public ReplaceWordLore(String from, String to)
    {
        from = ConfigFile.convertColor(from);
        to = ConfigFile.convertColor(to);

        textReplacement = TextReplacementConfig.builder()
                .match(from)
                .replacement(to)
                .build();
    }

    @Override
    public void apply(ChangeSession session)
    {
        NItem nbt = session.nbt();
        List<Object> loreNMS = nbt.getLoreCopy();
        if(loreNMS == null)
            return;

        for (int i = 0; i < loreNMS.size(); i++)
        {
            Object lineNMS = loreNMS.get(i);
            Component component = Comp.nmsToComponent(lineNMS);
            component = component.replaceText(textReplacement);
            loreNMS.set(i, Comp.componentToNms(component));
        }
        nbt.setLore(loreNMS);
        nbt.save();
    }
}
