# Booking Application - Data Flow Between Users

## System Overview
The booking application facilitates interactions between three main user types:
- **Customers**: Book appointments and manage their bookings
- **Service Providers**: Offer services and manage their schedules
- **Administrators**: Oversee the entire system

---

## 🔄 COMPLETE DATA FLOW DIAGRAM

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                           BOOKING APPLICATION ECOSYSTEM                          │
└─────────────────────────────────────────────────────────────────────────────────┘

┌──────────────┐         ┌──────────────────┐         ┌─────────────────┐
│   CUSTOMER   │◄───────►│   SPRING BOOT    │◄───────►│ SERVICE PROVIDER│
│              │         │    BACKEND       │         │                 │
│ - Browse     │         │                  │         │ - Manage        │
│ - Book       │         │ ┌──────────────┐ │         │   Services      │
│ - Manage     │         │ │   DATABASE   │ │         │ - Set Schedule  │
│ - Rate       │◄───────►│ │   (MySQL)    │ │◄───────►│ - Handle        │
└──────────────┘         │ │              │ │         │   Appointments  │
                         │ │ - Users      │ │         └─────────────────┘
┌──────────────┐         │ │ - Services   │ │         
│     ADMIN    │◄───────►│ │ - Providers  │ │         ┌─────────────────┐
│              │         │ │ - Appointments│ │         │   NOTIFICATIONS │
│ - Oversee    │         │ │ - TimeSlots  │ │◄───────►│                 │
│ - Approve    │         │ │ - Schedules  │ │         │ - Email Service │
│ - Manage     │         │ └──────────────┘ │         │ - SMS (Future)  │
│ - Analytics  │         └──────────────────┘         │ - In-App Alerts │
└──────────────┘                                      └─────────────────┘
```

---

## 📊 DETAILED DATA FLOW BY USER INTERACTION

### 1. CUSTOMER REGISTRATION & LOGIN FLOW

```
CUSTOMER → Registration Form → Backend Validation → Database → Email Verification
    ↓
Login Attempt → Spring Security → User Authentication → Role Assignment → Dashboard
```

**Data Elements:**
- Personal info (name, email, password)
- User preferences and profile data
- Authentication tokens and session data

---

### 2. SERVICE BROWSING & BOOKING FLOW

```
CUSTOMER → Browse Services → Filter/Search → View Provider Profiles → Select Service
    ↓
View Available Slots → Select Date/Time → Booking Form → Payment (Future) → Confirmation
    ↓
DATABASE UPDATES:
- TimeSlot.isBooked = true
- New Appointment record created
- Provider notification triggered
    ↓
NOTIFICATIONS:
- Customer: Booking confirmation email
- Provider: New booking alert
- Admin: Booking statistics update
```

**Data Flow Sequence:**
```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│  Customer   │───►│   Service   │───►│  TimeSlot   │───►│ Appointment │
│  Selects    │    │  Selected   │    │  Reserved   │    │   Created   │
└─────────────┘    └─────────────┘    └─────────────┘    └─────────────┘
                                           │
                                           ▼
                                   ┌─────────────┐
                                   │ Provider    │
                                   │ Notified    │
                                   └─────────────┘
```

---

### 3. SERVICE PROVIDER REGISTRATION & SETUP FLOW

```
PROVIDER → Registration → Admin Approval → Profile Setup → Service Creation
    ↓
Schedule Configuration → Time Slot Generation → Service Activation → Customer Visibility
```

**Data Elements:**
- Provider credentials and business info
- Service details (name, description, price, duration)
- Schedule patterns and availability rules
- Generated time slots for booking

---

### 4. PROVIDER SCHEDULE & APPOINTMENT MANAGEMENT

```
PROVIDER → Set Working Hours → Generate Time Slots → Receive Booking Requests
    ↓
Accept/Decline → Update Appointment Status → Manage Customer Communication
    ↓
