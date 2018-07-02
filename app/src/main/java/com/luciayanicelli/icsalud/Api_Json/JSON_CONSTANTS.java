package com.luciayanicelli.icsalud.Api_Json;

public class JSON_CONSTANTS {

    /**
     * Constantes para {@link Patients}
     */

    public static final String BASE_URL = "http://api.icsalud.com.ar/";

    public static final String OAUTH = "oauth/";

    public static final String TOKEN = "token";

    public static final String CLIENT_ID_VALUE = "1";

    public static final String CLIENT__SECRET_VALUE = "ElNJzvT1dQSz9YE0PUB0u11EgeIjBbj1x7c83xYn";

    public static final String GRANT_TYPE = "grant_type";

    public static final String CLIENT_CREDENTIALS = "client_credentials";

    public static final String CLIENT_ID = "client_id";

    public static final String CLIENT__SECRET = "client_secret";

    public static final String SCOPE = "scope";

    public static final String SCOPE_VALUE = "*";

    public static final String USERNAME = "username";

    public static final String JSON_STRING = "json_string";

    //REQUEST
    public static final String REQUEST_GET = "GET";
    public static final String REQUEST_POST = "POST";
    public static final String REQUEST_PUT = "PUT";
    public static final String REQUEST_DELETE = "DELETE";


    //HEADERS
    public static final String HEADER_ACCEPT = "Accept";
    public static final String HEADER_ACCEPT_VALUE_1 ="application/json, text/plain, */*";
    public static final String HEADER_ACCEPT_VALUE = "application/vnd.hf.v1+json";
    public static final String HEADER_CONTENT_TYPE ="Content-type";
    public static final String HEADER_CONTENT_TYPE_VALUE = "application/json";
    public static final String HEADER_ORIGIN = "Origin";
    public static final String HEADER_ORIGIN_VALUE = "http://ic.com.ar";
    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String HEADER_UNAUTHORIZED = "Unauthorized";
    public static final String HEADER_AUTHORIZATION_VALUE = "Bearer "; // + {{access_token}}

  //  public static final String POSTMAN_TOKEN_GET_PATIENTS = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImp0aSI6IjdmNzRkNWI3ZGM0OGMwMWFhOTNhYTIyYjA2Nzc1ZGE1NDE4MTkwNmQyMTFlNjhjNmI1NzY1MTliNzE5NTUwNWM1MDAxZTNlY2I1OTJhNWFkIn0.eyJhdWQiOiIxIiwianRpIjoiN2Y3NGQ1YjdkYzQ4YzAxYWE5M2FhMjJiMDY3NzVkYTU0MTgxOTA2ZDIxMWU2OGM2YjU3NjUxOWI3MTk1NTA1YzUwMDFlM2VjYjU5MmE1YWQiLCJpYXQiOjE1MTI2NTM0MzAsIm5iZiI6MTUxMjY1MzQzMCwiZXhwIjoxNTE1MjQ1NDMwLCJzdWIiOiIiLCJzY29wZXMiOltdfQ.tnoaAKWw0WoGBRUw65shS7jHVLLYuNT25wHadV-o_hJ-AkEYYc98--5FetP6E9u0F3Z5P51dqBlN1K4Qs1J75SHi_tM4BvDs8R_NoFQWg6uI2n4fMIYY1uXfysOxJgtphnPawJBJC_lC3IfrbzBP8vkYEe6yZ20PAbAuoRQkAUwwNT2NfmdEq-uyEC54J1y0gDcgvbxQ8xkNsg2NBKbeK-s4N2DNovo743yl5yfUSriMJ-841HHvFldlElLAQgUm77X1F_MOXHyf5UD25WqebzKnJkJlGpXHSquAPZw41W9uG6ThZpZqLth84sb3-poe3L4pQpIY5aWu3GSZNDvBi9PewpEanr4Lo6CEn6FHIGKfYGjyF61ZLk9NprLJNp3BMm8g_rqqmeuPIOPmG_37P1xFKwvPsujN_J90oWdA1_vFKVh70951ix5N51On0w-Qgx4wB09mxEIsluqHPNKXRw8OxsdxzsKQZHGl2fP7Edm81c_1Sph3a-UdQPDK1wuHcTZmYaQ89T5QKTly1Iciem-NHVeRX6KW3f6R-uMQTtQfUgBqyha6yB8ehNRveaovrKCAKOXap0EePmgQDP6O9JHi0LsArzESi5VnIuz__v0J3FlKAIdkis7ipV9vZCmE3E-5a4eMJKZWilbKigNUDBXW-836PnwrskUUaXb3-hU";


