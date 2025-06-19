#!/bin/bash

echo "Testing Admin Endpoints..."
echo "========================="

BASE_URL="http://localhost:8080"

# Test basic endpoints
echo "1. Testing Home Page:"
curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/"
echo ""

echo "2. Testing Login Page:"
curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/login"
echo ""

echo "3. Testing Register Page:"
curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/register"
echo ""

echo "4. Testing Booking Page:"
curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/booking"
echo ""

# Note: Admin endpoints require authentication, so these might return 302 (redirect to login)
echo "5. Testing Admin Dashboard (will redirect if not authenticated):"
curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/admin"
echo ""

echo "6. Testing Admin Users Page:"
curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/admin/users"
echo ""

echo "7. Testing Admin Services Page:"
curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/admin/services"
echo ""

echo "8. Testing Admin Categories Page:"
curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/admin/categories"
echo ""

echo "9. Testing Admin Analytics Page:"
curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/admin/analytics"
echo ""

echo "Testing complete!"
