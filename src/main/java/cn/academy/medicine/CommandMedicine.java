package cn.academy.medicine;


import cn.academy.util.ACCommand;
import cn.lambdalib2.util.PlayerUtils;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

import java.util.Objects;

//@RegCommand
public class CommandMedicine extends ACCommand {
    String[] commands = {"help", "props", "synth"};

    public CommandMedicine(){
        super("med");
    }

    private static void sendChat(ICommandSender s, String key, Object ...pars) {
        PlayerUtils.sendChat(s, key, pars);
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender ics, String[] args ) {
//        def msg (content:String, args:Any *) ={
//            LICommandBase.sendChat(ics, content, args.map(_.asInstanceOf[AnyRef]):_ *)
//        }
//
//        def synthFromArgs (method:MedicineApplyInfo =>ItemStack) ={
//            EntityPlayer player = CommandBase.getCommandSenderAsPlayer(ics);
//            val parsedProps = args.toList.drop(1).map(Properties.find)
//
//            if (parsedProps.forall(_.isDefined)) {
//                val props = parsedProps.flatten
//                val applyInfo = MedSynthesizer.synth(props)
//                val medicine = method(applyInfo)
//
//                val entity = new EntityItem(player.worldObj, player.posX, player.posY + 1, player.posZ, medicine)
//                player.worldObj.spawnEntityInWorld(entity)
//            } else {
//                msg(getLoc("no_prop"))
//            }
//        }

        if (args.length == 0) {
            sendChat(ics, getUsage(ics));
        } else {
            if (args[0].equals("help") || args[0].equals("?")) {
                for (String c : commands) {
                    sendChat(ics, getLoc(c));
                }
            } else if (args[0].equals("props")) {
                //TODO Properties.allProperties().map(prop -> prop.internalID() + ": " + prop.displayDesc).foreach(msg(_))
            } else if (args[0].equals("synth")) {
                //synthFromArgs(ItemMedicineBottle.create());
            } else if (args[0].equals("synth_syringe")) {

            } else
                sendChat(ics, locInvalid());
        }
    }
}