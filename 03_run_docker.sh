#! /usr/bin/env bash

echo $JUG_VIENNA_CLIENT_SECRET

docker run \
-e JUG_VIENNA_CLIENT_ID=$JUG_VIENNA_CLIENT_ID \
-e JUG_VIENNA_CLIENT_SECRET=$JUG_VIENNA_CLIENT_SECRET \
-e KEYCLOAK_APP_BASE_URL="https://java.local" \
-e SERVER_FORWARD_HEADERS_STRATEGY=native \
-l dev.orbstack.domains=java.local \
-p 8080:8080 jug-vienna-raffle:latest


#-e KEYCLOAK_APP_BASE_URL=$KEYCLOAK_APP_BASE_URL \