    //RESPONSE TOKEN
    public static final String RESPONSE_TOKEN_TYPE ="token_type";
    public static final String RESPONSE_EXPIRES_IN ="expires_in";
    public static final String RESPONSE_ACCESS_TOKEN ="access_token";


    //STATUS CODES
    public static final int STATUS_CODE_OK = 200;
    public static final int STATUS_CODE_CREATED = 201;
    public static final int STATUS_CODE_NO_CONTENT = 204;
    public static final int STATUS_CODE_UNAUTHORIZED = 401;
    public static final int STATUS_CODE_INTERNAL_SERVER_ERROR = 500;

    //RESPONSE
    public static final String RESPONSE_DATA = "data";
    public static final String RESPONSE_META = "meta";
    public static final String RESPONSE_PAGINATION = "pagination";
    public static final String RESPONSE_TOTAL = "total";
    public static final String RESPONSE_COUNT = "count";
    public static final String RESPONSE_TOTAL_PAGES = "total_pages";
    public static final String RESPONSE_PER_PAGE = "per_page";
    public static final String RESPONSE_CURRENT_PAGE = "current_page";



    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static final String PATIENTS = "patients";

    public static final String ID = "id";
    public static final String FIRST_NAME = "firstName";
    public static final String LAST_NAME = "lastName";
    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";
    public static final String BIRTHDAY = "birthday";


    public static final String FILTER = "?filter=";
    public static final String IGUAL = "%20eq%20"; //espacio en blanco en html %20


    /**
     * Constantes para Professionals
     */

    public static final String PROFESSIONALS = "professionals";

    public static final String PROFESSIONALS_LICENSE_NUMBER = "licenseNumber";


    public static final String CONTACTS = "contacts";

    public static final String CONTACTS_LAND_LINE = "landLine";
    public static final String CONTACTS_MOBILE_NUMBER = "mobileNumber";
    public static final String CONTACTS_PRIORITY = "priority";


    public static final String PRACTITIONER = "practitioners";

    public static final String PRACTITIONER_STATUS = "status";
    public static final int PRACTITIONER_STATUS_FRIEND       = 0;
    public static final int PRACTITIONER_STATUS_PENDING_PROFESSIONAL_APPROVAL = -1;
    public static final int PRACTITIONER_STATUS_PENDING_PATIENT_APPROVAL      = 1;



    public static final String WEIGHTS = "weights";

    public static final String WEIGHTS_KG = "kg";
    public static final String DATE_TIME = "dateTime";


    public static final String HEART_RATES = "heart-rates";

    public static final String HEART_RATES_PPM = "ppm";


    public static final String BLOOD_PRESSURES = "blood-pressures";

    public static final String BLOOD_PRESSURES_MMHG = "mmHg";
    public static final String BLOOD_PRESSURES_TYPE = "type";
    public static final String BLOOD_PRESSURES_SHIFT = "shift";

    public static final int TYPE_SYSTOLIC = 0;
    public static final int TYPE_DIASTOLIC = 1;
    public static final int SHIFT_MORNING  = 0;
    public static final int SHIFT_EVENING  = 1;


    public static final String ANSWERS = "answers";

    public static final String QUESTION_ID = "questionId";
    public static final String ANSWERS_RATE = "rate";


    public static final String QUESTIONS = "questions";

    public static final String QUESTIONS_TEXT = "text";


    //PATIENTS
    public static final String GENDER                                  = "gender";

    public static final String GENDER_MALE                                  = "m";
    public static final String GENDER_FEMALE                                = "f";

    public static final String OCCUPATION = "occupation";

    public static final String OCCUPATION_ACTIVE                            = "active";
    public static final String OCCUPATION_UNEMPLOYED                        = "unemployed";
    public static final String OCCUPATION_RETIRED                           = "retired";

