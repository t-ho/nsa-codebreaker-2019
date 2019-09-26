## Task 02 - Mobile APK Analysis

* Use [apktool]() to decompile `terrortime.apk`
```shell
$ apktool d terrortime.apk
I: Using Apktool 2.4.0-dirty on terrortime.apk
I: Loading resource table...
I: Decoding AndroidManifest.xml with resources...
I: Loading resource table from file: /home/0x8861/.local/share/apktool/framework/1.apk
I: Regular manifest package...
I: Decoding file-resources...
I: Decoding values */* XMLs...
I: Baksmaling classes.dex...
I: Baksmaling classes2.dex...
I: Baksmaling classes3.dex...
I: Copying assets and libs...
I: Copying unknown files...
I: Copying original files...
```

* Extract the app permissions:
```shell
$ cat AndroidManifest.xml | grep -i permission
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
```

* Use `apksigner` tool that is a part of [Android SDK](https://developer.android.com/studio/) and usually can be found in `${ANDROID_HOME}/build-tools/[version]/` directory to find the SHA256 of Signing Cert and Common Name of Signer:
```shell
$ apksigner verify --verbose terrortime.apk 
Verifies
Verified using v1 scheme (JAR signing): false
Verified using v2 scheme (APK Signature Scheme v2): true
Verified using v3 scheme (APK Signature Scheme v3): false
Number of signers: 1

$ apksigner verify --print-certs terrortime.apk 
Signer #1 certificate DN: CN=dev_terrorTime_451164, OU=TSuite
Signer #1 certificate SHA-256 digest: d9c83d0e3c47cde0f432876f1c928e13bf303d0c00ceb1286bcc225555ad6c97
Signer #1 certificate SHA-1 digest: e262f278ae0d997e08925eb3fbce957b150d2e7b
Signer #1 certificate MD5 digest: 28bc74a82e950b47d4f570b2d8bbe67a
```

