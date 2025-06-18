#!/bin/bash

# BookingApp Provider Flow Validation Script
echo "🔍 BOOKINGAPP PROVIDER FLOW VALIDATION"
echo "======================================"
echo ""

# Check if application is running
echo "📊 Application Status:"
if curl -s http://localhost:8080 > /dev/null; then
    echo "✅ Application is running on http://localhost:8080"
else
    echo "❌ Application is NOT running"
    exit 1
fi
echo ""

# Test provider registration page
echo "🎯 Testing Provider Registration:"
if curl -s http://localhost:8080/register/provider | grep -q "provider"; then
    echo "✅ Provider registration page accessible"
else
    echo "❌ Provider registration page not working"
fi
echo ""

# Test protected provider routes (should redirect to login)
echo "🔒 Testing Protected Routes:"
LOGIN_REDIRECT=$(curl -s -o /dev/null -w "%{redirect_url}" http://localhost:8080/provider/dashboard)
if echo "$LOGIN_REDIRECT" | grep -q "login"; then
    echo "✅ Provider dashboard properly protected (redirects to login)"
else
    echo "❌ Provider dashboard not properly protected"
fi
echo ""

# List available endpoints for testing
echo "🌐 Available Testing URLs:"
echo "   • Main App: http://localhost:8080"
echo "   • Login: http://localhost:8080/login"
echo "   • User Registration: http://localhost:8080/register"
echo "   • Provider Registration: http://localhost:8080/register/provider"
echo "   • Provider Dashboard: http://localhost:8080/provider/dashboard"
echo "   • Provider Services: http://localhost:8080/provider/services"
echo "   • Provider Appointments: http://localhost:8080/provider/appointments"
echo ""

echo "👤 Test Credentials:"
echo "   • Admin: admin/admin"
echo "   • User: user/user"
echo "   • Sarah (Hair): sarah/sarah123"
echo "   • Michael (Massage): michael/michael123"
echo "   • Emma (Beauty): emma/emma123"
echo "   • David (Healthcare): david/david123"
echo ""

echo "✅ Provider implementation is ready for manual testing!"
echo "📋 Next: Login as a provider and test dashboard functionality"
