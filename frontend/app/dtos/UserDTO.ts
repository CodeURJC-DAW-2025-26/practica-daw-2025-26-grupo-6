import type EventBasicDTO from "./EventBasicDTO";
import type GameDTO from "./GameDTO";
import type ImageDTO from "./ImageDTO";
import type NewBasicDTO from "./NewBasicDTO";

export default interface UserBasicDTO {
    userId: number,
    userName: string,
    userSurname: string,
    userNickname: string,
    userInterests: string,
    userEmail: string,
    userImage: ImageDTO,
    userRoles: Array<String>,
    userRegisteredEvents: Array<EventBasicDTO>,
    userOwnEvents: Array<EventBasicDTO>,
    userFavGames: Array<GameDTO>,
    userNews: Array<NewBasicDTO>
}