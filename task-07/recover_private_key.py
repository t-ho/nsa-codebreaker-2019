from Crypto.PublicKey import RSA
import argparse
import base64
import json
import hashlib
import sympy
import util

default_keyparams = {
    "256": {
        "params": {
            "privkey": "",
            "pubkey":
            "-----BEGIN PUBLIC KEY-----\nMDwwDQYJKoZIhvcNAQEBBQADKwAwKAIhAL3q7biU1tvH+280YFAetHj2AdynPE0F\n3m29lGXiz7ABAgMBAAE=\n-----END PUBLIC KEY-----\n",
            "r1": "E+3pDVN853hZnbloDQq2cuZNSf6+egdQLxm3lxg9Kso=",
            "r2": "aUfuNx47CquEh78rf2a1rzxrgquj3lIRDoMUx04gPH0="
        }
    },
    "512": {
        "params": {
            "privkey":
            "",
            "pubkey":
            "-----BEGIN PUBLIC KEY-----\nMFswDQYJKoZIhvcNAQEBBQADSgAwRwJAId7KyhItIk+zWp2L7rEKURr94WGgv1pa\nFyjGzFsblUpwEZid6vO3be4qvyOD34RpuZabZBjUT7EvsVhMejFj4QIDAQAB\n-----END PUBLIC KEY-----\n",
            "r1":
            "JNFP6sk10OqdMsJitbYPX6X+yGMYCfRhvfBAq/e4Q+j1w/xRW7iCGPfqh/zzN3Z5ChipkdB4qu+lh28ya6NWKQ==",
            "r2":
            "JH5XXqQVFd2lWwZgLrNrGpuz7AZXckaC3ve+Iyzmvz2/cPm7ltMHy0UWvb+L4pO8bF3qIfV+c/0LZPC3/1qFaw=="
        }
    },
    "1024": {
        "params": {
            "privkey":
            "",
            "pubkey":
            "-----BEGIN PUBLIC KEY-----\nMIGeMA0GCSqGSIb3DQEBAQUAA4GMADCBiAKBgDg7pAaqadA1GiOkS3r9Eh17Lw+6\n0ZgGd9zM0IGGUkLLbEoJOvwNSZ7MxU0tl/JX3U39oB/MCA/cimBept6GEIvB4OPr\nviWzyWoQ3iDgJUDbrAZAz4pTPX/NmQskNga0y9sem1Rbxq80i3AZ9wcjJ0SYSoEV\n1PCa4HOyj3DDEkj3AgMBAAE=\n-----END PUBLIC KEY-----\n",
            "r1":
            "HgXs0Dzbwl1oBPPsZw4RLAM1RyqxiNu4glAbkIejyW9aB+pI911saiAT2DcXXukCqAWHdpHG4BAWL8mIQve+03ZOiXkadaYYN6W1TETWmEsziXlWu2lPeuuMvq5lRBADMW/JM8pwO8ykqIAnyb4DjaMYzIjA1IMRYOHP7w7jhRY=",
            "r2":
            "P72R/c2IGTIjZFw38tFsUaJ7KIJDDIN7UeRBVzrY6066PTw3ojx8opPie6tInY3VuMw71QU3btegfpcHEyao2vOp6ENAaInqNna/wHiNmDDj3tO5wNvNe58Lt+p+L3IjdxAK4V0Vaws7f8tB27iK/AEeKdDAn2xtjgUerQXpYqI="
        }
    }
}


class Keyparams(object):
    def __init__(self, keyparams):
        self.data = keyparams

    def get_privkey(self, keysize):
        return self.data[str(keysize)]['params']['privkey']

    def get_pubkey(self, keysize):
        return self.data[str(keysize)]['params']['pubkey']

    def get_r1(self, keysize):
        return self.data[str(keysize)]['params']['r1']

    def get_r2(self, keysize):
        return self.data[str(keysize)]['params']['r2']

    def set_privkey(self, keysize, privkey):
        self.data[str(keysize)]['params']['privkey'] = privkey.exportKey(
            'PEM').decode('ascii')


def get_arguments():
    parser = argparse.ArgumentParser()
    parser.add_argument(
        '-p',
        '--pubkey',
        dest='pubkey_filename',
        help='The public key (RSA-512, RSA-1024, RSA-2048) file name')
    parser.add_argument('-k',
                        '--keyparams',
                        dest='keyparams',
                        help='The key params file name')
    parser.add_argument('-o',
                        '--output',
                        dest='output_filename',
                        help='The output file name')
    options = parser.parse_args()
    if not options.pubkey_filename:
        parser.error(
            '[-] Please specify the public key file name, use --help for more info.'
        )
    if not options.output_filename:
        parser.error(
            '[-] Please specify the output file name, use --help for more info.'
        )
    return options


def init(keyparams):
    # use https://www.alpertron.com.ar/ECM.HTM to find the prime factors of n_256
    p = 260102157123667008590537578252202112167
    pubkey = RSA.importKey(keyparams.get_pubkey(256))
    privkey = util.rsa_construct_private_key(p, pubkey)
    if privkey:
        keyparams.set_privkey(256, privkey)
    return keyparams


def bytes_xor(a_bytes, b_bytes):
    min_len = len(a_bytes)
    result = bytearray(b_bytes)
    if min_len > len(b_bytes):
        min_len = len(b_bytes)
        result = a_bytes[:]
    for i in range(0, min_len):
        result[i] = a_bytes[i] ^ b_bytes[i]
    return result


