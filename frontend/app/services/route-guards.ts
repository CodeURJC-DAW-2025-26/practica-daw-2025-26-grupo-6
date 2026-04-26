import type UserDTO from "~/dtos/UserDTO";
import { reqIsLogged } from "~/services/login-service";

export async function requireLoggedUser(): Promise<UserDTO> {
    try {
        return await reqIsLogged();
    } catch (error) {
        throw new Response("Unauthorized", { status: 401 });
    }
}

export async function requireAdminUser(): Promise<UserDTO> {
    const user = await requireLoggedUser();
    const isAdmin = user.userRoles?.includes("ADMIN");

    if (!isAdmin) {
        throw new Response("Forbidden", { status: 403 });
    }

    return user;
}

export function ensureOwnerOrAdmin(user: UserDTO, ownerUserId?: number): void {
    const isAdmin = user.userRoles?.includes("ADMIN");
    const isOwner = ownerUserId !== undefined && ownerUserId === user.userId;

    if (!isAdmin && !isOwner) {
        throw new Response("Forbidden", { status: 403 });
    }
}
