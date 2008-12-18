package com.marvinmessaging;

class Contact extends Object {
    public String first_name;
    public String last_name;
    public int mobile_num; //TODO: add support for multiple numbers
    public String public_key;
    public long convo_timeout; //in milliseconds
    public int msgs_per_convo;
}
