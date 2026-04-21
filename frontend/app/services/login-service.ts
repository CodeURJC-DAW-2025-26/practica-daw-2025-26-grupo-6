import type UserDTO from "~/dtos/UserDTO";

const API_USERS_URL = "/api/v1/users";
const API_AUTH_URL = "/api/v1/auth";

export class HttpError extends Error {
    status: number;

    constructor(status: number, message?: string) {
        super(message ?? `HTTP ${status}`);
        this.status = status;
    }
}

export async function reqIsLogged(): Promise<UserDTO> {
    const res = await fetch(`${API_USERS_URL}/me`);

    if (!res.ok) {
        throw new HttpError(res.status);
    }

    return await res.json();
}

export async function logIn(user: string, pass: string): Promise<void> {
    const res = await fetch(`${API_AUTH_URL}/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username: user, password: pass }),
    });

    if (!res.ok) {
        throw new Error();
    }
}

export async function logOut(): Promise<void> {
    const res = await fetch(`${API_AUTH_URL}/logout`, {
        method: "POST",
    });

    if (!res.ok) {
        throw new Error();
    }
}