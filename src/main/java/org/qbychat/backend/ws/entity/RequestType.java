package org.qbychat.backend.ws.entity;

public interface RequestType {
    // Messenger
    String SEND_MESSAGE = "send-message";
    String ADD_FRIEND = "add-friend";
    String ACCEPT_FRIEND_REQUEST = "accept-friend-request";
    String FETCH_LATEST_MESSAGES = "fetch-latest-messages";
}
