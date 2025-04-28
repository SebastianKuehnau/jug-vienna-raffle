package com.vaadin.demo.application.adapter.out.meetupclient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class DevMeetupClientImpl implements MeetupClient {


    protected final Log logger = LogFactory.getLog(getClass());

    @Override
    public Optional<MeetupEvent> getEvent(String meetupEventId) {
        try {
            String json = readResourceFile("mock/getEventWithRSVP_"+meetupEventId+".json");
            ObjectMapper mapper = new ObjectMapper();

            JsonNode root = mapper.readTree(json);
            JsonNode eventNode = root.path("data").path("event");

            return Optional.of(extractMeetupEvent(eventNode));
        } catch (Exception e) {
            logger.error("Fehler beim Parsen des Events", e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<MeetupEventWithRSVPs> getEventWithRSVPs(String meetupEventId) {
        return Optional.empty();
    }

    private static MeetupEvent extractMeetupEvent(JsonNode eventNode) {
        var id = eventNode.path("id").asText();
        var token = eventNode.path("token").asText();
        var title = eventNode.path("title").asText();
        var dateTime = OffsetDateTime.parse(eventNode.path("dateTime").asText());
        var description = eventNode.path("description").asText();
        var eventUrl = eventNode.path("eventUrl").asText();
        var status = eventNode.path("status").asText();

        var memberSet = new HashSet<Member>();
        eventNode.path("rsvps").path("edges").forEach(jsonNode -> {
            JsonNode node = jsonNode.path("node");
            var rsvp_id = node.path("id").asText();
            var rsvp_isHost = node.path("isHost").asBoolean(false);

            JsonNode memberNode = node.path("member");
            var member_id = memberNode.path("id").asText();
            var member_name = memberNode.path("name").asText();
            var member_email = memberNode.path("email").asText();

            var member = new Member(member_id, member_name, member_email, rsvp_id, rsvp_isHost, false);
            memberSet.add(member);
        });

        MeetupEvent event = new MeetupEvent(id, token, title, dateTime, description, eventUrl, status, memberSet);
        return event;
    }

    @Override
    public Set<MeetupEvent> getEvents() {
        // Parse den statischen JSON-String und erstelle MeetupEvent-Objekte
        ObjectMapper mapper = new ObjectMapper();
        try {
            String json = readResourceFile("mock/getEvents.json");
            JsonNode root = mapper.readTree(json);
            JsonNode edges = root.path("data").path("groupByUrlname").path("events").path("edges");

            Set<MeetupEvent> events = new HashSet<>();
            for (JsonNode edge : edges) {
                var node = edge.path("node");
                events.add(extractMeetupEvent(node));
            }

            return events;
        } catch (Exception e) {
            // Logging anstelle von System.err.println
            logger.error("Fehler beim Parsen des Mock-JSON", e);
            return Collections.emptySet();
        }
    }

    @Override
    public MeResponse getMe() {
        return null;
    }

    @Override
    public String getMeInGroup() {
        return "";
    }

    @Override
    public String getMyGroups() {
        return "";
    }

    @Override
    public String legacyQuery(String query) {
        return "";
    }

    @Override
    public String queryNew(String query) {
        return "";
    }

    private String readResourceFile(String fileName) throws IOException {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(fileName)) {
            if (is == null) throw new FileNotFoundException("Resource not found: " + fileName);
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
