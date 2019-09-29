## Task 04 - Cryptography - Reverse Engineering - Language Analysis

* Use [dex2jar](https://github.com/pxb1988/dex2jar) to decompile the apk file to jar file.
```shell
$ d2j-dex2jar -f terrorTime.apk
```
* Use [jd-gui](https://github.com/java-decompiler/jd-gui) to open created `*.jar` file.

* Or use online [apkdecompilers](https://www.apkdecompilers.com/) tool to decompile the apk.

* By examining the `Client.java` file, we found that the `pin` is hashed (`sha256`) before storing in the database. We also knew that the `pin` used in `terrortime` is exactly 6 digits. Therefore, it is quite easy to bruteforce the `pin`.
```java
// Client.java
public final SecretKey generateSymmetricKey() throws Exception {
    String str = "generateSymmetricKey: ";
    String str2 = "EXCEPTION LOG";
    String ePin = this.encryptPin.getValue();
    if (!this.encryptPin.isDefaultValue()) {
        byte[] salt = this.checkPin.getValue();
        byte[] hash = null;
        try {
            hash = MessageDigest.getInstance("SHA-256").digest(ePin.getBytes(StringUtils.UTF8));
            if (salt == null) {
                salt = hash;
                this.checkPin.setValue(salt);
            }
        } catch (Exception e) {
            Log.e(str2, str, e);
        }
        if (new BlobAppField(hash).equals(this.checkPin)) {
            try {
                return new SecretKeySpec(SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(new PBEKeySpec(ePin.toCharArray(), salt, 10000, 256)).getEncoded(), "AES");
            } catch (Exception e2) {
                Log.e(str2, str, e2);
                throw new RuntimeException(e2);
            }
        } else {
            throw new RuntimeException("Invalid Pin");
        }
    } else {
        throw new RuntimeException("Unset Pin");
    }
}


// LoginActivity.java
public boolean validateFields(Map<EditText, String> fieldMap) {
    ParameterValidatorClass pvalidator = new ParameterValidatorClass();
    this.mChatUserNameField.setError(null);
    this.mPinField.setError(null);
    for (EditText field : fieldMap.keySet()) {
        try {
            String fName = (String) fieldMap.get(field);
            if (field != null) {
                String fText = field.getText().toString();
                if (fText.isEmpty()) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(fName);
                    sb.append(": ");
                    sb.append(getString(R.string.error_field_required));
                    field.setError(sb.toString());
                    return false;
                } else if (field == this.mChatUserNameField && !pvalidator.isValidUserName(fText)) {
                    field.setError(getString(R.string.error_invalid_userName));
                    return false;
                } else if (field == this.mPinField && !pvalidator.isValidPin(fText)) {
                    field.setError(getString(R.string.error_invalid_pin)); // "This pin is invalid. Must be EXACTLY 6 number character..."
                    return false;
                }
            } else {
                StringBuilder sb2 = new StringBuilder();
                sb2.append("Fatal Error: ");
                sb2.append(fName);
                sb2.append(" was null.");
                throw new RuntimeException(sb2.toString());
            }
        } catch (Exception e) {
            Log.e("EXCEPTION LOG", "validateFields: ", e);
            Toast.makeText(getApplicationContext(), e.getMessage(), 1).show();
        }
    }
    return true;
}
```

* The bruteforce_pin method can be written as follow in Python:
```python
def brute_force_pin(hexdigest):
    for pin in ['{:06d}'.format(i) for i in range(0, 1000000)]:
        digest = hashlib.sha256(pin.encode()).hexdigest()
        if hashlib.sha256(pin.encode()).hexdigest() == hexdigest:
            print('\n[+] PIN: %s' % pin)
            break;
```

* The code below shows how `terrortime` app encrypts and decrypts the client secret:

```java
public final byte[] getOAuth2ClientSecret(String pin) {
    return decryptClientBytes(pin, this.oAuth2ClientSecret.getValue());
}

public final void setOAuth2ClientSecret(String pin, byte[] clientSecret) {
    if (clientSecret != null) {
        this.oAuth2ClientSecret.setValue(encryptClientBytes(pin, clientSecret));
    }
}

public final SecretKey generateSymmetricKey() throws Exception {
    String str = "generateSymmetricKey: ";
    String str2 = "EXCEPTION LOG";
    String ePin = this.encryptPin.getValue();
    if (!this.encryptPin.isDefaultValue()) {
        byte[] salt = this.checkPin.getValue();
        byte[] hash = null;
        try {
            hash = MessageDigest.getInstance("SHA-256").digest(ePin.getBytes(StringUtils.UTF8));
            if (salt == null) {
                salt = hash;
                this.checkPin.setValue(salt);
            }
        } catch (Exception e) {
            Log.e(str2, str, e);
        }
        if (new BlobAppField(hash).equals(this.checkPin)) {
            try {
                return new SecretKeySpec(SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(new PBEKeySpec(ePin.toCharArray(), salt, 10000, 256)).getEncoded(), "AES");
            } catch (Exception e2) {
                Log.e(str2, str, e2);
                throw new RuntimeException(e2);
            }
        } else {
            throw new RuntimeException("Invalid Pin");
        }
    } else {
        throw new RuntimeException("Unset Pin");
    }
}

public byte[] encryptClientBytes(String pin, byte[] data) throws Exception {
    byte[] rst = null;
    SecretKey dbKey = generateSymmetricKey();
    String str = "encryptClientBytes";
    if (data == null) {
        Log.d(str, "Empty (NULL) Client variable passed to function. This very well might NOT be an error.");
    } else {
        try {
            rst = CryptHelper.aesEncrypt_ECB(dbKey, data);
        } catch (Exception e) {
            throw new Exception(e);
        }
    }
    if (data != null) {
        Log.d(str, "Ran successfully on non-NULL value.");
    }
    return rst;
}

public byte[] decryptClientBytes(String pin, byte[] data) throws Exception {
    byte[] rst = null;
    SecretKey dbKey = generateSymmetricKey();
    String str = "decryptClientBytes";
    if (data == null) {
        Log.d(str, "Empty (NULL) Client variable passed to function. This very well might NOT be an error.");
    } else {
        try {
            rst = CryptHelper.aesDecrypt_ECB(dbKey, data);
        } catch (Exception e) {
            throw new Exception(e);
        }
    }
    if (data != null) {
        Log.d(str, "Ran successfully on non-NULL value.");
    }
    return rst;
}

public static byte[] aesEncrypt_ECB(SecretKey key, byte[] data) {
    Cipher msgCipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
    msgCipher.init(1, key);
    return msgCipher.doFinal(data);
}

public static byte[] aesDecrypt_ECB(SecretKey key, byte[] data) {
    Cipher msgCipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
    msgCipher.init(2, key);
    return msgCipher.doFinal(data);
}
```

* The algorithm used: `AES/ECB/PKCS5Padding`, `PBKDF2WithHmacSHA256`. In Python, we can do as follow:

```python
import binascii
import hashlib
from Crypto.Cipher import AES

def generate_symmetric_key(encryptPin):
    salt = hashlib.sha256(encryptPin.encode()).digest()
    return hashlib.pbkdf2_hmac('sha256', encryptPin.encode(), salt, 10000)

def aes_encrypt_ecb(encryptPin, encoded_bytes):
    symmetric_key = generate_symmetric_key(encryptPin)
    cipher = AES.new(symmetric_key, AES.MODE_ECB)
    return cipher.encrypt(encoded_bytes)

def aes_decrypt_ecb(encryptPin, encrypted_bytes):
    symmetric_key = generate_symmetric_key(encryptPin)
    cipher = AES.new(symmetric_key, AES.MODE_ECB)
    return cipher.decrypt(encrypted_bytes)
```

* Run [bruteforce_db.py](./bruteforce_db.py) against the sqlite database to get the arrested terrorist account:

```shell
$ python3 bruteforce_db.py -d clientDB.db
[*] Bruteforce PIN for client "elias--vhost-1310@terrortime.app"
[*] 390164 b86e32c2871c2f9775be56aec62e27b0f9a5ced9ed8d8517f64c35812520afdc

[*] CLIENT INFO -- elias--vhost-1310@terrortime.app
[+]    xmpp_ip: chat.terrortime.app
[+]    auth_ip: register.terrortime.app
[+]    id: elias--vhost-1310@terrortime.app
[+]    secret: yGdkc4nujYW5A0
[+]    pin: 390164
```


* Start an Android emulator, then run command below to install [terrorTime.apk](../assets/terrorTime.apk)
```shell
$ adb devices
List of devices attached
emulator-5554	device

$ adb -s emulator-5554 install terrorTime.apk
Performing Streamed Install
Success
```

* After registering a account and logging in as the arrested terrorist `elias`, we cannot see the messages or chat history as the public and private key on our emulator are different from the keys on elias's phone.

* Next step, we need to copy the keys from the elias's database to our emulator's database in order to get the messages.

* Run [push_keys.sh](./push_keys.sh) to push elias's `key.pub` and `key.priv` to emulator's `databases` directory. 
```shell
$ ./push-keys.sh clientDB.db 
[+] Pushed key.pub to emulator's databases directory
[+] Pushed key.priv to emulator's databases directory
```

* Then use `adb` and `sqlite3` tools to update the database in emulator:
```shell
$ adb root

$ adb shell
generic_x86_64:/ # cd /data/data/com.badguy.terrortime/databases

generic_x86_64:/data/data/com.badguy.terrortime/databases # ls -la
total 44
drwxrwx--x 2 u0_a68 u0_a68  4096 2019-09-29 00:12 .
drwx------ 5 u0_a68 u0_a68  4096 2019-09-28 21:22 ..
-rw-rw-rw- 1 root   root   24576 2019-09-29 00:11 clientDB.db
-rw-rw-rw- 1 root   root    1680 2019-09-29 00:05 key.priv
-rw-rw-rw- 1 root   root     451 2019-09-29 00:05 key.pub

generic_x86_64:/data/data/com.badguy.terrortime/databases # sqlite3
SQLite version 3.22.0 2018-01-22 18:45:57
Enter ".help" for usage hints.
Connected to a transient in-memory database.
Use ".open FILENAME" to reopen on a persistent database.

sqlite> .open clientDB.db

sqlite> UPDATE Clients SET pubkey=readfile("key.pub"), privkey=readfile("key.priv") WHERE cid="elias--vhost-1310@terrortime.app";

sqlite> .exit
```

* Now we can login as `elias` on our emulator and see the chat history.

* Based on the chat history, we can deduce that `Mendel` is the leader and the action date is `2020-09-08 17:54:00 UTC` (1 day after the Labor Day 2020)

```
# Convert to UNIX timestamp
date -d "2020-09-08 17:54:00 UTC" +"%s"
1599587640
```

### Summary
1. Arrested terrorist account
* XMPP server: `chat.terrortime.app`
* OAUTH server: `register.terrortime.app`
* Client ID: `elias--vhost-1310@terrortime.app`
* Client secret: `yGdkc4nujYW5A0`
* PIN: `390164`

2. Terror Cell Leader info
* Username: `mendel--vhost-1310@terrortime.app`

3. Action Date
* Datetime: `2020-09-08 17:54:00 UTC`
* Unix timestamp: `1599587640`
