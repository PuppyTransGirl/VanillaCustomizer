package dev.lone.vanillacustomizer.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import dev.lone.vanillacustomizer.annotations.Expensive;
import net.kyori.adventure.platform.bukkit.BukkitComponentSerializer;
import net.kyori.adventure.platform.bukkit.MinecraftComponentSerializer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.ParsingException;
import net.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.json.JSONOptions;
import net.kyori.adventure.text.serializer.json.legacyimpl.NBTLegacyHoverEventSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.option.OptionState;
import net.md_5.bungee.api.chat.BaseComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Utility wrapper to call useful Adventure API methods.
 * NOTE: This is using the builtin LoneLibs Adventure API, not Paper builtin one.
 */
@SuppressWarnings({"deprecation", "UnnecessaryUnicodeEscape"})
public class Comp
{
    public static GsonComponentSerializer GSON_SERIALIZER;
    public static final MiniMessage MINIMESSAGE = MiniMessage.miniMessage();

    public static TextColor WHITE = TextColor.color(255, 255, 255);
    private static final Style ITALIC_STYLE_FALSE = Style.style(builder -> {
        builder.decoration(TextDecoration.ITALIC, false);
    });
    public static Style WHITE_NORMAL_TEXT_STYLE = Style.style(builder -> {
        builder.color(WHITE);
        builder.decoration(TextDecoration.ITALIC, false);
    });

    public static final String EMPTY_JSON_COMPONENT = "{\"text\":\"\"}";

