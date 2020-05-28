package com.techyourchance.testdrivendevelopment.exercise8;

import com.techyourchance.testdrivendevelopment.exercise8.contacts.Contact;
import com.techyourchance.testdrivendevelopment.exercise8.networking.ContactSchema;
import com.techyourchance.testdrivendevelopment.exercise8.networking.GetContactsHttpEndpoint;

import java.util.ArrayList;
import java.util.List;

public class FetchContactsUseCase {

    public interface Listener {
        Void onContactsFetched(List<Contact> capture);
        void onContactsFetchedFailed();
    }

    private final GetContactsHttpEndpoint mGetContactsHttpEndpoint;
    private final List<Listener> mListeners = new ArrayList<>();

    public FetchContactsUseCase(GetContactsHttpEndpoint getContactsHttpEndpoint) {
        mGetContactsHttpEndpoint = getContactsHttpEndpoint;
    }

    public void fetchContactsAndNotify(String string) {
        mGetContactsHttpEndpoint.getContacts(string, new GetContactsHttpEndpoint.Callback() {
            @Override
            public void onGetContactsSucceeded(List<ContactSchema> contactSchemas) {
                for(Listener listener:mListeners){
                    listener.onContactsFetched(contactItemFromSchemas(contactSchemas));
                }
            }

            @Override
            public void onGetContactsFailed(GetContactsHttpEndpoint.FailReason failReason) {
                switch (failReason) {
                    case GENERAL_ERROR:
                    case NETWORK_ERROR:
                        for (Listener listener : mListeners) {
                            listener.onContactsFetchedFailed();
                        }
                        break;
                }
            }
        });
    }

    private List<Contact> contactItemFromSchemas(List<ContactSchema> contactSchemas) {
        List<Contact> contactItems = new ArrayList<>();
        for(ContactSchema schema :contactSchemas){
            contactItems.add(new Contact(
                    schema.getId(),
                    schema.getFullName(),
                    schema.getImageUrl()));
        }
        return  contactItems;
    }

    public void registerListeners(Listener listener) {
        mListeners.add(listener);
    }

    public void unregisterListener(Listener listener) {
        mListeners.remove(listener);
    }
}
