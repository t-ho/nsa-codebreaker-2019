package terrortimesol;

import java.net.InetAddress;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ReconnectionManager;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration.Builder;
import org.jivesoftware.smackx.mam.MamManager;
import org.jivesoftware.smackx.mam.MamManager.MamQuery;
import org.jivesoftware.smackx.mam.MamManager.MamQueryArgs;
import org.jivesoftware.smackx.vcardtemp.VCardManager;
import org.json.JSONObject;
import org.jxmpp.jid.Jid;

public class XmppClient {
	private static final boolean isDebug = false;

	private AbstractXMPPConnection mConnection = null;
	private MamManager mamManager;
	private VCardManager vCardManager;
	private Roster roster;
	private String username;
	private String password;
	private int port;
	private String domain;
	private String serverIp;

	/**
	 * Constructor
	 * 
	 * @param username The username
	 * 
	 * Note: The password is an access token which is requested automatically
	 */
	public XmppClient(String username) {
		AccessToken accessToken = new AccessToken();
		String password = accessToken.getToken().getString("access_token");
		this.init(username, password);
	}

	/**
	 * Constructor
	 * 
	 * @param username The username
	 * @param password The password
	 */
	public XmppClient(String username, String password) {
		this.init(username, password);
	}
	
	private void init(String username, String password) {
		this.username = username;
		this.password = password;
		this.domain = "terrortime.app";
		this.port = 443;
		this.serverIp = "chat.terrortime.app";
		login();
	}

	private void login() {
		System.out.println("\n[*] Login as " + username);
		ReconnectionManager mReconnectionManager = null;
		SmackConfiguration.DEBUG = isDebug;
		try {
			Builder configBuilder = XMPPTCPConnectionConfiguration.builder();
			configBuilder.setUsernameAndPassword(username, password);
			configBuilder.setResource((CharSequence) "chat");
			configBuilder.setHostAddress(InetAddress.getByName(serverIp));
			configBuilder.setXmppDomain(domain);
			configBuilder.setPort(port);
			configBuilder.setHostnameVerifier(new HostnameVerifier() {
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			});
			configBuilder.setCustomX509TrustManager(new X509TrustManager() {
				public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				}

				public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				}

				public X509Certificate[] getAcceptedIssuers() {
					return new X509Certificate[0];
				}
			});
			mConnection = new XMPPTCPConnection(configBuilder.build());
			mConnection.setReplyTimeout(30000);

			mReconnectionManager = ReconnectionManager.getInstanceFor(mConnection);
			mReconnectionManager.enableAutomaticReconnection();
			mConnection.connect();
			mConnection.login();
			this.roster = Roster.getInstanceFor(this.mConnection);
			this.vCardManager = VCardManager.getInstanceFor(this.mConnection);
			this.mamManager = MamManager.getInstanceFor(this.mConnection);
		} catch (Exception e) {
			if (mConnection != null && mConnection.isConnected()) {
				e.printStackTrace();
				mConnection.disconnect();
			}
		}
	}

	public List<Message> getMessageArchive(int noOfMessages) {
		try {
			if (this.mamManager.isSupported()) {
				MamQueryArgs mamQueryArgs = MamQueryArgs.builder().setResultPageSizeTo(noOfMessages).build();
				MamQuery mamQuery = this.mamManager.queryArchive(mamQueryArgs);
				return mamQuery.getMessages();
			}
		} catch (Exception e) {
			System.out.println("[-] Connection failed. Please check the username and password");
		}
		return Collections.emptyList();
	}

	public void printTheLastEncryptedMessage() {
		LinkedList<Message> messageList = new LinkedList<Message>(this.getMessageArchive(10000));
		if (messageList.size() > 0) {
			JSONObject jsonLastMessage = new JSONObject(messageList.getLast().getBody());
			System.out.println("[+] The last encrypted message:\n");
			System.out.println(messageList.getLast());
			System.out.println(jsonLastMessage.toString(4) + "\n");
		}
	}

	public void disconnect() {
		if (mConnection != null && mConnection.isConnected()) {
			this.mConnection.disconnect();
		}
		System.out.println("[*] Connection closed");
	}


	/**
	 * @return the roster
	 */
	public Roster getRoster() {
		return roster;
	}

	/**
	 * @return the mConnection
	 */
	public AbstractXMPPConnection getmConnection() {
		return mConnection;
	}

	/**
	 * @return the mamManager
	 */
	public MamManager getMamManager() {
		return mamManager;
	}

	/**
	 * @return the vCardManager
	 */
	public VCardManager getvCardManager() {
		return vCardManager;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}
	
	/**
	 * @return the Jid of the login user
	 */
	public Jid getJid() {
		return this.mConnection.getUser().asBareJid();
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @return the domain
	 */
	public String getDomain() {
		return domain;
	}

	/**
	 * @return the server ip
	 */
	public String getServerIp() {
		return serverIp;
	}

}
