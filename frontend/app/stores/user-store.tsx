import { create } from "zustand";

import type UserDTO from "~/dtos/UserDTO";
import { HttpError, logIn, logOut, reqIsLogged } from "~/services/login-service";

interface UserState {
    user: UserDTO | null;
    errorMessage: string | null;
    lastImageChange: number;
    updateLastImageChange: () => void;
    loadLoggedUser: () => Promise<void>;
    loginUser: (username: string, password: string) => Promise<void>;
    logoutUser: () => Promise<void>;
}

export const useUserStore = create<UserState>((set, get) => ({
    user: null,
    errorMessage: null,
    lastImageChange: 0,

    updateLastImageChange: () => set({ lastImageChange: Date.now() }),

    loadLoggedUser: async () => {
        // Keep the previous user data in memory while we fetch the updated one.
        // This prevents the UI from flashing the "Login" button (Stale-While-Revalidate pattern).
        set({ errorMessage: null, lastImageChange: Date.now() });

        try {
            const user = await reqIsLogged();
            set({ user });
        } catch (error) {
            if (error instanceof HttpError && error.status === 401) {
                // Only clear the user if the backend explicitly says we are unauthorized.
                set({ user: null, errorMessage: null });
                return;
            }

            console.log(error);
            set({ errorMessage: "Error al cargar al usuario" });
        }
    },

    loginUser: async (username: string, password: string) => {
        // Clear current user state before attempting a new login.
        set({ user: null, errorMessage: null });

        try {
            await logIn(username, password);
            await get().loadLoggedUser();
        } catch (error) {
            const message = "Correo electrónico o contraseña incorrectos.";
            set({ errorMessage: message });
        }
    },

    logoutUser: async () => {
        // Optimistic UI update: instantly clear the user from the state for a snappy experience.
        set({ user: null, errorMessage: null });

        try {
            await logOut();
        } catch (error) {
            console.log(error);
            set({ errorMessage: "Cierre de sesión fallido. Inténtalo de nuevo." });
        }
    },
}));