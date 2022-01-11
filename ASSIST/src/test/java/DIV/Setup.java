package DIV;

import org.aion.tetryon.*;
import org.junit.Test;

import javax.xml.ws.Holder;
import java.math.BigInteger;

import static org.junit.Assert.assertEquals;

public class Setup {
    //generator P1 in G1, generator P2 in G2, U's encryption key sk, U's decryption key pk, U's signing key ssk, U's verifying key spk, randomness seed theta_setup
    //Note: We set sk = ssk, pk = spk
    public static G2Point g2 = new G2Point(
            new Fp2(
                    new BigInteger("10857046999023057135944570762232829481370756359578518086990519993285655852781"),
                    new BigInteger("11559732032986387107991004021392285783925812861821192530917403151452391805634")
            ),
            new Fp2(
                    new BigInteger("8495653923123431417604973247489272438418190587263600148770280649306958101930"),
                    new BigInteger("4082367875863433681332203403145435568316851327593401208105741076214120093531")
            )
    );
    public static G1Point g1 = Pairing.P1();
    public static Holder<BigInteger> sk = new Holder<>();
    public static Holder<G2Point> pk = new Holder<>();
    public static Holder<BigInteger> ssk = new Holder<>();
    public static Holder<G2Point> spk = new Holder<>();
    public static Holder<BigInteger> theta_setup =  new Holder<>();

    public static void insSysParams(Holder<BigInteger> sk, Holder<G2Point> pk,
                                    Holder<BigInteger> ssk, Holder<G2Point> spk,
                                    Holder<BigInteger> theta_setup) {
        if (sk == null) {
            sk = new Holder<>();
        }
        if (pk == null) {
            pk = new Holder<>();
        }
        if (ssk == null) {
            ssk = new Holder<>();
        }
        if (spk == null) {
            spk = new Holder<>();
        }
        if (theta_setup == null) {
            theta_setup = new Holder<>();
        }

        //generate keypair
        BigInteger c = CryptoTools.genRandom();
        BigInteger d = CryptoTools.genRandom();
        BigInteger t = (CryptoTools.h1(c.add(d))).c0;
        //RF send t to U as a commitment
        BigInteger k = CryptoTools.genRandom();
        G2Point K = G2.ECTwistMul(g2, k);
        //U send K to RF
        spk.value = G2.ECTwistAdd(K, G2.ECTwistMul(g2, c));
        pk.value = spk.value;
        //RF send (c, d) to U
        assertEquals(t, (CryptoTools.h1(c.add(d))).c0);
        ssk.value = c.add(k);
        sk.value = ssk.value;
        //U verify and compute sk

        //generate a randomness seed
        BigInteger s1 = CryptoTools.genRandom();
        BigInteger c1 = CryptoTools.h1(s1).c0;
        //U send c1 to RF
        BigInteger s2 = CryptoTools.genRandom();
        BigInteger c2 = CryptoTools.h1(s2).c0;
        //RF send c2 to U
        // U send s1 to RF, RF send s2 to U
        assertEquals(c2, CryptoTools.h1(s2).c0);
        theta_setup.value = s1.add(s2);
        //U verify c2, compute theta as a randomness seed
        assertEquals(c1, CryptoTools.h1(s1).c0);
        theta_setup.value = s2.add(s1);
        //RF verify c1, compute theta as a randomness seed
    }

    @Test
    public void testSetup() {
        insSysParams(sk, pk, ssk, spk, theta_setup);
        assertEquals(pk.value, G2.ECTwistMul(g2, sk.value));
        assertEquals(spk.value, G2.ECTwistMul(g2, ssk.value));
    }
}