def permute_r_key(r, keysize):
    r_bytes = util.to_bytes(r)
    if keysize == 512:
        return util.to_number(hashlib.sha256(r_bytes).digest())
    elif keysize == 1024:
        return util.to_number(hashlib.sha512(r_bytes).digest())
    elif keysize == 2048:
        first_hash = hashlib.sha512(r_bytes).digest()
        second_hash = hashlib.sha512(first_hash).digest()
        return util.to_number(first_hash + second_hash)
    else:
        print('[-] Cannot permute r key')
        return None


def determine_key_size(pubkey):
    keysize = pubkey.size()
    if keysize < 384:
        keysize = 256
    elif keysize < 768:
        keysize = 512
    elif keysize < 1536:
        keysize = 1024
    else:
        keysize = 2048
    return keysize


def recover_privkey_helper(pubkey, keysize, keyparams):
    print('[*] [RSA-%d] Finding the prime number p...' % keysize)
    n_bytes = util.to_bytes(pubkey.n)

    kp_keysize = keysize // 2
    kp_pubkey = RSA.importKey(keyparams.get_pubkey(kp_keysize))
    kp_r2_bytes = base64.b64decode(keyparams.get_r2(kp_keysize))
    kp_r2 = util.to_number(kp_r2_bytes)
    kp_privkey = RSA.importKey(keyparams.get_privkey(kp_keysize))

    encrypted_p_xor_r1_xor_r2 = n_bytes[0:len(kp_r2_bytes)]
    original_kp_r1_bytes = base64.b64decode(keyparams.get_r1(kp_keysize))

    for i in range(0, 0xffffff):
        kp_r2_bytes = util.to_bytes(kp_r2)
        encrypted_p_xor_r1 = bytes_xor(encrypted_p_xor_r1_xor_r2, kp_r2_bytes)
        p_xor_r1_bytes = util.rsa_decrypt(kp_privkey,
                                          bytes(encrypted_p_xor_r1))
        kp_r1 = util.to_number(original_kp_r1_bytes)
        for j in range(0, 0xa):
            for k in range(0, 0xa):
                kp_r1_bytes = util.to_bytes(kp_r1)
                p_bytes = bytes_xor(p_xor_r1_bytes, kp_r1_bytes)
                p = util.to_number(p_bytes)
                util.print_message('[RSA-%d] [%d:%d:%d] %d' %
                                   (keysize, i, j, k, p))
                if sympy.isprime(p) and sympy.isprime((p - 1) // 2):
                    privkey = util.rsa_construct_private_key(p, pubkey)
                    if privkey:
                        print('\n[+] [RSA-%d] p = %d' % (keysize, p))
                        print('[+] [RSA-%d] Private key is recovered' %
                              keysize)
                        return privkey
                kp_r1 += 1
            kp_r1 = permute_r_key(kp_r1, keysize)
        kp_r2 += 1
    print('\n[-] [RSA-%d] Cannot recover the private key' % keysize)
    return None


def recover_privkey(pubkey, keyparams):
    keysize = determine_key_size(pubkey)
    kp_keysize = 512
    while kp_keysize < keysize:
        kp_privkey = keyparams.get_privkey(kp_keysize)
        if kp_privkey == "":
            print('[*] Recovering keyparams...')
            kp_pubkey = RSA.importKey(keyparams.get_pubkey(kp_keysize))
            kp_privkey = recover_privkey_helper(kp_pubkey, kp_keysize,
                                                keyparams)
            if kp_privkey:
                keyparams.set_privkey(kp_keysize, kp_privkey)
                print('')
            else:
                return None
        kp_keysize *= 2

    print('[*] Recovering private key...')
    return recover_privkey_helper(pubkey, keysize, keyparams)


def export_keys(pem_pubkey, keyparams, output_filename):
    filename_priv_pem = output_filename + '_priv.pem'
    filename_pub_pem = output_filename + '_pub.pem'
    filename_priv_bin = output_filename + '_priv.bin'

    pubkey = RSA.importKey(pem_pubkey)
    privkey = recover_privkey(pubkey, keyparams)
    if privkey:
        util.rsa_write_key_to_pem_file(pubkey, filename_pub_pem)
        util.rsa_write_key_to_pem_file(privkey, filename_priv_pem)
        encrypted_privkey = util.aes_encrypt_ecb('390164',
                                                 privkey.exportKey('PEM'))
        with open(filename_priv_bin, 'wb') as f:
            f.write(encrypted_privkey)
        print('[+] Encrypted private key has been written to file %s' %
              filename_priv_bin)
        return True
    return False


def main():
    print('--:[ TERRORTIME PRIVATE KEY RECOVER ]:--')

    options = get_arguments()

    keyparams = Keyparams(default_keyparams)
    if options.keyparams:
        keyparams = Keyparams(util.read_json_file(options.keyparams))

    keyparams = init(keyparams)

    with open(options.pubkey_filename, 'r') as f:
        pem_pubkeys = f.read().split(':')

    print('[+] %d public keys found in the file %s\n' %
          (len(pem_pubkeys), options.pubkey_filename))

    count = 0
    for i, pem_pubkey in enumerate(pem_pubkeys):
        output_filename = '{0}_{1:02d}'.format(options.output_filename, i + 1)
        is_success = export_keys(pem_pubkey, keyparams, output_filename)
        if is_success:
            count += 1
        print('')

    print('[+] %d private key(s) recovered' % count)


if __name__ == '__main__':
    main()