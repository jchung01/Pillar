/**
 * This class was created by <TehNut>. It's distributed as
 * part of the Pillar Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Pillar
 *
 * Pillar is Open Source and distributed under the
 * [ADD-LICENSE-HERE]
 *
 * File Created @ [25/06/2016, 21:09:43 (GMT)]
 */
package vazkii.pillar.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import vazkii.pillar.Pillar;
import vazkii.pillar.StructureLoader;

import javax.annotation.Nullable;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CommandPillarCopy extends CommandBase {

    private static final FileFilter NBT_FILTER = (FileFilter) FileFilterUtils.suffixFileFilter(".nbt");

    @Override
    public String getCommandName() {
        return "pillar-copy";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "pillar-copy <sourceFile>";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        try {
            if (args.length > 0) {
                File structureFolder = server.getActiveAnvilConverter().getFile(server.getFolderName(), "structures");
                if (!structureFolder.exists() || structureFolder.isFile()) {
                    sender.addChatMessage(new TextComponentString("The world's structure folder could not be found.").setStyle(new Style().setColor(TextFormatting.RED)));
                    return;
                }

				String requestedName = "";
				if (args.length != 1)
					for (String arg : args)
						requestedName += (requestedName.length() > 0 ? " " : "") + arg;
				else
					requestedName = args[0];

                File[] structures = structureFolder.listFiles(NBT_FILTER);
                for (File file : structures) {
					String fileName = file.getName();
                    if (fileName.equalsIgnoreCase(requestedName + ".nbt")) {
                        FileUtils.copyFileToDirectory(file, Pillar.structureDir);
						File jsonFile = new File(Pillar.pillarDir, fileName.replace(".nbt", ".json"));
						if (!jsonFile.exists()) {
							String schemaJson = StructureLoader.jsonifySchema(StructureLoader.getDefaultSchema());
							FileWriter fileWriter = new FileWriter(jsonFile);
							fileWriter.write(schemaJson);
							fileWriter.close();
						}
						sender.addChatMessage(new TextComponentString("Successfully copied structure '" + fileName.replace(".nbt", "") + "'").setStyle(new Style().setColor(TextFormatting.GREEN)));
						return;
                    }
                }

				sender.addChatMessage(new TextComponentString("No structure file by that name was found!").setStyle(new Style().setColor(TextFormatting.RED)));
            } else {
				throw new WrongUsageException("/" + getCommandUsage(sender));
			}
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos) {
		if (args.length == 1) {
			List<String> files = new ArrayList<>();
			File structureFolder = server.getActiveAnvilConverter().getFile(server.getFolderName(), "structures");
			File[] structures = structureFolder.listFiles(NBT_FILTER);
			for (File structure : structures)
				files.add(structure.getName().replace(".nbt", ""));

			return getListOfStringsMatchingLastWord(args, files);
		}
        return super.getTabCompletionOptions(server, sender, args, pos);
    }

}
