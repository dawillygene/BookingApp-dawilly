#!/bin/bash

# Login credentials
USERNAME="provider"
PASSWORD="password"

# Create cookie jar file
COOKIE_JAR="cookies.txt"

echo "1. Getting login page to capture CSRF token..."
LOGIN_PAGE=$(curl -s -c "$COOKIE_JAR" http://localhost:8080/login)

# Extract CSRF token if needed - depends on your implementation
# CSRF_TOKEN=$(echo "$LOGIN_PAGE" | grep -o 'name="_csrf" value="[^"]*"' | sed 's/name="_csrf" value="\([^"]*\)"/\1/')

echo "2. Attempting to login as provider..."
curl -s -L -b "$COOKIE_JAR" -c "$COOKIE_JAR" -d "username=$USERNAME&password=$PASSWORD" http://localhost:8080/login -o /dev/null

echo "3. Accessing provider/services page..."
curl -s -L -b "$COOKIE_JAR" -c "$COOKIE_JAR" http://localhost:8080/provider/services > provider_services_response.html

echo "Done! Response saved to provider_services_response.html"

# Let's check if we got redirected to login or got a valid page
if grep -q "login-form" provider_services_response.html; then
  echo "ERROR: We were redirected to the login page. Authentication failed."
else
  echo "SUCCESS: We received the provider services page!"
fi
