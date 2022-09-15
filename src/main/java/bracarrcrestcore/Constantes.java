package bracarrcrestcore;

import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class Constantes {

    static String APP_BASE_URL = "https://barrigarest.wcaquino.me";
    static Integer APP_PORT = 443;
    static String APP_BASE_PATH = "";

    static ContentType APP_CONTENT_TYPE = ContentType.JSON;
    
    static Long MAX_TIMEOUT = 6000L;
}
