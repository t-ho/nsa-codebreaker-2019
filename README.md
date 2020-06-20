# NSA Codebreaker Challenge 2019 (Sep-20-2019 to Jan-10-2020)

## UPDATE 2020-06-19: Received the prize from NSA 

![medallion](./assets/medallion.png)

See the [shellcode](./medallion-shellcode/README.md)

**The challenge can be found [here](https://codebreaker.ltsnet.net/challenge)**

## Background
DISCLAIMER - The following is a FICTITIOUS story meant for providing realistic context for the Codebreaker Challenge and is not tied in any way to actual events.

Tech savvy terrorists have developed a new suite of communication tools to use for attack planning purposes. Their most recent creation — TerrorTime — is a secure mobile chat application that runs on Android devices. This program is of particular interest since recent intelligence suggests the majority of their communications are happening via this app. Your mission is to reverse-engineer and develop new exploitation capabilities to help discover and thwart future attacks before they happen. There are 7 tasks of increasing difficulty that you will be working through as part of this challenge. Ultimately, you will be developing capabilities that will enable the following:

* Spoof TerrorTime messages
* Masquerade (i.e., authenticate) as TerrorTime users without knowledge of their credentials
* Decrypt TerrorTime chat messages

The first three tasks of the challenge will provide you with everything you need to install and run TerrorTime in an Android emulator. You will also discover account information for two TerrorTime users, which will enable you to send chat messages between the users by running the app in two emulators. Beginning with Task 4, the difficulty will increase significantly as you begin working towards the goals outlined above. We hope you enjoy the challenge!

## Task 1 - It Begins - [Getting Started - Part 1] - [Network Traffic Analysis]

There are many challenges that we will need to overcome in order to exploit TerrorTime. The first is that we do not yet have a copy of it. We have learned few details through intelligence sources, but the terrorists have managed to keep the app hidden. It is not publicly available on any App Store. Fortunately, as part of a recent military operation, specialized collection gear was forward deployed near the terrorist's area of operations. This resulted in a trove of collected traffic and we need your help to analyze it. Your first task is to find and extract a copy of the TerrorTime Android Package (APK) file from the packet capture. Submit the APK's SHA256 hash. This will help us search other intelligence sources for connections to users. To test out the app, we also need the registration information their leadership uses to register each client. Analyze the packet capture for this data and submit the registration information for 2 clients (in any order).

* Download:
  * [Capture Traffic (terrortime.pcapng)](./task-01/terrortime.pcapng)

#### Solution can be found [here](./task-01/README.md)

## Task 2 - Permissions - [Getting Started - Part 2] - [Mobile APK Analysis]

The TerrorTime APK file contains metadata that describes various security properties of the application that we want to know. Since we now have a copy of the APK thanks to the military operation described in Task 1, we need you to identify and submit the following:

1. App Permissions
2. The SHA256 hash of the Code Signing Certificate
3. The Common Name of the Certificate Signer

Please note that completion of task 1 is required to complete this task.

* Download:
  * [Capture Traffic (terrortime.pcapng)](./task-01/terrortime.pcapng)

#### Solution can be found [here](./task-02/README.md)

## Task 3 - Turn of Events - [Getting Started - Part 3] - [Database Analysis]

Analysts found TerrorTime installed on a device that was taken from a terrorist arrested at a port of entry in the US. They were able to recover the SQLite database from the TerrorTime installation on the device, which should provide us with more in-depth knowledge about the terrorist's communications and the TerrorTime infrastructure. Your goal for this task is to analyze the database and submit the addresses of the TerrorTime OAUTH (authentication) and XMPP (chat) servers.

* Download:
  * [Database (clientDB.db)](./assets/clientDB.db)

#### Solution can be found [here](./task-03/README.md)

## Task 4 - Schemes - [Cryptography - Reverse Engineering - Language Analysis]

While analyzing the TerrorTime SQLite database found on the terrorist’s device, analysts discovered that the database has cached credentials but requires a pin to log in. If we can determine how the credentials are protected and find a way to recover the pin, we should be able to masquerade as the arrested terrorist. Perform reverse engineering to identify how the terrorist’s credentials are protected and submit the terrorist's Client ID and Client Secret. Once you have uncovered their credentials, masquerade (i.e., login) as him using the TerrorTime app. Review their chat history and assess additional information regarding their organization. Though the app encrypts messages back and forth, the terrorists have previously spoken in some form of code to ensure their plans were protected. To prove completion of this task, you will need to analyze the terrorist's conversation history in order to uncover/deduce the following information:
1. Terror Cell Leader's Username
2. The date on which the action will occur

#### Solution can be found [here](./task-04/README.md)

## Task 5 - Masquerade - [Vulnerability Analysis]

The app uses a bespoke application of the OAUTH protocol to authorize and authenticate TerrorTime users to the chat service. Our intelligence indicates that individual terrorists are provided phones with TerrorTime installed and pre-registered to them. They simply need to enter their username and secret PIN to access the chat service, which uses OAUTH behind the scenes to generate a unique token that is used for authentication. This is a non-standard way of using the protocol, but they believe it to be superior to normal password-based authentication since a unique token is used per login vs. a static password. Whether that is indeed the case is up to you to analyze and assess for possible vulnerabilities. Our forensics team recovered a deleted file from the terrorist's hard drive that may aid in your analysis.

Through other intelligence means, we know that the arrested terrorist is a member of one of many cells in a larger organization. He has shown no signs of someone who is acting in a leadership role -- he simply carries out orders given to him from his cell leader, who is likely relaying information from the top-level organizational leader. To uncover information from the cell leader’s conversations, we need access to their account. The messages are end-to-end encrypted, so without the leader's private key we won't be able to decrypt his messages, but we may be able to learn more about the members of the cell and the organization's structure. Analyze the client and server-side components of the authentication process and find a way to masquerade as arbitrary users without knowing their credentials. Take advantage of this vulnerability and masquerade as the cell leader. Access and review the cell leader’s relevant information stored on the server. Use this information to identify and submit the top-level organizational leader’s username and go a step further and submit a copy of the last (still encrypted) message body from the organization leader’s chat history. It’s suggested to complete task 4 before attempting this task as task 4 aids in discovering the cell leader’s identity.

* Download:
  * [Authentication Program (auth_verify.pyc)](./task-05/auth_verify.pyc)

#### Solution can be found [here](./task-05/README.md)

## Task 6a - Message Spoofing - [Vulnerability Analysis - Cryptanalysis]

The ability to masquerade as a TerrorTime user is helpful, even when we are not able to access the plaintext version of their messages. We want to be able to send "spoofed" messages (i.e., messages appearing to be from the user we are masquerading as) to other TerrorTime users as a way of disrupting their attack plans. Critically, any conversation we have as the masqueraded user should never be visible to that user the next time they access their account. But complicating matters is the fact that all messages sent and received through TerrorTime are archived on the chat server and downloaded each time a user logs in to their account. For this task, identify a vulnerabilty that will allow you to send/receive messages as a TerrorTime user without that user ever seeing those messages the next time they access their account. To prove your solution, submit the encrypted message body of a spoofed message that was sent from the organization leader to a cell leader. Submit the full client id of the cell leader you chose. Put the organization leader's account in a state such that replies to your spoofed message will never be seen by them, but still readable by you.

#### Solution can be found [here](./task-06/README.md)

## Task 6b - Future Message Decryption - [Vulnerability Analysis - Cryptanalysis]

Though we might be unable to decrypt messages sent and received in the past without a user's private key, it may still be possible to view future messages in the clear. For this task generate a new public/private key pair and make whatever changes are necessary such that all future messages sent/received within TerrorTime may be decrypted with this private key. Critically, you can not disrupt future legitimate conversations between users.

#### Solution can be found [here](./task-06/README.md)

## Task 7 - Distrust - [Reverse Engineering - Cryptography - Exploit Development]


The arrested terrorist (see Task 3) was not cooperative during initial questioning. He claimed we’d never defeat the underlying cryptography implemented in TerrorTime and the only way to read encrypted messages was if you were one of the communicants. After additional questioning, he revealed that he is actually the lead software developer for TerrorTime and the organization leader directed him to provide a secret way of decrypting and reading everyone's messages. He did not divulge how this was possible, but claimed to have engineered another, more subtle weakness as an insurance policy in case of his capture. After receiving this information, the analysts who found TerrorTime on the suspect’s mobile device mentioned seeing an executable called keygen on his laptop. The terrorist confirmed it is an executable version of the library included with TerrorTime. They have shared a copy of the keygen executable for you to reverse engineer and look for potential vulnerabilities. As expected from the terrorist's statement, the chats stored on the server are all encrypted. Based on your analysis of keygen, develop an attack that can decrypt any TerrorTime message, including those sent in the past, and use this capability to decrypt messages from the organization leader to other cell leaders. Completing task 4 and task 5 are recommended before beginning this task. To prove task completion, submit the following information:

1. Plaintext version of the latest encrypted message from the organization leader
2. Enter the future action (i.e., beyond the current one) they are planning
3. The target (of the terrorist action’s) identity (First and Last Name)
4. The location where the action is to take place
5. Enter the action planned by the terrorists

* Download:
  * [TerrorTime Key Generator (keygen)](./task-07/keygen)

#### Solution can be found [here](./task-07/README.md)

