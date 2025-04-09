package com.vaadin.demo.application.services.meetup;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class DevMeetupServiceImpl implements MeetupService {


    protected final Log logger = LogFactory.getLog(getClass());

    @Override
    public Optional<MeetupEvent> getEvent(String meetupEventId) {
        try {
            String json = MeetupMockData.eventMap.get(meetupEventId);
            ObjectMapper mapper = new ObjectMapper();

            JsonNode root = mapper.readTree(json);
            JsonNode eventNode = root.path("data").path("event");

            return Optional.of(extractMeetupEvent(eventNode));
        } catch (Exception e) {
            logger.error("Fehler beim Parsen des Events", e);
            return Optional.empty();
        }
    }

    private static MeetupEvent extractMeetupEvent(JsonNode eventNode) {
        var id = eventNode.path("id").asText();
        var token = eventNode.path("token").asText();
        var title = eventNode.path("title").asText();
        var dateTime = OffsetDateTime.parse(eventNode.path("dateTime").asText());
        var description = eventNode.path("description").asText();

        MeetupEvent event = new MeetupEvent(id, token, title, dateTime, description);
        return event;
    }

    @Override
    public Set<MeetupEvent> getEvents() {
        // Parse den statischen JSON-String und erstelle MeetupEvent-Objekte
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode root = mapper.readTree(MeetupMockData.events);
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
}
