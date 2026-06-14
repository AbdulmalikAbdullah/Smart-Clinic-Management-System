import { showBookingOverlay } from '../loggedPatient.js';
import { deleteDoctor } from '../services/doctorServices.js';
import { getPatientData } from '../services/patientServices.js';

export function createDoctorCard(doctor) {
  const card = document.createElement('div');
  card.className = 'doctor-card';

  const role = localStorage.getItem('userRole');

  const infoDiv = document.createElement('div');
  infoDiv.className = 'doctor-info';

  const nameEl = document.createElement('h3');
  nameEl.textContent = doctor.name;

  const specialtyEl = document.createElement('p');
  specialtyEl.textContent = `Specialty: ${doctor.specialty}`;

  const emailEl = document.createElement('p');
  emailEl.textContent = `Email: ${doctor.email}`;

  const timesEl = document.createElement('p');
  const times = doctor.availableTimes || [];
  timesEl.textContent = `Available: ${times.length ? times.join(', ') : 'N/A'}`;

  infoDiv.appendChild(nameEl);
  infoDiv.appendChild(specialtyEl);
  infoDiv.appendChild(emailEl);
  infoDiv.appendChild(timesEl);

  const actionsDiv = document.createElement('div');
  actionsDiv.className = 'card-actions';

  if (role === 'admin') {
    const deleteBtn = document.createElement('button');
    deleteBtn.className = 'dashboard-btn';
    deleteBtn.textContent = 'Delete';
    deleteBtn.addEventListener('click', async () => {
      const token = localStorage.getItem('token');
      const result = await deleteDoctor(doctor.id, token);
      if (result.success) {
        alert(result.message);
        card.remove();
      } else {
        alert(result.message);
      }
    });
    actionsDiv.appendChild(deleteBtn);
  } else if (role === 'patient') {
    const bookBtn = document.createElement('button');
    bookBtn.className = 'dashboard-btn';
    bookBtn.textContent = 'Book Now';
    bookBtn.addEventListener('click', () => {
      alert('Please log in to book an appointment.');
    });
    actionsDiv.appendChild(bookBtn);
  } else if (role === 'loggedPatient') {
    const bookBtn = document.createElement('button');
    bookBtn.className = 'dashboard-btn';
    bookBtn.textContent = 'Book Now';
    bookBtn.addEventListener('click', async (e) => {
      const token = localStorage.getItem('token');
      if (!token) {
        window.location.href = '/';
        return;
      }
      const patient = await getPatientData(token);
      if (patient) {
        showBookingOverlay(e, doctor, patient);
      }
    });
    actionsDiv.appendChild(bookBtn);
  }

  card.appendChild(infoDiv);
  card.appendChild(actionsDiv);
  return card;
}
