package dev.lone.vanillacustomizer;

import dev.lone.vanillacustomizer.customization.Customization;
import org.bukkit.entity.Player;

import java.lang.reflect.Proxy;
public class IgnoreCheck
{
    public static void main(String[] args)
    {
        Customizations customizations = new Customizations();
        Customization first = new Customization(false, false, null);
        first.addRule(item -> true);
        customizations.customizations.put("first", first);

        Customization ignore = new Customization(false, false, null);
        ignore.ignore = true;
        ignore.addRule(item -> true);
        customizations.customizations.put("ignore", ignore);

        Player player = (Player) Proxy.newProxyInstance(Player.class.getClassLoader(), new Class<?>[]{Player.class},
                (proxy, method, arguments) -> null);
        if (!customizations.isIgnored(new ChangeSession(null, player, false)))
            throw new AssertionError("Matching ignore rule was missed after another customization");
        System.out.println("Ignore rule check passed");
    }
}
