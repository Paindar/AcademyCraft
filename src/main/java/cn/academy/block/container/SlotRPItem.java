/**
 * Copyright (c) Lambda Innovation, 2013-2016
 * This file is part of the AcademyCraft mod.
 * https://github.com/LambdaInnovation/AcademyCraft
 * Licensed under GPLv3, see project root for more information.
 */
package cn.academy.block.container;

import cn.academy.crafting.RevProcRecipes;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotRPItem extends Slot {

    private int slot;

    public SlotRPItem(IInventory inv, int _slot, int x, int y) {
        super(inv, _slot, x, y);
        slot = _slot;
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        if(slot == 0) {
            for (RevProcRecipes.RecipeObject obj : RevProcRecipes.INSTANCE.getAllRecipes()) {
                if (obj.input.getItem() == stack.getItem()) return true;
            }
        }
        return false;
    }

}
