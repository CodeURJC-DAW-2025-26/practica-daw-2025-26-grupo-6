import { useNavigate } from "react-router";
import { useActionState } from "react";
import type { Route } from "./+types/games-edit";
import GamesForm from "../components/games-form";
import {
    getGame,
    replaceGameImage,
    updateGame,
    uploadGameImage,
} from "~/services/games-service";
import { requireAdminUser } from "~/services/route-guards";

export async function clientLoader({ params }: Route.ClientLoaderArgs) {
    const [game] = await Promise.all([
        getGame(Number(params.id!)),
        requireAdminUser(),
    ]);

    return { game };
}

export default function GameEdit({ loaderData }: Route.ComponentProps) {
    const { game } = loaderData;
    const navigate = useNavigate();

    async function editGameAction(
        prevState: { success: boolean; error: string | null } | null,
        formData: FormData,
    ) {
        const id = Number(formData.get("gameId"));
        const name = formData.get("gameName") as string;
        const description = formData.get("gameDescription") as string;
        const minPlayers = Number(formData.get("minPlayers"));
        const maxPlayers = Number(formData.get("maxPlayers"));
        const minDuration = Number(formData.get("minDuration"));
        const maxDuration = Number(formData.get("maxDuration"));
        const genre = formData.get("genre") as string;
        const imageFile = formData.get("imageField") as File | null;

        // Validate logical constraints for players and duration
        if (minPlayers > maxPlayers) {
            return {
                success: false,
                error: "El número mínimo de jugadores no puede ser superior al máximo.",
            };
        }

        if (minDuration > maxDuration) {
            return {
                success: false,
                error: "La duración mínima no puede ser superior a la máxima.",
            };
        }

        try {
            await updateGame(id, name, description, minPlayers, maxPlayers, minDuration, maxDuration, genre);

            // Handle image updates
            if (imageFile && imageFile.size > 0 && game.gameImage) {
                await replaceGameImage(game.gameImage.id, imageFile);
            } else if (imageFile && imageFile.size > 0 && !game.gameImage) {
                await uploadGameImage(Number(id), imageFile);
            }

            navigate(`/games/${id}`);
            return { success: true, error: null };
        } catch (error: any) {
            console.error("Validation error caught:", error);

            // Logic to extract the real error message from the backend
            let errorMessage = "Hubo un error al editar el juego. Revisa los datos introducidos.";

            if (Array.isArray(error)) {
                errorMessage = error.join(" | ");
            } else if (error?.response?.data?.errors) {
                errorMessage = error.response.data.errors.join(" | ");
            } else if (error?.response?.data?.message) {
                errorMessage = error.response.data.message;
            } else if (error instanceof Error) {
                errorMessage = error.message;
            } else if (typeof error === "string") {
                errorMessage = error;
            }

            return {
                success: false,
                error: errorMessage,
            };
        }
    }

    const [state, formAction, isPending] = useActionState(editGameAction, null);

    return (
        <GamesForm
            game={game}
            actionState={[state, formAction, isPending]}
            onCancel={() => navigate(`/games/${game.gameId}`)}
        />
    );
}