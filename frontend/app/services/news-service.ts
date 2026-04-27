import type NewDTO from "~/dtos/NewDTO";

const API_URL = "/api/v1/news";
const API_IMAGES_URL = "/api/v1/images";
const PAGE_SIZE = 10;

export type NewsPageResult = {
    items: NewDTO[];
    hasNext: boolean;
};

export async function getNews(name: string, tag: string, page: number,): Promise<NewsPageResult> {
    const res = await fetch(`${API_URL}/?page=${page}&size=${PAGE_SIZE}&name=${encodeURIComponent(name)}&tag=${encodeURIComponent(tag)}`);
    if (!res.ok) {
        throw new Error("Error fetching news");
    }

    const data = await res.json();

    if (Array.isArray(data?.content)) {
        return { items: data.content, hasNext: data.page.number < data.page.totalPages - 1 };
    }

    return { items: [], hasNext: false };
}

export async function getNew(id: number): Promise<NewDTO> {
    const res = await fetch(`${API_URL}/${id}`);
    if (!res.ok) {
        throw new Error("Post not found");
    }
    return await res.json();
}

export async function createNew(name: string, tag: string, description: string): Promise<NewDTO> {
    const response = await fetch(`${API_URL}/`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
            newName: name,
            newDescription: description,
            newTag: tag,
            validated: false,
        }),
    });

    if (!response.ok) {
        throw new Error("Error al crear el post");
    }

    return await response.json();
}

export async function uploadNewImage(id: number, imageFile: File): Promise<void> {
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

export async function removeNew(id: number): Promise<void> {
    const response = await fetch(`${API_URL}/${id}`, {
        method: "DELETE",
    });

    if (!response.ok) {
        throw new Error("Error removing post");
    }
}

export async function deleteNewImage(newId: number, imageId: number): Promise<void> {
    const response = await fetch(`${API_URL}/${newId}/images/${imageId}`, {
        method: "DELETE",
    });

    if (!response.ok) {
        throw new Error("Error deleting image");
    }
}

export async function updateNew(id: number, name: string, tag: string, description: string): Promise<NewDTO> {
    const response = await fetch(`${API_URL}/${id}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
            newName: name,
            newTag: tag,
            newDescription: description,
            validated: false,
        }),
    });

    if (!response.ok) {
        throw new Error("Error updating post");
    }

    return await response.json();
}

export async function replaceNewImage(imageId: number, imageFile: File): Promise<void> {
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