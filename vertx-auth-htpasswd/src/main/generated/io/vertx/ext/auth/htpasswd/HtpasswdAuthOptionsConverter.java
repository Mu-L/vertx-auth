package io.vertx.ext.auth.htpasswd;

import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

/**
 * Converter and mapper for {@link io.vertx.ext.auth.htpasswd.HtpasswdAuthOptions}.
 * NOTE: This class has been automatically generated from the {@link io.vertx.ext.auth.htpasswd.HtpasswdAuthOptions} original class using Vert.x codegen.
 */
public class HtpasswdAuthOptionsConverter {

   static void fromJson(Iterable<java.util.Map.Entry<String, Object>> json, HtpasswdAuthOptions obj) {
    for (java.util.Map.Entry<String, Object> member : json) {
      switch (member.getKey()) {
        case "plainTextEnabled":
          if (member.getValue() instanceof Boolean) {
            obj.setPlainTextEnabled((Boolean)member.getValue());
          }
          break;
        case "htpasswdFile":
          if (member.getValue() instanceof String) {
            obj.setHtpasswdFile((String)member.getValue());
          }
          break;
      }
    }
  }

   static void toJson(HtpasswdAuthOptions obj, JsonObject json) {
    toJson(obj, json.getMap());
  }

   static void toJson(HtpasswdAuthOptions obj, java.util.Map<String, Object> json) {
    json.put("plainTextEnabled", obj.isPlainTextEnabled());
    if (obj.getHtpasswdFile() != null) {
      json.put("htpasswdFile", obj.getHtpasswdFile());
    }
  }
}
