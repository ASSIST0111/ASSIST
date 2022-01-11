package DIV;

import contract.BLS2;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;

import java.math.BigInteger;
import java.util.List;

public class Contract {
    private static BLS2 bls2;
    private Web3j web3j;
    private Credentials credentials;    //The private key of the first account
    private String CONTRACT_ADDRESS = "0x35fC769D21E3e83451C352099480291bd58344D9";   //The deployed contract address
    private BigInteger ethBase = BigInteger.valueOf(10).pow(18);   // 1 eth = 10^18 wei

    public Contract() {
        try {
            web3j = Web3j.build(new HttpService("http://localhost:7545"));  //local ganache gui
            credentials = Credentials.create("67b3052413046ff205e952f33bcabe1111028280e3ddfcd7c851200ccc9f1372");  //The private key of the first account
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Contract test = new Contract();
        test.deployContract();
    }

    //部署合约
    public void deployContract() {
        try {
            //Contract deployment
            bls2 = bls2.deploy(
                    web3j, credentials,
                    new BigInteger("22000000000"), new BigInteger("510000"))
                    .send();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Load the deployed contract
    public void loadContract() {
        try {
            bls2 = bls2.load(
                    CONTRACT_ADDRESS, web3j, credentials,
                    new BigInteger("22000000000"), new BigInteger("510000"));
            System.out.println("LoadContract OK");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void verify(List<BigInteger> negSigma, List<BigInteger> third, List<BigInteger> pk) {
        // contract function call
        try {
            TransactionReceipt tr = (bls2.verify(negSigma, third, pk)).send();
            System.out.println("contract verify:" + bls2.getNotifyEvents(tr).get(0).note);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
