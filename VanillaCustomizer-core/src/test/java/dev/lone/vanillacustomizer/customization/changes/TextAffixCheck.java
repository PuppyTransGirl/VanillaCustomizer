package dev.lone.vanillacustomizer.customization.changes;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public class TextAffixCheck
{
    public static void main(String[] args)
    {
        Component original = Component.text("Firstlore").color(NamedTextColor.RED);
        Component result = TextAffix.decorate(original, Component.text("AAA"), Component.text("BBB"));
        if (!PlainTextComponentSerializer.plainText().serialize(result).equals("AAAFirstloreBBB"))
            throw new AssertionError("Affix text missing");
        if (!result.children().contains(original))
            throw new AssertionError("Original formatting lost");
        Component repeated = TextAffix.decorate(result, Component.text("AAA"), Component.text("BBB"));
        if (!PlainTextComponentSerializer.plainText().serialize(repeated).equals("AAAFirstloreBBB"))
            throw new AssertionError("Affixes duplicated on a second pass");
        System.out.println("Text affix checks passed");
    }
}
