pragma solidity 0.5.0;

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

contract BLS2 {
    uint[2] P1 = [1, 2];

    uint[4] P2 = [
    11559732032986387107991004021392285783925812861821192530917403151452391805634,
    10857046999023057135944570762232829481370756359578518086990519993285655852781,
    4082367875863433681332203403145435568316851327593401208105741076214120093531,
    8495653923123431417604973247489272438418190587263600148770280649306958101930
    ];

    event notify(string note);

    // function verify2() public returns(bool) {
    //     uint[2] memory g1 = [11181692345848957662074290878138344227085597134981019040735323471731897153462,
    //     15408496424792704861810691495984499005908379011086059411341239226197844282416];

    //     uint[2] memory h = [20149421967983502318226724386255136356408556890336186742179977631390150035241,
    //     16251366894813361993800608195558474704961902796192284831853186885509923697836];

    //     uint[4] memory v = [
    //         18523194229674161632574346342370534213928970227736813349975332190798837787897,
    //         5725452645840548248571879966249653216818629536104756116202892528545334967238,
    //         3816656720215352836236372430537606984911914992659540439626020770732736710924,
    //         677280212051826798882467475639465784259337739185938192379192340908771705870
    //     ];

    //     return pairing(g1, P2, h, v);
    // }

    function verify(uint[2] memory negSigma, uint[2] memory third, uint[4] memory pk) public {
        if (pairing(negSigma, P2, third, pk)) {
            emit notify("verify succeed!");
        } else {
            emit notify("verify failed!");
        }

    }

    //    function test() public returns (bool) {
    //        uint[2] memory negSigma = [
    //        17432261763866387095582049976796525256479578351141356484603392714179114955684,
    //        9757289451970752754805169688107053619978027705305722623354262801279908936346
    //        ];
    //
    //        uint[2] memory third = [
    //        20188147869696374457947683918983989609936403516176055814675733402367078336518,
    //        11109119319205035407439475164385621667323273220679402197763727966547531138806
    //        ];
    //
    //        uint[4] memory pk = [
    //        18494616575503903120376342216781239457744054585972897284515369100821805514708,
    //        6058699925139029963510971388154417849683991092423853405538953619046157720129,
    //        10447029222341628412073874387780384247408405486505063737448475402228176014405,
    //        9623884690961721645090435833266382374848607517917752138280956443886007043089
    //        ];
    //
    //        return pairing(negSigma, P2, third, pk);
    //    }

    // return the result of computing the pairing check
    function pairing(uint[2] memory a1, uint[4] memory a2, uint[2] memory b1, uint[4] memory b2) internal returns (bool) {
        uint inputSize = 2 * 6;
        uint[] memory input = new uint[](inputSize);

        input[0] = a1[0];
        input[1] = a1[1];
        input[2] = a2[0];
        input[3] = a2[1];
        input[4] = a2[2];
        input[5] = a2[3];

        input[6] = b1[0];
        input[7] = b1[1];
        input[8] = b2[0];
        input[9] = b2[1];
        input[10] = b2[2];
        input[11] = b2[3];

        uint[1] memory out;
        bool success;

        assembly {
            success := call(sub(gas, 2000), 8, 0, add(input, 0x20), mul(inputSize, 0x20), out, 0x20)
        // Use "invalid" to make gas estimation work
            switch success case 0 {invalid()}
        }
        require(success);
        return out[0] != 0;
    }
}