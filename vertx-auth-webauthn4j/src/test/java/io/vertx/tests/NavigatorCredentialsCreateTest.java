package io.vertx.tests;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.webauthn4j.Authenticator;
import io.vertx.ext.auth.webauthn4j.RelyingParty;
import io.vertx.ext.auth.webauthn4j.WebAuthn4J;
import io.vertx.ext.auth.webauthn4j.WebAuthn4JCredentials;
import io.vertx.ext.auth.webauthn4j.WebAuthn4JOptions;
import io.vertx.ext.auth.webauthn4j.*;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertNotNull;

@RunWith(VertxUnitRunner.class)
public class NavigatorCredentialsCreateTest {

  private final DummyStore database = new DummyStore();

  @Rule
  public final RunTestOnContext rule = new RunTestOnContext();

  @Before
  public void resetDatabase() {
    database.clear();
  }

  @Test
  public void testRequestRegister(TestContext should) {
    final Async test = should.async();

    WebAuthn4J webAuthN = WebAuthn4J.create(
        rule.vertx(),
        new WebAuthn4JOptions().setRelyingParty(new RelyingParty().setName("ACME Corporation"))
          .setAttestation(Attestation.of("direct")))
        .credentialStorage(database);

    // Dummy user
    JsonObject user = new JsonObject()
      // id is expected to be a base64url string
      .put("id", "000000000000000000000000")
      .put("name", "john.doe@email.com")
      .put("displayName", "John Doe")
      .put("icon", "https://pics.example.com/00/p/aBjjjpqPb.png");

    webAuthN
      .createCredentialsOptions(user)
      .onFailure(should::fail)
      .onSuccess(challengeResponse -> {
        assertNotNull(challengeResponse);
        // important fields to be present
        assertNotNull(challengeResponse.getString("challenge"));
        assertNotNull(challengeResponse.getJsonObject("rp"));
        assertNotNull(challengeResponse.getJsonObject("user"));
        assertNotNull(challengeResponse.getJsonArray("pubKeyCredParams"));
        // ensure that challenge and user.id are base64url encoded
        assertNotNull(challengeResponse.getBinary("challenge"));
        assertNotNull(challengeResponse.getJsonObject("user").getBinary("id"));
        test.complete();
      });
  }

  @Test
  public void testRegister(TestContext should) {
    final Async test = should.async();

    WebAuthn4J webAuthN = WebAuthn4J.create(
        rule.vertx(),
        new WebAuthn4JOptions().setRelyingParty(new RelyingParty().setName("ACME Corporation")))
        .credentialStorage(database);

    // dummy request
    JsonObject request = new JsonObject()
      .put("id", "Q-MHP0Xq20CKM5LW3qBt9gu5vdOYLNZc3jCcgyyLncRav5Ivd7T1dav3eWrI7CT8HmzU_yAYJrmja4in8OFL3A")
      .put("rawId", "Q-MHP0Xq20CKM5LW3qBt9gu5vdOYLNZc3jCcgyyLncRav5Ivd7T1dav3eWrI7CT8HmzU_yAYJrmja4in8OFL3A")
      .put("type", "public-key")
      .put("response", new JsonObject()
        .put("attestationObject", "o2NmbXRkbm9uZWdhdHRTdG10oGhhdXRoRGF0YVjEfxV8VVBPmz66RLzscHpg5yjRhO28Y_fPwYO5AVwzBEJBAAAAAwAAAAAAAAAAAAAAAAAAAAAAQEPjBz9F6ttAijOS1t6gbfYLub3TmCzWXN4wnIMsi53EWr-SL3e09XWr93lqyOwk_B5s1P8gGCa5o2uIp_DhS9ylAQIDJiABIVggN_D3u-03a0GzONOHfaML881QZtOCc5oTNRB2wlyqUEUiWCD3878XoO_bIJf0mEPDILODFhVmkc4QeR6hOIDvwvXzYQ")
        .put("clientDataJSON", "eyJ0eXBlIjoid2ViYXV0aG4uY3JlYXRlIiwiY2hhbGxlbmdlIjoiQkg3RUtJRFhVNkN0Xzk2eFR6RzBsNjJxTWhXX0VmX0s0TVFkRExvVk5jMVVYTVFZNHFOOWFnNXlETm1MSTd2RlJzbGtRYmJqMEpaV0p4R1ZmTXVnWGciLCJvcmlnaW4iOiJodHRwczovLzE5Mi4xNjguMTc4LjIwNi54aXAuaW86ODQ0MyIsImNyb3NzT3JpZ2luIjpmYWxzZX0"));

    webAuthN
      .authenticate(
        new WebAuthn4JCredentials()
          .setUsername("paulo")
          .setOrigin("https://192.168.178.206.xip.io:8443")
          .setDomain("192.168.178.206.xip.io")
          .setChallenge("BH7EKIDXU6Ct_96xTzG0l62qMhW_Ef_K4MQdDLoVNc1UXMQY4qN9ag5yDNmLI7vFRslkQbbj0JZWJxGVfMugXg")
          .setWebauthn(request))
      .onFailure(should::fail)
      .onSuccess(response -> {
        assertNotNull(response);
        test.complete();
      });
  }


