# Database Schema & Relationships

## 🗄️ ENTITY RELATIONSHIP DIAGRAM

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                              DATABASE SCHEMA                                    │
└─────────────────────────────────────────────────────────────────────────────────┘

┌─────────────────┐         ┌─────────────────┐         ┌─────────────────┐
│      USER       │         │ SERVICE_PROVIDER│         │    SERVICE      │
├─────────────────┤         ├─────────────────┤         ├─────────────────┤
│ id (PK)         │────────►│ id (PK)         │────────►│ id (PK)         │
│ username        │  1:1    │ user_id (FK)    │  1:N    │ provider_id (FK)│
│ email           │         │ business_name   │         │ name            │
│ password        │         │ description     │         │ description     │
│ first_name      │         │ phone           │         │ duration        │
│ last_name       │         │ address         │         │ price           │
│ role            │         │ specializations │         │ category        │
│ created_at      │         │ rating          │         │ is_active       │
│ is_active       │         │ is_verified     │         │ created_at      │
└─────────────────┘         │ created_at      │         └─────────────────┘
         │                  └─────────────────┘                  │
         │                                                       │
         │                  ┌─────────────────┐                  │
         │                  │  TIME_SLOT      │                  │
         │                  ├─────────────────┤                  │
         │                  │ id (PK)         │                  │
         │                  │ provider_id (FK)│◄─────────────────┤
         │                  │ service_id (FK) │◄─────────────────┘
         │                  │ date            │
         │                  │ start_time      │
         │                  │ end_time        │
         │                  │ is_booked       │
         │                  │ is_available    │
         │                  └─────────────────┘
         │                           │
         │                           │
         │                  ┌─────────────────┐
         └─────────────────►│  APPOINTMENT    │
                    1:N     ├─────────────────┤
                            │ id (PK)         │
                            │ customer_id (FK)│
                            │ provider_id (FK)│
                            │ service_id (FK) │
                            │ timeslot_id (FK)│◄────────────────┘
                            │ status          │
                            │ notes           │
                            │ created_at      │
                            │ updated_at      │
                            └─────────────────┘
                                     │
                                     │
                            ┌─────────────────┐
                            │    RATING       │
                            ├─────────────────┤
                            │ id (PK)         │
                            │ appointment_id  │◄────────────────┘
                            │ rating (1-5)    │
                            │ comment         │
                            │ created_at      │
                            └─────────────────┘
```

## 📊 DATA FLOW BY BUSINESS PROCESS

### 1. USER REGISTRATION PROCESS
```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│  Frontend   │───►│  Backend    │───►│  Database   │
│             │    │             │    │             │
│ Registration│    │ Validation  │    │ User Table  │
│ Form        │    │ Password    │    │ Insert      │
│             │    │ Encoding    │    │             │
└─────────────┘    └─────────────┘    └─────────────┘
                            │
                            ▼
                   ┌─────────────┐
                   │ Email       │
                   │ Service     │
                   │ (Welcome)   │
                   └─────────────┘
```

### 2. SERVICE PROVIDER ONBOARDING
```
Provider Registration → Admin Approval → Profile Setup → Service Creation
        │                     │              │               │
        ▼                     ▼              ▼               ▼
┌─────────────┐    ┌─────────────┐   ┌─────────────┐  ┌─────────────┐
│ USER table  │    │ Approval    │   │SERVICE_     │  │ SERVICE     │
│ role =      │    │ workflow    │   │PROVIDER     │  │ table       │
│ 'PROVIDER'  │    │ status      │   │ table       │  │ entries     │
└─────────────┘    └─────────────┘   └─────────────┘  └─────────────┘
```

### 3. BOOKING CREATION PROCESS
```
Customer → Service Selection → Time Slot → Booking Confirmation → Provider Notification
    │             │               │              │                       │
    ▼             ▼               ▼              ▼                       ▼
┌─────────┐ ┌─────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────────┐
│Customer │ │Service  │ │TIME_SLOT    │ │APPOINTMENT  │ │Email/SMS        │
│browses  │ │details  │ │is_booked =  │ │record       │ │notification     │
│services │ │loaded   │ │TRUE         │ │created      │ │sent             │
└─────────┘ └─────────┘ └─────────────┘ └─────────────┘ └─────────────────┘
```

---

## 🔐 SECURITY & ACCESS CONTROL

### Role-Based Data Access
```
┌─────────────────────────────────────────────────────────────────┐
│                     DATA ACCESS MATRIX                          │
├─────────────┬─────────────┬─────────────┬─────────────────────┤
│ Entity      │ Customer    │ Provider    │ Admin               │
├─────────────┼─────────────┼─────────────┼─────────────────────┤
│ Users       │ Own profile │ Own profile │ All users          │
│ Services    │ Read all    │ Own services│ All services       │
│ Providers   │ Read public │ Own data    │ All providers      │
│ TimeSlots   │ Available   │ Own slots   │ All slots          │
│ Appointments│ Own bookings│ Own bookings│ All appointments   │
│ Ratings     │ Own ratings │ Received    │ All ratings        │
│ Analytics   │ Personal    │ Business    │ System-wide        │
└─────────────┴─────────────┴─────────────┴─────────────────────┘
```

### Data Filtering Examples
```
// Customer viewing appointments
SELECT * FROM appointments WHERE customer_id = :currentUserId

// Provider viewing their appointments  
SELECT * FROM appointments WHERE provider_id = :currentProviderId

// Admin viewing all appointments
SELECT * FROM appointments // No filtering

// Customer viewing available time slots
SELECT * FROM time_slots WHERE is_available = true AND date >= CURRENT_DATE

