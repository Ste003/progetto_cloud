import axios from "axios";

const API_URL = "http://localhost:8080/api/todos"; // Cambia la porta se serve

export const fetchTodos = async () => {
  try {
    const response = await axios.get(API_URL, { withCredentials: true });
    return response.data;
  } catch (error) {
    console.error("Errore nel recupero dei Todo:", error);
    return [];
  }
};

export const addTodo = async (title) => {
  try {
    const response = await axios.post(API_URL, { title, completed: false }, { withCredentials: true });
    return response.data;
  } catch (error) {
    console.error("Errore nell'aggiunta del Todo:", error);
    return null;
  }
};

export const loginWithGoogle = async () => {
  // Reindirizza l'utente all'endpoint OAuth2 del backend per iniziare il login
  window.location.href = "http://localhost:8080/oauth2/authorization/google";
  // Dopo il login, potrebbe essere necessario recuperare i dati dell'utente
  const user = await getUser();
  if (user) {
    window.location.href = "/dashboard";  // Reindirizza l'utente alla dashboard
  }
};


export const getUser = async () => {
  try {
    // Modifica la richiesta GET per ottenere i dettagli dell'utente autenticato
    const response = await axios.get("http://localhost:8080/user", { withCredentials: true });
    console.log("User data:", response.data);  // Log dei dati dell'utente
    if (response.data) {
      localStorage.setItem('user', JSON.stringify(response.data));  // Salva l'utente nel localStorage
    }
    return response.data;
  } catch (error) {
    if (error.response && error.response.status === 404) {
      console.log("Utente non autenticato.");
    } else {
      console.error("Errore nel recupero dell'utente:", error);
    }
    return null;
  }
};

