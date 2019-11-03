package sa.aqwas.wainnakolsample.utils;

public class Constants {
    public static final String PACKAGE_NAME = "sa.aqwas.wainnakolsample";


    //url constants
    private static final String DOMAIN_URl = "https://apps.b-social.com";

    public static final String GET_RESTAURANT_URL = DOMAIN_URl + "/mobile/v1/%s/get_restaurant";

    public static final String PARAMETER_STATUS = "check";
    public static final String PARAMETER_CODE = "code";
    public static final String PARAMETER_MESSAGE = "msg";
    public static final String PARAMETER_DATA = "data";


    //user item parameters
    public static final String PARAMETER_USER_ID = "id";
    public static final String PARAMETER_USER_NAME = "name";


    public static final int MAP_DEFAULT_ZOOM_LEVEL = 15;
    public static final int MAP_LOCATION_ZOOM_LEVEL = 12;


    public static final String DB_NAME = "main-db";

    //notification channel constant, only for api26+
    public static final String CHANNEL_ID = "144";
    public static final String CHANNEL_ID_MUTE = "144_mute";
}
