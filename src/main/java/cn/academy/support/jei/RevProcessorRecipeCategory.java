package cn.academy.support.jei;

import cn.academy.ACBlocks;
import cn.academy.crafting.ImagFusorRecipes;
import cn.academy.crafting.ImagFusorRecipes.IFRecipe;
import cn.academy.crafting.RevProcRecipes;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 
 * @author KSkun
 *
 */
public class RevProcessorRecipeCategory extends IACRecipeCategory implements IDrawable
{
    public static List<IRecipeWrapper> recipeWrapper = loadCraftingRecipes();
    private static ResourceLocation bg = new ResourceLocation("academy", "textures/guis/nei_rev_processor.png");
    private IDrawable bgComp, costComp;

    public RevProcessorRecipeCategory(IGuiHelper guiHelper)
    {
        super(ACBlocks.revProcessor);
        bgComp = guiHelper.createDrawable(bg,  0, 0, 94, 57, 94, 57);
        //costComp = guiHelper.
    }

    @Override
    public String getTitle() {
        return I18n.format("tile.rev_processor.name");
    }

    //TODO 物品槽的具体位置还需要调整
    @Override
    public List<SlotPos> getInputs() {
        return Collections.singletonList(new SlotPos(5, 23));
    }

    @Override
    public List<SlotPos> getOutputs() {
        return Collections.singletonList(new SlotPos(71, 23));
    }

    private static List<IRecipeWrapper> loadCraftingRecipes() {
        List<IRecipeWrapper> lists = new ArrayList<>();
        for(RevProcRecipes.RecipeObject r : RevProcRecipes.INSTANCE.getAllRecipes()) {
            lists.add(iIngredients -> {
                iIngredients.setInput(ItemStack.class, r.input);
                iIngredients.setOutput(ItemStack.class, r.output);
                //r.cost
            });
        }
        return lists;
    }

    @Override
    public void drawExtras(Minecraft minecraft)
    {
        bgComp.draw(minecraft);
        //costComp.draw(minecraft);
    }

    @Override
    public IDrawable getBackground() {

        /*

        HudUtils.rect(24.5f, 0, 0, 0, 115, 66, 115, 66);
        GL20.glUseProgram(0);
        if(tick >= 50) tick = 0;
        GL11.glEnable(GL11.GL_BLEND);
        Resources.font().draw(String.valueOf(((IFCachedRecipe) arecipes.get(recipe)).liquid),
                75, 11.5f, new FontOption(13, new Color(0xFF373737)));
        ShaderMono.instance().useProgram();
        GL11.glColor4f(55f / 151, 55f / 151, 55f / 151, 1);
        RenderUtils.loadTexture(new ResourceLocation("academy:textures/guis/progress/progress_fusor.png"));
        HudUtils.rect(62, 40.5f, 0, 0, 40d * (tick / 50d), 10, 126 * (tick / 50d), 30);
        GL20.glUseProgram(0);
         */
        return this;
    }

    public int getWidth() {
        return 94;
    }

    public int getHeight() {
        return 57;
    }
    @Override
    public void draw(Minecraft minecraft)
    {
        bgComp.draw(minecraft);
        //costComp.draw(minecraft);
    }

    @Override
    public void draw(Minecraft minecraft, int xOffset, int yOffset) {

    }

}