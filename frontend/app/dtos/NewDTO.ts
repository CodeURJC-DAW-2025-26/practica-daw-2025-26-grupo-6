import type EventBasicDTO from "./EventBasicDTO";
import type ImageDTO from "./ImageDTO";
import type UserBasicDTO from "./UserBasicDTO";

export default interface NewDTO {
  newId: number;
  newName: string;
  newDescription: string;
  newImage: ImageDTO;
  newTag: string;
  newEvents: EventBasicDTO[];
  newCreator: UserBasicDTO;
  validated: boolean;
  creationDate: string;
}