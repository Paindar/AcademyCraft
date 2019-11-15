package cn.academy.block.tileentity;

import cn.academy.block.container.ContainerRevProcessor;
import cn.academy.crafting.RevProcRecipes;
import cn.academy.energy.IFConstants;
import cn.academy.support.EnergyItemHelper;
import cn.lambdalib2.registry.mc.RegTileEntity;
import cn.lambdalib2.s11n.network.NetworkMessage;
import cn.lambdalib2.s11n.network.NetworkMessage.*;
import cn.lambdalib2.s11n.network.TargetPoints;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;

import static net.minecraft.util.EnumFacing.DOWN;
import static net.minecraft.util.EnumFacing.UP;

@RegTileEntity
public class TileRevProcessor extends TileGeneratorBase implements ISidedInventory
{
    public static final int
            SLOT_IN = ContainerRevProcessor.SLOT_IN,
            SLOT_OUT = ContainerRevProcessor.SLOT_OUT,
            SLOT_CHARGE = ContainerRevProcessor.SLOT_CHARGE;
    public static final int WORK_TICKS = 100;
    public static final int MAX_ENERGY = 15000;

    private static final int[] slotsTop = new int[] {SLOT_IN};
    private static final int[] slotsBottom = new int[] {SLOT_CHARGE, SLOT_OUT};
    private static final int[] slotsSides = new int[] {SLOT_OUT};

    public RevProcRecipes.RecipeObject current;
    private int workCounter = 0;
    public int updateCounter;

    public TileRevProcessor()
    {
        super("rev_processor", 3, MAX_ENERGY, IFConstants.LATENCY_MK2);
    }

    @Override
    public double getGeneration(double required)
    {
        return 0;
    }

    @Override
    public int[] getSlotsForFace(EnumFacing face)
    {
        return face == DOWN ? slotsBottom : (face == UP ? slotsTop : slotsSides);
    }

    @Override
    public void update()
    {
        super.update();
        World world = getWorld();
        if(!world.isRemote) {
            ItemStack inputSlot = this.getStackInSlot(SLOT_IN);

            if(current != null) {
                // Process recipe
                ++workCounter;
                if(inputSlot.isEmpty() || current.input.getItem() != inputSlot.getItem())
                {
                    current = null;
                    workCounter = 0;
                }
                else if (workCounter >= WORK_TICKS) { // Finish the job.
                    ItemStack outputSlot = this.getStackInSlot(SLOT_OUT);
                    inputSlot.shrink(current.input.getCount());
                    if(inputSlot.getCount() == 0)
                        this.setInventorySlotContents(SLOT_IN, ItemStack.EMPTY);

                    if( !outputSlot.isEmpty())
                        outputSlot.grow(current.output.getCount());
                    else
                        this.setInventorySlotContents(SLOT_OUT, current.output.copy());
                    this.addEnergy(current.produce);
                    current = null;
                    workCounter = 0;
                }
            } else {
                if(++workCounter == 5) {
                    current = RevProcRecipes.INSTANCE.getRecipe(this.getStackInSlot(SLOT_IN));
                    workCounter = 0;
                }
            }

            /* Process energy in/out */
            {
                ItemStack stack = this.getStackInSlot(SLOT_CHARGE);
                if(!stack.isEmpty() && EnergyItemHelper.isSupported(stack)) {
                    double gain = EnergyItemHelper
                            .pull(stack, Math.min(MAX_ENERGY - getEnergy(), getBandwidth()), false);
                    this.addEnergy(gain);
                }
            }

            if(++updateCounter == 10) {
                updateCounter = 0;
                sync();
            }
        }
    }
    private void sync() {
        NetworkMessage.sendToAllAround(
                TargetPoints.convert(this, 12),
                this, "sync",
                workCounter, current
        );
    }

    public boolean isWorkInProgress() {
        return current != null;
    }

    public double getWorkProgress() {
        return isWorkInProgress() ? (double) workCounter / WORK_TICKS : 0;
    }


    @Listener(channel="sync", side= Side.CLIENT)
    private void syncData(int counter, @NullablePar RevProcRecipes.RecipeObject recipe) {
        this.workCounter = counter;
        this.current = recipe;
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction)
    {
        return this.isItemValidForSlot(index, itemStackIn);
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        return direction == DOWN;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        return super.writeToNBT(nbt);
    }
}
