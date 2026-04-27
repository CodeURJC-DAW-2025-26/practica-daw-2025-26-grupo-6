import { useNavigate } from "react-router";
import { useActionState } from "react";
import type { Route } from "./+types/games-create";
import GamesForm from "../components/games-form";
import { createGame, uploadGameImage } from "~/services/games-service";
import { requireAdminUser } from "~/services/route-guards";

export async function clientLoader(_: Route.ClientLoaderArgs) {
    await requireAdminUser();
    return null;
}

export default function GameSave({ }: Route.ComponentProps) {
    const navigate = useNavigate();

    async function saveGame(
        prevState: {
            success: boolean;
            error: string | null;
        } | null,
        formData: FormData,
    ) {
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
            const Game = await createGame(name, description, minPlayers, maxPlayers, minDuration, maxDuration, genre);

            // Prevent ghost uploads if there is no actual file
            if (imageFile && imageFile.size > 0) {
                await uploadGameImage(Game.gameId, imageFile);
            }

            navigate(`/new/games`);
            return { success: true, error: null };
        } catch (error: any) {
            console.error("Validation error caught:", error);

            // Logic to extract the real error message from the backend
            let errorMessage = "No se ha podido guardar el juego. Revisa los datos introducidos.";

            // If the service throws an array of errors directly
            if (Array.isArray(error)) {
                errorMessage = error.join(" | ");
            }
            // If it comes in Axios format from Spring Boot (@Valid)
            else if (error?.response?.data?.errors) {
                errorMessage = error.response.data.errors.join(" | ");
            }
            // If it's a single message from the backend
            else if (error?.response?.data?.message) {
                errorMessage = error.response.data.message;
            }
            // Fallbacks for standard JS errors
            else if (error instanceof Error) {
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

    const [state, formAction, isPending] = useActionState(saveGame, null);

    return (
        <GamesForm
            actionState={[state, formAction, isPending]}
            onCancel={() => navigate("/new/games")}
        />
    );
}