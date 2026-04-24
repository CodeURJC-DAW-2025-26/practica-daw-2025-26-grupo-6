import type NewBasicDTO from "~/dtos/NewBasicDTO";
import type EventBasicDTO from "~/dtos/EventBasicDTO";
import type UserDTO from "~/dtos/UserDTO";

const ADMIN_API_URL = "/api/v1/admin";
const USERS_API_URL = "/api/v1/users";

export interface PendingContent {
    news: NewBasicDTO[];
    events: EventBasicDTO[];
}

export async function getPendingContent(): Promise<PendingContent> {
    const res = await fetch(`${ADMIN_API_URL}/pending`);
    if (!res.ok) throw new Error("Error cargando contenido pendiente");
    return await res.json();
}

export async function approveNews(id: number): Promise<boolean> {
    const res = await fetch(`${ADMIN_API_URL}/news/${id}`, { method: "PUT" });
    return res.ok;
}

export async function rejectNews(id: number): Promise<boolean> {
    const res = await fetch(`${ADMIN_API_URL}/news/${id}`, { method: "DELETE" });
    return res.ok;
}

export async function approveEvent(id: number): Promise<boolean> {
    const res = await fetch(`${ADMIN_API_URL}/events/${id}`, { method: "PUT" });
    return res.ok;
}

export async function rejectEvent(id: number): Promise<boolean> {
    const res = await fetch(`${ADMIN_API_URL}/events/${id}`, { method: "DELETE" });
    return res.ok;
}

export async function getTopGames() {
    const res = await fetch(`${ADMIN_API_URL}/top-favorite-games`);
    return await res.json();
}

export async function getTopEvents() {
    const res = await fetch(`${ADMIN_API_URL}/top-events-participants`);
    return await res.json();
}

export async function getAllUsers(): Promise<UserDTO[]> {
    const res = await fetch(`${USERS_API_URL}/`);
    if (!res.ok) throw new Error("Error al obtener usuarios");
    const data = await res.json();
    return data.content || [];
}