// Provider managing their time slots
SELECT * FROM time_slots WHERE provider_id = :currentProviderId
```

---

## 📈 ANALYTICS DATA AGGREGATION

### Customer Analytics Queries
```sql
-- Popular services
SELECT s.name, COUNT(a.id) as booking_count 
FROM services s 
JOIN appointments a ON s.id = a.service_id 
GROUP BY s.id 
ORDER BY booking_count DESC;

-- Peak booking times
SELECT HOUR(ts.start_time) as hour, COUNT(a.id) as bookings
FROM time_slots ts 
JOIN appointments a ON ts.id = a.timeslot_id
GROUP BY HOUR(ts.start_time);

-- Customer retention
SELECT customer_id, COUNT(*) as total_bookings,
       MAX(created_at) as last_booking
FROM appointments 
GROUP BY customer_id;
```

### Provider Performance Metrics
```sql
-- Provider revenue tracking
SELECT p.business_name, 
       SUM(s.price) as total_revenue,
       COUNT(a.id) as total_bookings,
       AVG(r.rating) as avg_rating
FROM service_providers p
JOIN services s ON p.id = s.provider_id
JOIN appointments a ON s.id = a.service_id
LEFT JOIN ratings r ON a.id = r.appointment_id
GROUP BY p.id;

-- Schedule utilization
SELECT p.business_name,
       COUNT(CASE WHEN ts.is_booked = true THEN 1 END) as booked_slots,
       COUNT(*) as total_slots,
       (COUNT(CASE WHEN ts.is_booked = true THEN 1 END) * 100.0 / COUNT(*)) as utilization_rate
FROM service_providers p
JOIN time_slots ts ON p.id = ts.provider_id
GROUP BY p.id;
```

---

## 🔄 REAL-TIME DATA UPDATES

### WebSocket Integration (Future Enhancement)
```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│  Browser    │◄──►│ WebSocket   │◄──►│  Database   │
│             │    │ Server      │    │  Changes    │
│ Real-time   │    │             │    │             │
│ Updates     │    │ Broadcast   │    │ Triggers    │
└─────────────┘    └─────────────┘    └─────────────┘

Events that trigger real-time updates:
• New booking created → Update provider dashboard
• Time slot becomes unavailable → Update customer booking page
• Appointment status changed → Notify relevant parties
• Provider comes online → Update availability display
```

### Database Triggers for Data Consistency
```sql
-- Trigger to update time slot when appointment is created
DELIMITER //
CREATE TRIGGER appointment_created 
AFTER INSERT ON appointments 
FOR EACH ROW 
BEGIN
    UPDATE time_slots 
    SET is_booked = TRUE 
    WHERE id = NEW.timeslot_id;
END//

-- Trigger to release time slot when appointment is cancelled
CREATE TRIGGER appointment_cancelled 
AFTER UPDATE ON appointments 
FOR EACH ROW 
BEGIN
    IF NEW.status = 'CANCELLED' AND OLD.status != 'CANCELLED' THEN
        UPDATE time_slots 
        SET is_booked = FALSE 
        WHERE id = NEW.timeslot_id;
    END IF;
END//
DELIMITER ;
```

---

## 📱 API DATA EXCHANGE PATTERNS

### RESTful Endpoints Data Flow
```
┌─────────────────────────────────────────────────────────────────┐
│                      API ENDPOINTS                              │
├─────────────────────────────────────────────────────────────────┤
│ POST /api/auth/login                                            │
│ ├─ Input: {username, password}                                  │
│ └─ Output: {token, user, role, redirectUrl}                     │
│                                                                 │
│ GET /api/services                                               │
│ ├─ Input: {category?, providerId?, search?}                     │
│ └─ Output: [{id, name, description, price, duration, provider}] │
│                                                                 │
│ GET /api/timeslots                                              │
│ ├─ Input: {serviceId, providerId, date, duration}               │
│ └─ Output: [{id, startTime, endTime, available}]                │
│                                                                 │
│ POST /api/appointments                                          │
│ ├─ Input: {serviceId, providerId, timeslotId, notes}            │
│ └─ Output: {appointmentId, status, confirmationCode}            │
│                                                                 │
│ PUT /api/appointments/{id}                                      │
│ ├─ Input: {newTimeslotId?, status?, notes?}                     │
│ └─ Output: {success, message, updatedAppointment}               │
└─────────────────────────────────────────────────────────────────┘
```

### JSON Data Structure Examples
```json
// Service Provider Profile
{
  "id": 1,
  "businessName": "Hair Studio Elite",
  "description": "Professional hair care services",
  "rating": 4.8,
  "services": [
    {
      "id": 1,
      "name": "Haircut & Style",
      "duration": 60,
      "price": 45.00,
      "category": "Hair Care"
    }
  ],
  "availability": {
    "monday": {"start": "09:00", "end": "17:00"},
    "tuesday": {"start": "09:00", "end": "17:00"}
  }
}

// Appointment Booking Request
{
  "customerId": 123,
  "serviceId": 1,
  "providerId": 1,
  "timeslotId": 456,
  "appointmentDate": "2025-06-20",
  "startTime": "10:00",
  "notes": "First time client, consultation needed",
  "contactMethod": "email"
}

// Booking Confirmation Response
{
  "appointmentId": 789,
  "confirmationCode": "ABC123XYZ",
  "status": "CONFIRMED",
  "details": {
    "service": "Haircut & Style",
    "provider": "Hair Studio Elite",
    "dateTime": "2025-06-20T10:00:00",
    "duration": 60,
    "totalCost": 45.00
  },
  "cancellationPolicy": "Cancel up to 24 hours before",
  "reminderSent": true
}
```

This comprehensive data flow documentation shows exactly how information moves between all users in the booking application, providing a clear understanding of the system's architecture and data relationships.