Complete Appointment → Request Customer Rating → Update Statistics
```

**Bi-directional Data Flow:**
```
┌─────────────────┐ ◄─── Booking Requests ───► ┌─────────────────┐
│    PROVIDER     │                            │    CUSTOMER     │
│                 │ ──── Confirmations ──────► │                 │
│ - Accept/Decline│ ◄─── Reschedule Requests── │ - Book/Cancel   │
│ - Set Schedule  │ ──── Status Updates ─────► │ - Rate Service  │
│ - Mark Complete │ ◄─── Payments (Future) ─── │ - View History  │
└─────────────────┘                            └─────────────────┘
```

---

### 5. ADMINISTRATIVE OVERSIGHT FLOW

```
ADMIN → Monitor System → Review Provider Applications → Approve/Reject
    ↓
Manage User Accounts → Generate Reports → System Configuration → Policy Updates
    ↓
Handle Disputes → Performance Analytics → System Maintenance
```

**Admin Data Access:**
```
┌─────────────────────────────────────────────────────────────────┐
│                        ADMIN DASHBOARD                          │
├─────────────────────────────────────────────────────────────────┤
│ User Management     │ Provider Management  │ System Analytics   │
│ - View all users    │ - Approve providers  │ - Booking trends   │
│ - Role assignment   │ - Monitor performance│ - Revenue tracking │
│ - Account status    │ - Handle complaints  │ - User engagement  │
├─────────────────────────────────────────────────────────────────┤
│ Service Management  │ Appointment Oversight│ System Health      │
│ - Category setup    │ - View all bookings  │ - Error monitoring │
│ - Featured services │ - Resolve disputes   │ - Performance logs │
│ - Content moderation│ - Refund management  │ - Security audits  │
└─────────────────────────────────────────────────────────────────┘
```

---

## 🔄 REAL-TIME DATA SYNCHRONIZATION

### Appointment Lifecycle Data Flow

```
┌──────────────┐
│   CREATED    │ ──── Customer books ────► Database Update
└──────────────┘                               │
        ▲                                      ▼
        │                              ┌──────────────┐
        │                              │  CONFIRMED   │ ──── Provider accepts
        │                              └──────────────┘
        │                                       │
        │                                       ▼
┌──────────────┐                       ┌──────────────┐
│  CANCELLED   │ ◄──── Various ──────── │ IN_PROGRESS  │ ──── Appointment time
└──────────────┘       reasons         └──────────────┘
        │                                       │
        │                                       ▼
        │                               ┌──────────────┐
        └────── Refund process ──────── │  COMPLETED   │ ──── Provider marks done
                                        └──────────────┘
                                                │
                                                ▼
                                        ┌──────────────┐
                                        │   RATED      │ ──── Customer provides feedback
                                        └──────────────┘
```

### Data Triggers and Notifications

```
EVENT                  → TRIGGERED ACTIONS                → AFFECTED PARTIES
─────────────────────────────────────────────────────────────────────────────
New Booking           → • TimeSlot blocked               → Provider + Customer
                      → • Confirmation email sent        
                      → • Provider notification          
                      → • Admin stats updated            

Cancellation          → • TimeSlot released              → Provider + Customer
                      → • Cancellation emails sent       
                      → • Refund processed (future)      
                      → • Statistics updated             

Provider Schedule     → • Time slots generated/updated   → All Customers
Change                → • Existing bookings validated    
                      → • Customers notified of changes  

Rating Submitted      → • Provider rating updated        → Provider + Admin
                      → • Review published               
                      → • Analytics updated              
```

---

## 📈 DATA ANALYTICS FLOW

### Customer Analytics
```
Customer Behavior → Booking Patterns → Preference Analysis → Personalized Recommendations
     │                    │                   │                        │
     ▼                    ▼                   ▼                        ▼
