#!/bin/bash

echo "=== BookingApp Provider Flow Test (Post Template Fix) ==="
echo "Date: $(date)"
echo ""

# Function to test URL
test_url() {
    local url=$1
    local description=$2
    echo "Testing: $description"
    local status=$(curl -s -o /dev/null -w "%{response_code}" "$url")
    echo "URL: $url"
    echo "HTTP Status: $status"
    if [ "$status" = "200" ]; then
        echo "✓ Success"
    elif [ "$status" = "302" ] || [ "$status" = "301" ]; then
        echo "⚠ Redirect (expected for secured endpoints)"
    else
        echo "✗ Failed"
    fi
    echo ""
}

# Test basic endpoints
test_url "http://localhost:8080/" "Home page"
test_url "http://localhost:8080/login" "Login page"
test_url "http://localhost:8080/register/provider" "Provider registration"

# Test authenticated provider endpoints (should redirect to login)
test_url "http://localhost:8080/provider/dashboard" "Provider dashboard (unauthenticated)"
test_url "http://localhost:8080/provider/appointments" "Provider appointments (unauthenticated)"
test_url "http://localhost:8080/provider/schedule" "Provider schedule (unauthenticated)"

echo "=== Test Login Flow ==="
echo "Attempting to login as provider 'sarah'..."

# Login and capture cookies
curl -c cookies.txt -s -X POST "http://localhost:8080/login" \
     -H "Content-Type: application/x-www-form-urlencoded" \
     -d "username=sarah&password=sarah123" \
     -w "Login HTTP Status: %{response_code}\n" \
     -o login_response.html

echo "Login response saved to login_response.html"

# Test authenticated access
echo ""
echo "Testing authenticated provider endpoints..."

curl -b cookies.txt -s -o dashboard_response.html -w "Dashboard HTTP Status: %{response_code}\n" \
     "http://localhost:8080/provider/dashboard"

curl -b cookies.txt -s -o appointments_response.html -w "Appointments HTTP Status: %{response_code}\n" \
     "http://localhost:8080/provider/appointments"

curl -b cookies.txt -s -o schedule_response.html -w "Schedule HTTP Status: %{response_code}\n" \
     "http://localhost:8080/provider/schedule"

echo ""
echo "=== Response Analysis ==="
echo "Dashboard response size: $(wc -c < dashboard_response.html) bytes"
echo "Appointments response size: $(wc -c < appointments_response.html) bytes"
echo "Schedule response size: $(wc -c < schedule_response.html) bytes"

# Check for error indicators in responses
echo ""
echo "=== Error Check ==="
if grep -q "error\|Error\|Exception" dashboard_response.html; then
    echo "⚠ Dashboard may have errors"
    echo "First few lines of dashboard response:"
    head -5 dashboard_response.html
else
    echo "✓ Dashboard response looks clean"
fi

if grep -q "error\|Error\|Exception" appointments_response.html; then
    echo "⚠ Appointments may have errors"
    echo "First few lines of appointments response:"
    head -5 appointments_response.html
else
    echo "✓ Appointments response looks clean"
fi

if grep -q "error\|Error\|Exception" schedule_response.html; then
    echo "⚠ Schedule may have errors"
    echo "First few lines of schedule response:"
    head -5 schedule_response.html
else
    echo "✓ Schedule response looks clean"
fi

echo ""
echo "=== Test Complete ==="
echo "Check the *_response.html files for detailed output"
echo "Use: 'open -a 'Firefox' dashboard_response.html' to view in browser"

# Cleanup cookies
rm -f cookies.txt
