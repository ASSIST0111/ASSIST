pragma solidity ^0.8.0;

/*
   Example of how to verify BLS signatures and BGLS aggregate signatures in
     Ethereum.

     Signatures are generated using https://github.com/Project-Arda/bgls
       Code is based on https://github.com/jstoxrocky/zksnarks_example
                */

/*
    The groups G_1 and G_2 are cyclic groups of prime order q =
    21888242871839275222246405745257275088548364400416034343698204186575808495617.
    The group G_1 is defined on the curve Y^2 = X^3 + 3 over the field F_p with p =
    21888242871839275222246405745257275088696311157297823662689037894645226208583 with generator P1 = (1, 2).
    The group G_2 is defined on the curve Y^2 = X^3 + 3/(i+9) over a different field F_p^2 = F_p[i] / (i^2 + 1) (p is the same as above) with generator

    P2 = (
        11559732032986387107991004021392285783925812861821192530917403151452391805634 * i +
        10857046999023057135944570762232829481370756359578518086990519993285655852781,
        4082367875863433681332203403145435568316851327593401208105741076214120093531 * i +
        8495653923123431417604973247489272438418190587263600148770280649306958101930
    )

    Note that G_2 is the only group of order q of that elliptic curve over the field F_p^2. Any other generator of order q instead of P2 would define the same G_2. However, the concrete value of P2 is useful for skeptical readers who doubt the existence of a group of order q. They can be instructed to compare the concrete values of q * P2 and P2.
*/

contract BLSExample {
    struct G1Point {
        uint X;
        uint Y;
    }

    struct G2Point {
        uint[2] X;
        uint[2] Y;
    }

    // @return the generator of G1
    G1Point P1 = G1Point(1, 2);
    G2Point P2 = G2Point(
        [10857046999023057135944570762232829481370756359578518086990519993285655852781,
        11559732032986387107991004021392285783925812861821192530917403151452391805634],

        [8495653923123431417604973247489272438418190587263600148770280649306958101930,
        4082367875863433681332203403145435568316851327593401208105741076214120093531]
    );

    function verify(G1Point memory negSigma, G1Point memory third, G2Point memory pk) public returns (bool) {
        return pairing2(negSigma, P2, third, pk);
    }

    // Example of BLS signature verification
    function verifyBLS() public returns (bool) {
        G1Point memory negSigma = G1Point(
            17432261763866387095582049976796525256479578351141356484603392714179114955684,
            9757289451970752754805169688107053619978027705305722623354262801279908936346
        );

        G1Point memory third = G1Point(
            20188147869696374457947683918983989609936403516176055814675733402367078336518,
            11109119319205035407439475164385621667323273220679402197763727966547531138806
        );
        G2Point memory pk = G2Point(
            [18494616575503903120376342216781239457744054585972897284515369100821805514708,
            6058699925139029963510971388154417849683991092423853405538953619046157720129],
            [10447029222341628412073874387780384247408405486505063737448475402228176014405,
            9623884690961721645090435833266382374848607517917752138280956443886007043089]
        );
        return pairing2(negSigma, P2, third, pk);
    }


    // return the result of computing the pairing check
    function pairing(G1Point[] memory p1, G2Point[] memory p2) internal returns (bool) {
        require(p1.length == p2.length);
        uint elements = p1.length;
        uint inputSize = elements * 6;
        uint[] memory input = new uint[](inputSize);

        for (uint i = 0; i < elements; i++)
        {
            input[i * 6 + 0] = p1[i].X;
            input[i * 6 + 1] = p1[i].Y;
            input[i * 6 + 2] = p2[i].X[0];
            input[i * 6 + 3] = p2[i].X[1];
            input[i * 6 + 4] = p2[i].Y[0];
            input[i * 6 + 5] = p2[i].Y[1];
        }

        uint[1] memory out;
        bool success;

        assembly {
            success := call(2000, 8, 0, add(input, 0x20), mul(inputSize, 0x20), out, 0x20)
        // Use "invalid" to make gas estimation work
        //    switch success case 0 {invalid()}
        }
        // require(success);
        return out[0] != 0;
    }

    /// Convenience method for a pairing check for two pairs.
    function pairing2(G1Point memory a1, G2Point memory a2, G1Point memory b1, G2Point memory b2) internal returns (bool) {
        G1Point[] memory p1 = new G1Point[](2);
        G2Point[] memory p2 = new G2Point[](2);
        p1[0] = a1;
        p1[1] = b1;
        p2[0] = a2;
        p2[1] = b2;
        return pairing(p1, p2);
    }


    // function hashToG1(bytes memory message) internal returns (G1Point memory) {
    //     uint256 h = uint256(keccak256(message));
    //     return mul(P1(), h);
    // }

    // function modPow(uint256 base, uint256 exponent, uint256 modulus) internal returns (uint256) {
    //     uint256[6] memory input = [32, 32, 32, base, exponent, modulus];
    //     uint256[1] memory result;
    //     assembly {
    //     // call调用8号预编译智能合约https://github.com/ethereum/EIPs/blob/master/EIPS/eip-197.md
    //     // call的输入长度必须是192，即0xc0
    //     // G2群的阶为q = 21888242871839275222246405745257275088548364400416034343698204186575808495617
    //         if iszero(call(not(0), 0x05, 0, input, 0xc0, result, 0x20)) {
    //             revert(0, 0)
    //         }
    //     }
    //     return result[0];
    // }

    // // return the negation of p, i.e. p.add(p.negate()) should be zero.
    // function negate(G1Point memory p) internal returns (G1Point memory) {
    //     // The prime q in the base field F_q for G1
    //     uint q = 21888242871839275222246405745257275088696311157297823662689037894645226208583;
    //     if (p.X == 0 && p.Y == 0)
    //         return G1Point(0, 0);
    //     return G1Point(p.X, q - (p.Y % q));
    // }

    // // return the sum of two points of G1
    // function add(G1Point memory p1, G1Point memory p2) internal returns (G1Point memory r) {
    //     uint[4] memory input;
    //     input[0] = p1.X;
    //     input[1] = p1.Y;
    //     input[2] = p2.X;
    //     input[3] = p2.Y;
    //     bool success;
    //     assembly {
    //         success := call(2000, 6, 0, input, 0xc0, r, 0x60)
    //     // Use "invalid" to make gas estimation work
    //         switch success case 0 {invalid()}
    //     }
    //     require(success);
    // }
    // // return the product of a point on G1 and a scalar, i.e.
    // // p == p.mul(1) and p.add(p) == p.mul(2) for all points p.
    // function mul(G1Point memory p, uint s) internal returns (G1Point memory r) {
    //     uint[3] memory input;
    //     input[0] = p.X;
    //     input[1] = p.Y;
    //     input[2] = s;
    //     bool success;
    //     assembly {
    //         success := call(2000, 7, 0, input, 0x80, r, 0x60)
    //     // Use "invalid" to make gas estimation work
    //     // switch success case 0 {invalid()}
    //     }
    //     // require(success);
    // }
}