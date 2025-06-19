#!/bin/bash

# Test the complete booking flow
echo "=== Testing Booking Flow ==="

# Step 1: Login as user
echo "Step 1: Login as user..."
curl -c cookies.txt -s -X POST \
  -d "username=john_doe" \
  -d "password=password123" \
  http://localhost:8080/login > /dev/null

if [ $? -eq 0 ]; then
    echo "✓ User login successful"
else
    echo "✗ User login failed"
    exit 1
fi

# Step 2: Access booking page
echo "Step 2: Access booking page..."
BOOKING_RESPONSE=$(curl -b cookies.txt -s http://localhost:8080/booking)
if echo "$BOOKING_RESPONSE" | grep -q "Book New Appointment"; then
    echo "✓ Booking page accessible"
else
    echo "✗ Booking page not accessible"
    echo "Response: $BOOKING_RESPONSE"
    exit 1
fi

# Step 3: Check if we can get providers
echo "Step 3: Check available providers..."
PROVIDERS_RESPONSE=$(curl -b cookies.txt -s http://localhost:8080/booking/providers/1)
echo "Providers response: $PROVIDERS_RESPONSE"

# Step 4: Try to get available slots for today
TODAY=$(date +%Y-%m-%d)
echo "Step 4: Check available slots for $TODAY with provider ID 1..."
SLOTS_RESPONSE=$(curl -b cookies.txt -s "http://localhost:8080/booking/available-slots?providerId=1&date=$TODAY")
echo "Available slots response: $SLOTS_RESPONSE"

if [ "$SLOTS_RESPONSE" = "[]" ]; then
    echo "No slots available. Let's try to generate some..."
    
    # Step 5: Generate time slots for testing
    echo "Step 5: Generate time slots for today..."
    GENERATE_RESPONSE=$(curl -b cookies.txt -s -X POST \
      -d "providerId=1" \
      -d "date=$TODAY" \
      -d "intervalMinutes=60" \
      -d "startHour=9" \
      -d "endHour=17" \
      http://localhost:8080/booking/generate-slots)
    echo "Generate slots response: $GENERATE_RESPONSE"
    
    # Step 6: Check slots again
    echo "Step 6: Check available slots again..."
    SLOTS_RESPONSE=$(curl -b cookies.txt -s "http://localhost:8080/booking/available-slots?providerId=1&date=$TODAY")
    echo "Available slots after generation: $SLOTS_RESPONSE"
fi

echo "=== Test Complete ==="
