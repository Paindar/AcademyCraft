package cn.academy.medicine.blocks;

import cn.academy.block.container.TechUIContainer;
import cn.academy.medicine.ModuleMedicine;
import cn.academy.medicine.items.ItemPowder;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import java.util.function.Function;
import java.util.function.Predicate;

import static cn.academy.medicine.blocks.TileMedSynthesizer.bottleSlot;
import static cn.academy.medicine.blocks.TileMedSynthesizer.outputSlot;

public class ContainerMedSynthesizer extends TechUIContainer<TileMedSynthesizer> {

    class MedSynSlot extends Slot
    {
        public MedSynSlot(IInventory p_i1824_1_, int p_i1824_2_, int p_i1824_3_, int p_i1824_4_)
        {
            super(p_i1824_1_, p_i1824_2_, p_i1824_3_, p_i1824_4_);
        }

        @Override
        public boolean isItemValid(ItemStack itemStack)
        {
            if(slotNumber<4)
                return itemStack.getItem() instanceof ItemPowder;
            else if (slotNumber == bottleSlot)
                return itemStack.getItem() == ModuleMedicine.emptyBottle;
            return false;
        }
    }
    public ContainerMedSynthesizer(EntityPlayer _player, TileMedSynthesizer _tile) {
        super(_player, _tile);

        addSlotToContainer(new MedSynSlot(tile, 0, 34, 12));
        addSlotToContainer(new MedSynSlot(tile, 1, 10, 32));
        addSlotToContainer(new MedSynSlot(tile, 2, 10, 58));
        addSlotToContainer(new MedSynSlot(tile, 3, 35, 78));

        addSlotToContainer(new MedSynSlot(tile, bottleSlot, 50, 45));
        addSlotToContainer(new MedSynSlot(tile, outputSlot, 138, 44));

        SlotGroup gInv = gRange(6, 6 + 36);
        SlotGroup gPowders = gRange(0, 4);
        SlotGroup gBottle = gSlots(4);
        SlotGroup gOutput = gSlots(5);

        addTransferRule(gInv, p(stack -> stack.getItem() instanceof ItemPowder), gPowders);
        addTransferRule(gInv, p(stack -> stack.getItem() == ModuleMedicine.emptyBottle), gBottle);
        addTransferRule(gRange(0, 6), gInv);

        mapPlayerInventory();
    }

    private Predicate<ItemStack> p(Function<ItemStack, Boolean> fn){
        return fn::apply;
    }

}
