# User Stories

# Admin User Stories

## User Story 1

**Title:**
*As an Admin, I want to create doctor accounts, so that authorized doctors can access the system.*

**Acceptance Criteria:**

1. Admin can create a doctor account.
2. Doctor information is validated before saving.
3. The account is available after creation.

**Priority:** High
**Story Points:** 5

**Notes:**

* Email addresses must be unique.

---

## User Story 2

**Title:**
*As an Admin, I want to update doctor information, so that records remain accurate.*

**Acceptance Criteria:**

1. Admin can edit doctor details.
2. Changes are saved successfully.
3. Updated information is displayed immediately.

**Priority:** High
**Story Points:** 3

**Notes:**

* Audit logs may be added later.

---

## User Story 3

**Title:**
*As an Admin, I want to manage patient records, so that patient information stays current.*

**Acceptance Criteria:**

1. Admin can view patient records.
2. Admin can update patient details.
3. Changes are stored in the database.

**Priority:** High
**Story Points:** 5

**Notes:**

* Sensitive information should be protected.

---

## User Story 4

**Title:**
*As an Admin, I want to view all appointments, so that I can monitor clinic operations.*

**Acceptance Criteria:**

1. All appointments are displayed.
2. Appointments can be filtered by date.
3. Appointments can be searched by doctor or patient.

**Priority:** Medium
**Story Points:** 3

**Notes:**

* Results should load efficiently.

---

## User Story 5

**Title:**
*As an Admin, I want to manage user permissions, so that only authorized users can access system features.*

**Acceptance Criteria:**

1. Admin can assign roles.
2. Admin can modify permissions.
3. Unauthorized users cannot access restricted features.

**Priority:** High
**Story Points:** 5

**Notes:**

* Role-based access control should be enforced.

# Patient User Stories

## User Story 1

**Title:**
*As a Patient, I want to register an account, so that I can use the clinic services.*

**Acceptance Criteria:**

1. Patient can submit registration information.
2. Required fields are validated.
3. Account is created successfully.

**Priority:** High
**Story Points:** 5

**Notes:**

* Email should be unique.

---

## User Story 2

**Title:**
*As a Patient, I want to book an appointment, so that I can receive medical care.*

**Acceptance Criteria:**

1. Available doctors are displayed.
2. Patient can select a time slot.
3. Appointment is confirmed successfully.

**Priority:** High
**Story Points:** 5

**Notes:**

* Double bookings are not allowed.

---

## User Story 3

**Title:**
*As a Patient, I want to view my appointments, so that I can track upcoming visits.*

**Acceptance Criteria:**

1. Upcoming appointments are displayed.
2. Past appointments are accessible.
3. Appointment details are visible.

**Priority:** Medium
**Story Points:** 3

**Notes:**

* Results should be sorted by date.

---

## User Story 4

**Title:**
*As a Patient, I want to reschedule appointments, so that I can adjust my schedule.*

**Acceptance Criteria:**

1. Patient can select a new available time.
2. Appointment is updated successfully.
3. Notifications are sent after the change.

**Priority:** Medium
**Story Points:** 3

**Notes:**

* Only future appointments can be modified.

---

## User Story 5

**Title:**
*As a Patient, I want to view my prescriptions, so that I can follow my treatment plan.*

**Acceptance Criteria:**

1. Prescriptions are displayed.
2. Prescription details are readable.
3. Historical prescriptions are accessible.

**Priority:** High
**Story Points:** 5

**Notes:**

* Data is retrieved from MongoDB.

# Doctor User Stories

## User Story 1

**Title:**
*As a Doctor, I want to view my schedule, so that I can manage my appointments.*

**Acceptance Criteria:**

1. Daily appointments are displayed.
2. Appointment details are visible.
3. Schedule updates automatically.

**Priority:** High
**Story Points:** 3

**Notes:**

* Calendar view may be added later.

---

## User Story 2

**Title:**
*As a Doctor, I want to manage my availability, so that patients can book appointments during working hours.*

**Acceptance Criteria:**

1. Doctor can add available slots.
2. Doctor can update availability.
3. Patients only see available slots.

**Priority:** High
**Story Points:** 5

**Notes:**

* Availability changes affect future bookings only.

---

## User Story 3

**Title:**
*As a Doctor, I want to access patient records, so that I can review medical history before consultations.*

**Acceptance Criteria:**

1. Patient information is displayed.
2. Medical history is accessible.
3. Records load successfully.

**Priority:** High
**Story Points:** 5

**Notes:**

* Access should be secure.

---

## User Story 4

**Title:**
*As a Doctor, I want to update patient records, so that treatment information remains accurate.*

**Acceptance Criteria:**

1. Doctor can edit patient notes.
2. Changes are saved successfully.
3. Updated records are visible immediately.

**Priority:** High
**Story Points:** 5

**Notes:**

* Previous versions may be stored in future releases.

---

## User Story 5

**Title:**
*As a Doctor, I want to create prescriptions, so that patients receive appropriate treatment instructions.*

**Acceptance Criteria:**

1. Doctor can create a prescription.
2. Prescription is stored successfully.
3. Patient can access the prescription.

**Priority:** High
**Story Points:** 5

**Notes:**

* Prescription data is stored in MongoDB.