    public static final DecimalFormat DECIMAL_FORMAT_MINIMAL;
    public static final DecimalFormat DECIMAL_FORMAT_ENDING_ZEROES;
    static
    {
        DECIMAL_FORMAT_MINIMAL = new DecimalFormat("#.##");
        DECIMAL_FORMAT_MINIMAL.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ROOT));

        DECIMAL_FORMAT_ENDING_ZEROES = new DecimalFormat("0.0#");
        DECIMAL_FORMAT_ENDING_ZEROES.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ROOT));

        GSON_SERIALIZER = GsonComponentSerializer.builder()

                .legacyHoverEventSerializer(NBTLegacyHoverEventSerializer.get())
                .options(
                        OptionState.optionState()
                                // after 1.16
                                .value(JSONOptions.EMIT_RGB, Boolean.TRUE)
                                .value(JSONOptions.EMIT_HOVER_EVENT_TYPE, JSONOptions.HoverEventValueMode.MODERN_ONLY)
                                // after 1.20.3
                                .value(JSONOptions.EMIT_COMPACT_TEXT_COMPONENT, Boolean.TRUE)
                                .value(JSONOptions.EMIT_HOVER_SHOW_ENTITY_ID_AS_INT_ARRAY, Boolean.TRUE)
                                .value(JSONOptions.VALIDATE_STRICT_EVENTS, Boolean.TRUE)
                                .build()
                )
                .build();
    }

    /**
     * Converts legacy text to json. Supports also Spigot hex color notation.
     *
     * @param legacy a legacy text like: §cHello World!
     * @return json.
     */
    public static String legacyToJson(@NotNull String legacy)
    {
        return GSON_SERIALIZER.serialize(Comp.text(legacy));
    }

    /**
     * Converts json to legacy text.
     *
     * @param json a json like: {"text":"Hello world!"}
     * @return legacy text.
     */
    public static String jsonToLegacy(String json) throws JsonSyntaxException
    {
        return LegacyComponentSerializer.legacy('\u00A7').serialize(jsonToComponent(json));
    }

    /**
     * Converts adventure component object to json.
     *
     * @param component adventure component object.
     * @return json.
     */
    public static String componentToJson(@NotNull Component component)
    {
        return GSON_SERIALIZER.serialize(component);
    }

    /**
     * Converts adventure component object to json.
     *
     * @param component adventure component object.
     * @return json.
     */
    public static @NotNull JsonElement componentToJsonTree(@NotNull Component component)
    {
        return GSON_SERIALIZER.serializeToTree(component);
    }

    /**
     * Converts json to adventure component object.
     *
     * @param json json.
     * @return adventure component object.
     */
    @SuppressWarnings("RedundantThrows")
    @NotNull
    public static Component jsonToComponent(String json) throws JsonSyntaxException
    {
        // If it's not json
        if(!json.startsWith("{"))
            return Component.text(json).style(WHITE_NORMAL_TEXT_STYLE);
        return GSON_SERIALIZER.deserialize(json);
    }

    @Expensive
    public static Object componentToNms(Component component)
    {
        return MinecraftComponentSerializer.get().serialize(component);
    }

    @Nullable
    public static Component nmsToComponent(Object nms)
    {
        if (nms == null)
            return null;
        return MinecraftComponentSerializer.get().deserialize(nms);
    }

    @Expensive
    public static BaseComponent[] componentToBukkit(Component component)
    {
        return BungeeComponentSerializer.legacy().serialize(component);
    }

    /**
     * Equivalent of {@link Component#text(String)}} but supports also Spigot hex color notation.
     * It surely is slower than {@link Component#text(String)}}.
     *
     * @param text a legacy text
     * @return text component
     */
    public static Component text(String text)
    {
        TextComponent component = BukkitComponentSerializer.legacy().deserialize(text);
        return component.decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);
    }

    /**
     * Equivalent of {@link Component#text(String)}} but supports also Spigot hex color notation.
     * It surely is slower than {@link Component#text(String)}}.
     *
     * @param text     a legacy text
     * @param noItalic forces no italic, useful for lores
     * @return text component
     */
    public static Component text(String text, boolean noItalic)
    {
        if (!noItalic)
            return BukkitComponentSerializer.legacy().deserialize(text);
        return BukkitComponentSerializer.legacy().deserialize(text).style(ITALIC_STYLE_FALSE);
    }

    public static boolean mightBeJson(String str)
    {
        return str.trim().startsWith("{");
    }

    public static String convertColor(String message)
    {
        return message.replace("&", "\u00A7");
    }

    public static TranslatableComponent translatable(String key)
    {
        TranslatableComponent component = Component.translatable(key);
        return (TranslatableComponent) component.decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);
    }

    public static Style.@org.jetbrains.annotations.NotNull Builder removePropertyIfDefault(Style style, Style.Builder builder, TextDecoration textDecoration, TextDecoration.State def)
    {
        if (style.decoration(textDecoration) == def)
            return builder.decoration(textDecoration, TextDecoration.State.NOT_SET);
        return builder.decoration(textDecoration, style.decoration(textDecoration));
    }

    public static Component optimize(Component component)
    {
        // Important! Compact it before editing the text decorations! compact() method seems to add useless text decorations=false for some reason.
        component = component.compact();

        // Remove default properties if their value is the default one
        Style style = component.style();
        Style.Builder builder = style.toBuilder();

        builder = Comp.removePropertyIfDefault(style, builder, TextDecoration.BOLD, TextDecoration.State.FALSE);
        builder = Comp.removePropertyIfDefault(style, builder, TextDecoration.UNDERLINED, TextDecoration.State.FALSE);
        builder = Comp.removePropertyIfDefault(style, builder, TextDecoration.STRIKETHROUGH, TextDecoration.State.FALSE);
        builder = Comp.removePropertyIfDefault(style, builder, TextDecoration.OBFUSCATED, TextDecoration.State.FALSE);
        // I do not remove ITALIC since lore lines and display name are ITALIC=true by default and I want to explicitely set
        // ITALIC=false in this case.

        return component.style(builder);
    }

    public static String legacy(Component component)
    {
        return LegacyComponentSerializer.legacy('\u00A7').serialize(component);
    }

    public static boolean isColored(Component component)
    {
        return component.color() != null;
    }

    public static String mergeLegacyToJson(String... strs)
    {
        Component comp = Component.text("");
        for (String str : strs)
        {
            comp = comp.append(Component.text(str));
        }
        return componentToJson(comp);
    }

    public static Component minimessage(String str)
    {
        // Has legacy HEX notation
        // https://github.com/PluginBugs/Issues-ItemsAdder/issues/3057
        Component text;
        if(str.contains("&#") || str.contains("\u00A7#"))
            str = Comp.convertColor(str);

        try
        {
            str = Comp.convertColor(str);
            text = MINIMESSAGE.deserialize(str);
        }
        catch (ParsingException ignored)
        {
            // Do not use both Minimessage and legacy formatting code in the same text
            text = Comp.text(str);
        }

        // To avoid italic when not wanted. Example: lore.
        return text.colorIfAbsent(Comp.WHITE).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);
    }

    public static String round(double value, boolean trailingZeros)
    {
        if(trailingZeros)
            return DECIMAL_FORMAT_ENDING_ZEROES.format(value);
        return DECIMAL_FORMAT_MINIMAL.format(value);
    }
}