// Import the base API URL from the config file
import { API_BASE_URL } from "../config/config.js";
import "../components/modals.js";

// Define constants for the admin and doctor login API endpoints
const ADMIN_LOGIN_API = `${API_BASE_URL}/api/admin/login`;
const DOCTOR_LOGIN_API = `${API_BASE_URL}/api/doctor/login`;

// Define adminLoginHandler function on the global window object
window.adminLoginHandler = async function() {
  try {
    const username = document.getElementById("username").value;
    const password = document.getElementById("password").value;

    const admin = {
      username: username,
      password: password
    };

    const response = await fetch(ADMIN_LOGIN_API, {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(admin)
    });

    if (response.ok) {
      const data = await response.json();
      const token = data.token;

      localStorage.setItem("token", token);
      localStorage.setItem("userRole", "admin");

      closeModal();
      selectRole("admin");
    } else {
      alert("Invalid admin credentials. Please try again.");
    }
  } catch (error) {
    console.error("Admin login error:", error);
    alert("An error occurred during login. Please try again.");
  }
};

// Define doctorLoginHandler function on the global window object
window.doctorLoginHandler = async function() {
  try {
    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;

    const doctor = {
      email: email,
      password: password
    };

    const response = await fetch(DOCTOR_LOGIN_API, {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(doctor)
    });

    if (response.ok) {
      const data = await response.json();
      const token = data.token;

      localStorage.setItem("token", token);
      localStorage.setItem("userRole", "doctor");

      closeModal();
      selectRole("doctor");
    } else {
      alert("Invalid doctor credentials. Please try again.");
    }
  } catch (error) {
    console.error("Doctor login error:", error);
    alert("An error occurred during login. Please try again.");
  }
};
