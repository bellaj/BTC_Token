/**
 * Created by Bellaj badr on 11/8/2015.
 */

package dev_btc;
import org.bitcoinj.core.*;
import org.bitcoinj.core.listeners.AbstractWalletEventListener;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.params.RegTestParams;
import org.bitcoinj.params.TestNet2Params;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;
import org.bitcoinj.script.ScriptOpCodes;
import org.bitcoinj.store.BlockStore;
import org.bitcoinj.store.BlockStoreException;
import org.bitcoinj.store.MemoryBlockStore;
import org.bitcoinj.utils.BriefLogFormatter;
import org.bitcoinj.utils.MonetaryFormat;
import org.bitcoinj.wallet.KeyChain;
import org.spongycastle.util.Store;
import sun.text.normalizer.UTF16;
import static org.bitcoinj.core.Utils.HEX;

import java.io.*;
import java.math.BigInteger;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main_BTC {

    public String get_token(WalletAppKit k){


       // get address source and return sent

        return "ok";
    }
    /* store_TX() gets Transactions from blocks and stores them in a file */
    static protected void store_TX( NetworkParameters params,WalletAppKit kit) throws BlockStoreException, FileNotFoundException, UnsupportedEncodingException {

        File txf = new File("TX.txt");
        PrintWriter hwriter = new PrintWriter("TX.txt", "UTF-8");

        BlockChain chain = kit.chain();
        BlockStore block_store = chain.getBlockStore();

        StoredBlock stored_block = block_store.getChainHead();
        // if stored_block.prev() returns null then break otherwise get block transactions
        while (stored_block!=null){

            Block block = stored_block.getHeader();
            List<Transaction> tx_list = block.getTransactions();
            if (tx_list != null && tx_list.size() > 0){
                hwriter.println(block.getHashAsString());
            }

            stored_block = stored_block.getPrev(block_store);
        }
        hwriter.close();
    }
    public static void main(String[] args) {

        System.out.println("********************* Morocco Coin******************************************");
        // This line makes the log output more compact and easily read, especially when using the JDK log adapter.
         BriefLogFormatter.init();
         final NetworkParameters params = TestNet3Params.get();


        //cQWVW6Vj3E7qEH4DSxN5HGcE29YTPQhpKBzJGXEcQ7LtWtEZ9YGK
      //  final DumpedPrivateKey dumpedPrivateKey = new DumpedPrivateKey(params, "cMqjVrzQuV6RaTRsQtutxS2jnEJEzp6wi6mXkdgp7SWT8nBXwETe");
      //  final ECKey key = dumpedPrivateKey.getKey();
      //  System.out.println("BTC address that will be added: " + key.toAddress(params));
      //  System.out.println("Private key that will be added: " + key.getPrivateKeyEncoded(params));
     //   Address addressFromKey = key.toAddress(params);
        //System.out.println("On the network, we can use this address:\n" + addressFromKey);

        final WalletAppKit kit = new WalletAppKit(params, new File("testnet"), "1") {
            protected void onSetupCompleted() {

            }
        };
        kit.setAutoSave(true);
         kit.connectToLocalHost();
        System.out.println("************************Download blockchain********************");
        kit.startAsync();
       kit.awaitRunning();
         System.out.println("you could send money to : " + kit.wallet().currentReceiveAddress());
        //System.out.println("******Pending***:"+kit.wallet().getPendingTransactions());
        //System.out.println("******unspent***:"+kit.wallet().getUnspents());

        System.out.println("*********************Votre Wallet *******************************");
        System.out.println("/////////////////////////////////////////////////////////////////////");

        //System.out.println("wallet: " + kit.wallet()); //display all wallt content keys and key to watch and more
       // System.out.println("******GET TRansactions***:"+kit.wallet().getTransactions(true));

        System.out.println("/////////////////////////////////////////////////////////////////////");
        System.out.println("*********************Test de Keys*********************************");
/*
        if (kit.wallet().isPubKeyHashMine(key.getPubKeyHash())) {
            System.out.println("Oui c est ma clé.");
        } else {
            System.out.println("Non cette clé n appartient pas à ce wallet.");
        }*/
        /*for (ECKey k : kit.wallet().getImportedKeys()) {
            System.out.println("private keys encoded :."+k.getPrivateKeyEncoded(params));
        }
*/
        System.out.println("************ Start_op *********************************************");
        System.out.println(" Vous disposez de :"+MonetaryFormat.BTC.noCode().format(kit.wallet().getBalance()).toString()+"Tokens");
        System.out.println(" Give the amount to send :");
        Scanner s=new Scanner(System.in);
        double ti=0;
        ti=s.nextDouble();
        System.out.println(" The amount is set to 0.00001");
     //   ti=0.00001;
        Coin amount = Coin.parseCoin(Double.toString(ti));
        System.out.println("# the amount is to "+ti);
        Address destination = new Address(params,"mfcjN5E6vp2NWpMvH7TM2xvTywzRtNvZWR");//test aacount
       // Wallet.SendRequest req;
        //req=Wallet.SendRequest.to(destination, amount);
        //req.fee=Coin.parseCoin(Double.toString(0.023456));//Set Fee
        System.out.println("t*********************************************************");
        //System.out.println("Transaction Data"+ kit.wallet().getTransaction(new Sha256Hash("98e23a0423a919b4e177dfecb78f3ab5709b021f7af74944a7838884cb6ba527")));
       // System.out.println("t*********************************************************");
       // System.out.println("waiting money "+kit.wallet());


        kit.wallet().addEventListener(new AbstractWalletEventListener() {
            @Override
            public void onCoinsReceived(Wallet w, Transaction tx, Coin prevBalance, Coin newBalance) {
                // Runs in the dedicated "user thread".
                // The transaction "tx" can either be pending, or included into a block (we didn't see the broadcast).
                Coin value = tx.getValueSentToMe(w);
                System.out.println("Received tx for " + value.toFriendlyString() + ": " + tx);
                System.out.println("Transaction will be forwarded after it confirms.");
                System.out.println("coins sent. transaction hash: " + tx.getHashAsString());
                System.out.println("recu tx.getValue(w"+tx.getValue(w));

                // Wait until it's made it into the block chain (may run immediately if it's already there).
                //
                // For this dummy app of course, we could just forward the unconfirmed transaction. If it were
                // to be double spent, no harm done. Wallet.allowSpendingUnconfirmedTransactions() would have to
                // be called in onSetupCompleted() above. But we don't do that here to demonstrate the more common
                // case of waiting for a block.

            }

            @Override
            public void onTransactionConfidenceChanged(Wallet wallet, Transaction tx) {
                System.out.println("-----> confidence changed: " + tx.getHashAsString());
                TransactionConfidence confidence = tx.getConfidence();
                System.out.println("new block depth: " + confidence.getDepthInBlocks());
            }

            @Override
            public void onCoinsSent(Wallet wallet, Transaction tx, Coin prevBalance, Coin newBalance) {
                System.out.println("coins sent");
            }

            @Override
            public void onReorganize(Wallet wallet) {
                System.out.println("wlt onReorganize");

            }

            @Override
            public void onWalletChanged(Wallet wallet) {
                System.out.println("wlt chng");
            }

            @Override
            public void onKeysAdded(List<ECKey> keys) {
                System.out.println("new key added");
            }

            @Override
            public void onScriptsChanged(Wallet wallet, List<Script> scripts, boolean isAddingScripts) {
                System.out.println("new script added");
            }
        });

     /*   kit.wallet().addWatchedAddress(new Address(params, "mpQLtdMxbPCpMzzCnuREMfyaYiU6nY94XK"));
        kit.wallet().addEventListener(new AbstractWalletEventListener() {
            @Override
            public synchronized void onCoinsReceived(Wallet w, Transaction tx, Coin prevBalance, Coin newBalance) {

                System.out.println("[main]***************************************************: Tx RECIEVED!");
                System.out.println("\nReceived tx with id " + tx.getHashAsString());
                System.out.println("\nsent to me in this tx" + tx.getValueSentToMe(w));

                System.out.println("Adddress ***************************************************: Tx");

                String Txx=tx.toString();
                List<TransactionInput> inputs = tx.getInputs();
                List<TransactionOutput> outputs = tx.getOutputs();

                for(TransactionOutput out : outputs){
                    System.out.println("  address output "+out.getAddressFromP2PKHScript(params)); // si je recoi c mon adress
                     //System.out.println("out ad"+out.getAddressFromP2SH(params));
                 }

                for(TransactionInput in : inputs){
                    System.out.println("address input "+in.getFromAddress()); // si je recoit si adress expediteur
                  }

                if (Txx.contains("RETURN PUSHDATA")&!tx.isPending())
                {
                    // System.out.println("this tx  "+Txx);
                     String mydata = tx.getOutputs().toString(); //you could use out.getScriptPubKey()
                     mydata.split("RETURN");
                    String[] parti = mydata.split("RETURN PUSHDATA"+"\\((.*?)\\)");
                    String parta = parti[0];
                    String partb = parti[1];

                     Pattern pattern = Pattern.compile("\\[(.*?)\\]");
                    Matcher matcher = pattern.matcher(partb);
                     if (matcher.find())
                    {
                        String token=matcher.group(0).replace("[", "");
                        token=token.replace("]", "");
                        System.out.println("received token is" +token);
                        //System.out.println(matcher.group(1));
                    }

                }

            }
        });
*/

        if(ti==0)  {


            try {

                /*System.out.println("Amount to send is null ");
                System.out.println("*********************Start transaction op_return************************************");

                Transaction tx = new Transaction(params);
                tx.addOutput(amount, destination);
                System.out.println(" Give Token pointer :");
                String Message_="123456";
                //Scanner Message_=new Scanner(System.in);

                byte[] bytes = Utils.HEX.decode(Message_);
                Script script = new ScriptBuilder().op(ScriptOpCodes.OP_RETURN).data(bytes).build();
                tx.addOutput(Transaction.MIN_NONDUST_OUTPUT, script);//bitcoinj will prevent putting transaction                 outputs that have value lower than Transaction.MIN_NONDUST_OUTPUT (546 satoshis).
                kit.wallet().sendCoins( Wallet.SendRequest.forTx(tx));
               */  System.out.println("*****************************End transaction op_return****************************");


            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                System.out.println("*********************Start transaction op_return************************************");

                Transaction tx = new Transaction(params);
                tx.addOutput(amount, destination);
                System.out.println(" Give Token pointer :");
//                 Sha256Hash Hashd_token = new Sha256Hash("My message is ");
                 //String Message_="1234";
               // byte[] bytes = Utils.HEX.decode(Message_);
                Script script = new ScriptBuilder().op(ScriptOpCodes.OP_RETURN).data("my message".getBytes()).build();                //op(int opcode)                 Adds the given opcode to the end of the program.
                //.data() Adds a copy of the given byte array as a data element (i.e. PUSHDATA) at the end of the program.
               // tx.addOutput(Coin.CENT, script);
                tx.addOutput(Transaction.MIN_NONDUST_OUTPUT, script);                              // bitcoinj will prevent putting transaction                 outputs that have value lower than Transaction.MIN_NONDUST_OUTPUT (546 satoshis).
                 System.out.println("after changement"+tx);
                 //System.out.println("cfidance"+tx.getConfidence());
                 System.out.println("Outputus"+tx.getOutputs());
                System.out.println("t*********************************************************");
                  kit.wallet().sendCoins( Wallet.SendRequest.forTx(tx));
           System.out.println("*****************************End transaction op_return****************************");
                //        ///DOC http://plan99.net/~mike/bitcoinj/0.11/com/google/bitcoin/core/Wallet.html
               //// https://bitcoinj.github.io/javadoc/0.12/org/bitcoinj/script/ScriptBuilder.html
               // System.out.println("******isrelevent***:"+kit.wallet().isPendingTransactionRelevant(tx));

            } catch (Exception e) { //InsufficientMoneyException
                e.printStackTrace();
            }

        }

/*****************************
 *
 *
 WalletEventListener - for things that happen to your wallet
 BlockChainListener - for events related to the block chain
 PeerEventListener - for events related to a peer in the network
 TransactionConfidence.Listener - for events related to the level of rollback security a transaction has

 *
 *
 */

        for (Transaction txi : kit.wallet().getTransactions(true)) {

           // System.out.println("t**********************            kit.wallet().currentReceiveAddress();\n***********************************");


            //System.out.println( kit.wallet().currentReceiveAddress());
          //  tx.addOutput(Transaction.MIN_NONDUST_OUTPUT, new ScriptBuilder().op(ScriptOpCodes.OP_RETURN).data("hello".getBytes()).build());
         }

/*******************************************
 *
 * Blockchain
 */
        BlockStore blockStore = new MemoryBlockStore(params);//BlockStore instance which keeps the block chain data structure somewhere, like on disk.
        BlockChain chain;//BlockChain instance which manages the shared, global data structure which makes Bitcoin work.
        //
        //wallet.connectToLocalHost(); //if wlletappkit

        try {
            chain = new BlockChain(params, kit.wallet(), blockStore);

            ///chain.
        } catch (BlockStoreException e) {
            e.printStackTrace();
        }


        System.out.println("************ END *****************************");

        System.out.println("Stopping");
        System.out.println("Stopped!: " + kit.stopAsync());

        kit.awaitTerminated();
        System.exit(0);

    }

}
