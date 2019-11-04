package cn.academy.ability.vanilla.electromaster.skill;

import cn.academy.ability.AbilityContext;
import cn.lambdalib2.util.RandUtils;
import cn.lambdalib2.util.ReflectionUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.network.datasync.DataParameter;

import java.lang.reflect.Field;

public class EMDamageHelper {

    public static DataParameter<Boolean> CREEPER_POWERED = null;

    public static void powerCreeper(EntityCreeper creeper)
    {
        if(CREEPER_POWERED ==null) {
            Field field = ReflectionUtils.getObfField(EntityCreeper.class, "POWERED", "field_184714_b");
            field.setAccessible(true);
            try
            {
                CREEPER_POWERED = (DataParameter<Boolean>) field.get(EntityCreeper.class);//TODO need Test
            } catch (IllegalAccessException e)
            {
                e.printStackTrace();
            }

        }
        // Set the creeper to be powered
        creeper.getDataManager().set(CREEPER_POWERED, true);
    }
    /**
     * TODO maybe a event will be better.
     * Attack with a change to generate a high-voltage creeper.
     */
    static void attack(AbilityContext ctx, Entity target, float dmg) {
        ctx.attack(target, dmg);
        if(target instanceof EntityCreeper) {
            if(RandUtils.nextFloat() < 0.3f) {
                powerCreeper((EntityCreeper) target);
            }
        }
    }
    
}