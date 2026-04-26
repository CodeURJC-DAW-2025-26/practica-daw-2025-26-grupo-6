import type UserDTO from "~/dtos/UserDTO";
import type ImageDTO from "~/dtos/ImageDTO";

const USERS_API_URL = "/api/v1/users";

export async function register(userEmail: string, password: string, confirmPassword: string, userName: string, userSurname: string, userNickname: string): Promise<string[]> {

    const response = await fetch(`${USERS_API_URL}/`, {
        method: 'POST',
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({
            userEmail,
            password,
            confirmPassword,
            userName,
            userSurname,
            userNickname
        })
    });

    if (!response.ok) {
        if (response.status === 400) {
            const errorBody = await response.json();
            return errorBody.errors;
        }
        throw new Error("Error al registrar el usuario");
    }

    return [];
}

export async function getUser(id: number): Promise<UserDTO> {
    const res = await fetch(`${USERS_API_URL}/${id}`);
    if (!res.ok) throw new Error("Error al obtener el usuario");
    return await res.json();
}

export async function updateProfile(id: number, userEmail: string, password: string, confirmPassword: string, userName: string, userSurname: string, userNickname: string, userInterests: string): Promise<string[]> {
    const response = await fetch(`${USERS_API_URL}/${id}`, {
        method: 'PUT',
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({
            userEmail,
            password,
            confirmPassword,
            userName,
            userSurname,
            userNickname,
            userInterests
        })
    });

    if (!response.ok) {
        if (response.status === 400) {
            const errorBody = await response.json();
            return errorBody.errors;
        }
        throw new Error("Error al actualizar el usuario");
    }

    return [];
}

export async function deleteProfile(id: number): Promise<void> {
    const res = await fetch(`${USERS_API_URL}/${id}`, {
        method: 'DELETE'
    });
    if (!res.ok) throw new Error("Error al eliminar el usuario");
}

export async function getUserImage(id: number): Promise<Blob> {
    const res = await fetch(`${USERS_API_URL}/${id}/image`);
    if (!res.ok) throw new Error("Error al obtener la imagen del usuario");
    return await res.blob();
}

export async function updateUserImage(id: number, image: File): Promise<ImageDTO> {
    const formData = new FormData();
    formData.append('userImage', image);

    const res = await fetch(`${USERS_API_URL}/${id}/image`, {
        method: 'PUT',
        body: formData
    });
    if (!res.ok) throw new Error("Error al actualizar la imagen del usuario");
    return await res.json();
}
