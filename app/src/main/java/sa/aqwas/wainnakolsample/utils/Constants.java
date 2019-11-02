package com.vhorus.saloni.barberapp.utils;

public class Constants {
    public static final String PACKAGE_NAME = "com.vhorus.saloni.barbersapp";


    //url constants
    private static final String DOMAIN_URl = "https://apps.b-social.com";

    public static final String LOGIN_URL = DOMAIN_URl + "/mobile/v1/%s/login";
    public static final String REGISTER_URL = DOMAIN_URl + "/mobile/v1/%s/register";
    public static final String VERIFY_URL = DOMAIN_URl + "/mobile/v1/%s/activate_user";
    public static final String VERIFY_RESEND_CODE_URL = DOMAIN_URl + "/mobile/v1/%s/activate_user_resend_code";
    public static final String GUEST_LOGIN_URL = DOMAIN_URl + "/mobile/v1/%s/guest_login";

    public static final String PARAMETER_NEXT_PAGE_URL = "next_page_url";

    public static final String PARAMETER_STATUS = "check";
    public static final String PARAMETER_CODE = "code";
    public static final String PARAMETER_MESSAGE = "msg";
    public static final String PARAMETER_DATA = "data";


    //user item parameters
    public static final String PARAMETER_USER_ID = "id";
    public static final String PARAMETER_USER_NAME = "name";




    //authentication activity destination constants
    public static final String AUTHENTICATION_ACTIVITY_DESTINATION_KEY = "destination";
    public static final String AUTHENTICATION_ACTIVITY_DESTINATION_LOGIN = "login";
    public static final String AUTHENTICATION_ACTIVITY_DESTINATION_REGISTER = "register";



    //registeration length constants
    public static final int REGISTERATION_NAME_LENGTH = 2;
    public static final int REGISTERATION_PHONE_COUNTRY_CODE_LENGTH = 1;
    public static final int REGISTERATION_PHONE_NUMBER_LENGTH = 7;
    public static final int REGISTERATION_PASSWORD_LENGTH = 4;


    public static final String DB_NAME = "main-db";

    //notification channel constant, only for api26+
    public static final String CHANNEL_ID = "144";
    public static final String CHANNEL_ID_MUTE = "144_mute";
}
