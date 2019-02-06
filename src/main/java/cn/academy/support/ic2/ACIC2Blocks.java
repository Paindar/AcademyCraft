package cn.academy.support.ic2;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;

import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.client.model.ModelLoader;

import cn.lambdalib2.registry.RegistryCallback;
import cn.lambdalib2.registry.StateEventCallback;

import cn.academy.block.block.*;
import cn.academy.item.*;
import net.minecraftforge.fml.common.Optional;
import cn.academy.block.block.BlockNode.NodeType;
import cn.academy.ability.develop.DeveloperType;
import cn.lambdalib2.multiblock.*;

/**
 * Automatically generated by LambdaLib2.xconf in 2019-02-06 21:37:03.
 */
@Optional.Interface(modid = IC2Support.IC2_MODID, iface = IC2Support.IC2_IFACE)
public class ACIC2Blocks {



    @RegistryCallback
    private static void registerBlocks(RegistryEvent.Register<Block> event) {
    }

    @RegistryCallback
    @SuppressWarnings("sideonly")
    private static void registerItems(RegistryEvent.Register<Item> event) {

        if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            registerItemRenderers();
        }
    }

    @SideOnly(Side.CLIENT)
    private static void registerItemRenderers() {
    }

}