package cn.academy.medicine.blocks;

import cn.academy.block.block.ACBlockContainer;
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
import net.minecraft.world.World;

public class BlockMatExtractor extends ACBlockContainer {

    public static final PropertyDirection FACING = BlockHorizontal.FACING;

    @RegGuiHandler
    public static final GuiHandlerBase guiHandlerMatExtractor = new GuiHandlerBase() {
        @Override
        protected Object getClientContainer(EntityPlayer player, World world, int x, int y, int z) {
            Object tile = getServerContainer(player, world, x, y, z);
            if(tile instanceof ContainerMatExtractor)
                return GuiMatExtractor.createNewScreen((ContainerMatExtractor) tile);
            System.out.println("tile is null or something other:"+tile);
            return null;
        }

        @Override
        protected Object getServerContainer(EntityPlayer player, World world, int x, int y, int z) {
            TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
            if (tile instanceof TileMatExtractor )
                return new ContainerMatExtractor(player, (TileMatExtractor) tile);
            System.out.println("Server tile is null or something other:"+tile);
            return null;
        }
    };


    public BlockMatExtractor() {
        super(Material.ROCK, guiHandlerMatExtractor);
        setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
        setHardness(3.0f);
        setHarvestLevel("pickaxe", 1);
    }
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING);
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
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).ordinal();
    }

    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        worldIn.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing().getOpposite()), 2);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int p_149915_2_) {
        return new TileMatExtractor();
    }
}