  @Test
  public void testRegisterExistingUser(TestContext should) {
    final Async test = should.async();

    WebAuthn4J webAuthN = WebAuthn4J.create(
        rule.vertx(),
        new WebAuthn4JOptions().setRelyingParty(new RelyingParty().setName("ACME Corporation")))
        .credentialStorage(database);

    database.add(
        new Authenticator()
          .setUsername("paulo")
          .setCredID("O3ZJlAdXvra6PwvL4I9AP99dS1_v3DDRuB_SwTAHFbUfMtvWTOFycCeb6CkXZXiPWi9Nr0ptUnlnHP3U40ptEA")
          .setPublicKey("pQECAyYgASFYIBl0C67nFN_OwbODu_iE0hI5nM0ppUkqjhU9NhQvBaiLIlggffUTx8E6OM85huU3DcadeuaBBh8kGI8vdm3zesf3YRc")
          .setCounter(2)
      );

    // dummy request
    JsonObject request = new JsonObject()
      .put("id", "Q-MHP0Xq20CKM5LW3qBt9gu5vdOYLNZc3jCcgyyLncRav5Ivd7T1dav3eWrI7CT8HmzU_yAYJrmja4in8OFL3A")
      .put("rawId", "Q-MHP0Xq20CKM5LW3qBt9gu5vdOYLNZc3jCcgyyLncRav5Ivd7T1dav3eWrI7CT8HmzU_yAYJrmja4in8OFL3A")
      .put("type", "public-key")
      .put("response", new JsonObject()
        .put("attestationObject", "o2NmbXRkbm9uZWdhdHRTdG10oGhhdXRoRGF0YVjEfxV8VVBPmz66RLzscHpg5yjRhO28Y_fPwYO5AVwzBEJBAAAAAwAAAAAAAAAAAAAAAAAAAAAAQEPjBz9F6ttAijOS1t6gbfYLub3TmCzWXN4wnIMsi53EWr-SL3e09XWr93lqyOwk_B5s1P8gGCa5o2uIp_DhS9ylAQIDJiABIVggN_D3u-03a0GzONOHfaML881QZtOCc5oTNRB2wlyqUEUiWCD3878XoO_bIJf0mEPDILODFhVmkc4QeR6hOIDvwvXzYQ")
        .put("clientDataJSON", "eyJ0eXBlIjoid2ViYXV0aG4uY3JlYXRlIiwiY2hhbGxlbmdlIjoiQkg3RUtJRFhVNkN0Xzk2eFR6RzBsNjJxTWhXX0VmX0s0TVFkRExvVk5jMVVYTVFZNHFOOWFnNXlETm1MSTd2RlJzbGtRYmJqMEpaV0p4R1ZmTXVnWGciLCJvcmlnaW4iOiJodHRwczovLzE5Mi4xNjguMTc4LjIwNi54aXAuaW86ODQ0MyIsImNyb3NzT3JpZ2luIjpmYWxzZX0"));

    webAuthN
      .authenticate(
        new WebAuthn4JCredentials()
          .setUsername("paulo")
          .setOrigin("https://192.168.178.206.xip.io:8443")
          .setDomain("192.168.178.206.xip.io")
          .setChallenge("BH7EKIDXU6Ct_96xTzG0l62qMhW_Ef_K4MQdDLoVNc1UXMQY4qN9ag5yDNmLI7vFRslkQbbj0JZWJxGVfMugXg")
          .setWebauthn(request))
      .onFailure(x -> test.complete())
      .onSuccess(response -> {
        should.fail("We got logged in while another user already existed with the same user name");
      });
  }
}
