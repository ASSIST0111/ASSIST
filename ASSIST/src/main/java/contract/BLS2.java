package contract;

import io.reactivex.Flowable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.BaseEventResponse;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 4.5.5.
 */
@SuppressWarnings("rawtypes")
public class BLS2 extends Contract {
    private static final String BINARY = "60c060405260016080908152600260a081905261001e916000916100e1565b50604080516080810182527f198e9393920d483a7260bfb731fb5d25f1aa493335a9e71297e485b7aef312c281527f1800deef121f1e76426a00665e5c4479674322d4f75edadd46debd5cd992f6ed60208201527f090689d0585ff075ec9e99ad690c3395bc4b313370b38ef355acdadcd122975b918101919091527f12c85ea5db8c6deb4aab71808dcb408fe3d1e7690c43d37b4ce6cc0166fa7daa60608201526100ce906002906004610124565b503480156100db57600080fd5b5061016f565b8260028101928215610114579160200282015b82811115610114578251829060ff169055916020019190600101906100f4565b50610120929150610152565b5090565b8260048101928215610114579160200282015b82811115610114578251825591602001919060010190610137565b61016c91905b808211156101205760008155600101610158565b90565b6104308061017e6000396000f3fe6080604052600436106100405763ffffffff7c010000000000000000000000000000000000000000000000000000000060003504166393181aea8114610045575b600080fd5b34801561005157600080fd5b506100f3600480360361010081101561006957600080fd5b60408051808201825291830192918183019183906002908390839080828437600092019190915250506040805180820182529295949381810193925090600290839083908082843760009201919091525050604080516080818101909252929594938181019392509060049083908390808284376000920191909152509194506100f59350505050565b005b6040805160808101918290526101309185919060029060049082845b8154815260200190600101908083116101115750505050508484610200565b1561019a57604080516020808252600f908201527f76657269667920737563636565642100000000000000000000000000000000008183015290517fa396f8d98c67c164560cb969bf42c7fd20318f6a13d8a83e7a589a61fb1214b69181900360600190a16101fb565b604080516020808252600e908201527f766572696679206661696c6564210000000000000000000000000000000000008183015290517fa396f8d98c67c164560cb969bf42c7fd20318f6a13d8a83e7a589a61fb1214b69181900360600190a15b505050565b60408051600c8082526101a082019092526000919060609082602082016101808038833950508851825192935091839150600090811061023c57fe5b60209081029091010152866001602002015181600181518110151561025d57fe5b6020908102919091010152855181518290600290811061027957fe5b60209081029091010152856001602002015181600381518110151561029a57fe5b602090810291909101015260408601518151829060049081106102b957fe5b602090810291909101015260608601518151829060059081106102d857fe5b602090810291909101015284518151829060069081106102f457fe5b60209081029091010152846001602002015181600781518110151561031557fe5b6020908102919091010152835181518290600890811061033157fe5b60209081029091010152836001602002015181600981518110151561035257fe5b6020908102919091010152604084015181518290600a90811061037157fe5b6020908102919091010152606084015181518290600b90811061039057fe5b602090810290910101526103a26103e5565b60006020826020860260208601600060086107d05a03f190508080156103c7576103c9565bfe5b508015156103d657600080fd5b50511515979650505050505050565b602060405190810160405280600190602082028038833950919291505056fea165627a7a72305820ada328815cada1068edc7ac31b5ebd6558d0de69cbb6d8c9c876598665c00ef60029";

    public static final String FUNC_VERIFY = "verify";

    public static final Event NOTIFY_EVENT = new Event("notify", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
    ;

    @Deprecated
    protected BLS2(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected BLS2(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected BLS2(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected BLS2(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteFunctionCall<TransactionReceipt> verify(List<BigInteger> negSigma, List<BigInteger> third, List<BigInteger> pk) {
        final Function function = new Function(
                FUNC_VERIFY, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.StaticArray2<org.web3j.abi.datatypes.generated.Uint256>(
                        org.web3j.abi.datatypes.generated.Uint256.class,
                        org.web3j.abi.Utils.typeMap(negSigma, org.web3j.abi.datatypes.generated.Uint256.class)), 
                new org.web3j.abi.datatypes.generated.StaticArray2<org.web3j.abi.datatypes.generated.Uint256>(
                        org.web3j.abi.datatypes.generated.Uint256.class,
                        org.web3j.abi.Utils.typeMap(third, org.web3j.abi.datatypes.generated.Uint256.class)), 
                new org.web3j.abi.datatypes.generated.StaticArray4<org.web3j.abi.datatypes.generated.Uint256>(
                        org.web3j.abi.datatypes.generated.Uint256.class,
                        org.web3j.abi.Utils.typeMap(pk, org.web3j.abi.datatypes.generated.Uint256.class))), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public List<NotifyEventResponse> getNotifyEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(NOTIFY_EVENT, transactionReceipt);
        ArrayList<NotifyEventResponse> responses = new ArrayList<NotifyEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            NotifyEventResponse typedResponse = new NotifyEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.note = (String) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<NotifyEventResponse> notifyEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new io.reactivex.functions.Function<Log, NotifyEventResponse>() {
            @Override
            public NotifyEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(NOTIFY_EVENT, log);
                NotifyEventResponse typedResponse = new NotifyEventResponse();
                typedResponse.log = log;
                typedResponse.note = (String) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<NotifyEventResponse> notifyEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(NOTIFY_EVENT));
        return notifyEventFlowable(filter);
    }

    @Deprecated
    public static BLS2 load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new BLS2(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static BLS2 load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new BLS2(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static BLS2 load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new BLS2(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static BLS2 load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new BLS2(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<BLS2> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(BLS2.class, web3j, credentials, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<BLS2> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(BLS2.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    public static RemoteCall<BLS2> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(BLS2.class, web3j, transactionManager, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<BLS2> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(BLS2.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }

    public static class NotifyEventResponse extends BaseEventResponse {
        public String note;
    }
}
