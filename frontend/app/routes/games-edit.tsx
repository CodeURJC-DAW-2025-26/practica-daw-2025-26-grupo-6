import { useNavigate } from "react-router";
import { useActionState } from "react";
import type { Route } from "./+types/games-edit";
import GamesForm from "../components/games-form";
import {
  deleteGameImage,
  getGame,
  replaceGameImage,
  updateGame,
  uploadGameImage,
} from "~/services/games-service";

export async function clientLoader({ params }: Route.ClientLoaderArgs) {
  const game = await getGame(Number(params.id!));
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

    try {
      await updateGame(id, name, description, minPlayers, maxPlayers, minDuration, maxDuration, genre);

      if (imageFile && imageFile.size > 0 && game.gameImage) {
        await replaceGameImage(game.gameImage.id, imageFile);
      } else if (imageFile && imageFile.size > 0 && !game.gameImage) {
        await uploadGameImage(Number(id), imageFile);
      }

      navigate(`/new/games/${id}`);
      return { success: true, error: null };
    } catch (error) {
      console.error(error);
      return {
        success: false,
        error: "Hubo un error al editar el juego",
      };
    }
  }

  const [state, formAction, isPending] = useActionState(editGameAction, null);

  return (
    <GamesForm
      game={game}
      actionState={[state, formAction, isPending]}
      onCancel={() => navigate(`/new/games/${game.gameId}`)}
    />
  );
}
