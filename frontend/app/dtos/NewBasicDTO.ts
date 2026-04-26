import type ImageDTO from "./ImageDTO";

export default interface NewBasicDTO {
    newId: number;
    newName: string;
    newImage?: ImageDTO;
    validated: boolean;
}