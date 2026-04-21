import { create } from "zustand";

import type UserDTO from "~/dtos/UserDTO";
import { HttpError, logIn, logOut, reqIsLogged } from "~/services/login-service";

interface UserState {
    user: UserDTO | null;
    errorMessage: string | null;
    loadLoggedUser: () => Promise<void>;
    loginUser: (username: string, password: string) => Promise<void>;
    logoutUser: () => Promise<void>;
}

export const useUserStore = create<UserState>((set, get) => ({
    user: null,
    errorMessage: null,

    loadLoggedUser: async () => {
        set({ user: null, errorMessage: null });

        try {
            const user = await reqIsLogged();
            set({ user });
        } catch (error) {
            if (error instanceof HttpError && error.status === 401) {
                set({ user: null, errorMessage: null });
                return;
            }

            console.log(error);
            set({ errorMessage: "Error al cargar al usuario" });
        }
    },

    loginUser: async (username: string, password: string) => {
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
        set({ user: null, errorMessage: null });

        try {
            await logOut();
        } catch (error) {
            console.log(error);
            set({ errorMessage: "Cierre de sesión fallido. Inténtalo de nuevo." });
        }
    },
}));