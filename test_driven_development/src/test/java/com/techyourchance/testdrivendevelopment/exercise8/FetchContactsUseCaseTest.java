package com.techyourchance.testdrivendevelopment.exercise8;

import com.techyourchance.testdrivendevelopment.exercise8.contacts.Contact;
import com.techyourchance.testdrivendevelopment.exercise8.networking.ContactSchema;
import com.techyourchance.testdrivendevelopment.exercise8.networking.GetContactsHttpEndpoint;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(MockitoJUnitRunner.class)
public class FetchContactsUseCaseTest {

    //Region Constants
    public static final String FILTER_STRING = "String";
    public static final String ID = "id";
    public static final String FULLNAME = "fullname";
    public static final String PHONE_NUMBER = "phoneNmmber";
    public static final String IMAGE_URL = "imageUrl";
    public static final double AGE = 20;
    //End Region Constants

    //Region Helper Fields
    @Mock
    GetContactsHttpEndpoint mGetContactsHttpEndpoint;
    @Mock FetchContactsUseCase.Listener mListenerMock1;
    @Mock FetchContactsUseCase.Listener mListenerMock2;

    @Captor
    ArgumentCaptor<List<Contact>> mAcListContactItem;
    //End Region helper Fields

    FetchContactsUseCase SUT;

    @Before
    public void setup() throws Exception {
        SUT = new FetchContactsUseCase(mGetContactsHttpEndpoint);
        success();

    }

    //Filter String is passed to the endpoint
    @Test
    public void fetchContacts_filterStringPassedToEndPoint() {
        //Arrange
        ArgumentCaptor<String> acString = ArgumentCaptor.forClass(String.class);
        //Act
        SUT.fetchContactsAndNotify(FILTER_STRING);
        //Assert
        verify(mGetContactsHttpEndpoint).getContacts(acString.capture(), any(GetContactsHttpEndpoint.Callback.class));
        assertThat(acString.getValue(),is(FILTER_STRING));
    }

    //Success all observers notified with correct data
    @Test
    public void fetchContact_success_observersNotifiedwithCorrectData() {
        //Arrange
        //Act
        SUT.registerListeners(mListenerMock1);
        SUT.registerListeners(mListenerMock2);
        SUT.fetchContactsAndNotify(FILTER_STRING);
        //Assert
        verify(mListenerMock1).onContactsFetched(mAcListContactItem.capture());
        verify(mListenerMock2).onContactsFetched(mAcListContactItem.capture());
        List<List<Contact>> captures = mAcListContactItem.getAllValues();
        List<Contact> capture1 = captures.get(0);
        List<Contact> capture2 = captures.get(1);
        assertThat(capture1,is(getContactItems()));
        assertThat(capture2,is(getContactItems()));
    }

    //Success - unsubscibed observers not notified
    @Test
    public void fetchContact_success_unsubscribedObserversNotNotified() {
        //Arrange
        //Act
        SUT.registerListeners(mListenerMock1);
        SUT.registerListeners(mListenerMock2);
        SUT.unregisterListener(mListenerMock2);
        SUT.fetchContactsAndNotify(FILTER_STRING);
        //Assert
        verify(mListenerMock1).onContactsFetched(any(List.class));
        verifyNoMoreInteractions(mListenerMock2);
    }

    //General error - observers notified of failure
    @Test
    public void fetchContact_generalError_observersNotifiedOfFailure() {
        //Arrange
        generalError();
        //Act
        SUT.registerListeners(mListenerMock1);
        SUT.registerListeners(mListenerMock2);
        SUT.fetchContactsAndNotify(FILTER_STRING);
        //Assert
        verify(mListenerMock1).onContactsFetchedFailed();
        verify(mListenerMock2).onContactsFetchedFailed();
    }

    //network error - observers notified of failure
    @Test
    public void fetchContact_networkError_observersNotifiedOfFailure() {
        //Arrange
        networkError();
        //Act
        SUT.registerListeners(mListenerMock1);
        SUT.registerListeners(mListenerMock2);
        SUT.fetchContactsAndNotify(FILTER_STRING);
        //Assert
        verify(mListenerMock1).onContactsFetchedFailed();
        verify(mListenerMock2).onContactsFetchedFailed();
    }

    //Region Methods
    private void success() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                GetContactsHttpEndpoint.Callback callback = (GetContactsHttpEndpoint.Callback)args[1];
                callback.onGetContactsSucceeded(getContactSchemes());
                return null;
            }
        }).when(mGetContactsHttpEndpoint).getContacts(anyString(),any(GetContactsHttpEndpoint.Callback.class));
    }

    private void networkError() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                GetContactsHttpEndpoint.Callback callback = (GetContactsHttpEndpoint.Callback)args[1];
                callback.onGetContactsFailed(GetContactsHttpEndpoint.FailReason.NETWORK_ERROR);
                return null;
            }
        }).when(mGetContactsHttpEndpoint).getContacts(anyString(),any(GetContactsHttpEndpoint.Callback.class));
    }

    private void generalError() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                GetContactsHttpEndpoint.Callback callback = (GetContactsHttpEndpoint.Callback)args[1];
                callback.onGetContactsFailed(GetContactsHttpEndpoint.FailReason.GENERAL_ERROR);
                return null;
            }
        }).when(mGetContactsHttpEndpoint).getContacts(anyString(),any(GetContactsHttpEndpoint.Callback.class));
    }

    private List<ContactSchema> getContactSchemes() {
        List<ContactSchema> contactSchemas = new ArrayList<>();
        contactSchemas.add(new ContactSchema(ID, FULLNAME, PHONE_NUMBER, IMAGE_URL,AGE));
        return contactSchemas;
    }
    private List<Contact> getContactItems() {
        List<Contact> contactItems = new ArrayList<>();
        contactItems.add(new Contact(ID, FULLNAME, IMAGE_URL));
        return contactItems;
    }

    //End Region Methods

    //Region Classes
    //End Region classes
}