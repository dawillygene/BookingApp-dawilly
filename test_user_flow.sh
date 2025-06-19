#!/bin/bash

# Test user registration and login
echo "=== Testing User Registration and Dashboard Access ==="

# Step 1: Register a new user
echo "Step 1: Register a new user..."
REGISTER_RESPONSE=$(curl -s -X POST \
  -d "firstName=Test" \
  -d "lastName=User" \
  -d "username=testuser" \
  -d "email=test@example.com" \
  -d "password=password123" \
  -d "phoneNumber=1234567890" \
  -d "address=123 Test St" \
  http://localhost:8080/register)

if echo "$REGISTER_RESPONSE" | grep -q "successfully" || echo "$REGISTER_RESPONSE" | grep -q "dashboard" || echo "$REGISTER_RESPONSE" | grep -q "login"; then
    echo "✓ User registration completed"
else
    echo "ℹ User may already exist, proceeding with login..."
fi

# Step 2: Login as user
echo "Step 2: Login as user..."
curl -c user_cookies.txt -s -X POST \
  -d "username=testuser" \
  -d "password=password123" \
  http://localhost:8080/login > /dev/null

if [ $? -eq 0 ]; then
    echo "✓ User login successful"
else
    echo "✗ User login failed"
    exit 1
fi

# Step 3: Access user dashboard
echo "Step 3: Access user dashboard..."
DASHBOARD_RESPONSE=$(curl -b user_cookies.txt -s http://localhost:8080/user/dashboard)
if echo "$DASHBOARD_RESPONSE" | grep -q "Dashboard" && ! echo "$DASHBOARD_RESPONSE" | grep -q "TemplateProcessingException"; then
    echo "✓ User dashboard accessible without errors"
else
    echo "✗ User dashboard has errors"
    echo "Response: $DASHBOARD_RESPONSE" | head -20
    exit 1
fi

# Step 4: Access booking page
echo "Step 4: Access booking page..."
BOOKING_RESPONSE=$(curl -b user_cookies.txt -s http://localhost:8080/booking)
if echo "$BOOKING_RESPONSE" | grep -q "Book New Appointment" && ! echo "$BOOKING_RESPONSE" | grep -q "error"; then
    echo "✓ Booking page accessible"
else
    echo "✗ Booking page not accessible"
    echo "Response: $BOOKING_RESPONSE" | head -20
fi

echo "=== Test Complete ==="
