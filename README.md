# Fair access Framework (the github repos is just a small part of the project)
3rd Reward at the CSAW 2016
**Special credit : AAfaf ouaddah (protocol desing)**
**Developement Team : Badr Bellaj Fahd Iaaza James Andro** 

![alt tag](https://preview.ibb.co/fKho6F/IMG_20161116_155038.jpg )

FairAccess system is based on bitcoinj (Java implementation or the Bitcoin Core) providing an access control Framework for IOT using the Bitcoin Blockchain. 
FairAccess provides several useful mechanisms using the blockchain. In fact, it is considered as a database or, a policy retrieval point, where all encrypted access control policies for each resource are stored in form of transactions. It plays the role of  a public witness that garantees the correct enforcement of pre-defined access control policies through the consensus mechanims. it serves also as logging databases that ensures auditing functions. Furthermore, it prevents forgery of token through transactions integrity checks and detects token reuse through the double spending detection mechanism
This project helps users to define the control access policy and ditribute accesss tokens to the requesters. the Tokens are exchanged over cutsom bitcoin's transactions.

![Image of BTC tokens Schema](https://s11.postimg.org/90khsvjub/schema.png)
<br>
![Image of BTC tokens transaction](https://s15.postimg.org/qtfa361hn/tx1.png)
**Notice:**
To run the code you need to setup a local bitcoin node by installing bitcoin core. <br>
**This project is a poc for a scientific paper.**



**USE CASE:**
In this scenario, the system is a home webcam security system. It is built using a raspberry pi 2 board with its dedicated camera .The Raspberry pi is connected to an Ethernet LAN providing a remote access to this system.
In this case, the camera represent the resource to control access to, in consequence the authorized clients, depending ontheir rights could perform multiple actions (video reording, live streaming, Time-Lapse Photography etc.).
As a proof of concept, we will take a snapshot and save it on the raspberry pi SD card, define our control policy and give the clients a remote access using a token distribution over our local blockchain. 
Components:
The raspberry pi 2 is a low coast, credit-card sized computer which includes quad-core ARM Cortex-A7 CPU and 1 GB of RAM. It’s capable of running many Linux distributions and recently Windows 10.  
The Raspberry Pi camera module can be used to take high-definition video. It has a five megapixel fixed-focus camera that supports 1080p30, 720p60 and VGA90 video modes, as well as stills capture. It attaches via a 15cm ribbon cable to the CSI port on the Raspberry Pi.
The Raspberry Pi with its attached camera makes a good IOT system.
Setting up the camera software
Step 1: Install Raspbian on your RPi
Step 2: Attach camera to RPi and enable camera support 
Detailed information could be found in : (http://www.raspberrypi.org/camera)
Setting up the camera software
In order to use the camera module, there are three applications provided, raspistill, raspivid and raspistillyuv. raspistill and raspistillyuv are very similar and are intended for capturing images, raspivid is for capturing video. In this scenario we will use 

raspistill -v -o Token1.jpg
The camera will take a picture within 5 second, save it to the file Token1.jpg. by giving the –V option we will get various informational messages.
Setting up the Bitcoin node. And the wallet
The bitcoin installation process could be found under this link : http://raspnode.com/diyBitcoin.html
After a successful installation, we create the file bitcoin.conf and edit it, adding an rpc user and a password.
bitcoin.conf should look something like this:
rpcuser=myuser
rpcpassword=21Hy2d5kycuoLzWxdJjQoVN1jtL7Q5kzqhHz3ZfuYNCU

Afterward we could run our bitcoin node using bitcoind –regtest to join the local blockchain. And the bitcoin client  bitcoin-cli –regtest.
The -regtest option helps us to get a self-contained testing environment: everything (the Peers included could be running on my machine).
Due to the technical constraints of the IOT objects, in this scenario we use Lightweight wallets. This kind of wallets use a simplified payment verification (SPV) mode which only requires them to download part of the blockchain.
For a testing environment, it’s safer and costless to use Bitcoin’s test network (testnet) or regression test mode (regtest). In this scenario we are using a Regtest, where everything (the Peers included could be running on the local machine or on our LAN).
Program anatomy

Bitcoinj is a Java implementation of the Bitcoin protocol. This library provide us a wallet, and perform the required operation: send/receive transactions without needing a local copy of Bitcoin Core and has many other advanced features. Bitcoin is suitable for usage on constrained devices providing the SPV mode. 
A  Getting started tutorial for Java, on how to use the library is available https://bitcoinj.github.io/getting-started.
Our system aims to distribute access tokens across the bitcoin network inside financial transactions exchanged between the actors. These transactions flows between wallets, they are digitally signed for security reason and recorded after confirmation into the blockchain. Bitcoin uses a scripting system for transactions. In fact, inside the transaction a list of instructions are included to describe how the receiver could spend it, and therefore he can gain access to them and to its contents (in our case the access token). A transaction is valid if the script is excuted without error using the inputs provided by the receiver.
This kind of distribution is benfical, the history of a transaction can be traced back to the point where the bitcoins were produced.
In the rect of this article we will, continue using tokens instead of bitcoins. So our system will exchanges Tokens included on original bitcoins tx.
**OP_return**
The access token is encapsulated inside the output script using the op_return operation. The original blockchain was intended to provide a ledger for financial transactions, not a record for arbitrary data. However, this system provide us the ability to store within OP_RETURN, 80 bytes of arbitrary data in the blockchain. Enough, for our token which is an SHA256 hash concatenated to an 8 bytes custom header as a prefix, the needed size is 40 bytes.
OP_RETURN has the advantage of not creating bogus UTXO entries
Storing data using the op_return script in the main blockchain is used by many services like: 
•	CoinSpark
•	Proof of Existence 
•	Crypto Copyright
•	BlockSign 
•	Open Assets 
•	Stampd
•	Factom
•	Tradle
•	LaPreuve
•	Blockstore 
For example, the proof of existence service, anonymously and securely store an online distributed proof of existence for any document. As they mention on their official website https://proofofexistence.com/about  the document is certified via embedding its SHA256 digest in the bitcoin blockchain. This is done by generating a special bitcoin transaction that encodes/contains the hash via an OP_RETURN script

**What does a simple token transaction look like?**
If A would like to give B access to its controlled resource, he sends an access token to B. To do so he creates a transaction with three pieces of information:
•	Input transaction: To precise which resource address was used to send the token to A in the first place (she received them from miners).
•	B’s bitcoin address.*
•	The access token 
A sign this transaction with his private key and sends it to the network. From there, miners verify the transaction, putting it into a transaction block and record it into the blockchain.

bitcoin-cli getrawtransaction

"vin" : [
    {
        "txid" : "33e25fd78c075dff3b0474911ca2244c5246cb40b0b34653e2fe1ca6f7c26d46",
        "vout" : 1,
        "scriptSig" : {
            "asm" : "3044022009ee6ccde46a58d7e5347ed28e8e946b1cbca3518c5066bcf1809abdfef9ef940220254fa3ed7f80831f08f760958d870a575f612aa5bb67add4cd4eb7d135a75b6a01 02a54cf1e2ecaea3e56721af6e90eb8e934597ee77cd03e060bbed47b8db03a754",
            "hex" : "473044022009ee6ccde46a58d7e5347ed28e8e946b1cbca3518c5066bcf1809abdfef9ef940220254fa3ed7f80831f08f760958d870a575f612aa5bb67add4cd4eb7d135a75b6a012102a54cf1e2ecaea3e56721af6e90eb8e934597ee77cd03e060bbed47b8db03a754"
        },
        "sequence" : 4294967295
    }
the transaction output
"vout" : [
    ...
    {
        "value" : 2.39990000,
        "n" : 1,
        "scriptPubKey" : {
            "asm" : "OP_DUP OP_HASH160 1fab219d5483021bd791453e155f7b956f4b3ed4 OP_EQUALVERIFY OP_CHECKSIG",
            "hex" : "76a9141fab219d5483021bd791453e155f7b956f4b3ed488ac",
            "reqSigs" : 1,
            "type" : "pubkeyhash",
            "addresses" : [
                "miQQFQse4DwFE9rkMkZ78CwvTHkg3sTJmc"
            ]
        }
    }
miQQFQse4DwFE9rkMkZ78CwvTHkg3sTJmc is the address that funded my original transaction

What does a multi-sig transaction look like?

**Program**
In order to setup the access control system, we developed a java program using bitcoinj. we list here the important functions:
Create token
Grant access
..

When running this program, users could send and receive tokens over a simple or multisig transaction. 
   
**Proposition:**
Op_return is opening a debate inside the bitcoin community about if storing non-financial data into the blockchain is acceptable? To avoid such limitation, we propose to fork the bitcoin and to setup a custom blockchain, reserved to record tokens exchange. 
To keep miners verification **  we propose to pay them **
at this stage we exchange just a token’s hash and not the token itself because, as a security reinforcement we aim to … 
 

