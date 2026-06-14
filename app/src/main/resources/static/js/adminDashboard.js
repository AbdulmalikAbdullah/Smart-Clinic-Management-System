// Import required services and components
import { getDoctors, filterDoctors, saveDoctor } from './services/doctorServices.js';
import { createDoctorCard } from './components/doctorCard.js';
import './components/modals.js';

// Initialize variables
const contentDiv = document.getElementById('content');

// Attach a click listener to the "Add Doctor" button
const addDoctorBtn = document.getElementById('addDocBtn');
if (addDoctorBtn) {
  addDoctorBtn.addEventListener('click', function() {
    openModal('addDoctor');
  });
}

// When the DOM is fully loaded, load all doctor cards
document.addEventListener('DOMContentLoaded', function() {
  loadDoctorCards();
  
  // Attach event listeners to search and filter inputs
  const searchBar = document.getElementById('doctorSearch');
  const timeFilter = document.getElementById('timeFilter');
  const specialtyFilter = document.getElementById('specialtyFilter');
  
  if (searchBar) {
    searchBar.addEventListener('input', filterDoctorsOnChange);
  }
  if (timeFilter) {
    timeFilter.addEventListener('change', filterDoctorsOnChange);
  }
  if (specialtyFilter) {
    specialtyFilter.addEventListener('change', filterDoctorsOnChange);
  }
});

/**
 * Function: loadDoctorCards
 * Purpose: Fetch all doctors and display them as cards
 */
async function loadDoctorCards() {
  try {
    // Call getDoctors() from the service layer
    const doctors = await getDoctors();
    
    // Clear the current content area
    contentDiv.innerHTML = '';
    
    // For each doctor returned, create and append a card
    doctors.forEach(doctor => {
      const card = createDoctorCard(doctor);
      contentDiv.appendChild(card);
    });
  } catch (error) {
    console.error('Error loading doctor cards:', error);
  }
}

/**
 * Function: filterDoctorsOnChange
 * Purpose: Filter doctors based on name, available time, and specialty
 */
async function filterDoctorsOnChange() {
  try {
    // Read values from the search bar and filters
    const searchBar = document.getElementById('doctorSearch');
    const timeFilter = document.getElementById('timeFilter');
    const specialtyFilter = document.getElementById('specialtyFilter');
    
    let name = searchBar ? searchBar.value.trim() : '';
    let time = timeFilter ? timeFilter.value : '';
    let specialty = specialtyFilter ? specialtyFilter.value : '';
    
    // Normalize empty values to null
    name = name || null;
    time = time || null;
    specialty = specialty || null;
    
    // Call filterDoctors from the service
    const result = await filterDoctors(name, time, specialty);
    const doctors = result.doctors || [];
    
    // If doctors are found, render them
    if (doctors.length > 0) {
      renderDoctorCards(doctors);
    } else {
      // If no doctors match the filter, show a message
      contentDiv.innerHTML = '<p>No doctors found with the given filters.</p>';
    }
  } catch (error) {
    console.error('Error filtering doctors:', error);
    alert('Error filtering doctors. Please try again.');
  }
}

/**
 * Function: renderDoctorCards
 * Purpose: A helper function to render a list of doctors passed to it
 */
function renderDoctorCards(doctors) {
  // Clear the content area
  contentDiv.innerHTML = '';
  
  // Loop through the doctors and append each card to the content area
  doctors.forEach(doctor => {
    const card = createDoctorCard(doctor);
    contentDiv.appendChild(card);
  });
}

/**
 * Function: adminAddDoctor
 * Purpose: Collect form data and add a new doctor to the system
 */
window.adminAddDoctor = async function() {
  try {
    // Collect input values from the modal form
    const name = document.getElementById('doctorName').value;
    const email = document.getElementById('doctorEmail').value;
    const phone = document.getElementById('doctorPhone').value;
    const password = document.getElementById('doctorPassword').value;
    const specialty = document.getElementById('specialization').value;
    const availableTimes = Array.from(document.querySelectorAll('input[name="availability"]:checked'))
      .map(checkbox => checkbox.value);
    
    // Retrieve the authentication token from localStorage
    const token = localStorage.getItem('token');
    if (!token) {
      alert('No authentication token found. Please log in again.');
      return;
    }
    
    // Build a doctor object with the form values
    const doctor = {
      name: name,
      email: email,
      phone: phone,
      password: password,
      specialty: specialty,
      availableTimes: availableTimes
    };
    
    // Call saveDoctor from the service
    const result = await saveDoctor(doctor, token);
    
    // If save is successful
    if (result.success) {
      alert('Doctor added successfully!');
      closeModal();
      loadDoctorCards(); // Reload the doctor list
    } else {
      alert('Error: ' + result.message);
    }
  } catch (error) {
    console.error('Error adding doctor:', error);
    alert('An error occurred while adding the doctor. Please try again.');
  }
};
