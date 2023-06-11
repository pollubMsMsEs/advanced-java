import axios from "axios";

const axiosClient = axios.create({
    baseURL: `http://localhost:80/api`,
});

axiosClient.interceptors.request.use((config) => {
    const token = localStorage.getItem("JWT_TOKEN");
    config.headers.Authorization = `Bearer ${token}`;
    return config;
});

axiosClient.interceptors.response.use(
    (response) => {
        return response;
    },
    (error) => {
        const { response } = error;
        if (response.status === 401) {
            localStorage.removeItem("JWT_TOKEN");
            location.reload();
        }

        throw error;
    }
);

export default axiosClient;
