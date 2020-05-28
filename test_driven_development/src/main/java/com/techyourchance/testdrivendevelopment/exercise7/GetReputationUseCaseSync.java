package com.techyourchance.testdrivendevelopment.exercise7;

import com.techyourchance.testdrivendevelopment.exercise7.networking.GetReputationHttpEndpointSync;

import static com.techyourchance.testdrivendevelopment.exercise7.networking.GetReputationHttpEndpointSync.*;

public class GetReputationUseCaseSync {

    public enum UseCaseResult {FAILURE, SUCCESS}
    private final GetReputationHttpEndpointSync mGetReputationHttpEndpointSync;

    public GetReputationUseCaseSync(GetReputationHttpEndpointSync getReputationHttpEndpointSync) {
        mGetReputationHttpEndpointSync = getReputationHttpEndpointSync;
    }

    EndpointResult fetchReputation() {
       EndpointResult result = mGetReputationHttpEndpointSync.getReputationSync();
        switch (result.getStatus()) {
            case SUCCESS:
                return new EndpointResult(Status.SUCCESS, 1);
            case GENERAL_ERROR:
                return new EndpointResult(Status.GENERAL_ERROR, 0);
            case NETWORK_ERROR:
                return new EndpointResult(Status.NETWORK_ERROR, 0);
            default:
                throw new RuntimeException("Invalid result: " + result);
        }
    }

}
