#!/bin/bash

# Script to initialize sample data for the JUG Vienna Raffle application

# Default host and port
HOST="localhost"
PORT="8080"

# Parse command line arguments
while [[ $# -gt 0 ]]; do
  case $1 in
    --host=*)
      HOST="${1#*=}"
      shift
      ;;
    --port=*)
      PORT="${1#*=}"
      shift
      ;;
    -h|--help)
      echo "Usage: $0 [--host=hostname] [--port=port]"
      echo "  --host=hostname  Specify the hostname (default: localhost)"
      echo "  --port=port      Specify the port (default: 8080)"
      echo "  -h, --help       Show this help message"
      exit 0
      ;;
    *)
      echo "Unknown option: $1"
      echo "Use -h or --help for usage information"
      exit 1
      ;;
  esac
done

echo "Initializing sample data for JUG Vienna Raffle application..."
echo "Host: $HOST"
echo "Port: $PORT"

# Make the HTTP request
RESPONSE=$(curl -s -o /dev/null -w "%{http_code}" -X POST http://$HOST:$PORT/api/data/init)

# Check the response code
if [ "$RESPONSE" == "200" ]; then
  echo "✅ Sample data initialized successfully!"
else
  echo "❌ Failed to initialize sample data. Response code: $RESPONSE"
  echo "Make sure the application is running and accessible at http://$HOST:$PORT"
  exit 1
fi