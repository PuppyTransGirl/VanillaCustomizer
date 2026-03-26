package dev.lone.vanillacustomizer.nms;

import beer.devs.fastnbt.nms.Version;
import dev.lone.vanillacustomizer.Main;
import dev.lone.vanillacustomizer.utils.Msg;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;

/**
 * Utility to initialize NMS wrappers and avoid Maven circular dependency problems.
 */
public class NMS
{
    /**
     * Gets a suitable implementaion for the current Minecraft server version.
     *
     * @param implClazz   the interface class which every implementation implements.
     * @param nmsHolder   the wrapper which holds NMS methods as Singleton utility.
     * @param ignoreError If loading errors should be ignored, for example if no implementation exists. This is hacky but can
     *                    be useful in some cases.
     * @return the correct implementation.
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public static <T> T findImplementation(@SuppressWarnings("unused") Class<T> implClazz, Object nmsHolder, boolean ignoreError)
    {
        String nmsVersion = Version.get().name();

        try
        {
            Class<?> implClass = Class.forName(nmsHolder.getClass().getPackage().getName() + ".impl." + nmsVersion);
            try
            {
                //To fix a bug: For example implementations of IBlockInfo class have BlockInfo has first param of the constructor.
                return (T) implClass.getDeclaredConstructor(nmsHolder.getClass()).newInstance(nmsHolder);
            }
            catch (NoSuchMethodException e)
            {
                return (T) implClass.getDeclaredConstructor().newInstance();
            }
        }
        catch (ClassNotFoundException | InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e)
        {
            if (ignoreError)
                return null;

            Msg.error("Error getting implementation for " + nmsHolder.getClass() + " - NMS " + nmsVersion);
            e.printStackTrace();

            Bukkit.getPluginManager().disablePlugin(Main.inst());
            Bukkit.shutdown();
        }
        return null;
    }
}
