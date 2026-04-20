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
