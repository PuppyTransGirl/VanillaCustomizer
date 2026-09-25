package dev.lone.vanillacustomizer.customization.matchers;

import java.util.Arrays;

public class RuleNbtMatcherCheck
{
    public static void main(String[] args)
    {
        check(new RuleNbtMatcher("itemsadder.id", Arrays.asList("spiritual_sand", "abnormal_slab"), "string").matchesValue("abnormal_slab"));
        check(!new RuleNbtMatcher("itemsadder.id", Arrays.asList("spiritual_sand", "abnormal_slab"), "string").matchesValue("stone"));
        RuleNbtMatcher range = new RuleNbtMatcher("ItemDamage.Value", Arrays.asList(">=15.5", "<25"), "int");
        check(range.matchesValue(16));
        check(!range.matchesValue(15));
        check(!range.matchesValue(25));
        RuleNbtMatcher scalar = new RuleNbtMatcher("x.y", "7", "int");
        check(scalar.matchesValue(7));
        check(scalar.getNbtValue().equals(7));
        System.out.println("NBT matcher checks passed");
    }

    private static void check(boolean result)
    {
        if (!result)
            throw new AssertionError("NBT matcher check failed");
    }
}
