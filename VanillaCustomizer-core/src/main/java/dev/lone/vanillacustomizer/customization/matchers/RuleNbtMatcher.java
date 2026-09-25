package dev.lone.vanillacustomizer.customization.matchers;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTItem;
import de.tr7zw.changeme.nbtapi.NBTType;
import dev.lone.vanillacustomizer.customization.rules.IRule;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.DoublePredicate;

public class RuleNbtMatcher implements IRule
{
    final String nbtPath;
    final Object nbtValue;
    final List<Object> values = new ArrayList<>();
    final List<DoublePredicate> bounds = new ArrayList<>();

    String[] nbtPathSplit;

    NBTType nbtValueType;

    public RuleNbtMatcher(String nbtPath, Object nbtValue, String nbtValueTypeStr)
    {
        if (nbtPath == null || nbtPath.isBlank() || nbtValue == null)
            throw new IllegalArgumentException("nbt.path and nbt.value are required.");
        this.nbtPath = nbtPath;

        String nbtTypeFixed = "NBTTag" + StringUtils.capitalize(nbtValueTypeStr.toLowerCase());

        try
        {
            nbtValueType = NBTType.valueOf(nbtTypeFixed);
        }
        catch (IllegalArgumentException exc)
        {
            throw new IllegalArgumentException("Unknown nbt.type '" + nbtValueTypeStr + "' for nbt path '" + nbtPath + "'." +
                    ChatColor.GRAY + " Allowed: string, int, float, double, byte, short");
        }

        List<?> entries = nbtValue instanceof List<?> ? (List<?>) nbtValue : List.of(nbtValue);
        if (entries.isEmpty())
            throw new IllegalArgumentException("nbt.value must contain at least one value for '" + nbtPath + "'.");
        for (Object entry : entries)
        {
            String value = String.valueOf(entry).trim();
            if (nbtValueType != NBTType.NBTTagString && value.matches("^(>=|<=|>|<).+"))
            {
                int operatorLength = value.charAt(1) == '=' ? 2 : 1;
                String operator = value.substring(0, operatorLength);
                double threshold = Double.parseDouble(value.substring(operatorLength).trim());
                bounds.add(actual -> switch (operator)
                {
                    case ">=" -> actual >= threshold;
                    case "<=" -> actual <= threshold;
                    case ">" -> actual > threshold;
                    default -> actual < threshold;
                });
            }
            else
            {
                values.add(switch (nbtValueType)
                {
                    case NBTTagInt -> Integer.parseInt(value);
                    case NBTTagFloat -> Float.parseFloat(value);
                    case NBTTagDouble -> Double.parseDouble(value);
                    case NBTTagByte -> Byte.parseByte(value);
                    case NBTTagShort -> Short.parseShort(value);
                    default -> String.valueOf(entry);
                });
            }
        }
        this.nbtValue = nbtValue instanceof List<?> || values.isEmpty() ? nbtValue : values.get(0);

        this.nbtPathSplit = nbtPath.split("\\.");
    }

    public String getNbtPath()
    {
        return nbtPath;
    }

    public Object getNbtValue()
    {
        return nbtValue;
    }

    @Override
    public boolean matches(ItemStack item)
    {
        NBTItem nbt = new NBTItem(item);
        NBTCompound currentCompound = nbt;
        for (int i = 0; i < nbtPathSplit.length - 1; i++)
        {
            if (!currentCompound.hasTag(nbtPathSplit[i]))
                return false;
            currentCompound = currentCompound.getCompound(nbtPathSplit[i]);
            if (currentCompound == null)
                return false;
        }

        if (!currentCompound.hasTag(nbtPathSplit[nbtPathSplit.length - 1]))
            return false;

        NBTType nbtType = currentCompound.getType(nbtPathSplit[nbtPathSplit.length - 1]);
        if (nbtType != nbtValueType)
            return false;

        Object actual = switch (nbtType)
        {
            case NBTTagString -> currentCompound.getString(nbtPathSplit[nbtPathSplit.length - 1]);
            case NBTTagInt -> currentCompound.getInteger(nbtPathSplit[nbtPathSplit.length - 1]);
            case NBTTagDouble -> currentCompound.getDouble(nbtPathSplit[nbtPathSplit.length - 1]);
            case NBTTagFloat -> currentCompound.getFloat(nbtPathSplit[nbtPathSplit.length - 1]);
            case NBTTagByte -> currentCompound.getByte(nbtPathSplit[nbtPathSplit.length - 1]);
            case NBTTagShort -> currentCompound.getShort(nbtPathSplit[nbtPathSplit.length - 1]);
            default -> null;
        };
        return matchesValue(actual);
    }

    boolean matchesValue(Object actual)
    {
        if (actual == null || (!values.isEmpty() && !values.contains(actual)))
            return false;
        if (!bounds.isEmpty())
        {
            if (!(actual instanceof Number))
                return false;
            double number = ((Number) actual).doubleValue();
            for (DoublePredicate bound : bounds)
                if (!bound.test(number))
                    return false;
        }
        return true;
    }
}
