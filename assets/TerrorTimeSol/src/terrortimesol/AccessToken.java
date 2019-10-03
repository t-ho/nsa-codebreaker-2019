package terrortimesol;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.Base64;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.jivesoftware.smack.util.TLSUtils;
import org.json.JSONObject;

public class AccessToken {
	private String clientId;
	private String clientSecret;

	/**
	 * Constructor
	 */
	public AccessToken() {
		this.clientId = "elias--vhost-1310@terrortime.app";
		this.clientSecret = "yGdkc4nujYW5A0";
	}
	
	/**
	 * Constructor
	 * 
	 * @param clientId The client Id
	 * @param clientSecrect The client secret
	 */
	public AccessToken(String clientId, String clientSecrect) {
		this.clientId = clientId;
		this.clientSecret = clientSecrect;
	}

	private SSLContext createAcceptAllCertsContext() throws Exception {
		TrustManager[] tms = { new X509TrustManager() {
			public void checkClientTrusted(X509Certificate[] chain, String authType) {
			}

			public void checkServerTrusted(X509Certificate[] chain, String authType) {
			}

			public X509Certificate[] getAcceptedIssuers() {
				return new X509Certificate[0];
			}
		} };
		try {
			SSLContext context = SSLContext.getInstance(TLSUtils.TLS);
			context.init(null, tms, null);
			return context;
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	private String getBase64AuthorizationString(String clientId, String secret) {
		StringBuilder sb = new StringBuilder();
		sb.append(clientId);
		sb.append(":");
		sb.append(secret);
		return Base64.getUrlEncoder().encodeToString(sb.toString().getBytes());
	}

	/**
	 * Get access token from register.terrortime.app server
	 * 
	 * @return JSONObject access token object. Otherwise, null
	 */
	public JSONObject getToken() {
		JSONObject jsonTokenResponse = null;

		try {
			String authServer = "https://register.terrortime.app/oauth2/token";
			int port = 443;
			String grantType = "client_credentials";
			String scope = "chat";
			String audience = "";

			String authorization = "Basic " + getBase64AuthorizationString(clientId, clientSecret);

			URL authServerUrl = new URL(authServer);
			URL connectURL = new URL(authServerUrl.getProtocol(), authServerUrl.getHost(), port,
					authServerUrl.getFile());
			SSLContext sslContext = createAcceptAllCertsContext();
			HttpsURLConnection connect = (HttpsURLConnection) connectURL.openConnection();
			connect.setSSLSocketFactory(sslContext.getSocketFactory());
			connect.setHostnameVerifier(new HostnameVerifier() {
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			});
			connect.setRequestMethod("POST");
			connect.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			connect.setRequestProperty("Authorization", authorization);
			connect.setRequestProperty("X-Server-Select", "oauth");
			connect.setDoInput(true);
			connect.setDoOutput(true);
			connect.setUseCaches(false);
			connect.setReadTimeout(30000);
			connect.setConnectTimeout(30000);

			StringBuilder postBuilder = new StringBuilder();
			postBuilder.append("audience=" + audience + "&");
			postBuilder.append("grant_type=" + grantType + "&");
			postBuilder.append("scope=" + scope);

			OutputStream postStream = new BufferedOutputStream(connect.getOutputStream());
			byte[] tx = postBuilder.toString().getBytes(org.jivesoftware.smack.util.StringUtils.UTF8);
			StringBuilder sb3 = new StringBuilder();
			sb3.append("Requesting token. Destination: ");
			sb3.append(authServer);
			sb3.append(". Authorization property: ");
			sb3.append(authorization);
			sb3.append(". Request: ");
			sb3.append(postBuilder.toString());
			postStream.write(tx, 0, tx.length);
			postStream.close();
			StringBuffer responseBuffer = new StringBuffer();
			InputStream inStream = connect.getInputStream();
			InputStreamReader inStreamReader = new InputStreamReader(inStream);
			BufferedReader inResponse = new BufferedReader(inStreamReader);
			String line;
			while (true) {
				String readLine = inResponse.readLine();
				line = readLine;
				if (readLine == null) {
					break;
				}
				responseBuffer.append(line);
			}
			if (responseBuffer.toString().length() != 0) {
				String responseFromHydra = responseBuffer.toString();
				jsonTokenResponse = new JSONObject(responseFromHydra);
				return jsonTokenResponse;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
