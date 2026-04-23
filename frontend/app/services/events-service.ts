import type EventDTO from "~/dtos/EventDTO";

const API_URL = "/api/v1/events";
const API_IMAGES_URL = "/api/v1/images";
const PAGE_SIZE = 10;

export type EventsPageResult = {
    items: EventDTO[];
    hasNext: boolean;
};

export async function getEvents(name: string, tag: string, page: number,): Promise<EventsPageResult> {
    const res = await fetch(`${API_URL}?page=${page}&size=${PAGE_SIZE}&name=${encodeURIComponent(name)}&tag=${encodeURIComponent(tag)}`);
    if (!res.ok) {
        throw new Error("Error fetching events");
    }

    const data = await res.json();

    if (Array.isArray(data?.content)) {
        return { items: data.content, hasNext: data.page.number < data.page.totalPages - 1 };
    }

    return { items: [], hasNext: false };
}

export async function getEvent(id: number): Promise<EventDTO> {
    const res = await fetch(`${API_URL}/${id}`);
    if (!res.ok) {
        throw new Error("Event not found");
    }
    return await res.json();
}

export async function createEvent(name: string, description: string, tag: string, requiresRegistration: boolean, registerLink: string, eventDate: string, maxParticipants: number | null): Promise<EventDTO> {
    const response = await fetch(`${API_URL}`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
            eventName: name,
            eventDescription: description,
            eventTag: tag,
            requiresRegistration: requiresRegistration,
            link: registerLink,
            eventDate: eventDate,
            maxParticipants: maxParticipants,
            validated: false,
        }),
    });

    if (!response.ok) {
        throw new Error("Error adding event");
    }

    return await response.json();
}

export async function uploadEventImage(id: number, imageFile: File): Promise<void> {
    const formData = new FormData();
    formData.append("imageFile", imageFile);

    const response = await fetch(`${API_URL}/${id}/images`, {
        method: "POST",
        body: formData,
    });

    if (!response.ok) {
        throw new Error("Error uploading image");
    }
}

export async function removeEvent(id: number): Promise<void> {
    const response = await fetch(`${API_URL}/${id}`, {
        method: "DELETE",
    });

    if (!response.ok) {
        throw new Error("Error removing event");
    }
}

export async function deleteEventImage(eventId: number, imageId: number): Promise<void> {
    const response = await fetch(`${API_URL}/${eventId}/images/${imageId}`, {
        method: "DELETE",
    });

    if (!response.ok) {
        throw new Error("Error deleting image");
    }
}

export async function updateEvent(id: number, name: string, description: string, tag: string, requiresRegistration: boolean, registerLink: string, eventDate: string, maxParticipants: number | null): Promise<EventDTO> {
    const response = await fetch(`${API_URL}/${id}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
            eventName: name,
            eventDescription: description,
            eventTag: tag,
            requiresRegistration: requiresRegistration,
            link: registerLink,
            eventDate: eventDate,
            maxParticipants: maxParticipants,
            validated: false,
        }),
    });

    if (!response.ok) {
        throw new Error("Error updating event");
    }

    return await response.json();
}

export async function replaceEventImage(imageId: number, imageFile: File): Promise<void> {
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