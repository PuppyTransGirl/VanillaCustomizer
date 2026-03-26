package dev.lone.vanillacustomizer.customization.changes;

import beer.devs.fastnbt.nms.nbt.NItem;
import dev.lone.vanillacustomizer.ChangeSession;
import dev.lone.vanillacustomizer.utils.Comp;
import dev.lone.vanillacustomizer.utils.ConfigFile;
import org.bukkit.ChatColor;

public class Renamer implements IChange
{
    private final String name;

    public Renamer(String name)
    {
        name = ConfigFile.convertColor(name);
        this.name = !name.startsWith("&f") ? ChatColor.WHITE + name : name;
    }

    @Override
    public void apply(ChangeSession session)
    {
        NItem nbt = session.nbt();
        nbt.setCustomName(Comp.legacyToJson(IChange.replacePlaceholders(session, name)));
        nbt.save();
    }
}
