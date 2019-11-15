package cn.academy.block.container;

import cn.academy.block.tileentity.TileRevProcessor;
import cn.academy.support.EnergyItemHelper;
import net.minecraft.entity.player.EntityPlayer;

public class ContainerRevProcessor extends TechUIContainer<TileRevProcessor>
{
    public static final int SLOT_IN = 0, SLOT_OUT = 1, SLOT_CHARGE = 2;
    public ContainerRevProcessor(TileRevProcessor _tile, EntityPlayer _player)
    {
        super(_player, _tile);

        initInventory();
    }

    private void initInventory() {
        this.addSlotToContainer(new SlotRPItem(tile, 0, 13, 24));
        this.addSlotToContainer(new SlotRPItem(tile, 1, 143, 24));
        this.addSlotToContainer(new SlotRPItem(tile, 2, 42, 55));

        mapPlayerInventory();

        SlotGroup gMachine = gSlots(0, 1, 2);
        SlotGroup gInv = gRange(3, 3+36);

        addTransferRule(gMachine, gInv);
        addTransferRule(gInv, EnergyItemHelper::isSupported, gSlots(SLOT_CHARGE));
        addTransferRule(gInv, gSlots(SLOT_IN));
    }
}
