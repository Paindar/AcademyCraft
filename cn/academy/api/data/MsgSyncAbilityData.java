package cn.academy.api.data;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class MsgSyncAbilityData implements IMessage {
	
	private NBTTagCompound data;
	
	 public MsgSyncAbilityData(EntityPlayer player) {
	      data = new NBTTagCompound();
	      AbilityDataMain.getData(player).saveNBTData(data);
	  }
	 
	 //Reciver-Side
	 public MsgSyncAbilityData() {}

	@Override
	public void fromBytes(ByteBuf buf) {
		data = ByteBufUtils.readTag(buf);

	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeTag(buf, data);

	}
	
	 public static class Handler implements IMessageHandler<MsgSyncAbilityData, IMessage> {
	      @Override
	      @SideOnly(Side.CLIENT)
	      public IMessage onMessage(MsgSyncAbilityData message, MessageContext ctx) {
	          EntityPlayer player = Minecraft.getMinecraft().thePlayer;
	          AbilityDataMain.getData(player).loadNBTData(message.data);
	          return null;
	      }
	  }

}