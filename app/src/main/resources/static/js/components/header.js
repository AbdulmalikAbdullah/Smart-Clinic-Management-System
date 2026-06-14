// Step 1: Define the renderHeader Function
// Renders the entire header based on user's session, role, and login status

function renderHeader() {
  // Step 2: Select the Header Div
  const headerDiv = document.getElementById("header");

  // Step 3: Check if the Current Page is the Root Page
  if (window.location.pathname.endsWith("/")) {
    localStorage.removeItem("userRole");
    headerDiv.innerHTML = `
      <header class="header">
        <div class="logo-section">
          <img src="../assets/images/logo/logo.png" alt="Hospital CRM Logo" class="logo-img">
          <span class="logo-title">Hospital CMS</span>
        </div>
      </header>`;
    return;
  }

  // Step 4: Retrieve the User's Role and Token from LocalStorage
  const role = localStorage.getItem("userRole");
  const token = localStorage.getItem("token");

  // Step 5: Initialize Header Content with basic header HTML
  let headerContent = `<header class="header">
    <div class="logo-section">
      <img src="../assets/images/logo/logo.png" alt="Hospital CRM Logo" class="logo-img">
      <span class="logo-title">Hospital CMS</span>
    </div>
    <nav>`;

  // Step 6: Handle Session Expiry or Invalid Login
  if ((role === "loggedPatient" || role === "admin" || role === "doctor") && !token) {
    localStorage.removeItem("userRole");
    alert("Session expired or invalid login. Please log in again.");
    window.location.href = "/";
    return;
  }

  // Step 7: Add Role-Specific Header Content
  if (role === "admin") {
    headerContent += `
      <button id="addDocBtn" class="adminBtn" onclick="openModal('addDoctor')">Add Doctor</button>
      <a href="#" onclick="logout()">Logout</a>`;
  } else if (role === "doctor") {
    headerContent += `
      <button class="adminBtn" onclick="selectRole('doctor')">Home</button>
      <a href="#" onclick="logout()">Logout</a>`;
  } else if (role === "patient") {
    headerContent += `
      <button id="patientLogin" class="adminBtn">Login</button>
      <button id="patientSignup" class="adminBtn">Sign Up</button>`;
  } else if (role === "loggedPatient") {
    headerContent += `
      <button id="home" class="adminBtn" onclick="window.location.href='/pages/loggedPatientDashboard.html'">Home</button>
      <button id="patientAppointments" class="adminBtn" onclick="window.location.href='/pages/patientAppointments.html'">Appointments</button>
      <a href="#" onclick="logoutPatient()">Logout</a>`;
  }

  // Step 9: Close the Header Section
  headerContent += `
    </nav>
  </header>`;

  // Step 10: Render the Header Content
  headerDiv.innerHTML = headerContent;

  // Step 11: Attach Event Listeners to Header Buttons
  attachHeaderButtonListeners();
}

// Helper Function 1: attachHeaderButtonListeners
// Adds event listeners to login buttons for "Doctor" and "Admin" roles
function attachHeaderButtonListeners() {
  const patientLoginBtn = document.getElementById("patientLogin");
  const patientSignupBtn = document.getElementById("patientSignup");

  if (patientLoginBtn) {
    patientLoginBtn.addEventListener("click", function() {
      openModal("patientLogin");
    });
  }

  if (patientSignupBtn) {
    patientSignupBtn.addEventListener("click", function() {
      openModal("patientSignup");
    });
  }
}

// Helper Function 2: logout
// Removes user session data and redirects to the root page
function logout() {
  localStorage.removeItem("userRole");
  localStorage.removeItem("token");
  window.location.href = "/";
}

// Helper Function 3: logoutPatient
// Removes the patient's session token and redirects to the patient dashboard
function logoutPatient() {
  localStorage.removeItem("userRole");
  localStorage.removeItem("token");
  window.location.href = "/";
}

// Step 16: Call renderHeader on page load
document.addEventListener("DOMContentLoaded", renderHeader);
   
