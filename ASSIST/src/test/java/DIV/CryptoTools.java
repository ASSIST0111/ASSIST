package DIV;

import org.aion.tetryon.Fp;
import org.aion.tetryon.G1;
import org.aion.tetryon.G1Point;
import org.bouncycastle.jcajce.provider.digest.Keccak;
import org.bouncycastle.jcajce.provider.digest.SHA3;

import java.math.BigInteger;
import java.util.Random;

public class CryptoTools {

    public static BigInteger genRandom() {
        BigInteger b = new BigInteger(CryptoTools.intToByte(new Random().nextInt()));
        while (b.equals(BigInteger.ZERO)) {
            b = new BigInteger(CryptoTools.intToByte(new Random().nextInt()));
        }
        return b.abs();
    }

    /**
     * @param input {0,1}*
     * @return G1Point
     */
    public static Fp h1(BigInteger input) {
        try {
            input = new BigInteger(sha3(input.toByteArray()));
            input = input.signum() < 0 ? input.negate() : input;
            return G1.mul(Setup.g1, input.mod(Fp.FIELD_MODULUS)).x;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param input {0,1}*
     * @return G1Point
     */
    public static G1Point h2(BigInteger input) {
        try {
            input = new BigInteger(sha3(input.toByteArray()));
            input = input.signum() < 0 ? input.negate() : input;
            return G1.mul(Setup.g1, input.mod(Fp.FIELD_MODULUS));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param bytes byte array
     * @return 64bytes/512bit
     */
    public static byte[] sha3(byte[] bytes) {
        SHA3.DigestSHA3 digestSHA3 = new SHA3.Digest512();
        return digestSHA3.digest(bytes);
    }

    public static byte[] sha3_224(byte[] bytes) {
        SHA3.DigestSHA3 digestSHA3 = new SHA3.Digest224();
        return digestSHA3.digest(bytes);
    }

    public static byte[] keccak256(byte[] bytes) {
        Keccak.Digest256 digest256 = new Keccak.Digest256();
        return digest256.digest(bytes);
    }

    public static int pai(String key, int input) {
        return new BigInteger(key, 16).intValue() + input;
    }

    public static int f(String key, int input) {
        return new BigInteger(key, 16).intValue() + input;
    }

    public static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte aByte : bytes) {
            String hex = Integer.toHexString(0xFF & aByte);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    public static byte[] hexToByteArr(String hexStr) {
        String HexStr = "0123456789abcdef";
        char[] charArr = hexStr.toCharArray();
        byte[] btArr = new byte[charArr.length / 2];
        int index = 0;
        for (int i = 0; i < charArr.length; i++) {
            int highBit = HexStr.indexOf(charArr[i]);
            int lowBit = HexStr.indexOf(charArr[++i]);
            btArr[index] = (byte) (highBit << 4 | lowBit);
            index++;
        }
        return btArr;
    }

    /**
     * @param values byte array to merge
     * @return merged byte array
     */
    public static byte[] mergeByte(byte[]... values) {
        int byte_length = 0;
        for (byte[] bytes : values) {
            byte_length += bytes.length;
        }

        byte[] mergedByte = new byte[byte_length];
        int countlength = 0;
        for (byte[] value : values) {
            System.arraycopy(value, 0, mergedByte, countlength, value.length);
            countlength += value.length;
        }
        return mergedByte;
    }

    /**
     * low byte first
     *
     * @param n int
     * @return byte[]
     */
    public static byte[] intToByte(int n) {
        byte[] b = new byte[4];
        b[0] = (byte) (n & 0xff);
        b[1] = (byte) (n >> 8 & 0xff);
        b[2] = (byte) (n >> 16 & 0xff);
        b[3] = (byte) (n >> 24 & 0xff);
        return b;
    }

    /**
     * low byte first
     *
     * @param b byte
     * @return int
     */
    public static int byteToInt(byte[] b) {
        int res = 0;
        for (int i = 0; i < b.length; i++) {
            res += (b[i] & 0xff) << (i * 8);
        }
        return res;
    }
}
