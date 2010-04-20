package cmd;

public abstract class Command {

	private String usage;
	
	public Command(String usage) {
		this.usage = usage;
	}
	
	public String getUsage() {
		return usage;
	}
	
}
