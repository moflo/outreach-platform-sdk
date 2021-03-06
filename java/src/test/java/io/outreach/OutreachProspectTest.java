package io.outreach;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.BeforeClass;
import org.junit.Test;

import io.outreach.security.TrustStore;

public class OutreachProspectTest {
    private static Outreach outreach;

    @BeforeClass
    public static void init() {
        try (FileInputStream propertiesFile = new FileInputStream("src/test/resources/application.properties")) {
            Properties appProperties = new Properties();
            appProperties.load(propertiesFile);

            Outreach.ApplicationCredentials app_creds = new Outreach.ApplicationCredentials(
                  appProperties.getProperty("app_identifier"),
                  appProperties.getProperty("app_secret_key"),
                  appProperties.getProperty("app_return_url"));
            outreach = new Outreach(app_creds, appProperties.getProperty("authorize_code"), TrustStore.get());
        } catch (IOException e) {
            assertTrue(false);
            return;
        }
    }

    @Test
    public void createProspectAllFields() {
        JSONParser parser = new JSONParser();

        try {
            String prospect = parser.parse(
              "{\"data\": {"
            + "  \"attributes\": {"
            + "    \"address\": {"
            + "      \"city\": \"test-address-city\","
            + "      \"state\": \"test-address-state\","
            + "      \"country\": \"test-address-country\","
            + "      \"street\": [\"test-address-street-0\",\"test-address-street-1\"],"
            + "      \"zip\": 12345,"
            + "    },"
            + "    \"company\": {"
            + "      \"name\": \"test-company-name\","
            + "      \"type\": \"test-company-type\","
            + "      \"industry\": \"test-company-industry\","
            + "      \"size\": \"test-company-size\","
            + "      \"locality\" :\"test-company-locality\""
            + "    },"
            + "    \"contact\": {"
            + "      \"timezone\": \"Pacific/Honolulu\","
            + "      \"email\" : \"test-contact-email-unique-" + System.currentTimeMillis() + "@test.com\","
            + "      \"phone\": {"
            + "        \"personal\": \"206-123-4567\","
            + "        \"work\": \"206-987-6543\""
            + "      }"
            + "    },"
            + "    \"personal\": {"
            + "      \"name\": {"
            + "        \"first\": \"Tester\","
            + "        \"last\": \"McTest\""
            + "      },"
            + "      \"gender\": \"male\","
            + "      \"occupation\": \"test-personal-occupation\","
            + "      \"title\": \"test-personal-title\""
            + "    },"
            + "    \"social\": {"
            + "      \"facebook\": \"http://www.facebook.com/test\","
            + "      \"linkedin\": \"http://www.linkedin.com/test\","
            + "      \"plus\": \"http://plus.google.com/test\","
            + "      \"quora\": \"http://www.quora.com/test\","
            + "      \"website\": \"http://www.companywebsite.com/\","
            + "      \"twitter\": \"http://www.twitter.com/test\""
            + "    },"
            + "    \"metadata\": {"
            + "      \"tags\": [\"Tag1\", \"Tag2\"],"
            + "      \"notes\": ["
            + "        \"Custom long form note number one.\","
            + "        \"Custom long form note number two.\""
            + "      ],"
            + "      \"source\": \"source-information\","
            + "      \"custom\": ["
            + "        \"custom-string1\","
            + "        \"custom-string2\","
            + "        \"custom-string3\","
            + "        \"custom-string4\","
            + "        \"custom-string5\","
            + "        \"custom-string6\","
            + "        \"custom-string7\""
            + "      ]"
            + "    }"
            + "  }"
            + "}}").toString();

            // Example response: {"data":{"attributes":{"created":"2015-09-18T22:28:10.959Z","updated":"2015-09-18T22:28:10.959Z"},"id":48438,"type":"prospect"}}
            JSONObject response = outreach.addProspect(prospect);
            assertNotNull(response);

            JSONObject data = (JSONObject) response.get("data");
            assertNotNull(data);
            assertNotNull(data.get("id"));
        } catch (ParseException e) {
            assertTrue(false);
        }
    }

