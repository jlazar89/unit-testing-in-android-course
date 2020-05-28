package com.techyourchance.mockitofundamentals.exercise5;

import com.techyourchance.mockitofundamentals.exercise5.UpdateUsernameUseCaseSync.UseCaseResult;
import com.techyourchance.mockitofundamentals.exercise5.eventbus.EventBusPoster;
import com.techyourchance.mockitofundamentals.exercise5.eventbus.UserDetailsChangedEvent;
import com.techyourchance.mockitofundamentals.exercise5.networking.NetworkErrorException;
import com.techyourchance.mockitofundamentals.exercise5.networking.UpdateUsernameHttpEndpointSync;
import com.techyourchance.mockitofundamentals.exercise5.networking.UpdateUsernameHttpEndpointSync.EndpointResult;
import com.techyourchance.mockitofundamentals.exercise5.networking.UpdateUsernameHttpEndpointSync.EndpointResultStatus;
import com.techyourchance.mockitofundamentals.exercise5.users.User;
import com.techyourchance.mockitofundamentals.exercise5.users.UsersCache;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class UpdateUsernameUseCaseSyncTest {

    public static final String USER_ID = "userID";
    public static final String USERNAME = "username";

    UpdateUsernameHttpEndpointSync mUpdateUsernameHttpEndpointSyncMock;
    UsersCache mUsersCacheMock;
    EventBusPoster mEventBusPosterMock;

    UpdateUsernameUseCaseSync SUT;

    @Before
    public void setUp() throws Exception {
        mUpdateUsernameHttpEndpointSyncMock = mock(UpdateUsernameHttpEndpointSync.class);
        mUsersCacheMock = mock(UsersCache.class);
        mEventBusPosterMock = mock(EventBusPoster.class);

        SUT = new UpdateUsernameUseCaseSync(mUpdateUsernameHttpEndpointSyncMock, mUsersCacheMock, mEventBusPosterMock);
        success();
    }

    //UserName passed to the ednpoint
    @Test
    public void updateUsername_success_userIdPassedToEndpoint() throws Exception {
        ArgumentCaptor<String> ac = ArgumentCaptor.forClass(String.class);
        SUT.updateUsernameSync(USER_ID, USERNAME);
        verify(mUpdateUsernameHttpEndpointSyncMock, times(1)).updateUsername(ac.capture(), ac.capture());
        List<String> capturfes = ac.getAllValues();
        assertThat(capturfes.get(0), is(USER_ID));
        assertThat(capturfes.get(1), is(USERNAME));
    }

    //if update user succeeds - username must be cached

    @Test
    public void updateUsername_success_userCached() {
        //Arrange
        ArgumentCaptor<User> ac = ArgumentCaptor.forClass(User.class);
        //Act
        SUT.updateUsernameSync(USER_ID, USERNAME);
        //Assert
        verify(mUsersCacheMock).cacheUser(ac.capture());
        User cachedUser = ac.getValue();
        assertThat(cachedUser.getUserId(), is(USER_ID));
        assertThat(cachedUser.getUsername(), is(USERNAME));
    }


    //if update user fails - username must is not changed
    @Test
    public void updateUsername_generalError_UserNotCached() throws Exception {
        generalError();
        SUT.updateUsernameSync(USER_ID,USERNAME);
        verifyNoMoreInteractions(mUsersCacheMock);
    }

    @Test
    public void updateUsername_authError_UserNotCached() throws Exception {
        authError();
        SUT.updateUsernameSync(USER_ID,USERNAME);
        verifyNoMoreInteractions(mUsersCacheMock);
    }

    @Test
    public void updateUsername_serverError_UserNotCached() throws Exception {
        serverError();
        SUT.updateUsernameSync(USER_ID,USERNAME);
        verifyNoMoreInteractions(mUsersCacheMock);
    }

    //if update user succeeds - update username event posted to eventbus
    @Test
    public void updateUsername_succeeds_loggedInEventPosted() throws Exception {
        ArgumentCaptor<Object> ac = ArgumentCaptor.forClass(Object.class);
        SUT.updateUsernameSync(USER_ID,USERNAME);
        verify(mEventBusPosterMock).postEvent(ac.capture());
        assertThat(ac.getValue(), is(instanceOf(UserDetailsChangedEvent.class)));
    }

    //if update user fails - no event posted
    @Test
    public void updateUsername_generalError_noInteractionWithEventBusPoster() throws Exception{
        generalError();
        SUT.updateUsernameSync(USER_ID,USERNAME);
        verifyNoMoreInteractions(mEventBusPosterMock);
    }

    @Test
    public void updateUsername_authError_noInteractionWithEventBusPoster() throws Exception{
        authError();
        SUT.updateUsernameSync(USER_ID,USERNAME);
        verifyNoMoreInteractions(mEventBusPosterMock);
    }


    @Test
    public void updateUsername_serverError_noInteractionWithEventBusPoster() throws Exception{
        serverError();
        SUT.updateUsernameSync(USER_ID,USERNAME);
        verifyNoMoreInteractions(mEventBusPosterMock);
    }


    //if update user succeeds - succes returned

    @Test
    public void updateUsername_success_successReturned() throws Exception{
        UseCaseResult result  = SUT.updateUsernameSync(USER_ID,USERNAME);
        assertThat(result,is(UseCaseResult.SUCCESS));

    }


    //fail - fail returned

    @Test
    public void updateUsername_serverError_failureReturned() throws Exception {
        serverError();
        UseCaseResult result = SUT.updateUsernameSync(USER_ID,USERNAME);
        assertThat(result,is(UseCaseResult.FAILURE));
    }

    @Test
    public void updateUsername_authError_failureReturned() throws Exception {
        authError();
        UseCaseResult result = SUT.updateUsernameSync(USER_ID,USERNAME);
        assertThat(result,is(UseCaseResult.FAILURE));
    }

    @Test
    public void updateUsername_generalError_failureReturned() throws Exception {
        generalError();
        UseCaseResult result = SUT.updateUsernameSync(USER_ID,USERNAME);
        assertThat(result,is(UseCaseResult.FAILURE));
    }

    //network - netwok error occured

    @Test
    public void updateUsername_networkError_netwoekErrorReturned() throws Exception {
        networkError();
        UseCaseResult result = SUT.updateUsernameSync(USER_ID, USERNAME);
        assertThat(result, is(UseCaseResult.NETWORK_ERROR));
    }



    private void success() throws NetworkErrorException {
        when(mUpdateUsernameHttpEndpointSyncMock.updateUsername(any(String.class), any(String.class)))
                .thenReturn(new EndpointResult(EndpointResultStatus.SUCCESS, USER_ID, USERNAME));
    }


    private void generalError() throws Exception {
        when(mUpdateUsernameHttpEndpointSyncMock.updateUsername(any(String.class), any(String.class)))
                .thenReturn(new EndpointResult(EndpointResultStatus.GENERAL_ERROR, "", ""));
    }

    private void authError() throws Exception {
        when(mUpdateUsernameHttpEndpointSyncMock.updateUsername(any(String.class), any(String.class)))
                .thenReturn(new EndpointResult(EndpointResultStatus.AUTH_ERROR, "", ""));
    }

    private void serverError() throws Exception {
        when(mUpdateUsernameHttpEndpointSyncMock.updateUsername(any(String.class), any(String.class)))
                .thenReturn(new EndpointResult(EndpointResultStatus.SERVER_ERROR, "", ""));
    }

    private void networkError() throws Exception {
       doThrow(new NetworkErrorException())
               .when(mUpdateUsernameHttpEndpointSyncMock).updateUsername(anyString(), anyString());
    }

}