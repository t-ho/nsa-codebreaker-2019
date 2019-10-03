## Task 06 - Message Spoofing - Future Message Decryption

* We found that the `terrortime` use `XMPP` `VCardManager` to store the public keys and these keys are used to encrypt the messages. Therefore, in order to prevent the organization leader from seeing the replies to spoofed messages and the spoofed messages, all we need to do is replacing the public keys of the organization leader with our public key in the `VCardManager` while we are spoofing messages to other cell leaders.

* First, we generate our public and private keys by register a new account in the `terrortime` app using `elias` key secret.

* Then pull the database from the emulator to our machine:
```shell
$ adb root

$ adb -s emulator-5554 pull /data/data/com.badguy.terrortime/databases/clientDB.db
/data/data/com.badguy.terrortime/databases/clientDB.db: 1 file pulled. 28.3 MB/s (24576 bytes in 0.001s)
```

* Run the [bruteforce_db.py](./bruteforce_db.py) script to get your public and private key files.
```shell
$ python3 bruteforce_db.py -d clientDB.db
[*] Public key is saved to pem_my_key.pub file
[*] Private key is saved to pem_my_key.priv file
[*] Encrypted private key is saved to bin_my_key.priv file
```

* Key files can be found:
  * [bin_my_key.priv](../assets/TerrorTimeSol/bin_my_key.priv)
  * [pem_my_key.priv](../assets/TerrorTimeSol/pem_my_key.priv)
  * [pem_my_key.pub](../assets/TerrorTimeSol/pem_my_key.pub)

* After obtaining the keys, run the [TerrorTimeSol](../assets/TorrorTimeSol) app, and choose option 3 to masquerade as `julianna`.

* Then update the database in the emulator so that we can log in as `julianna` to send spoofed messages.

```shell
adb shell
generic_x86_64:/ # cd /data/data/com.badguy.terrortime/databases
generic_x86_64:/data/data/com.badguy.terrortime/databases # sqlite3
SQLite version 3.22.0 2018-01-22 18:45:57
Enter ".help" for usage hints.
Connected to a transient in-memory database.
Use ".open FILENAME" to reopen on a persistent database.
sqlite> .open clientDB.db
sqlite> UPDATE Clients SET cid="julianna--vhost-1310@terrortime.app", xname="julianna--vhost-1310@terrortime.app" WHERE cid="elias--vhost-1310@terrortime.app";
sqlite> .exit
```

* Choose option 4, to add our public key to all account for future decryption


### Summary
1. Use [TerrorTimeSol](../assets/TerrorTimeSol) app (choose option 3) to masquerade any `terrortime` user.
2. Our key files for future decryption:
  * [bin_my_key.priv](../assets/TerrorTimeSol/bin_my_key.priv)
  * [pem_my_key.priv](../assets/TerrorTimeSol/pem_my_key.priv)
  * [pem_my_key.pub](../assets/TerrorTimeSol/pem_my_key.pub)
