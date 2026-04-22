import { useNavigate } from "react-router";
import { useActionState } from "react";
import type { Route } from "./+types/games-create";
import GamesForm from "../components/games-form";
import { createGame, uploadGameImage } from "~/services/games-service";

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

    try {
      const Game = await createGame(name, description, minPlayers, maxPlayers, minDuration, maxDuration, genre);

      if (imageFile) {
        await uploadGameImage(Game.gameId, imageFile);
      }

      navigate(`/new/games`);
      return { success: true, error: null };
    } catch (error) {
      console.error(error);
      return {
        success: false,
        error: "No se ha podido guardar el juego",
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
