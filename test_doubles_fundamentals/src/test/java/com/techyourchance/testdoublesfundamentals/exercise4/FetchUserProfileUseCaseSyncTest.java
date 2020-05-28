package com.techyourchance.testdoublesfundamentals.exercise4;

import com.techyourchance.testdoublesfundamentals.example4.networking.NetworkErrorException;
import com.techyourchance.testdoublesfundamentals.exercise4.networking.UserProfileHttpEndpointSync;
import com.techyourchance.testdoublesfundamentals.exercise4.users.User;
import com.techyourchance.testdoublesfundamentals.exercise4.users.UsersCache;

import org.jetbrains.annotations.Nullable;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.techyourchance.testdoublesfundamentals.exercise4.FetchUserProfileUseCaseSync.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class FetchUserProfileUseCaseSyncTest {
    private static final String USERID = "userID";
    public static final String FULL_NAME = "fullName";
    public static final String IMAGE_URL = "imageUrl";


    UserProfileHttpEndpointSyncTd mUserProFileHttpEndpointSyncTd;
    UsersCacheTd mUsersCacheTd;

    FetchUserProfileUseCaseSync SUT;

    @Before
    public void setUp() throws Exception {
        mUserProFileHttpEndpointSyncTd = new UserProfileHttpEndpointSyncTd();
        mUsersCacheTd = new UsersCacheTd();
        SUT = new FetchUserProfileUseCaseSync(mUserProFileHttpEndpointSyncTd, mUsersCacheTd);

    }

    @Test
    public void fetchUser_succes_userIdPassedToEndpoint() {
        SUT.fetchUserProfileSync(USERID);
        assertThat(mUserProFileHttpEndpointSyncTd.mUserId, is(USERID));

    }

    @Test
    public void fetchUser_success_userCached() {
        SUT.fetchUserProfileSync(USERID);
        User mUser = mUsersCacheTd.getUser(USERID);
        assertThat(mUsersCacheTd.getUser(USERID),is(mUser));
    }

    @Test
    public void fetchUser_generalError_userNotCached() {
        mUserProFileHttpEndpointSyncTd.mIsGeneralError = true;
        SUT.fetchUserProfileSync(USERID);
        assertThat(mUsersCacheTd.getUser(USERID), is(nullValue()));
    }

    @Test
    public void fetchUser_authError_userNotCached() {
        mUserProFileHttpEndpointSyncTd.mIsAuthError = true;
        SUT.fetchUserProfileSync(USERID);
        assertThat(mUsersCacheTd.getUser(USERID), is(nullValue()));
    }

    @Test
    public void fetchUser_serverError_userNotCached() {
        mUserProFileHttpEndpointSyncTd.mIsServerError = true;
        SUT.fetchUserProfileSync(USERID);
        assertThat(mUsersCacheTd.getUser(USERID), is(nullValue()));
    }

    @Test
    public void fetchUser_success_successReturned() {
       UseCaseResult result = SUT.fetchUserProfileSync(USERID);
       assertThat(result,is(UseCaseResult.SUCCESS));

    }

    @Test
    public void fetchUser_serverError_failureReturned() {
        mUserProFileHttpEndpointSyncTd.mIsServerError = true;
        UseCaseResult result = SUT.fetchUserProfileSync(USERID);
        assertThat(result,is(UseCaseResult.FAILURE));

    }

    @Test
    public void fetchUser_authError_failureReturned() {
        mUserProFileHttpEndpointSyncTd.mIsAuthError = true;
        UseCaseResult result = SUT.fetchUserProfileSync(USERID);
        assertThat(result,is(UseCaseResult.FAILURE));

    }

    @Test
    public void fetchUser_generalError_failureReturned() {
        mUserProFileHttpEndpointSyncTd.mIsGeneralError = true;
        UseCaseResult result = SUT.fetchUserProfileSync(USERID);
        assertThat(result,is(UseCaseResult.FAILURE));

    }

    @Test
    public void fetchUser_network_networkErrorReturned() {
        mUserProFileHttpEndpointSyncTd.mIsNetworkError = true;
        UseCaseResult result = SUT.fetchUserProfileSync(USERID);
        assertThat(result,is(UseCaseResult.NETWORK_ERROR));

    }


    /**
     * Helper Classes
     */
    public static class UserProfileHttpEndpointSyncTd implements UserProfileHttpEndpointSync {
        public String mUserId = "";
        public boolean mIsGeneralError;
        public boolean mIsAuthError;
        public boolean mIsServerError;
        public boolean mIsNetworkError;

        @Override
        public EndpointResult getUserProfile(String userId) throws NetworkErrorException {
            mUserId = userId;
            if (mIsGeneralError) {
                return new EndpointResult(EndpointResultStatus.GENERAL_ERROR, "", "", "");
            } else if (mIsAuthError) {
                return new EndpointResult(EndpointResultStatus.AUTH_ERROR, "", "", "");
            }  else if (mIsServerError) {
                return new EndpointResult(EndpointResultStatus.SERVER_ERROR, "", "", "");
            } else if (mIsNetworkError) {
                throw new NetworkErrorException();
            } else {
                return new EndpointResult(EndpointResultStatus.SUCCESS, USERID, FULL_NAME, IMAGE_URL);
            }
        }

    }

    public static class UsersCacheTd implements UsersCache {
        private List<User> mUsers = new ArrayList<>(1);

        @Override
        public void cacheUser(User user) {
            User existingUser = getUser(user.getUserId());
            if (existingUser != null) {
                mUsers.remove(existingUser);
            }
            mUsers.add(user);
        }

        @Override
        @Nullable
        public User getUser(String userId) {
            for (User user : mUsers) {
                if (user.getUserId().equals(userId)) {
                    return user;
                }
            }
            return null;
        }
    }
}