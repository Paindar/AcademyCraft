package cn.academy.medicine;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;

import java.util.*;

public class MatExtraction {

  public static class ItemMeta{
      public Item item;
      public int meta;
      public String id;
      public ItemMeta(Item item, int meta) {
          this.item=item;
          this.meta=meta;
          String itemName = Item.REGISTRY.getNameForObject(item).toString();
          id = item.getHasSubtypes()?(itemName + "_" + meta):itemName;
      }

      @Override
      public int hashCode() {
          return id.hashCode();
      }

      @Override
      public boolean equals(Object obj) {
          return obj instanceof ItemMeta && ((ItemMeta) obj).meta==this.meta && ((ItemMeta) obj).item == this.item;
      }
  }

    //type Recipe = (List[ItemMeta], List[Property])

    public static List<ItemMeta> ofBlock(Block block)
    {
        return ofItem(Item.getItemFromBlock(block));
    }

    public static List<ItemMeta> ofBlock(Block block, int... damages){
      return ofItem(Item.getItemFromBlock(block), damages);
    }

    public static List<ItemMeta> ofItem(Item item){
        int[] dmgs=new int[item.getMaxDamage()+1];
        for(int i=0;i<=item.getMaxDamage();i++){
            dmgs[i]=i;
        }
      return ofItem(item,dmgs);
    }
    public static List<ItemMeta> ofItem(Item item, int... damage){
        List<ItemMeta> ret=new ArrayList<>();
        for(int i:damage){
            ret.add(new ItemMeta(item,i));
        }
        return ret;
    }
    public static class Recipe{
        public List<ItemMeta> metas;
        public List<Properties.Property> props;
        public Recipe(List<ItemMeta> metas, List<Properties.Property> props){
            this.metas=metas;
            this.props=props;
        }
    }
    public static Map<ItemMeta, List<Properties.Property>> allRecipes = new LinkedHashMap<ItemMeta, List<Properties.Property>>(){{
        for(ItemMeta meta:ofItem(Items.APPLE))
            put(meta, Arrays.asList(Properties.instance.Targ_Life, Properties.instance.Str_Normal));
        for(ItemMeta meta:ofItem(Items.POTATO))
            put(meta, Collections.singletonList(Properties.instance.Targ_CP));
        for(ItemMeta meta:ofItem(Items.POISONOUS_POTATO))
                put(meta, Arrays.asList(Properties.instance.Var_Fluct));
        for(ItemMeta meta:ofItem(Items.WHEAT))
                put(meta, Arrays.asList(Properties.instance.Apply_Continuous_Incr));
        for(ItemMeta meta:ofItem(Items.REEDS))
                put(meta, Arrays.asList(Properties.instance.Targ_MoveSpeed));
        for(ItemMeta meta:ofBlock(Blocks.CACTUS))
                put(meta, Arrays.asList(Properties.instance.Str_Mild));
        for(ItemMeta meta:ofItem(Items.WHEAT_SEEDS))
                put(meta, Arrays.asList(Properties.instance.Apply_Continuous_Incr, Properties.instance.Str_Mild));
        for(ItemMeta meta:ofItem(Items.CARROT))
                put(meta, Arrays.asList(Properties.instance.Str_Weak));
        for(ItemMeta meta:ofItem(Items.MELON))
                put(meta, Arrays.asList(Properties.instance.Targ_Life));
        for(ItemMeta meta:ofItem(Items.DYE))
                put(meta, Arrays.asList(Properties.instance.Targ_Overload, Properties.instance.Apply_Continuous_Decr));
        for(ItemMeta meta:ofItem(Items.EGG))
                put(meta, Arrays.asList(Properties.instance.Apply_Instant_Incr));
        for(ItemMeta meta:ofItem(Items.MILK_BUCKET))
                put(meta, Arrays.asList(Properties.instance.Apply_Continuous_Incr, Properties.instance.Var_Stabilize));
        for(ItemMeta meta:ofItem(Items.FEATHER))
                put(meta, Arrays.asList(Properties.instance.Targ_Jump));
        for(ItemMeta meta:ofItem(Items.SPIDER_EYE))
                put(meta, Arrays.asList(Properties.instance.Targ_Cooldown));
        for(ItemMeta meta:ofItem(Items.BONE))
                put(meta, Arrays.asList(Properties.instance.Str_Mild));
        for(ItemMeta meta:ofItem(Items.ROTTEN_FLESH))
                put(meta, Arrays.asList(Properties.instance.Var_Fluct));
        for(ItemMeta meta:ofItem(Items.PORKCHOP))
                put(meta, Arrays.asList(Properties.instance.Targ_CP));
        for(ItemMeta meta:ofItem(Items.COOKED_PORKCHOP))
                put(meta, Arrays.asList(Properties.instance.Targ_CP));
        for(ItemMeta meta:ofItem(Items.CHICKEN))
                put(meta, Arrays.asList(Properties.instance.Targ_CP));
        for(ItemMeta meta:ofItem(Items.COOKED_CHICKEN))
                put(meta, Arrays.asList(Properties.instance.Targ_CP));
        for(ItemMeta meta:ofItem(Items.BEEF))
                put(meta, Arrays.asList(Properties.instance.Targ_CP));
        for(ItemMeta meta:ofItem(Items.COOKED_BEEF))
                put(meta, Arrays.asList(Properties.instance.Targ_CP));
        for(ItemMeta meta:ofItem(Items.FISH))
                put(meta, Arrays.asList(Properties.instance.Apply_Continuous_Incr)); // Var_Stablize
        for(ItemMeta meta:ofItem(Items.COOKED_FISH))
                put(meta, Arrays.asList(Properties.instance.Apply_Continuous_Incr));
        for(ItemMeta meta:ofItem(Items.ENDER_PEARL))
                put(meta, Arrays.asList(Properties.instance.Targ_CP, Properties.instance.Targ_Overload,
                        Properties.instance.Var_Fluct));
        for(ItemMeta meta:ofItem(Items.BLAZE_POWDER))
                put(meta, Arrays.asList(Properties.instance.Targ_Attack, Properties.instance.Str_Strong));
        // for(ItemMeta meta:ofItem(Items.redstone))
        //        put(meta, Arrays.asList(Var_Neturalize));
        for(ItemMeta meta:ofItem(Items.GLOWSTONE_DUST))
                put(meta, Arrays.asList(Properties.instance.Str_Strong, Properties.instance.Var_Desens));
        for(ItemMeta meta:ofItem(Items.NETHER_STAR))
                put(meta, Arrays.asList(Properties.instance.Var_Infinity));
    }};

    public static boolean checkIfReciped(Item item, int i){
        return allRecipes.containsKey(new ItemMeta(item, i));
    }

}
