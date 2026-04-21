import type ImageDTO from "./ImageDTO";

export default interface GameDTO {
    gameId: number,
    gameName: string,
    gameDescription: string,
    gameImage: ImageDTO,
    minPlayers: number,
    maxPlayers: number,
    minDuration: number,
    maxDuration: number,
    genre: string
}