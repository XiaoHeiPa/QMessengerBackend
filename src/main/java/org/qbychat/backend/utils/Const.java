package org.qbychat.backend.utils;

public interface Const {
    // Messenger
    String JWT_BLACKLIST = "qbychat:jwt:bl:";
    String ACCOUNT_VERIFY = "qbychat:account:verify:";
    String INVITATION = "qbychat:account:invitation:";
    String CACHED_MESSAGE = "qbychat:cache:";
    String FCM_TOKEN = "qbychat:fcm:token:";

    // Device Finder
    String DEVICE_LOCATION = "find-device:location:";
}
