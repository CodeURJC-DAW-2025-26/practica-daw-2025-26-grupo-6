import type NewBasicDTO from "./NewBasicDTO";
import type ImageDTO from "./ImageDTO";
import type UserBasicDTO from "./UserBasicDTO";

export default interface EventDTO {
    eventId: number;
    eventName: string;
    eventDescription: string;
    eventImage: ImageDTO;
    eventTag: string;
    requiresRegistration: boolean;
    link: string;
    eventCreator: UserBasicDTO;
    eventNews: NewBasicDTO[];
    creationDate: string;
    eventDate: string;
    maxParticipants: number;
    validated: boolean;
    participantCount?: number;
    participants?: Array<UserBasicDTO>;
}