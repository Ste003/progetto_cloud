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

export const loginWithGoogle = () => {
    // Reindirizza l'utente all'endpoint OAuth2 del backend per iniziare il login
    window.location.href = "http://localhost:8080/oauth2/authorization/google";
};

export const getUser = async () => {
    try {
        const response = await axios.get("http://localhost:8080/user", { withCredentials: true });
        return response.data;
    } catch (error) {
        console.error("Errore nel recupero dell'utente:", error);
        return null;
    }
};