    @Test
    public void createProspectMinimal() {
        JSONParser parser = new JSONParser();

        try {
            String prospect = parser.parse(
              "{\"data\": {"
            + "  \"attributes\": {"
            + "    \"personal\": {"
            + "      \"name\": {"
            + "        \"first\": \"Tester\","
            + "        \"last\": \"Nameonly\""
            + "      }"
            + "    }"
            + "  }"
            + "}}").toString();

            // Example response: {"data":{"attributes":{"created":"2015-09-18T22:28:10.959Z","updated":"2015-09-18T22:28:10.959Z"},"id":48438,"type":"prospect"}}
            JSONObject response = outreach.addProspect(prospect);
            assertNotNull(response);

            JSONObject data = (JSONObject) response.get("data");
            assertNotNull(data);
            assertNotNull(data.get("id"));
        } catch (ParseException e) {
            assertTrue(false);
        }
    }

    @Test
    public void createProspectNoInput() {
        JSONParser parser = new JSONParser();

        try {
            String prospect = parser.parse(
              "{\"data\": {"
            + "  \"attributes\": {"
            + "  }"
            + "}}").toString();

            // Example response: {"data":{"attributes":{"created":"2015-09-18T22:28:10.959Z","updated":"2015-09-18T22:28:10.959Z"},"id":48438,"type":"prospect"}}
            JSONObject response = outreach.addProspect(prospect);
            assertNotNull(response);
            assertNotNull(response.get("data"));
        } catch (ParseException e) {
            assertTrue(false);
        }
    }

    @Test
    public void getProspectsPaging() {
        JSONObject response = outreach.getProspects(null, null, null, null, 2);
        JSONObject meta = (JSONObject) response.get("meta");
        JSONObject page = (JSONObject) meta.get("page");
        assertEquals(page.get("current").toString(), "2");
    }

    @Test
    public void getProspectById() {
        JSONParser parser = new JSONParser();
        int createdProspectId = -1;

        try {
            String prospect = parser.parse(
              "{\"data\": {"
            + "  \"attributes\": {"
            + "    \"personal\": {"
            + "      \"name\": {"
            + "        \"first\": \"Fetch\","
            + "        \"last\": \"ById\""
            + "      }"
            + "    }"
            + "  }"
            + "}}").toString();

            // Example response: {"data":{"attributes":{"created":"2015-09-18T22:28:10.959Z","updated":"2015-09-18T22:28:10.959Z"},"id":48438,"type":"prospect"}}
            JSONObject response = outreach.addProspect(prospect);
            assertNotNull(response);

            JSONObject data = (JSONObject) response.get("data");
            assertNotNull(data);
            assertNotNull(data.get("id"));

            createdProspectId = Integer.parseInt(data.get("id").toString());
        } catch (ParseException e) {
            assertTrue(false);
        }

        JSONObject response = outreach.getProspect(createdProspectId);

        assertNotNull(response);
        JSONObject data = (JSONObject) response.get("data");
        assertNotNull(data);
        assertNotNull(data.get("attributes")); // ... validate content?
    }

    @Test
    public void getProspectsByEmail() {
        JSONParser parser = new JSONParser();
        String uniqueEmail = "test-contact-email-unique-" + System.currentTimeMillis() + "@test.com";

        try {
            String prospect = parser.parse(
              "{\"data\": {"
            + "  \"attributes\": {"
            + "    \"personal\": {"
            + "      \"name\": {"
            + "        \"first\": \"Fetch\","
            + "        \"last\": \"ById\""
            + "      }"
            + "    },"
            + "    \"contact\": {"
            + "      \"email\" : \"" + uniqueEmail + "\""
            + "    }"
            + "  }"
            + "}}").toString();

            // Example response: {"data":{"attributes":{"created":"2015-09-18T22:28:10.959Z","updated":"2015-09-18T22:28:10.959Z"},"id":48438,"type":"prospect"}}
            JSONObject response = outreach.addProspect(prospect);
            assertNotNull(response);
        } catch (ParseException e) {
            assertTrue(false);
        }

        JSONObject response = outreach.getProspects(null, null, null, uniqueEmail, 1);
        assertNotNull(response);
        JSONArray data = (JSONArray) response.get("data");
        assertFalse(data.isEmpty()); // NOTE: There's sometimes a consistency race between creation and fetch where unique email doesn't show up.
    }

