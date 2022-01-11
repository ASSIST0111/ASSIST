package DIV;

import org.aion.tetryon.*;
import org.junit.Test;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.http.HttpService;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static DIV.Outsource.*;

public class Audit {
    public static String theta1;
    public static String theta2;
    public static int c;
    public static BigInteger[] mu;
    public static G1Point sigma;
    public static int[] k;
    public static int[] v;
    public static byte[] record;

    @Test
    public void genSeeds() {
        //CS extracts the latest confirmed 12 hash values
        //ropsten test chain, the address comes from the infura agent
        Web3j web3j = Web3j.build(new HttpService("https://ropsten.infura.io/v3/5e8867a88096409aa3373f4b4b15ed0b"));
        EthBlock.Block latest;
        StringBuilder hash = new StringBuilder();
        try {
            latest = web3j.ethGetBlockByNumber(DefaultBlockParameterName.LATEST, true).send().getBlock();
            for (int i = 11; i >= 0; i--) {
                BigInteger thisNumber = BigInteger.valueOf(latest.getNumber().intValue() - i);
                EthBlock.Block block = web3j.ethGetBlockByNumber(DefaultBlockParameter.valueOf(thisNumber), true).send().getBlock();
                hash.insert(0, block.getHash());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        theta1 = hash.toString().replace("0x", "") + "1";
        theta2 = hash.toString().replace("0x", "") + "2";
        theta1 = Objects.requireNonNull(CryptoTools.h1(new BigInteger(theta1, 16))).toString();
        theta2 = Objects.requireNonNull(CryptoTools.h1(new BigInteger(theta2, 16))).toString();
        System.out.println("genSeeds OK: CS extracts the latest confirmed 12 hash values");
    }

    //CS computes challenged index-coefficient pairs
    public void genChallenges() {
        c = n / 10; //the number of challenged blocks
        System.out.println("Number of challenged blocks: " + c);
        k = new int[c];
        for (int i = 0; i < c; i++) {
            int pai = CryptoTools.pai(theta1, i) % c;
            k[i] = (pai < 0) ? pai + c : pai;
        }
        Arrays.sort(k);
        int vLength = k[c - 1] - k[0] + 1;
        v = new int[vLength];
        for (int i = 0; i < c; i++) {
            int f = CryptoTools.f(theta2, i) % c;
            v[k[i]] = (f <= 0) ? f + c : f;
        }
        mu = new BigInteger[Outsource.s];
        Arrays.fill(mu, BigInteger.ZERO);
        for (int j = 0; j < mu.length; j++) {
            for (int i = k[0]; i <= k[c - 1]; i++) {
                BigInteger m_sha3_224 = new BigInteger(1, CryptoTools.sha3_224(M.value[i][j].toByteArray()));
                mu[j] = mu[j].add((BigInteger.valueOf(v[i])).multiply(m_sha3_224));
            }
        }
        try {
            sigma = Setup.g1;
            for (int i = k[0]; i <= k[c - 1]; i++) {
                G1Point v_sigma = G1.mul(Outsource.sigma[i], BigInteger.valueOf(v[i]));
                sigma = G1.add(sigma, v_sigma);
            }
            sigma = G1.add(sigma, G1.negate(Setup.g1));
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("genChallenges OK: CS computes challenged index-coefficient pairs");
    }

    public void callContract() {
        try {
            G1Point sum1 = Setup.g1;
            for (int i = k[0]; i <= k[c - 1]; i++) {
                G1Point hash = CryptoTools.h2(BigInteger.valueOf(i));
                G1Point mul = G1.mul(hash, BigInteger.valueOf(v[i]));
                sum1 = G1.add(sum1, mul);
            }
            sum1 = G1.add(sum1, G1.negate(Setup.g1));

            G1Point sum2 = Setup.g1;
            for (int j = 0; j < s; j++) {
                G1Point mul = G1.mul(U[j], mu[j]);
                sum2 = G1.add(sum2, mul);
            }
            sum2 = G1.add(sum2, G1.negate(Setup.g1));

            G1Point third = G1.add(sum1, sum2);

            List<BigInteger> negSigma = new ArrayList<>();
            negSigma.add(G1.negate(sigma).x.c0);
            negSigma.add(G1.negate(sigma).y.c0);

            List<BigInteger> thirdList = new ArrayList<>();
            thirdList.add(third.x.c0);
            thirdList.add(third.y.c0);

            //Reverse the order of G2point to fit solidity's library functions
            List<BigInteger> pk = new ArrayList<>();
            pk.add(Setup.pk.value.x.b);
            pk.add(Setup.pk.value.x.a);
            pk.add(Setup.pk.value.y.b);
            pk.add(Setup.pk.value.y.a);

            long time1 = System.currentTimeMillis();
            Contract bls2 = new Contract();
            bls2.loadContract();
            bls2.verify(negSigma, thirdList, pk);
            long time2 = System.currentTimeMillis();

            System.out.println("Call contract time：" + (time2 - time1));

            boolean isTrue = Pairing.pairingProd2(G1.negate(sigma), Setup.g2, third, Setup.pk.value);
            System.out.println("Local verify isTrue:" + isTrue);

            BigInteger allmu = BigInteger.ZERO;
            for (int i = 0; i < Outsource.s; i++) {
                allmu = allmu.add(mu[i]);
            }
            Fp hash = CryptoTools.h1(
                    BigInteger.valueOf(System.currentTimeMillis())
                            .add(sigma.x.c0)
                            .add(sigma.y.c0)
                            .add(allmu)
                            .add(BigInteger.valueOf(CryptoTools.byteToInt(tau.value)))
            );
            assert hash != null;

            if (isTrue) {
                record = CryptoTools.mergeByte(hash.c0.toByteArray(), "true".getBytes());
            } else {
                record = CryptoTools.mergeByte(hash.c0.toByteArray(), "false".getBytes());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("callContract OK");
    }

    @Test
    public void testParams() {
        G1Point negSigma = new G1Point(
                new Fp(new BigInteger("11181692345848957662074290878138344227085597134981019040735323471731897153462")),
                new Fp(new BigInteger("15408496424792704861810691495984499005908379011086059411341239226197844282416")
                ));
        G2Point p2 = new G2Point(
                new Fp2(
                        new BigInteger("10857046999023057135944570762232829481370756359578518086990519993285655852781"),
                        new BigInteger("11559732032986387107991004021392285783925812861821192530917403151452391805634")
                ),
                new Fp2(
                        new BigInteger("8495653923123431417604973247489272438418190587263600148770280649306958101930"),
                        new BigInteger("4082367875863433681332203403145435568316851327593401208105741076214120093531")
                )
        );
        G1Point third = new G1Point(
                new Fp(new BigInteger("20149421967983502318226724386255136356408556890336186742179977631390150035241")),
                new Fp(new BigInteger("16251366894813361993800608195558474704961902796192284831853186885509923697836")
                ));

        G2Point pk = new G2Point(
                new Fp2(
                        new BigInteger("5725452645840548248571879966249653216818629536104756116202892528545334967238"),
                        new BigInteger("18523194229674161632574346342370534213928970227736813349975332190798837787897")
                ),
                new Fp2(
                        new BigInteger("677280212051826798882467475639465784259337739185938192379192340908771705870"),
                        new BigInteger("3816656720215352836236372430537606984911914992659540439626020770732736710924")
                )
        );
        try {
            System.out.println(Pairing.pairingProd2(negSigma, p2, third, pk));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