    public static final String[] ARRAY_OCCUPATION_ENGLISH = new String[]{
            "Seleccione su Ocupación",
            JSON_CONSTANTS.OCCUPATION_ACTIVE,
            JSON_CONSTANTS.OCCUPATION_UNEMPLOYED,
            JSON_CONSTANTS.OCCUPATION_RETIRED };

    public static final String[] ARRAY_OCCUPATION_SPANISH = new String[]{
            "Seleccione su Ocupación",
            "En actividad",
            "Desempleado",
            "Jubilado" };

    public static final String EDUCATION_LEVEL                              = "educationLevel";

    public static final String EDUCATION_LEVEL_NONE                         = "none";
    public static final String EDUCATION_LEVEL_INCOMPLETE_ELEMENTARY_SCHOOL = "incomplete_elementary_school";
    public static final String EDUCATION_LEVEL_COMPLETE_ELEMENTARY_SCHOOL   = "complete_elementary_school";
    public static final String EDUCATION_LEVEL_INCOMPLETE_HIGH_SCHOOL       = "incomplete_high_school";
    public static final String EDUCATION_LEVEL_COMPLETE_HIGH_SCHOOL         = "complete_high_school";
    public static final String EDUCATION_LEVEL_INCOMPLETE_COLLEGE           = "incomplete_college";
    public static final String EDUCATION_LEVEL_COMPLETE_COLLEGE             = "complete_college";
    public static final String EDUCATION_LEVEL_INCOMPLETE_POSTGRADUATE      = "incomplete_postgraduate";
    public static final String EDUCATION_LEVEL_COMPLETE_POSTGRADUATE        = "complete_postgraduate";

    public static final String[] ARRAY_EDUCATION_LEVELS_ENGLISH = new String[]{
            "Seleccione su Nivel Educativo",
         //   JSON_CONSTANTS.EDUCATION_LEVEL_NONE,
            JSON_CONSTANTS.EDUCATION_LEVEL_INCOMPLETE_ELEMENTARY_SCHOOL,
            JSON_CONSTANTS.EDUCATION_LEVEL_COMPLETE_ELEMENTARY_SCHOOL,
            JSON_CONSTANTS.EDUCATION_LEVEL_INCOMPLETE_HIGH_SCHOOL,
            JSON_CONSTANTS.EDUCATION_LEVEL_COMPLETE_HIGH_SCHOOL,
            JSON_CONSTANTS.EDUCATION_LEVEL_INCOMPLETE_COLLEGE,
            JSON_CONSTANTS.EDUCATION_LEVEL_COMPLETE_COLLEGE,
            JSON_CONSTANTS.EDUCATION_LEVEL_INCOMPLETE_POSTGRADUATE,
            JSON_CONSTANTS.EDUCATION_LEVEL_COMPLETE_POSTGRADUATE };

    public static final String[] ARRAY_EDUCATION_LEVELS_SPANISH = new String[]{
            "Seleccione su Nivel Educativo",
          //  "Ninguno",
            "Primaria Incompleta",
            "Primaria Completa",
            "Secundaria Incompleta",
            "Secundaria Completa",
            "Universitario Incompleto",
            "Universitario Completo",
            "Posgrado Incompleto",
            "Posgrado Completo"};



    public static final String HEALTH_INSURANCE     = "healthInsurance";
    public static final String COEXISTENCE          =  "coexistence"; //int

    public static final String LAST_HOSPITALIZED_AT     = "lastHospitalizedAt"; //date
    public static final String DIAGNOSED_AT     = "diagnosedAt"; //date


    public static final String ERROR_MESSAGE     = "errorMessage"; //mensaje de error


    //ALERTAS
    public static final String ALERTS     = "alerts";

    public static final String ALERTS_LEVEL     = "level";
    public static final int ALERTA_LEVEL_RED     = 10;
    public static final int ALERTA_LEVEL_YELLOW     = 20;
    public static final int ALERTA_LEVEL_GREEN     = 30;

    public static final String ALERTS_TYPE     = "type";
    public static final int ALERTA_TYPE_WEIGHT     = 10;
    public static final int ALERTA_TYPE_BLOOD_PRESSURE     = 20;
    public static final int ALERTA_TYPE_HEART_RATE     = 30;
    public static final int ALERTA_TYPE_SYMPTOMS     = 40;
    public static final int ALERTA_TYPE_SOS     = 90;

    public static final String ALERTS_DESCRIPTION     = "description";



}