    @Test
    public void modifyProspect() {
        JSONParser parser = new JSONParser();
        int createdProspectId = -1;
        String uniqueEmail = "test-contact-email-unique-" + System.currentTimeMillis() + "@test.com";

        try {
            // Create
            String prospect = parser.parse(
              "{\"data\": {"
            + "  \"attributes\": {"
            + "    \"personal\": {"
            + "      \"name\": {"
            + "        \"first\": \"First\","
            + "        \"last\": \"Last\""
            + "      }"
            + "    },"
            + "    \"contact\": {"
            + "      \"email\": \"" + uniqueEmail + "\""
            + "    },"
            + "    \"metadata\": {"
            + "      \"tags\": [\"Tag1\", \"Tag2\"],"
            + "      \"custom\": ["
            + "        \"custom-string1\","
            + "        \"custom-string2\","
            + "        \"custom-string3\","
            + "        \"custom-string4\","
            + "        \"custom-string5\","
            + "        \"custom-string6\","
            + "        \"custom-string7\""
            + "      ]"
            + "    }"
            + "  }"
            + "}}").toString();

            JSONObject response = outreach.addProspect(prospect);
            assertNotNull(response);

            JSONObject data = (JSONObject) response.get("data");
            assertNotNull(data);
            assertNotNull(data.get("id"));

            createdProspectId = Integer.parseInt(data.get("id").toString());

            // Modify
            String modifiedProspect = parser.parse(
                    "{\"data\": {"
                  + "  \"attributes\": {"
                  + "    \"personal\": {"
                  + "      \"name\": {"
                  + "        \"last\": \"ModifiedLast\""
                  + "      }"
                  + "    },"
                  + "    \"contact\": {"
                  + "      \"email\": \"" + uniqueEmail + "\""
                  + "    },"
                  + "    \"metadata\": {"
                  + "      \"custom\": ["
                  + "        \"custom-modified-string1\","
                  + "        \"custom-modified-string2\","
                  + "        \"custom-modified-string3\","
                  + "        \"custom-modified-string4\","
                  + "        null,"
                  + "        null,"
                  + "        \"custom-modified-string7\""
                  + "      ]"
                  + "    }"
                  + "  }"
                  + "}}").toString();

            response = outreach.modifyProspect(createdProspectId, modifiedProspect);
            assertNotNull(response);
            assertNotNull(response.get("data"));
            
            // Re-fetch
            response = outreach.getProspect(createdProspectId);
            assertNotNull(response);
            data = (JSONObject) response.get("data");
            assertNotNull(data);
            JSONObject attrs = (JSONObject) data.get("attributes");
            assertNotNull(attrs);
            JSONObject personal = (JSONObject) attrs.get("personal");
            assertNotNull(personal);
            JSONObject name = (JSONObject) personal.get("name");
            assertNotNull(name);
            assertEquals(name.get("first"), "First");
            assertEquals(name.get("last"), "ModifiedLast");

        } catch (ParseException e) {
            assertTrue(false);
        }
    }
    
    @Test
    public void addProspectToSequence() {
        JSONParser parser = new JSONParser();
        int createdProspectId = -1;

        try {
            // Create
            String prospect = parser.parse(
              "{\"data\": {"
            + "  \"attributes\": {"
            + "    \"personal\": {"
            + "      \"name\": {"
            + "        \"first\": \"Sequence\","
            + "        \"last\": \"Tetser\""
            + "      }"
            + "    }"
            + "  }"
            + "}}").toString();

            JSONObject response = outreach.addProspect(prospect);
            assertNotNull(response);

            JSONObject data = (JSONObject) response.get("data");
            assertNotNull(data);
            assertNotNull(data.get("id"));

            createdProspectId = Integer.parseInt(data.get("id").toString());

            // Add to sequence
            String payload = parser.parse(
                    "{\"data\": {"
                  + "  \"relationships\": {"
                  + "    \"prospects\": [{"
                  + "      \"data\": {"
                  + "        \"id\": \"" + createdProspectId + "\""
                  + "      }"
                  + "    }]"
                  + "  }"
                  + "}}").toString();

            response = outreach.addProspectsToSequence(263, payload); // NOTE: Hardcoded sequence was manually created in staging
            assertNotNull(response);
            assertNotNull(response.get("data"));
            
            JSONObject links = (JSONObject) response.get("links");
            assertNotNull(links);
            assertNotNull(links.get("self"));
        } catch (ParseException e) {
            assertTrue(false);
        }
    }

    @Test
    public void getSequences() {
        JSONObject response = outreach.getSequences(1);
        assertNotNull(response);
        JSONArray data = (JSONArray) response.get("data");
        assertNotNull(data);
        assertNotNull(data.get(0));
    }
    
    @Test
    public void getInfo() {
    	JSONObject response = outreach.getInfo();
    	assertNotNull(response);
    	JSONObject meta = (JSONObject) response.get("meta");
    	assertNotNull(meta);
    	JSONObject user = (JSONObject) meta.get("user");
    	assertNotNull(user);
    	assertNotNull(user.get("email"));
    }
}
