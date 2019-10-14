from Crypto.PublicKey import RSA
from Crypto.Cipher import AES
from Crypto.Cipher import PKCS1_OAEP
import math
import json
import sympy
import sys
import hashlib


def rsa_read_key_from_pem_file(filename):
    with open(filename, 'r') as f:
        pem_key = f.read()
        return RSA.importKey(pem_key)


def rsa_write_key_to_pem_file(key, output_filename):
    pem_key = key.exportKey('PEM').decode('ascii')
    with open(output_filename, 'w') as f:
        f.write(pem_key)

    key_name = 'Public'
    if key.has_private():
        key_name = 'Private'

    print('[+] %s key has been written to file %s' %
          (key_name, output_filename))


def generate_symmetric_key(encryptPin):
    salt = hashlib.sha256(encryptPin.encode()).digest()
    return hashlib.pbkdf2_hmac('sha256', encryptPin.encode(), salt, 10000)


def pad(message):
    block = 16
    return message + (block - len(message) % block) * b'\n'


def aes_encrypt_ecb(encryptPin, encoded_bytes):
    encoded_bytes = pad(encoded_bytes)
    symmetric_key = generate_symmetric_key(encryptPin)
    cipher = AES.new(symmetric_key, AES.MODE_ECB)
    return cipher.encrypt(encoded_bytes)


def read_json_file(filename):
    with open(filename, 'r') as f:
        json_content = f.read()
        return json.loads(json_content)


def rsa_encrypt_PKCS1_OAEP(pubkey, message):
    encryptor = PKCS1_OAEP.new(pubkey)
    return encryptor.encrypt(message)


def rsa_encrypt(pubkey, message):
    return pubkey.encrypt(message, 0)[0]


def rsa_decrypt_PKCS1_OAEP(privkey, encrypted_message):
    decryptor = PKCS1_OAEP.new(privkey)
    return decryptor.decrypt(encrypted_message)


def rsa_decrypt(privkey, encrypted_message):
    return privkey.decrypt(encrypted_message)


def num_bits(number):
    return len(bin(number)[2:])


def num_bytes(number):
    return math.ceil(num_bits(number) / 8)


def print_message(message):
    if len(message) > 100:
        message = message[:100] + '...'
    sys.stdout.write('\r[*] %s' % message)
    sys.stdout.flush()


def rsa_validate_privkey(pubkey, privkey):
    message = b'0x8861'
    encrypted_message = rsa_encrypt(pubkey, message)
    try:
        decrypted_message = rsa_decrypt(privkey, encrypted_message)
        if message == decrypted_message:
            return True
    except:
        return False


def rsa_construct_private_key(p, pubkey):
    n = pubkey.n
    e = pubkey.e
    q = n // p
    if sympy.isprime(p) and sympy.isprime(q):
        d = sympy.mod_inverse(e, n - (p + q - 1))
        privkey = RSA.construct((n, e, d, p, q))
        if rsa_validate_privkey(pubkey, privkey):
            return privkey
    return None


def to_bytes(number):
    return number.to_bytes(num_bytes(number), 'big')


def to_number(bigend_bytes):
    return int.from_bytes(bigend_bytes, 'big')
