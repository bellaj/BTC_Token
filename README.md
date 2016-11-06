# Fair access Framework
FairAccess system is based on bitcoinj (Java implementation or the Bitcoin Core) providing an access control Framework for IOT using the Bitcoin Blockchain. 
FairAccess provides several useful mechanisms using the blockchain. In fact, it is considered as a database or, a policy retrieval point, where all encrypted access control policies for each resource are stored in form of transactions. It plays the role of  a public witness that garantees the correct enforcement of pre-defined access control policies through the consensus mechanims. it serves also as logging databases that ensures auditing functions. Furthermore, it prevents forgery of token through transactions integrity checks and detects token reuse through the double spending detection mechanism
This project helps users to define the control access policy and ditribute accesss tokens to the requesters. the Tokens are exchanged over cutsom bitcoin's transactions.

![Image of BTC tokens Schema](https://s11.postimg.org/90khsvjub/schema.png)
<br>
![Image of BTC tokens transaction](https://s15.postimg.org/qtfa361hn/tx1.png)
**Notice:**
To run the code you need to setup a local bitcoin node by installing bitcoin core. <br>
**This project is a poc for a scientific paper.**


