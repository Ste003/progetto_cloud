import axios from "axios";

const API_URL = "http://localhost:8080/api/todos"; // Cambia la porta se serve

export const fetchTodos = async () => {
    try {
        const response = await axios.get(API_URL);
        return response.data;
    } catch (error) {
        console.error("Errore nel recupero dei Todo:", error);
        return [];
    }
};

export const addTodo = async (title) => {
    try {
        const response = await axios.post(API_URL, { title, completed: false });
        return response.data;
    } catch (error) {
        console.error("Errore nell'aggiunta del Todo:", error);
        return null;
    }
};
