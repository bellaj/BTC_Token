
        package dev_btc;

        import com.google.common.collect.ImmutableList;
        import com.google.common.util.concurrent.FutureCallback;
        import com.google.common.util.concurrent.Futures;
        import com.google.common.util.concurrent.ListenableFuture;
        import org.bitcoinj.core.*;
        import org.bitcoinj.core.listeners.AbstractWalletEventListener;
        import org.bitcoinj.kits.WalletAppKit;
        import org.bitcoinj.params.RegTestParams;
        import org.bitcoinj.script.Script;
        import org.bitcoinj.script.ScriptBuilder;
        import org.bitcoinj.script.ScriptOpCodes;
        import org.bitcoinj.store.BlockStore;
        import org.bitcoinj.store.BlockStoreException;
        import org.bitcoinj.store.MemoryBlockStore;
        import org.bitcoinj.utils.BriefLogFormatter;
        import org.bitcoinj.utils.MonetaryFormat;
        import org.codehaus.jettison.json.JSONArray;
        import org.codehaus.jettison.json.JSONException;
        import org.codehaus.jettison.json.JSONObject;
        import org.slf4j.LoggerFactory;

        import java.io.File;
        import java.io.FileNotFoundException;
        import java.io.PrintWriter;
        import java.io.UnsupportedEncodingException;
        import java.math.BigInteger;
        import java.net.InetAddress;
        import java.text.SimpleDateFormat;
        import java.util.Iterator;
        import java.util.List;
        import java.util.Scanner;
        import java.util.TimeZone;
        import java.util.concurrent.Future;
        import java.util.regex.Matcher;
        import java.util.regex.Pattern;

