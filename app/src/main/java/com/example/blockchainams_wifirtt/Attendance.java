package com.example.blockchainams_wifirtt;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.BaseEventResponse;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple4;
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
 * <p>Generated with web3j version 1.4.2.
 */
@SuppressWarnings("rawtypes")
public class Attendance extends Contract {
    public static final String BINARY = "608060405234801561001057600080fd5b50600080546001600160a01b03191633178155600160208190527f4533c95d52b46b38f41b95d866a89331e87acf8b6d3d95829cc37e65b6d940328290557e2f36312fe718710bda6e71ff56951877af80c6adbaf3f1e4cc2a1247007cdb5560027fb4be2af242007639fc63c02637403c17b63ea04f098e9173900682e617fc1f2f5560037ff29f8ab690f0b467f25d1ab6640cb62537662a4cdfe9719a0729e4f216a538f95560047f47d8697a69fd19e4247d6d171c8575c87d07c216cfa6efb90a767757e66e1f5a5560057fd12af517889b555d6d18773ab23e33faf009136e125973ad64702325b318f42e5560067f96fc8b54d661b00b190246f4366ca6d51aa5992e2e7064d0cfc8bab9fe20e9085560077fd9ae031f09e0051b8dc913f25428ce3d66001b51807570858b8c2db402b833255560087f7bf50eb75282bf46098f19736a661eb0aa8f13dd08f64124b599447f1c2f65a05573d615cf4b5ce86c6c5eff7450d828be61fb3fadb9905260097fe3330473f243f97bb743762b6078950aac30ae6e6af490746269ad4c501302ce55610ab5806101b56000396000f3fe608060405234801561001057600080fd5b50600436106100935760003560e01c806375b831871161006657806375b8318714610109578063a87430ba1461011c578063b4a085e41461013c578063e581f9ee1461015f578063e8cd7cd01461016757610093565b8063153ee35914610098578063296c30dd146100c15780633dc38fc1146100e157806349f15e9e146100f6575b600080fd5b6100ab6100a63660046108a1565b61017a565b6040516100b8919061094e565b60405180910390f35b6100d46100cf36600461081f565b6102a5565b6040516100b8919061090a565b6100f46100ef3660046108a1565b61030a565b005b6100f4610104366004610837565b610347565b6100ab6101173660046107be565b6103fa565b61012f61012a366004610790565b610453565b6040516100b89190610992565b61014f61014a3660046108a1565b610465565b6040516100b89493929190610959565b61012f6105c6565b6100f46101753660046108a1565b6105cc565b600080546001600160a01b0316331461019257600080fd5b6000838152600260205260408120600301905b8154811015610298578181815481106101ce57634e487b7160e01b600052603260045260246000fd5b906000526020600020015484141561028657815482906101f0906001906109b6565b8154811061020e57634e487b7160e01b600052603260045260246000fd5b906000526020600020015482828154811061023957634e487b7160e01b600052603260045260246000fd5b90600052602060002001819055508180548061026557634e487b7160e01b600052603160045260246000fd5b6001900381819060005260206000200160009055905560019250505061029f565b8061029081610a38565b9150506101a5565b5060009150505b92915050565b6000818152600260209081526040918290206003018054835181840281018401909452808452606093928301828280156102fe57602002820191906000526020600020905b8154815260200190600101908083116102ea575b50505050509050919050565b6000546001600160a01b0316331461032157600080fd5b600091825260026020908152604083206003018054600181018255908452922090910155565b6000546001600160a01b0316331461035e57600080fd5b60035460008181526002602090815260409091208581558451909161038a916001840191870190610676565b5082516103a09060028301906020860190610676565b50600380549060006103b183610a38565b91905055507fe75cc56b09aae2fe392f4fcc8b3869d07ed5bfb11085dd67ef4c79890089c505848487426040516103eb9493929190610959565b60405180910390a15050505050565b60008160405160200161040d91906108ee565b604051602081830303815290604052805190602001208360405160200161043491906108ee565b6040516020818303038152906040528051906020012014905092915050565b60016020526000908152604090205481565b60008281526002602081815260408084208054868652600482019093529084205460018201805460609687969095869593949390910192909184906104a9906109fd565b80601f01602080910402602001604051908101604052809291908181526020018280546104d5906109fd565b80156105225780601f106104f757610100808354040283529160200191610522565b820191906000526020600020905b81548152906001019060200180831161050557829003601f168201915b50505050509350828054610535906109fd565b80601f0160208091040260200160405190810160405280929190818152602001828054610561906109fd565b80156105ae5780601f10610583576101008083540402835291602001916105ae565b820191906000526020600020905b81548152906001019060200180831161059157829003601f168201915b50505050509250935093509350935092959194509250565b60035481565b33600090815260016020526040902054829081146105e957600080fd5b6000838152600260209081526040808320858452600401909152812080549161061183610a38565b90915550506000838152600260209081526040808320858452600401909152908190205490517f40428cfaa67b52e00a7ae3a4c131d5e35e000ad8080b17af0e83243cfc936b6b91610669918691904290879061099b565b60405180910390a1505050565b828054610682906109fd565b90600052602060002090601f0160209004810192826106a457600085556106ea565b82601f106106bd57805160ff19168380011785556106ea565b828001600101855582156106ea579182015b828111156106ea5782518255916020019190600101906106cf565b506106f69291506106fa565b5090565b5b808211156106f657600081556001016106fb565b600082601f83011261071f578081fd5b813567ffffffffffffffff8082111561073a5761073a610a69565b604051601f8301601f19168101602001828111828210171561075e5761075e610a69565b604052828152848301602001861015610775578384fd5b82602086016020830137918201602001929092529392505050565b6000602082840312156107a1578081fd5b81356001600160a01b03811681146107b7578182fd5b9392505050565b600080604083850312156107d0578081fd5b823567ffffffffffffffff808211156107e7578283fd5b6107f38683870161070f565b93506020850135915080821115610808578283fd5b506108158582860161070f565b9150509250929050565b600060208284031215610830578081fd5b5035919050565b60008060006060848603121561084b578081fd5b83359250602084013567ffffffffffffffff80821115610869578283fd5b6108758783880161070f565b9350604086013591508082111561088a578283fd5b506108978682870161070f565b9150509250925092565b600080604083850312156108b3578182fd5b50508035926020909101359150565b600081518084526108da8160208601602086016109cd565b601f01601f19169290920160200192915050565b600082516109008184602087016109cd565b9190910192915050565b6020808252825182820181905260009190848201906040850190845b8181101561094257835183529284019291840191600101610926565b50909695505050505050565b901515815260200190565b60006080825261096c60808301876108c2565b828103602084015261097e81876108c2565b604084019590955250506060015292915050565b90815260200190565b93845260208401929092526040830152606082015260800190565b6000828210156109c8576109c8610a53565b500390565b60005b838110156109e85781810151838201526020016109d0565b838111156109f7576000848401525b50505050565b600281046001821680610a1157607f821691505b60208210811415610a3257634e487b7160e01b600052602260045260246000fd5b50919050565b6000600019821415610a4c57610a4c610a53565b5060010190565b634e487b7160e01b600052601160045260246000fd5b634e487b7160e01b600052604160045260246000fdfea2646970667358221220a7f0ed93aada9844a3b224b79e16b5a9ae9d6e60847a8ad34f08f9976486086f64736f6c63430008000033";

