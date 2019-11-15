package cn.academy.medicine.blocks;

import cn.academy.block.container.TechUIContainer;
import cn.academy.medicine.MatExtraction;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import java.util.function.Function;
import java.util.function.Predicate;

public class ContainerMatExtractor extends TechUIContainer<TileMatExtractor>
{
    class MatExtSlot extends Slot
    {
        public MatExtSlot(IInventory tile, int slot, int x, int y)
        {
            super(tile, slot, x, y);
        }

        @Override
        public boolean isItemValid(ItemStack itemStack)
        {
            if(slotNumber==0)
            {
                return MatExtraction.checkIfReciped(itemStack.getItem(), itemStack.getItemDamage());
            }
            return false;
        }
    }

    public ContainerMatExtractor(EntityPlayer _player, TileMatExtractor _tile)
    {
        super(_player, _tile);

        addSlotToContainer(new MatExtSlot(tile, 0, 22, 51));
        addSlotToContainer(new MatExtSlot(tile, 1, 71, 51));
        addSlotToContainer(new MatExtSlot(tile, 2, 94, 51));
        addSlotToContainer(new MatExtSlot(tile, 3, 117, 51));

        SlotGroup gInv = gRange(4, 4 + 36);
        SlotGroup gInput = gSlots(0);
        SlotGroup gOutput = gRange(1, 4);

        addTransferRule(gInv,  gInput);
        addTransferRule(gRange(0, 4), gInv);

        mapPlayerInventory();
    }

    private Predicate<ItemStack> p(Function<ItemStack, Boolean> fn){
        return fn::apply;
    }
}