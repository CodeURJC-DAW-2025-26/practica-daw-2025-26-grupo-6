import type ImageDTO from "./ImageDTO";

export default interface EventBasicDTO {
  eventId: number;
  eventName: string;
  eventImage?: ImageDTO;
}