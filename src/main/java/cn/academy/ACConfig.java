package cn.academy;

import cn.academy.event.ConfigUpdateEvent;
import cn.academy.network.NetworkManager;
import cn.lambdalib2.registry.StateEventCallback;
import cn.lambdalib2.util.ReflectionUtils;
import cn.lambdalib2.util.ResourceUtils;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public final class ACConfig {
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface GetACCfgValue
    {
        String path();
        boolean check() default false;
        boolean reload() default true;
    }
    private static Map<String, Field> valList = new HashMap<>();
    private static Map<Class, BiConsumer<String, Field>> valReader = new HashMap<>();

    private ACConfig() {}

    private static Config config;

    private static String lastError = null;

    private static void __init() {
        Logger log = AcademyCraft.log;

        ResourceLocation defaultRes = new ResourceLocation("academy:config/default.conf");

        Reader reader = new InputStreamReader(ResourceUtils.getResourceStream(defaultRes));

        config = ConfigFactory.parseReader(reader);

        File customFile = new File("config/academy-craft-data.conf");
        if (!customFile.isFile()) {
            try {
                Files.copy(ResourceUtils.getResourceStream(defaultRes), customFile.toPath());
            } catch (IOException ex) {
                log.error("Error when copying config template to config folder", ex);
            }
        }

        try {
            Config customConfig = ConfigFactory.parseFile(customFile);

            config = customConfig.withFallback(config);
        } catch (RuntimeException ex) {
            log.error("An error occured parsing custom config", ex);
            lastError = ex.toString();
        }
        updateConfigValue();
    }

    public static void updateConfig(Config cfg)
    {
        if(cfg==null)
            __init();
        else {
            config = cfg;
            updateConfigValue();
        }
    }

    @StateEventCallback
    private static void preInit(FMLPreInitializationEvent event)
    {
        Logger log = AcademyCraft.log;
        List<Field> fields = ReflectionUtils.getFields(GetACCfgValue.class);
        fields.forEach(field -> {
            if(!Modifier.isStatic(field.getModifiers()))
                log.warn(String.format("Field %s must be static",field.getName()));
            field.setAccessible(true);
            GetACCfgValue anno = field.getAnnotation(GetACCfgValue.class);
            if(anno != null)
            {
                valList.put(anno.path(), field);
            }
        });
        MinecraftForge.EVENT_BUS.register(new LoginEvents());
    }

    public static void updateConfigValue()
    {
        MinecraftForge.EVENT_BUS.post(new ConfigUpdateEvent(ConfigUpdateEvent.Phase.START));
        for(Map.Entry<String, Field> entry : valList.entrySet())
        {
            Class kls = entry.getValue().getType();
            BiConsumer<String, Field> consumer = valReader.get(kls);
            if(consumer != null)
                consumer.accept(entry.getKey(), entry.getValue());
            else
                throw new RuntimeException(String.format("Cannot update config value \"%s\" from \"%s\"",
                        entry.getKey(), entry.getValue()));
        }
        MinecraftForge.EVENT_BUS.post(new ConfigUpdateEvent(ConfigUpdateEvent.Phase.END));
    }

    public static List<Map.Entry<String, String>> printConfig()
    {
        List<Map.Entry<String, String>> result = new ArrayList<>();
        for(Map.Entry<String, Field> entry : valList.entrySet())
        {
            try
            {
                result.add(new HashMap.SimpleEntry<>(entry.getValue().getName(), entry.getValue().get(null).toString()));
            } catch (IllegalAccessException e)
            {
                e.printStackTrace();
            }
        }
        return result;
    }

    @StateEventCallback
    private static void init(FMLInitializationEvent event) {
        instance();

        MinecraftForge.EVENT_BUS.register(new LoginEvents());
    }

    public static Config instance() {
        synchronized (ACConfig.class) {
            if (config == null) {
                __init();
            }

            return config;
        }
    }

    /**
     * @return Last error, will clear the error storage. null if no error.
     */
    public static String fetchLastError() {
        String ret = lastError;
        lastError = null;
        return ret;
    }

    public static class LoginEvents {
        @SubscribeEvent
        public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent evt) {
            String err = fetchLastError();

            if (err != null) {
                evt.player.sendMessage(new TextComponentTranslation("ac.data_config_parse_fail"));
                evt.player.sendMessage(new TextComponentTranslation(err));
            }
            if(!evt.player.world.isRemote)
                NetworkManager.sendTo(config, (EntityPlayerMP) evt.player);
        }
    }

    static{
        BiConsumer<String, Field> base = (s, field) -> {
            List<Integer> values = instance().getIntList(s);
            try
            {
                int[] val = (int[]) field.get(null);
                for(int i=0;i<values.size();i++)
                {
                    val[i] = values.get(i);
                }
            } catch (IllegalAccessException e)
            {
                e.printStackTrace();
            }

        };
        valReader.put(int[].class, base);
    }

    static{
        BiConsumer<String, Field> base = (s, field) -> {
            List<Double> values = instance().getDoubleList(s);
            try
            {
                double[] val = (double[]) field.get(null);
                for(int i=0;i<values.size();i++)
                {
                    val[i] = values.get(i);
                }
            } catch (IllegalAccessException e)
            {
                e.printStackTrace();
            }

        };
        valReader.put(double[].class, base);
    }
    static{
        BiConsumer<String, Field> base = (s, field) -> {
            double value = instance().getDouble(s);
            try
            {
                field.set(null, value);
            } catch (IllegalAccessException e)
            {
                e.printStackTrace();
            }
        };
        valReader.put(double.class, base);
        valReader.put(float.class, base);
    }

}