package com.techyourchance.testdrivendevelopment.exercise7;

import com.techyourchance.testdrivendevelopment.exercise7.networking.GetReputationHttpEndpointSync;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static com.techyourchance.testdrivendevelopment.exercise7.GetReputationUseCaseSync.UseCaseResult;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GetReputationUseCaseSyncTest {

    //Region Constants
    static final int REPUTATION = 0;
    //End Region Constants

    //Region Helper Fields
    @Mock
    GetReputationHttpEndpointSync mGetReputationHttpEndpointSyncMock;
    //End Region helper Fields

    GetReputationUseCaseSync SUT;

    @Before
    public void setup() throws Exception {
        SUT = new GetReputationUseCaseSync(mGetReputationHttpEndpointSyncMock);
        success();
    }

    @Test
    public void fetchReputation_success_successReturned() {
        //Arrange
        //Act
        GetReputationHttpEndpointSync.EndpointResult result = SUT.fetchReputation();
        //Assert
        assertThat(result.getStatus(), is(GetReputationHttpEndpointSync.Status.SUCCESS));
    }

    @Test
    public void fetchReputation_success_ReputationReturned() {
        //Arrange
        //Act
        GetReputationHttpEndpointSync.EndpointResult result = SUT.fetchReputation();
        //Assert
        assertThat(result.getReputation(), is(1));
    }

    @Test
    public void fetchReputation_generalError_failureReturned() {
        //Arrange
        generalError();
        //Act
        //Act
        GetReputationHttpEndpointSync.EndpointResult result = SUT.fetchReputation();
        //Assert
        assertThat(result.getStatus(), is(GetReputationHttpEndpointSync.Status.GENERAL_ERROR));
    }

    @Test
    public void fetchReputation_networkError_failureReturned() {
        //Arrange
        networkError();
        //Act
        GetReputationHttpEndpointSync.EndpointResult result = SUT.fetchReputation();
        //Assert
        assertThat(result.getStatus(), is(GetReputationHttpEndpointSync.Status.NETWORK_ERROR));
    }


    //Region Methods
    private void success() {
        when(mGetReputationHttpEndpointSyncMock.getReputationSync()).thenReturn(new GetReputationHttpEndpointSync.EndpointResult(GetReputationHttpEndpointSync.Status.SUCCESS,REPUTATION));
    }

    private void generalError() {
        when(mGetReputationHttpEndpointSyncMock.getReputationSync()).thenReturn(new GetReputationHttpEndpointSync.EndpointResult(GetReputationHttpEndpointSync.Status.GENERAL_ERROR,REPUTATION));
    }

    private void networkError() {
        when(mGetReputationHttpEndpointSyncMock.getReputationSync()).thenReturn(new GetReputationHttpEndpointSync.EndpointResult(GetReputationHttpEndpointSync.Status.NETWORK_ERROR,REPUTATION));
    }
    //End Region Methods

    //Region Classes

    //End Region classes
}