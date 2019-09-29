#!/bin/bash
# Extract pubkey and privkey from specified database and store to key.pub and key.priv.
# Then push them into connected emulator's databases directory.

if [ $# != 1 ]; then
  echo "[-] Error: Please specify the database"
  echo "[*] Usage: $0 [database]"
  exit 1
fi

temp_dir=`mktemp -d`
cp "$1" ${temp_dir}/database.db
cd $temp_dir

status=0

# save pubkey to file
sqlite3 database.db 'select hex(pubkey) from Clients' | tr -d '\n' | xxd -r -p > key.pub
status=$(($status + $?))

# save privkey to file
sqlite3 database.db 'select hex(privkey) from Clients' | tr -d '\n' | xxd -r -p > key.priv
status=$(($status + $?))

adb root &> /dev/null
status=$(($status + $?))

adb push key.pub /data/data/com.badguy.terrortime/databases/ &> /dev/null
status=$(($status + $?))
adb push key.priv /data/data/com.badguy.terrortime/databases/ &> /dev/null
status=$(($status + $?))

if [ $status -eq 0 ]; then
  echo "[+] Pushed key.pub to emulator's databases directory"
  echo "[+] Pushed key.priv to emulator's databases directory"
else
  echo "[-] Error: Failed"
fi

rm -Rf ${temp_dir}/*
