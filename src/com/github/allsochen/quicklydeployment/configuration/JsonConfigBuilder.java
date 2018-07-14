package com.github.allsochen.quicklydeployment.configuration;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonConfigBuilder {

    private Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create();

    private static JsonConfigBuilder instance = new JsonConfigBuilder();

    private JsonConfigBuilder() {
    }

    public static JsonConfigBuilder getInstance() {
        return instance;
    }

    public String create() {
        JsonConfig jsonConfig = new JsonConfig();
        jsonConfig.setRootPath("");
        return gson.toJson(jsonConfig);
    }

    public JsonConfig deserialize(String json) {
        return gson.fromJson(json, JsonConfig.class);
    }
}
