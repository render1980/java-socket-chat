package command;

import server.Server;

public interface ICommand {
	void execute();
	void setServerInvoker(Server invoker);
}
