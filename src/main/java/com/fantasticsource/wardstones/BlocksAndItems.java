package com.fantasticsource.wardstones;

import com.fantasticsource.wardstones.block.BlockWardstone;
import com.fantasticsource.wardstones.block.BlockWardstoneBase;
import com.fantasticsource.wardstones.item.ItemWardstone;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

public class BlocksAndItems
{
    @GameRegistry.ObjectHolder("wardstones:wardstonebase")
    public static BlockWardstoneBase blockWardstoneBase;

    @GameRegistry.ObjectHolder("wardstones:wardstone")
    public static BlockWardstone blockWardstone;


    @GameRegistry.ObjectHolder("wardstones:wardstone")
    public static Item itemWardstone;


    public static CreativeTabs creativeTab = new CreativeTabs(Wardstones.MODID)
    {
        @Override
        public ItemStack getTabIconItem()
        {
            return new ItemStack(blockWardstone);
        }

        @Override
        public void displayAllRelevantItems(NonNullList<ItemStack> p_78018_1_)
        {
            super.displayAllRelevantItems(p_78018_1_);
        }
    };


    @SubscribeEvent
    public static void onBlockRegistry(RegistryEvent.Register<Block> event)
    {
        IForgeRegistry<Block> registry = event.getRegistry();
        registry.register(new BlockWardstoneBase());
        registry.register(new BlockWardstone());
    }

    @SubscribeEvent
    public static void onItemRegistry(RegistryEvent.Register<Item> event)
    {
        IForgeRegistry<Item> registry = event.getRegistry();
        registry.register(new ItemWardstone());
    }

    @SubscribeEvent
    public static void onModelRegistry(ModelRegistryEvent event)
    {
        ModelLoader.setCustomModelResourceLocation(itemWardstone, 0, new ModelResourceLocation("wardstones:wardstone", "inventory"));
    }
}
