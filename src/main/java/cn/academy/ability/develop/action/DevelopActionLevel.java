package cn.academy.ability.develop.action;

import cn.academy.ACConfig;
import cn.academy.ability.Category;
import cn.academy.ability.CategoryManager;
import cn.academy.datapart.AbilityData;
import cn.academy.ability.develop.IDeveloper;
import cn.academy.ability.develop.LearningHelper;
import cn.academy.event.ConfigUpdateEvent;
import cn.academy.item.ItemInductionFactor;
import cn.lambdalib2.registry.StateEventCallback;
import cn.lambdalib2.util.RandUtils;
import com.typesafe.config.Config;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.*;

/**
 * @author WeAthFolD
 */
public class DevelopActionLevel implements IDevelopAction {
    @ACConfig.GetACCfgValue(path="ac.ability.level_prob")
    private static int[] levelProb = {1,1,1,1,1,111110};

    private static Map<String, Integer> catProb = new HashMap<>();

    private int preLevel = 0;
    @Override
    public int getStimulations(EntityPlayer player) {
        return 5 * (AbilityData.get(player).getLevel() + 1);
    }

    @Override
    public boolean validate(EntityPlayer player, IDeveloper developer) {
        return LearningHelper.canLevelUp(developer.getType(), AbilityData.get(player));
    }

    @Override
    public void onLearned(EntityPlayer player) {
        AbilityData aData = AbilityData.get(player);
        if(!aData.hasCategory()) {
            aData.setCategory(chooseCategory(player));
        } else {
            aData.setLevel(aData.getLevel() + 1);
        }
    }

    private static int generateLevel()
    {
        int count = 0;
        for(int i : levelProb)
        {
            count+=i;
        }
        int result = RandUtils.nextInt(count);
        for(int i=0;i<6;i++)
        {
            if(result<levelProb[i])
                return i+1;
            else
                result-=levelProb[i];
        }
        throw new RuntimeException();
    }

    private Category chooseCategory(EntityPlayer player) {
        Optional<ItemStack> inductedCategory = DevelopActionReset.getFactor(player);
        if (inductedCategory.isPresent()) {
            ItemStack factor = inductedCategory.get();
            int factorIdx = player.inventory.mainInventory.indexOf(factor);
            player.inventory.setInventorySlotContents(factorIdx, ItemStack.EMPTY);
            return ItemInductionFactor.getCategory(factor);
        } else {
            CategoryManager man = CategoryManager.INSTANCE;
            int sumProb = 0;
            for(int c = 0;c<man.getCategoryCount();c++)
            {
                sumProb += catProb.get(man.getCategory(c).getName());
            }
            int nextInt = RandUtils.nextInt(sumProb);
            int idx = 0;
            String name;
            while (true)
            {
                name = man.getCategory(idx).getName();
                if (nextInt <catProb.get(name))
                    if(man.getCategoryCount()<=idx)
                        throw new RuntimeException();
                    else
                        return man.getCategory(idx);
                else
                {
                    nextInt -= catProb.get(name);
                    idx++;
                }
            }
        }
    }

    @StateEventCallback
    public static void preInit(FMLPreInitializationEvent evtea){
        MinecraftForge.EVENT_BUS.register(new EventHandler());
    }

    public static class EventHandler{
        @SubscribeEvent
        public void updateCatProb(ConfigUpdateEvent evt)
        {
            CategoryManager man = CategoryManager.INSTANCE;
            catProb.clear();
            Config cfg = ACConfig.instance().getConfig("ac.ability.cat_prob");
            for(int c = 0;c<man.getCategoryCount();c++)
            {
                String name = man.getCategory(c).getName();
                int prob = cfg.getInt(name);
                catProb.put(name, prob);
            }
        }
    }

}