package dev.lone.vanillacustomizer.customization.changes;

import beer.devs.fastnbt.nms.nbt.NItem;
import dev.lone.vanillacustomizer.ChangeSession;
import dev.lone.vanillacustomizer.utils.Comp;
import dev.lone.vanillacustomizer.utils.ConfigFile;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import java.util.List;

public class TextAffix implements IChange
{
    private final boolean lore;
    private final String prefix;
    private final String suffix;

    public TextAffix(boolean lore, String prefix, String suffix)
    {
        this.lore = lore;
        this.prefix = ConfigFile.convertColor(prefix);
        this.suffix = ConfigFile.convertColor(suffix);
    }

    @Override
    public void apply(ChangeSession session)
    {
        NItem nbt = session.nbt();
        if (lore)
        {
            List<Object> lines = nbt.getLoreCopy();
            if (lines == null)
                return;
            for (int i = 0; i < lines.size(); i++)
                lines.set(i, Comp.componentToNms(decorate(Comp.nmsToComponent(lines.get(i)), session)));
            nbt.setLore(lines);
        }
        else
        {
            Object name = nbt.getCustomName();
            if (name == null)
                name = nbt.getItemName();
            Component component = Comp.nmsToComponent(name);
            if (component == null)
                return;
            nbt.setCustomName(Comp.componentToJson(decorate(component, session)));
        }
        nbt.save();
    }

    private Component decorate(Component original, ChangeSession session)
    {
        return decorate(original,
                Comp.text(IChange.replacePlaceholders(session, prefix)),
                Comp.text(IChange.replacePlaceholders(session, suffix)));
    }

    static Component decorate(Component original, Component prefix, Component suffix)
    {
        PlainTextComponentSerializer plain = PlainTextComponentSerializer.plainText();
        String text = plain.serialize(original);
        // ponytail: A naturally matching edge is treated as already added; use a persistent marker if provenance matters.
        if (!text.startsWith(plain.serialize(prefix)))
            original = prefix.append(original);
        if (!text.endsWith(plain.serialize(suffix)))
            original = original.append(suffix);
        return original;
    }
}