    public static final String FUNC_ADDCOURSE = "addCourse";

    public static final String FUNC_COMPARESTRING = "compareString";

    public static final String FUNC_CREATESTUDENT = "createStudent";

    public static final String FUNC_DOATTENDANCE = "doAttendance";

    public static final String FUNC_GETPARTICULARSTUDENT = "getParticularStudent";

    public static final String FUNC_GETREGISTEREDCOURSE = "getRegisteredCourse";

    public static final String FUNC_REMOVECOURSE = "removeCourse";

    public static final String FUNC_STUDENTCOUNT = "studentCount";

    public static final String FUNC_USERS = "users";

    public static final Event ATTENDANCERECORDEVENT_EVENT = new Event("AttendanceRecordEvent",
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event STUDENTCREATIONEVENT_EVENT = new Event("studentCreationEvent",
            Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
    ;

    @Deprecated
    protected Attendance(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected Attendance(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected Attendance(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected Attendance(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

//    public static List<AttendanceRecordEventEventResponse> getAttendanceRecordEventEvents(TransactionReceipt transactionReceipt) {
//        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(ATTENDANCERECORDEVENT_EVENT, transactionReceipt);
//        ArrayList<AttendanceRecordEventEventResponse> responses = new ArrayList<AttendanceRecordEventEventResponse>(valueList.size());
//        for (Contract.EventValuesWithLog eventValues : valueList) {
//            AttendanceRecordEventEventResponse typedResponse = new AttendanceRecordEventEventResponse();
//            typedResponse.log = eventValues.getLog();
//            typedResponse.studentId = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
//            typedResponse.attVal = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
//            typedResponse.recordDate = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
//            typedResponse.courseId = (BigInteger) eventValues.getNonIndexedValues().get(3).getValue();
//            responses.add(typedResponse);
//        }
//        return responses;
//    }

    public Flowable<AttendanceRecordEventEventResponse> attendanceRecordEventEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, AttendanceRecordEventEventResponse>() {
            @Override
            public AttendanceRecordEventEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(ATTENDANCERECORDEVENT_EVENT, log);
                AttendanceRecordEventEventResponse typedResponse = new AttendanceRecordEventEventResponse();
                typedResponse.log = log;
                typedResponse.studentId = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.attVal = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
                typedResponse.recordDate = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
                typedResponse.courseId = (BigInteger) eventValues.getNonIndexedValues().get(3).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<AttendanceRecordEventEventResponse> attendanceRecordEventEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(ATTENDANCERECORDEVENT_EVENT));
        return attendanceRecordEventEventFlowable(filter);
    }

//    public static List<StudentCreationEventEventResponse> getStudentCreationEventEvents(TransactionReceipt transactionReceipt) {
//        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(STUDENTCREATIONEVENT_EVENT, transactionReceipt);
//        ArrayList<StudentCreationEventEventResponse> responses = new ArrayList<StudentCreationEventEventResponse>(valueList.size());
//        for (Contract.EventValuesWithLog eventValues : valueList) {
//            StudentCreationEventEventResponse typedResponse = new StudentCreationEventEventResponse();
//            typedResponse.log = eventValues.getLog();
//            typedResponse.fName = (String) eventValues.getNonIndexedValues().get(0).getValue();
//            typedResponse.lName = (String) eventValues.getNonIndexedValues().get(1).getValue();
//            typedResponse.age = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
//            typedResponse.createdDate = (BigInteger) eventValues.getNonIndexedValues().get(3).getValue();
//            responses.add(typedResponse);
//        }
//        return responses;
//    }

    public Flowable<StudentCreationEventEventResponse> studentCreationEventEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, StudentCreationEventEventResponse>() {
            @Override
            public StudentCreationEventEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(STUDENTCREATIONEVENT_EVENT, log);
                StudentCreationEventEventResponse typedResponse = new StudentCreationEventEventResponse();
                typedResponse.log = log;
                typedResponse.fName = (String) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.lName = (String) eventValues.getNonIndexedValues().get(1).getValue();
                typedResponse.age = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
                typedResponse.createdDate = (BigInteger) eventValues.getNonIndexedValues().get(3).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<StudentCreationEventEventResponse> studentCreationEventEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(STUDENTCREATIONEVENT_EVENT));
        return studentCreationEventEventFlowable(filter);
    }

    public RemoteFunctionCall<TransactionReceipt> addCourse(BigInteger _studId, BigInteger courseId) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_ADDCOURSE,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_studId),
                        new org.web3j.abi.datatypes.generated.Uint256(courseId)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<Boolean> compareString(String a, String b) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_COMPARESTRING,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(a),
                        new org.web3j.abi.datatypes.Utf8String(b)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteFunctionCall<TransactionReceipt> createStudent(BigInteger _age, String _fName, String _lName) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_CREATESTUDENT,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_age),
                        new org.web3j.abi.datatypes.Utf8String(_fName),
                        new org.web3j.abi.datatypes.Utf8String(_lName)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> doAttendance(BigInteger _studId, BigInteger _courseId) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_DOATTENDANCE,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_studId),
                        new org.web3j.abi.datatypes.generated.Uint256(_courseId)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<Tuple4<String, String, BigInteger, BigInteger>> getParticularStudent(BigInteger _studId, BigInteger _courseId) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETPARTICULARSTUDENT,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_studId),
                        new org.web3j.abi.datatypes.generated.Uint256(_courseId)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
        return new RemoteFunctionCall<Tuple4<String, String, BigInteger, BigInteger>>(function,
                new Callable<Tuple4<String, String, BigInteger, BigInteger>>() {
                    @Override
                    public Tuple4<String, String, BigInteger, BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple4<String, String, BigInteger, BigInteger>(
                                (String) results.get(0).getValue(),
                                (String) results.get(1).getValue(),
                                (BigInteger) results.get(2).getValue(),
                                (BigInteger) results.get(3).getValue());
                    }
                });
    }

    public RemoteFunctionCall<List> getRegisteredCourse(BigInteger _studId) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETREGISTEREDCOURSE,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_studId)),
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicArray<Uint256>>() {}));
        return new RemoteFunctionCall<List>(function,
                new Callable<List>() {
                    @Override
                    @SuppressWarnings("unchecked")
                    public List call() throws Exception {
                        List<Type> result = (List<Type>) executeCallSingleValueReturn(function, List.class);
                        return convertToNative(result);
                    }
                });
    }

    public RemoteFunctionCall<TransactionReceipt> removeCourse(BigInteger _studId, BigInteger _courseId) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_REMOVECOURSE,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_studId),
                        new org.web3j.abi.datatypes.generated.Uint256(_courseId)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<BigInteger> studentCount() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_STUDENTCOUNT,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> users(String param0) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_USERS,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, param0)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    @Deprecated
    public static Attendance load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new Attendance(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static Attendance load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new Attendance(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static Attendance load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new Attendance(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static Attendance load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new Attendance(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<Attendance> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(Attendance.class, web3j, credentials, contractGasProvider, BINARY, "");
    }

    public static RemoteCall<Attendance> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(Attendance.class, web3j, transactionManager, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<Attendance> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(Attendance.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<Attendance> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(Attendance.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }

    public static class AttendanceRecordEventEventResponse extends BaseEventResponse {
        public BigInteger studentId;

        public BigInteger attVal;

        public BigInteger recordDate;

        public BigInteger courseId;
    }

    public static class StudentCreationEventEventResponse extends BaseEventResponse {
        public String fName;

        public String lName;

        public BigInteger age;

        public BigInteger createdDate;
    }
}
