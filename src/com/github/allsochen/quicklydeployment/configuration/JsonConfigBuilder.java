package com.github.allsochen.quicklydeployment.configuration;

import com.github.allsochen.quicklydeployment.OsInfo;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Map;

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
        Map<String, String> env = System.getenv();
        String username = env.getOrDefault("USERNAME", env.getOrDefault("USER", "yourname"));
        if (OsInfo.isWindows()) {
            jsonConfig.setRootPath("Z:/allsochen/projects/MTT");
        } else {
            jsonConfig.setRootPath("/Volumes/dev/" + username + "/projects/MTT");
        }
        return gson.toJson(jsonConfig);
    }

    public JsonConfig deserialize(String json) {
        return gson.fromJson(json, JsonConfig.class);
    }
}
