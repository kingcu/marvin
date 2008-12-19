package com.marvinmessaging;

class Contact extends Object {
    public static final String KEY_ID = "_id";
    public static final String KEY_FIRST_NAME = "first_name";
    public static final String KEY_LAST_NAME = "last_name";
    public static final String KEY_MOB_NUM = "mobile_num";
    public static final String KEY_PUB_KEY = "pub_key";
    public static final String KEY_CONVO_TIMEOUT = "convo_timeout";
    public static final String KEY_MSGS_PER_CONVO = "msgs_per_convo";

    public String first_name;
    public String last_name;
    public int mobile_num; //TODO: add support for multiple numbers
    public String pub_key;
    public long convo_timeout; //in milliseconds
    public int msgs_per_convo;
}