• Page views         • Popular services   • Favorite providers    • Suggested services
• Search queries     • Peak booking times • Service categories    • Optimal time slots
• Booking history    • Seasonal trends    • Price sensitivity     • Promotional offers
```

### Provider Analytics
```
Provider Performance → Service Metrics → Customer Feedback → Business Insights
        │                    │                │                     │
        ▼                    ▼                ▼                     ▼
• Booking rates          • Popular services  • Ratings/reviews    • Revenue trends
• Customer retention     • Time utilization  • Complaint analysis • Growth opportunities
• Schedule optimization  • Service demand    • Improvement areas  • Market positioning
```

### System Analytics
```
Platform Usage → Performance Metrics → Business Intelligence → Strategic Decisions
      │                │                       │                      │
      ▼                ▼                       ▼                      ▼
• User growth       • Response times        • Market trends         • Feature development
• Feature adoption  • Error rates          • Competitive analysis  • Resource allocation
• Support requests  • Database performance • Revenue projections   • Platform scaling
```

---

## 🔐 DATA SECURITY & PRIVACY FLOW

### Authentication & Authorization
```
User Login → Spring Security → Role Verification → Permission Check → Resource Access
    │              │                │                   │                │
    ▼              ▼                ▼                   ▼                ▼
JWT Token      Password Hash    User Roles         API Endpoints    Data Filtering
Generation     Verification     (Customer/         Authorization    (User sees only
                                Provider/Admin)                     their data)
```

### Data Protection
```
Sensitive Data → Encryption → Secure Storage → Access Logging → Audit Trail
     │              │             │              │               │
     ▼              ▼             ▼              ▼               ▼
• Personal info  AES-256      Database        Security         Compliance
• Payment data   encryption   (encrypted      monitoring       reporting
• Medical notes  SSL/TLS      at rest)        Failed attempts  GDPR/HIPAA
```

---

## 🌐 EXTERNAL INTEGRATIONS DATA FLOW

### Email Service Integration
```
Booking Event → Email Template → SMTP Service → Delivery Status → Tracking
     │              │               │              │              │
     ▼              ▼               ▼              ▼              ▼
Trigger        Dynamic content   External        Success/Fail    Analytics
(booking,      (customer name,   email provider  notification    update
reminder,      appointment       (Gmail, AWS     logging
cancellation)  details)          SES, etc.)
```

### Future Payment Integration
```
Booking → Payment Request → Payment Gateway → Transaction Status → Booking Confirmation
   │           │                 │                  │                    │
   ▼           ▼                 ▼                  ▼                    ▼
Amount      Payment form      Stripe/PayPal    Success/Failure      Final status
calculation  rendering        processing       notification         update
```

---

## 📱 API ENDPOINTS & DATA EXCHANGE

### RESTful API Structure
```
Frontend (Thymeleaf/JS) ←→ Spring Boot Controllers ←→ Service Layer ←→ Repository ←→ Database

GET /api/services           → Retrieve available services
POST /api/bookings          → Create new booking
PUT /api/bookings/{id}      → Update booking (reschedule)
DELETE /api/bookings/{id}   → Cancel booking
GET /api/timeslots         → Get available time slots
POST /api/providers        → Register new provider
GET /api/analytics         → Retrieve system analytics
```

---

## 🎯 KEY DATA RELATIONSHIPS

### Entity Relationships
```
USER (1) ←─── has many ────→ (N) APPOINTMENTS
     │                            │
     │                            │
     └── can be ──→ SERVICE_PROVIDER (1) ←─── offers ────→ (N) SERVICES
                           │                                    │
                           │                                    │
                    (1) manages (N)                            │
                           │                                    │
                           ▼                                    │
                    TIME_SLOTS (N) ←────── associated with ────┘
                           │
                           │
                    (1) scheduled for (1)
                           │
                           ▼
                    APPOINTMENTS
```

This comprehensive data flow shows how information moves between all user types, ensuring transparency and understanding of the system's architecture. Each interaction is tracked, secured, and optimized for the best user experience.
