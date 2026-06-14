


// Import the base API URL from the config file
import { API_BASE_URL } from "../config/config.js";

// Define a constant DOCTOR_API to hold the full endpoint for doctor-related actions
const DOCTOR_API = `${API_BASE_URL}/api/doctors`;

/**
 * Function: getDoctors
 * Purpose: Fetch the list of all doctors from the API
 */
export async function getDoctors() {
  try {
    // Use fetch() to send a GET request to the DOCTOR_API endpoint
    const response = await fetch(DOCTOR_API);

    // Convert the response to JSON
    const data = await response.json();

    // Return the 'doctors' array from the response
    return data.doctors || [];
  } catch (error) {
    // If there's an error (e.g., network issue), log it and return an empty array
    console.error("Error fetching doctors:", error);
    return [];
  }
}

/**
 * Function: deleteDoctor
 * Purpose: Delete a specific doctor using their ID and an authentication token
 */
export async function deleteDoctor(doctorId, token) {
  try {
    // Use fetch() with the DELETE method
    // The URL includes the doctor ID and token as path parameters
    const response = await fetch(`${DOCTOR_API}/${doctorId}/${token}`, {
      method: "DELETE"
    });

    // Convert the response to JSON
    const data = await response.json();

    // Return an object with success status and message
    return {
      success: response.ok,
      message: data.message || "Doctor deleted successfully"
    };
  } catch (error) {
    // If an error occurs, log it and return a default failure response
    console.error("Error deleting doctor:", error);
    return {
      success: false,
      message: "Failed to delete doctor"
    };
  }
}

/**
 * Function: saveDoctor
 * Purpose: Save (create) a new doctor using a POST request
 */
export async function saveDoctor(doctorData, token) {
  try {
    // Use fetch() with the POST method
    // URL includes the token in the path
    const response = await fetch(`${DOCTOR_API}/${token}`, {
      method: "POST",
      // Set headers to specify JSON content type
      headers: {
        "Content-Type": "application/json"
      },
      // Convert the doctor object to JSON in the request body
      body: JSON.stringify(doctorData)
    });

    // Parse the JSON response and return
    const data = await response.json();
    return {
      success: response.ok,
      message: data.message || "Doctor saved successfully"
    };
  } catch (error) {
    // Catch and log errors
    console.error("Error saving doctor:", error);
    // Return a failure response if an error occurs
    return {
      success: false,
      message: "Failed to save doctor"
    };
  }
}

/**
 * Function: filterDoctors
 * Purpose: Fetch doctors based on filtering criteria (name, time, and specialty)
 */
export async function filterDoctors(name, time, specialty) {
  try {
    // Use fetch() with the GET method
    // Include the name, time, and specialty as URL path parameters
    const response = await fetch(`${DOCTOR_API}/filter/${name}/${time}/${specialty}`);

    // Check if the response is OK
    if (response.ok) {
      // If yes, parse and return the doctor data
      const data = await response.json();
      return data;
    } else {
      // If no, log the error and return an object with an empty 'doctors' array
      console.error("Error filtering doctors:", response.statusText);
      return { doctors: [] };
    }
  } catch (error) {
    // Catch any other errors, alert the user, and return a default empty result
    console.error("Error filtering doctors:", error);
    alert("Failed to filter doctors. Please try again.");
    return { doctors: [] };
  }
}
