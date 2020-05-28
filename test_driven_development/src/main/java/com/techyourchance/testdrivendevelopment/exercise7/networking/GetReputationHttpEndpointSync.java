package com.techyourchance.testdrivendevelopment.exercise7.networking;


public interface GetReputationHttpEndpointSync {

    enum Status {
        SUCCESS,
        GENERAL_ERROR,
        NETWORK_ERROR
    }

    class EndpointResult {
        private final Status mEndpointStatus;
        private final int mReputation;

        public EndpointResult(Status endpointStatus, int reputation) {
            mEndpointStatus = endpointStatus;
            mReputation = reputation;
        }

        public Status getStatus() {
            return mEndpointStatus;
        }

        public int getReputation() {
            return mReputation;
        }
    }

    EndpointResult getReputationSync();

}
