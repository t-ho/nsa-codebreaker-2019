## Database Analysis

**NOTE: Use [terrorTime.apk](../assets/terrorTime.apk) (version 2) for tasks 03-07**

* Use [sqlite3](https://sqlite.org/cli.html) CLI tool to dump the entire [clientDB.db](../assets/clientDB.db) database to ascii text file:
```shell
$ sqlite3 clientDB.db .dump > clientDB.txt
```
* The `clientBD.txt` can be found [here](../assets/clientDB.txt)

* Extract `xsip` and `asip` values that are domain names of XMPP server and OAUTH servers respectively.
```shell
$ cat clientDB.txt | grep -i 'clients value' | cut -z -d , -f 4,8 | sed "s/'//g" | xargs -d , -n 1 nslookup
Server:		10.0.3.1
Address:	10.0.3.1#53

Non-authoritative answer:
Name:	chat.terrortime.app
Address: 54.91.5.130

Server:		10.0.3.1
Address:	10.0.3.1#53

Non-authoritative answer:
register.terrortime.app	canonical name = codebreaker.ltsnet.net.
Name:	codebreaker.ltsnet.net
Address: 54.197.185.236
```
* XMPP server: chat.terrortime.app - 54.91.5.130
* OAUTH server: register.terrortime.app - 54.197.185.236