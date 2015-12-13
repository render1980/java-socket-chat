package client;

import command.ChangeRoomCommand;
import command.CloseConnectionCommand;
import command.GetAvailableRoomsCommand;
import command.GetPrivateMessagesHistoryCommand;
import command.ICommand;
import command.IdentifyClientCommand;
import command.SendMessageToRoomCommand;
import command.SendPrivateMessageCommand;

public final class CommandParser {

	private CommandParser() {}

	public static ICommand getCommand(String message) {
		ICommand myCommand;
		String[] args = message.split(" ");
		if (args[0].equals("chroom")) {
			myCommand = new ChangeRoomCommand(Integer.parseInt(args[1]));
		} else if (args[0].equals("ls")) {
			if (args[1].equals("-room")) {
				myCommand = new GetAvailableRoomsCommand();
			} else if (args[1].equals("-history")) {
				myCommand = new GetPrivateMessagesHistoryCommand();
			} else {
				myCommand = new SendMessageToRoomCommand(message);
			}
		} else if (args[0].equals("private")) {
			StringBuffer buffer = new StringBuffer();
			int length = args.length;
			for (int i = 2; i < length; i++) {
				buffer.append(args[i]);
				buffer.append(" ");
			}
			myCommand = new SendPrivateMessageCommand(args[1],
					buffer.toString().trim());
		} else if (args[0].equals("bye")) {
			myCommand = new CloseConnectionCommand();
		} else if (args[0].equals("#lgn")) {
			myCommand = new IdentifyClientCommand(args[1]);
		} else {
			myCommand = new SendMessageToRoomCommand(message);
		}
		return myCommand;
	}
}
