## Database Analysis

**NOTE: Use [terrorTime.apk](../assets/terrorTime.apk) (version 2) for tasks 03-07**

* Use [sqlite3](https://sqlite.org/cli.html) CLI tool to dump the entire [clientDB.db](../assets/clientDB.db) database to ascii text file:
```shell
$ sqlite3 clientDB.db .dump > clientDB.txt
```
* The `clientBD.txt` can be found [here](../assets/clientDB.txt)

* Extract `xsip` and `asip` values that are domain names of XMPP server and OAUTH servers respectively.
```shell
$ sqlite3 clientDB.db 'select xsip, asip from Clients' | tr "|" " " | xargs -n 1 nslookup
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
* Extract other useful info from the database:
```shell
# clientID
$ sqlite3 assets/clientDB.db 'select cid from Clients'
elias--vhost-1310@terrortime.app
```
### Summary
1. Server Info:
* XMPP:
  * Domain name: `chat.terrortime.app`
  * IP address: `54.91.5.130`
* OAUTH:
  * Domain name: `register.terrortime.app`
  * IP address: `54.197.185.236`

2. Arrested terrorist account:
* Client ID: `elias--vhost-1310@terrortime.app`
