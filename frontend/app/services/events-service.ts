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
