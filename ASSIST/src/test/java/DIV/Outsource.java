package DIV;

import org.aion.tetryon.*;
import org.junit.Test;
import util.CryptoUtil;

import javax.xml.ws.Holder;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class Outsource {
    //
    public static int n;
    public static int s = 10;
    public static Holder<BigInteger[][]> M = new Holder<>();
    public static G1Point[] sigma;
    public static Holder<byte[]> tau = new Holder<>();
    public static G1Point[] U;

    //U generate a encryption key and outsources the data to CS
    public static void genData(Holder<BigInteger[][]> M, Holder<byte[]> tau) {
        if (M == null) {
            M = new Holder<>();
        }
        if (tau == null) {
            tau = new Holder<>();
        }
        BigInteger ssk = Setup.ssk.value;
        G2Point spk = Setup.spk.value;

        //read file form filePath
        String filePath = "D:\\test.zip";
        byte[] fileBytes = new byte[]{};
        try {
            fileBytes = Files.readAllBytes(Paths.get(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }

        //U interacts whit RF and KS to generate a encryption key
        BigInteger r_keygen = CryptoTools.genRandom();
        BigInteger HM_keygen = new BigInteger(CryptoTools.sha3(fileBytes));
        BigInteger a = r_keygen.multiply(HM_keygen);
        //U send a to RF
        BigInteger r2_keygen = CryptoTools.genRandom();
        BigInteger b_keygen = r2_keygen.multiply(a);
        //RF send b to KS
        BigInteger alpha_keygen = CryptoTools.genRandom();
        BigInteger epsilon_keygen = alpha_keygen.multiply(b_keygen);
        //KS send epsilon to RF
        BigInteger x_keygen = epsilon_keygen.divide(r2_keygen);
        //RF send x to U
        BigInteger phi_keygen = x_keygen.divide(r_keygen);
        //U generates an encryption key K_keygen
        BigInteger K_keygen = CryptoTools.h1(phi_keygen).c0;

        //U interacts with CS, and outsources the data M to CS
        //U generates a ciphertext
        fileBytes = CryptoUtil.AESEncrypt(K_keygen.toByteArray(), fileBytes);
        //U divides C into n blocks, and splits each block into s sectors
        int fileSize = fileBytes.length;
        System.out.println("File Size: " + fileSize + "B");
        int index = 0;
        n = fileSize / (s * 10240); //分10个扇区，按10KB大小分
        System.out.println("Block number: " + n);
        M.value = new BigInteger[n][s];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < s; j++) {
                M.value[i][j] = BigInteger.ZERO;
            }
        }
        //divides C into n blocks
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < s; j++) {
                BigInteger b;
                if (index > fileSize) {
                    break;
                } else if (index + fileSize / (n * s) > fileSize) {
                    b = new BigInteger(Arrays.copyOfRange(fileBytes, index, fileSize));
                } else {
                    b = new BigInteger(Arrays.copyOfRange(fileBytes, index, index + fileSize / (n * s)));
                }
                M.value[i][j] = (b.signum() == 0 ? CryptoTools.genRandom() :
                        (b.signum() < 0 ? b.negate() : b));
                index += fileSize / (n * s);
            }
        }
        //U generates a random element set with the random seed theta_setup
        //With the random seed theta_setup and pseudorandom function f, U computes v_sigma
        BigInteger theta_setup = Setup.theta_setup.value;
        int[] f_outsource = new int[s];
        for (int i = 0; i < s; i++) {
            int f = CryptoTools.f(theta_setup.toString(), i);
            f_outsource[i] = (f <= 0) ? f + s : f;
        }
        U = new G1Point[s];
        try {
            for (int i = 0; i < s; i++) {
                U[i] = G1.mul(Setup.g1, BigInteger.valueOf(f_outsource[i]));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //U computes the data tag tao
        byte[] tau0 = CryptoTools.mergeByte();
        for (int i = 0; i < s; i++) {
            tau0 = CryptoTools.mergeByte(tau0, Util.serializeG1(U[i]));
        }
        G1Point sig_tau0 = sign(ssk, tau0);
        assert sig_tau0 != null;
        tau.value = CryptoTools.mergeByte(tau0, Util.serializeG1(sig_tau0));
        //U computes the verification tag sigma for each block
        sigma = new G1Point[n];
        try {
            for (int i = 0; i < n; i++) {
                G1Point hash = CryptoTools.h2(BigInteger.valueOf(i));
                G1Point sum = Setup.g1;
                for (int j = 0; j < s; j++) {
                    BigInteger m_sha3_224 = new BigInteger(1, CryptoTools.sha3_224(M.value[i][j].toByteArray()));
                    G1Point mul = G1.mul(U[j], m_sha3_224);
                    sum = G1.add(sum, mul);
                }
                sum = G1.add(sum, G1.negate(Setup.g1));
                sum = G1.add(hash, sum);
                sigma[i] = G1.mul(sum, ssk);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // sig = msg * ssk * g1
    public static G1Point sign(BigInteger ssk, byte[] msg) {
        BigInteger ssk_msg = BigInteger.valueOf(CryptoTools.byteToInt(msg)).multiply(ssk);
        try {
            return G1.mul(Setup.g1, ssk_msg);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Test
    public void testOutsource() {
        long time0 = System.currentTimeMillis();
        new Setup().testSetup();
        //U generate a encryption key and outsources the data to CS
        genData(M, tau);
        Audit audit = new Audit();
        long time1 = System.currentTimeMillis();
        audit.genSeeds();
        long time2 = System.currentTimeMillis();
        audit.genChallenges();
        audit.callContract();
        long time3 = System.currentTimeMillis();
        System.out.println("Data processing time：" + (time1 - time0));
        System.out.println("Query block time：" + (time2 - time1));
        System.out.println("Total audit time：" + (time3 - time1));
    }
}
