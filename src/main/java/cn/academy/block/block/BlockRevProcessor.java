package cn.academy.block.block;

import cn.academy.block.container.ContainerRevProcessor;
import cn.academy.block.tileentity.TileRevProcessor;
import cn.academy.client.ui.GuiRevProcessor;
import cn.lambdalib2.registry.mc.gui.GuiHandlerBase;
import cn.lambdalib2.registry.mc.gui.RegGuiHandler;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockRevProcessor extends ACBlockContainer
{
    public static final PropertyDirection FACING = BlockHorizontal.FACING;
    public BlockRevProcessor()
    {
        super(Material.ROCK, guiHandler);
        setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
        setHardness(3.0f);
        setHarvestLevel("pickaxe", 1);
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        EnumFacing enumfacing = EnumFacing.byIndex(meta);

        if (enumfacing.getAxis() == EnumFacing.Axis.Y)
        {
            enumfacing = EnumFacing.NORTH;
        }

        return this.getDefaultState().withProperty(FACING, enumfacing);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).ordinal();
    }

    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        worldIn.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing().getOpposite()), 2);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {
        return new TileRevProcessor();
    }

    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
        TileRevProcessor te = check(world, pos);
        if(te == null)
            return super.getLightValue(state, world, pos);
        return te.isWorkInProgress() ? 6 : 0;
    }

    @RegGuiHandler
    public static GuiHandlerBase guiHandler = new GuiHandlerBase() {
        @SideOnly(Side.CLIENT)
        @Override
        protected Object getClientContainer(EntityPlayer player, World world, int x, int y, int z) {
            TileRevProcessor te = check(world, new BlockPos(x,y,z));
            return te == null ? null : GuiRevProcessor.createNewScreen(new ContainerRevProcessor(te, player));
        }

        @Override
        protected Object getServerContainer(EntityPlayer player, World world, int x, int y, int z) {
            TileRevProcessor te = check(world, new BlockPos(x,y,z));
            return te == null ? null : new ContainerRevProcessor(te, player);
        }

    };

    private static TileRevProcessor check(IBlockAccess world, BlockPos pos) {
        TileEntity te = world.getTileEntity(pos);
        return (TileRevProcessor) (te instanceof TileRevProcessor ? te : null);
    }
}
