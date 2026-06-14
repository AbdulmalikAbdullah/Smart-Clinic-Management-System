// Import required services and components
import { getAllAppointments } from './services/appointmentRecordService.js';
import { createPatientRow } from './components/patientRows.js';

// Get the table body where patient rows will be added
const appointmentTableBody = document.getElementById('appointmentTableBody');

// Initialize selectedDate with today's date in 'YYYY-MM-DD' format
const today = new Date();
const year = today.getFullYear();
const month = String(today.getMonth() + 1).padStart(2, '0');
const day = String(today.getDate()).padStart(2, '0');
let selectedDate = `${year}-${month}-${day}`;

// Get the saved token from localStorage
const token = localStorage.getItem('token');

// Initialize patientName to null (used for filtering by name)
let patientName = null;

// Add an 'input' event listener to the search bar
const searchBar = document.getElementById('patientSearch');
if (searchBar) {
  searchBar.addEventListener('input', function() {
    const trimmedValue = this.value.trim();
    if (trimmedValue !== '') {
      patientName = trimmedValue;
    } else {
      patientName = null;
    }
    loadAppointments();
  });
}

// Add a click listener to the "Today" button
const todayBtn = document.getElementById('todayBtn');
if (todayBtn) {
  todayBtn.addEventListener('click', function() {
    selectedDate = `${year}-${month}-${day}`;
    const datePicker = document.getElementById('datePicker');
    if (datePicker) {
      datePicker.value = selectedDate;
    }
    loadAppointments();
  });
}

// Add a change event listener to the date picker
const datePicker = document.getElementById('datePicker');
if (datePicker) {
  datePicker.addEventListener('change', function() {
    selectedDate = this.value;
    loadAppointments();
  });
}

/**
 * Function: loadAppointments
 * Purpose: Fetch and display appointments based on selected date and optional patient name
 */
async function loadAppointments() {
  try {
    // Step 1: Call getAllAppointments with selectedDate, patientName, and token
    const appointments = await getAllAppointments(selectedDate, patientName, token);
    
    // Step 2: Clear the table body content before rendering new rows
    appointmentTableBody.innerHTML = '';
    
    // Step 3: If no appointments are returned
    if (!appointments || appointments.length === 0) {
      const emptyRow = document.createElement('tr');
      emptyRow.innerHTML = '<td colspan="5">No Appointments found for today.</td>';
      appointmentTableBody.appendChild(emptyRow);
    } else {
      // Step 4: If appointments exist
      appointments.forEach(appointment => {
        // Construct a 'patient' object with id, name, phone, and email
        const patient = {
          id: appointment.patientId,
          name: appointment.patientName,
          phone: appointment.patientPhone,
          email: appointment.patientEmail
        };
        
        // Call createPatientRow to generate a table row
        const row = createPatientRow(patient, appointment.id, appointment.doctorId);
        
        // Append each row to the table body
        appointmentTableBody.appendChild(row);
      });
    }
  } catch (error) {
    // Step 5: Catch and handle any errors during fetch
    console.error('Error loading appointments:', error);
    const errorRow = document.createElement('tr');
    errorRow.innerHTML = '<td colspan="5">Error loading appointments. Try again later.</td>';
    appointmentTableBody.innerHTML = '';
    appointmentTableBody.appendChild(errorRow);
  }
}

// When the page is fully loaded (DOMContentLoaded)
document.addEventListener('DOMContentLoaded', function() {
  // Call renderContent() to set up the UI layout
  if (typeof renderContent === 'function') {
    renderContent();
  }
  
  // Call loadAppointments() to display today's appointments by default
  loadAppointments();
});
