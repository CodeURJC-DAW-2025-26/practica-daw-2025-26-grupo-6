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