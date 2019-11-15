package cn.academy.crafting;

import cn.lambdalib2.registry.StateEventCallback;
import cn.lambdalib2.s11n.network.NetworkS11n;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

import java.util.ArrayList;
import java.util.List;

public enum RevProcRecipes
{
    INSTANCE;

    public static class RecipeObject {
        private int id = -1;

        public final double produce;
        public final ItemStack input;
        public final ItemStack output;

        private RecipeObject(ItemStack _input, ItemStack _output, double _produce) {
            input = _input;
            output = _output;
            produce = _produce;
        }

        public boolean accepts(ItemStack stack) {
            return  stack != null &&
                    input.getItem() == stack.getItem() &&
                    input.getCount() <= stack.getCount() &&
                    input.getItemDamage() == stack.getItemDamage();
        }
    }

    List<RecipeObject> objects = new ArrayList<>();

    public void add(ItemStack in, ItemStack out, double produce) {
        RecipeObject add = new RecipeObject(in, out, produce);
        add.id = objects.size();
        objects.add(add);
    }

    public RecipeObject getRecipe(ItemStack input) {
        for(RecipeObject recipe : objects) {
            if(recipe.accepts(input))
                return recipe;
        }
        return null;
    }

    public List<RecipeObject> getAllRecipes() {
        return objects;
    }

    @StateEventCallback
    private static void _init(FMLInitializationEvent evt) {
        NetworkS11n.addDirect(RecipeObject.class, new NetworkS11n.NetS11nAdaptor<RecipeObject>() {
            @Override
            public void write(ByteBuf buf, RecipeObject obj) {
                buf.writeByte(obj.id);
            }

            @Override
            public RecipeObject read(ByteBuf buf) throws NetworkS11n.ContextException
            {
                return INSTANCE.objects.get(buf.readByte());
            }
        });
    }

}
