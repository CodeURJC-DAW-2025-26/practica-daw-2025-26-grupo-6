import type GameDTO from "~/dtos/GameDTO";

const API_URL = "/api/v1/games";
const API_IMAGES_URL = "/api/v1/images";
const PAGE_SIZE = 10;

export type GamesPageResult = {
  items: GameDTO[];
  hasNext: boolean;
};

export async function getGames(name: string, tag: string, players: number, duration: number, page: number): Promise<GamesPageResult> {
  const queryParams = new URLSearchParams({
    page: String(page),
    size: String(PAGE_SIZE),
    name,
    tag,
  });

  if (players > 0) {
    queryParams.set("players", String(players));
  }

  if (duration > 0) {
    queryParams.set("duration", String(duration));
  }

  const res = await fetch(`${API_URL}/?${queryParams.toString()}`);
  if (!res.ok) {
    throw new Error("Error fetching games");
  }

  const data = await res.json();

  if (Array.isArray(data?.content)) {
    return { items: data.content, hasNext: data.page.number < data.page.totalPages - 1 };
  }

  return { items: [], hasNext: false };
}

export async function getGame(id: number): Promise<GameDTO> {
  const res = await fetch(`${API_URL}/${id}`);
  if (!res.ok) {
    throw new Error("Game not found");
  }
  return await res.json();
}

export async function createGame(name: string, description: string, minPlayers: number, maxPlayers: number, minDuration: number, maxDuration: number, genre: string): Promise<GameDTO> {
  const response = await fetch(`${API_URL}/`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      gameName: name,
      gameDescription: description,
      minPlayers: minPlayers.toString(),
      maxPlayers: maxPlayers.toString(),
      minDuration: minDuration.toString(),
      maxDuration: maxDuration.toString(),
      genre: genre
    }),
  });

  if (!response.ok) {
    throw new Error("Error adding game");
  }

  return await response.json();
}

export async function uploadGameImage(id: number, imageFile: File): Promise<void> {
  const formData = new FormData();
  formData.append("imageFile", imageFile);

  const response = await fetch(`${API_URL}/${id}/images/`, {
    method: "POST",
    body: formData,
  });

  if (!response.ok) {
    throw new Error("Error uploading image");
  }
}

export async function removeGame(id: number): Promise<void> {
  const response = await fetch(`${API_URL}/${id}`, {
    method: "DELETE",
  });

  if (!response.ok) {
    throw new Error("Error removing game");
  }
}

export async function deleteGameImage(gameId: number, imageId: number): Promise<void> {
  const response = await fetch(`${API_URL}/${gameId}/images/${imageId}`, {
    method: "DELETE",
  });

  if (!response.ok) {
    throw new Error("Error deleting image");
  }
}

export async function updateGame(id: number, name: string, description: string, minPlayers: number, maxPlayers: number, minDuration: number, maxDuration: number, genre: string): Promise<GameDTO> {
  const response = await fetch(`${API_URL}/${id}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      gameName: name,
      gameDescription: description,
      minPlayers: minPlayers.toString(),
      maxPlayers: maxPlayers.toString(),
      minDuration: minDuration.toString(),
      maxDuration: maxDuration.toString(),
      genre: genre
    }),
  });

  if (!response.ok) {
    throw new Error("Error updating game");
  }

  return await response.json();
}

export async function replaceGameImage(imageId: number, imageFile: File): Promise<void> {
  const formData = new FormData();
  formData.append("imageFile", imageFile);

  const response = await fetch(`${API_IMAGES_URL}/${imageId}/media`, {
    method: "PUT",
    body: formData,
  });

  if (!response.ok) {
    throw new Error("Error replacing image");
  }
}

export async function putFav(gameId: number): Promise<void> {

  const response = await fetch(`${API_URL}/${gameId}/favourites`, {
    method: "POST",
  });

  if (!response.ok) {
    throw new Error("Error adding game to favourites");
  }
}

export async function removeFav(gameId: number): Promise<void> {

  const response = await fetch(`${API_URL}/${gameId}/favourites`, {
    method: "DELETE",
  });

  if (!response.ok) {
    throw new Error("Error removing game from favourites");
  }
}