/**
 * Created by badr on 2/2/2016.
 */


 public class Token {



    public static boolean check_access(NetworkParameters params,Wallet wallet){

        //TODO: generation check access
        return true;
    }
    public static Transaction generate_token_Msign(NetworkParameters params,Wallet wallet){

// Create a random key.
        ECKey clientKey = new ECKey();
// We get the other parties public key from somewhere ...
        ECKey serverKey = new ECKey(null, "02d25ae7bed6febc73589ac12c009a99dbb6db30babeeaeb4dc3f6a7101131db75".getBytes());

// Prepare a template for the contract.
        Transaction contract = new Transaction(params);
        List<ECKey> keys = ImmutableList.of(clientKey, serverKey);
// Create a 2-of-2 multisig output script.
        Script script = ScriptBuilder.createMultiSigOutputScript(2, keys);
        Script script2 = new ScriptBuilder().op(ScriptOpCodes.OP_RETURN).data("0".getBytes()).build();
        System.out.println("script is "+script);
//script.createEmptyInputScript(clientKey,script2);

        //tx.addOutput(Transaction.MIN_NONDUST_OUTPUT, script);
// Now add an output for 0.50 bitcoins that uses that script.

        Coin amount = Coin.parseCoin("11.33");
        Coin amount2 = Coin.parseCoin("0");
         contract.addOutput(amount, script);
         contract.addOutput(amount2, script2);
         // contract.addo;//  contract.addOutput(Transaction.MIN_NONDUST_OUTPUT, new ScriptBuilder.op(ScriptOpCodes.OP_RETURN).data("0123456".getBytes()).build());

// We have said we want to make 0.5 coins controlled by us and them.
// But it's not a valid tx yet because there are no inputs.
        //Wallet.SendRequest req = Wallet.SendRequest.forTx(contract);
        try {

           wallet.sendCoins( Wallet.SendRequest.forTx(contract));

            // wallet.completeTx(req);   // Could throw InsufficientMoneyException
        } catch (InsufficientMoneyException e) {
            e.printStackTrace();
        }

// Broadcast and wait for it to propagate across the network.
// It should take a few seconds unless something went wrong.
      // peerGroup.broadcastTransaction(req.tx).get();
         return contract;
    }


    public static void grant_access(Transaction tx, Wallet w,NetworkParameters networkParams)
    {

        System.out.println("isAnyOutputSpent "+tx.isAnyOutputSpent());

     }
    public static boolean who_issued_token(Transaction tx, Wallet w,NetworkParameters networkParams)
    {

        Address from = null;
        boolean fromMine = false;
        //from=tx.getOutputs().get(0).getAddressFromP2PKHScript(networkParams);
        List<TransactionInput> inputs = tx.getInputs();

        for (TransactionInput in : inputs)
            try {
                from = in.getFromAddress(); //
                fromMine = w.isPubKeyHashMine(from.getHash160());

            } catch (Exception e) {
                Transaction tr = in.getParentTransaction();
                // System.out.println("parent tx " + tr);
                List<TransactionOutput> outp = tr.getOutputs();

                for (TransactionOutput out : outp) {
                    from = out.getAddressFromP2PKHScript(networkParams); //
                    fromMine = w.isPubKeyHashMine(from.getHash160());
//
                }
            }
        return fromMine;
    }

    public static String ressource_addresse(Transaction tx, Wallet w,NetworkParameters networkParams){
        String ressource_add="null";
        if (!who_issued_token(tx,w,networkParams))
            ressource_add=tx.getInputs().get(0).getFromAddress().toString();
        return ressource_add;

    }
    public static String[] getWalletAddress(NetworkParameters networkParams, WalletAppKit wallet) {
        int i=0,lenght=0;
        ECKey ecKey = null;
        List<ECKey> list_key= wallet.wallet().getIssuedReceiveKeys();
        for (ECKey k : list_key) {
            lenght++;
        }
        String[] Addres = new String[lenght];

        for (ECKey k : list_key) {
            ecKey = wallet.wallet().getIssuedReceiveKeys().get(i);// .getKeys().get(0);
            // System.out.println(" Addresses :"+ecKey.toAddress(networkParams).toString());

            Addres[i]=ecKey.toAddress(networkParams).toString();
            i++;
        }
        return Addres;
    }
     private static String getJSONFromTransaction(Transaction tx, Address destination, NetworkParameters networkParams, WalletAppKit wallet) throws ScriptException, JSONException {
         if (tx == null) {
             return null;
         }

         TransactionConfidence txConfidence = tx.getConfidence();
         TransactionConfidence.ConfidenceType confidenceType = txConfidence.getConfidenceType();
         String confidence;

         if (confidenceType == TransactionConfidence.ConfidenceType.BUILDING) {
             confidence = "building";
         } else if (confidenceType == TransactionConfidence.ConfidenceType.PENDING) {
             confidence = "pending";
         } else if (confidenceType == TransactionConfidence.ConfidenceType.DEAD) {
             confidence = "dead";
         } else {
             confidence = "unknown";
         }

         JSONArray inputs = new JSONArray();

         for (TransactionInput input : tx.getInputs()) {
             JSONObject inputData = new JSONObject();

             if (!input.isCoinBase()) {
                 try {
                     Script scriptSig = input.getScriptSig();
                     Address fromAddress = new Address(networkParams, Utils.sha256hash160(scriptSig.getPubKey()));
                     inputData.put("address", fromAddress);
                 } catch (ScriptException e) {
                     // can't parse script, give up
                 }
             }

             TransactionOutput source = input.getConnectedOutput();
             if (source != null) {
                 inputData.put("amount", source.getValue());
             }

             inputs.put(inputData);
         }

         JSONArray outputs = new JSONArray();

         for (TransactionOutput output : tx.getOutputs()) {
             JSONObject outputData = new JSONObject();

             try {
                 Script scriptPubKey = output.getScriptPubKey();

                 if (scriptPubKey.isSentToAddress() || scriptPubKey.isPayToScriptHash()) {
                     Address toAddress = scriptPubKey.getToAddress(networkParams);
                     outputData.put("address", toAddress);

                     if (toAddress.toString().equals(getWalletAddress(networkParams,wallet))) {
                         outputData.put("type", "own");
                     } else {
                         outputData.put("type", "external");
                     }
                 } else if (scriptPubKey.isSentToRawPubKey()) {
                     outputData.put("type", "pubkey");
                 } else if (scriptPubKey.isSentToMultiSig()) {
                     outputData.put("type", "multisig");
                 } else {
                     outputData.put("type", "unknown");
                 }
             } catch (ScriptException e) {
                 // can't parse script, give up
             }

             outputData.put("amount", output.getValue());
             outputs.put(outputData);
         }

         SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
         dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

         JSONObject result = new JSONObject();
         result.put("amount", tx.getValue(wallet.wallet()));
        // result.put("fee", getTransactionFee(tx));
         result.put("txid", tx.getHashAsString());
         result.put("time", dateFormat.format(tx.getUpdateTime()));
         result.put("confidence", confidence);
         result.put("peers", txConfidence.numBroadcastPeers());
         result.put("confirmations", txConfidence.getDepthInBlocks());
         result.put("inputs", inputs);
         result.put("outputs", outputs);

         return result.toString();
     }

    public BigInteger getTransactionFee(Transaction tx) {
        // TODO: this will break once we do more complex transactions with multiple sources/targets (e.g. coinjoin)

        BigInteger v = BigInteger.ZERO;

       /* for (TransactionInput input : tx.getInputs()) {
            TransactionOutput connected = input.getConnectedOutput();
            if (connected != null) {
                // v = v.add(connected.getValue());
            } else {
                // we can't calculate the fee amount without having all data
                return BigInteger.ZERO;
            }
        }

        for (TransactionOutput output : tx.getOutputs()) {
            //   v = v.subtract(output.getValue());
        }*/

        return v;
    }
    public static void send_token(Address destination,NetworkParameters params,WalletAppKit kit){
        System.out.println(" Give the amount to send :");
        Scanner s=new Scanner(System.in);
        double ti=0;
        ti=s.nextDouble();
        Coin amount = Coin.parseCoin(Double.toString(ti));
        System.out.println("# the amount is to "+ti);

        if(ti==0) {
            try {
                System.out.println("Balance is empty");


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            try {
                System.out.println("**************************_Start sending transaction op_return************************************");
                double rand=Math.random();
                Transaction tx = new Transaction(params);
                tx.addOutput(amount, destination);
                System.out.println(" Give Token pointer :");
                String tokenm="Token"+String.valueOf(rand);
                Script script = new ScriptBuilder().op(ScriptOpCodes.OP_RETURN).data(tokenm.getBytes()).build();
                tx.addOutput(Transaction.MIN_NONDUST_OUTPUT, script);
                //System.out.println("after changement"+tx);
                System.out.println( getJSONFromTransaction(tx,destination,params,kit));
                //System.out.println("Outputus"+tx.getOutputs());
                kit.wallet().sendCoins( Wallet.SendRequest.forTx(tx));
                System.out.println("*********************************** _ End transaction op_return_*********************************");
            } catch (Exception e) {

                System.out.println(" You can't sen new tokens, please get more TK");

            }


            final ListenableFuture<Coin> balanceFuture = kit.wallet().getBalanceFuture(amount, Wallet.BalanceType.AVAILABLE);
            FutureCallback<Coin> callback = new FutureCallback<Coin>() {
                public void onSuccess(Coin balance) {
                    System.out.println("coins arrived and the wallet now has enough balance"+balanceFuture);
                }

                public void onFailure(Throwable t) {
                    System.out.println("something went wrong");
                }
            };
            Futures.addCallback(balanceFuture, callback);


        }

    }

    public  static String get_token_from_tx(Transaction tx){
        String Txx=tx.toString();
        String token_="null";

        if (Txx.contains("RETURN PUSHDATA"))
        {
            String token;
            String mydata = tx.getOutputs().toString(); //you could use out.getScriptPubKey()
            mydata.split("RETURN");
            String[] parti = mydata.split("RETURN PUSHDATA"+"\\((.*?)\\)");
            String parta = parti[0];
            String partb = parti[1];

            Pattern pattern = Pattern.compile("\\[(.*?)\\]");
            Matcher matcher = pattern.matcher(partb);
            if (matcher.find())
            {
                token=matcher.group(0).replace("[", "");
                token=token.replace("]", "");
                // System.out.println("received token is" +token);
                //System.out.println(matcher.group(1));
                token_=token;
            }
        }
        return token_;
    }

    public  static  void My_recived_tokens(Wallet w,NetworkParameters networkParams){



        for (Transaction txi :w.getTransactions(true)) {
            if( get_token_from_tx(txi) !="null" )
            {if(who_issued_token(txi,w,networkParams))
                System.out.println("token sent "+get_token_from_tx(txi)+" ressource addresse : "+ txi.getInputs().get(0).getFromAddress());
            else
                System.out.println("recieved "+get_token_from_tx(txi)+" ressource addresse : "+txi.getInputs().get(0).getFromAddress());
            }
        }

    }

    static protected void store_TX( NetworkParameters params,WalletAppKit kit) throws BlockStoreException, FileNotFoundException, UnsupportedEncodingException {

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
                System.out.println("bc :"+block.getHashAsString());
            }

            stored_block = stored_block.getPrev(block_store);
        }

    }

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(Token.class);

    public static void fetch_block(NetworkParameters params,WalletAppKit kit, String block_hash) throws Exception {
        BriefLogFormatter.init();
        System.out.println("Connecting to node");
        // final NetworkParameters params = TestNet3Params.get();

        BlockStore blockStore = new MemoryBlockStore(params);
        BlockChain chain = new BlockChain(params, blockStore);
        PeerGroup peerGroup = new PeerGroup(params, chain);
        peerGroup.start();
        PeerAddress addr = new PeerAddress(InetAddress.getLocalHost(), params.getPort());
        peerGroup.addAddress(addr);
        peerGroup.waitForPeers(1).get();
        Peer peer = peerGroup.getConnectedPeers().get(0);

        Sha256Hash blockHash = Sha256Hash.wrap(block_hash);
        Future<Block> future = peer.getBlock(blockHash);
        System.out.println("Waiting for node to send us the requested block: " + blockHash);
        Block block = future.get();
        System.out.println(block);
        peerGroup.stopAsync();
    }

    public static void main(String[] args) {

        log.info("Starting");
        System.out.println("*********************************** ---------------------******************************************************");
        System.out.println("*                                   Fair access Framework                                                      *");
        System.out.println("*********************************** ---------------------******************************************************");
        BriefLogFormatter.init();
        final NetworkParameters params = RegTestParams.get();
        //conex
        final WalletAppKit kit = new WalletAppKit(params, new File("Token_"), "5") {
            @Override
            protected void onSetupCompleted() {
            }
        };
        kit.setAutoSave(true);
        kit.connectToLocalHost();
        //System.out.println("*/*start async*/*");
        kit.startAsync();
        kit.awaitRunning();
        System.out.println("allowSpendingUnconfirmedTransactions is enabled : ");

        kit.wallet().allowSpendingUnconfirmedTransactions();

        System.out.println("My Address is : " + kit.wallet().currentReceiveAddress());
        System.out.println("I have :" + MonetaryFormat.BTC.noCode().format(kit.wallet().getBalance()).toString() + " Tokens");
        System.out.println("-------------------------------------My Tokens wallet -------------------------------------------------------------------");
      System.out.println(" your Wallet data : \n" + kit.wallet());
        System.out.println(" All My Tokens : \n");
        My_recived_tokens(kit.wallet(),params);

        System.out.println("-------------------------------------*********-------------------------------------------------------------------");

        kit.wallet().addEventListener(new AbstractWalletEventListener() {
            @Override
            public void onCoinsReceived(Wallet w, Transaction tx, Coin prevBalance, Coin newBalance) {

                //System.out.println("Received "+who_issued_token(tx,w,params));
                if(!who_issued_token(tx,w,params)&& get_token_from_tx(tx)!="null") {
                    Coin value = tx.getValueSentToMe(w);
                    System.out.println("Received transaction for " + value.toFriendlyString());
                    System.out.println("-------------------------The recieved Tken Transaction in detail-------------------------------------------------------");
                    System.out.println(tx);
                    System.out.println("---------------------------------------------------------------------------------------------------------------");
                    System.out.println("transaction hash is : " + tx.getHashAsString());
                    if (get_token_from_tx(tx) != "null")
                        System.out.println("The recieved token is : " + get_token_from_tx(tx));
                    String Txx = tx.toString();



                }
                else if(get_token_from_tx(tx)!="null")
                    System.out.println("The token "+get_token_from_tx(tx)+"is sent. the Transaction  will be forwarded after mining.pending status :"+tx.isPending());


            }

            @Override
            public void onTransactionConfidenceChanged(Wallet wallet, Transaction tx) {

                  /*  System.out.println("token in blockchain: " + get_token_from_tx(tx));
                    System.out.println("confidence changed: " + tx.getHashAsString());
                    TransactionConfidence confidence = tx.getConfidence();
                    System.out.println("new block depth: " + confidence.getDepthInBlocks());*/
            }

            @Override
            public void onCoinsSent(Wallet wallet, Transaction tx, Coin prevBalance, Coin newBalance) {
                // System.out.println("New token sent but waiting for confirmation");
            }

               /* @Override
                public void onReorganize(Wallet wallet) {
                    System.out.println("wallet onReorganize");

                }*/

            /*  @Override
              public void onWalletChanged(Wallet wallet) {
                  //  System.out.println("The wallet has changed");
              }

              @Override
              public void onKeysAdded(List<ECKey> keys) {
                  System.out.println("new key added");
              }
*/
            @Override
            public void onScriptsChanged(Wallet wallet, List<Script> scripts, boolean isAddingScripts) {
                System.out.println("new script added");
            }

        });
//envoyer
   Address destination = new Address(params, "moSPGFrpHogSDfBryv5SFSnRWLtRprsCLJ");//badr aacount
    /*    Transaction muli_=generate_token_Msign(params,kit.wallet());
        try {
            System.out.println( getJSONFromTransaction(muli_,destination,params,kit));
        } catch (JSONException e) {
            e.printStackTrace();
        }/**/

       send_token(destination, params, kit);
     /*      //System.out.println("*************************************************** END Operation**************************************************");
        //System.out.println("Stopping");
        // A proposed transaction is now sitting in request.tx - send it in the background.
        //ListenableFuture<Transaction> future = peerGroup.broadcastTransaction(req.tx);

        // The future will complete when weve seen the transaction ripple across the network to a sufficient degree.
        // Here, we just wait for it to finish, but we can also attach a listener that'll get run on a background
        // thread when finished. Or we could just assume the network accepts the transaction and carry on.
        //  future.get();
        kit.awaitTerminated();
        ///////////////////////////////////////
                         /*        new Thread(new Runnable() {
                            public void run() {
                                      try {
                                             peer.run();
                                        } catch (PeerException e) {
                                              throw new RuntimeException(e);
                                           }
                                     }
                          }).start();*/

        //System.out.println("Stopped!: " + kit.stopAsync());

        //  kit.awaitTerminated();
        // System.exit(0);
////

        /**
         * For 2-element [input] scripts assumes that the paid-to-address can be derived from the public key.
         * The concept of a "from address" isn't well defined in Bitcoin and you should not assume the sender of a
         * transaction can actually receive coins on it. This method may be removed in future.
         @Deprecated
         public Address getFromAddress(NetworkParameters params) throws ScriptException {
         return new Address(params, Utils.sha256hash160(getPubKey()));
         }

         Gets the destination address from this script, if it's in the required form (see getPubKey).

         public Address getToAddress(NetworkParameters params) throws ScriptException {
         return getToAddress(params, false);
         }


          * Gets the destination address from this script, if it's in the required form (see getPubKey).
          *
          * @param forcePayToPubKey
         *            If true, allow payToPubKey to be casted to the corresponding address. This is useful if you prefer
         *            showing addresses rather than pubkeys.

        public Address getToAddress(NetworkParameters params, boolean forcePayToPubKey) throws ScriptException {
        if (isSentToAddress())
        return new Address(params, getPubKeyHash());
        else if (isPayToScriptHash())
        return Address.fromP2SHScript(params, this);
        else if (forcePayToPubKey && isSentToRawPubKey())
        return ECKey.fromPublicOnly(getPubKey()).toAddress(params);
        else
        throw new ScriptException("Cannot cast this script to a pay-to-address type");
        }
         */
    }

}