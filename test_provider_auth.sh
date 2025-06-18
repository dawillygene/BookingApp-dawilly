#!/bin/bash

# Test Provider Login and Dashboard Access
echo "🔐 Testing Provider Authentication Flow"
echo "======================================="

# Test 1: Login as provider Sarah (CSRF disabled)
echo "📋 Step 1: Logging in as Sarah (provider)..."
LOGIN_RESULT=$(curl -s -c cookies.txt \
    -d "username=sarah" \
    -d "password=sarah123" \
    -X POST \
    -w "%{http_code}" \
    -o /tmp/login_response.html \
    http://localhost:8080/login)

echo "Login response code: $LOGIN_RESULT"

# Test 2: Try to access provider dashboard
echo "📋 Step 2: Accessing provider dashboard..."
DASHBOARD_RESULT=$(curl -s -b cookies.txt \
    -w "%{http_code}" \
    -o /tmp/dashboard_response.html \
    http://localhost:8080/provider/dashboard)

echo "Dashboard response code: $DASHBOARD_RESULT"

# Test 3: Check if we can access provider services
echo "📋 Step 3: Accessing provider services..."
SERVICES_RESULT=$(curl -s -b cookies.txt \
    -w "%{http_code}" \
    -o /tmp/services_response.html \
    http://localhost:8080/provider/services)

echo "Services response code: $SERVICES_RESULT"

# Test 4: Check provider appointments
echo "📋 Step 4: Accessing provider appointments..."
APPOINTMENTS_RESULT=$(curl -s -b cookies.txt \
    -w "%{http_code}" \
    -o /tmp/appointments_response.html \
    http://localhost:8080/provider/appointments)

echo "Appointments response code: $APPOINTMENTS_RESULT"

# Check response content
echo ""
echo "📊 Response Analysis:"
if grep -q "Sarah" /tmp/dashboard_response.html 2>/dev/null; then
    echo "✅ Dashboard contains provider name"
else
    echo "❌ Dashboard doesn't contain provider name"
fi

if grep -q "Hair" /tmp/services_response.html 2>/dev/null; then
    echo "✅ Services page contains provider services"
else
    echo "❌ Services page doesn't contain expected content"
fi

# Cleanup
rm -f cookies.txt /tmp/login_response.html /tmp/dashboard_response.html /tmp/services_response.html 2>/dev/null

echo ""
echo "🎯 Test Summary:"
echo "   Login Response: $LOGIN_RESULT"
echo "   Dashboard Access: $DASHBOARD_RESULT" 
echo "   Services Access: $SERVICES_RESULT"
echo ""

if [ "$DASHBOARD_RESULT" = "200" ] && [ "$SERVICES_RESULT" = "200" ]; then
    echo "✅ Provider authentication and access working correctly!"
else
    echo "❌ Issues detected with provider authentication flow"
fi
