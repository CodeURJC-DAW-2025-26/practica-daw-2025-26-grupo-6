import { useNavigate } from "react-router";
import { useActionState } from "react";
import type { Route } from "./+types/news-edit";
import NewsForm from "../components/news-form";
import {
    getNew,
    replaceNewImage,
    updateNew,
    uploadNewImage,
} from "~/services/news-service";
import { ensureOwnerOrAdmin, requireLoggedUser } from "~/services/route-guards";

export async function clientLoader({ params }: Route.ClientLoaderArgs) {
    const newsId = Number(params.id);
    const [post, user] = await Promise.all([
        getNew(newsId),
        requireLoggedUser(),
    ]);

    ensureOwnerOrAdmin(user, post.newCreator?.userId);

    return { post };
}

export default function NewEdit({ loaderData }: Route.ComponentProps) {
    const { post } = loaderData;
    const navigate = useNavigate();

    async function editPostAction(
        prevState: { success: boolean; error: string | null } | null,
        formData: FormData,
    ) {
        const name = formData.get("newName") as string;

        // Extract tag and assign a default value if left blank to bypass strict backend validation
        const rawTag = formData.get("newTag") as string;
        const tag = rawTag.trim() === "" ? "General" : rawTag.trim();

        const description = formData.get("newDescription") as string;
        const imageFile = formData.get("imageField") as File | null;

        try {
            await updateNew(post.newId, name, tag, description);

            // Handle image updates preventing empty file uploads
            if (imageFile && imageFile.size > 0 && post.newImage) {
                await replaceNewImage(post.newImage.id, imageFile);
            } else if (imageFile && imageFile.size > 0 && !post.newImage) {
                await uploadNewImage(post.newId, imageFile);
            }

            navigate(`/news`);
            return { success: true, error: null };
        } catch (error: any) {
            console.error("Validation error caught:", error);

            // Logic to extract the real error message from the backend
            let errorMessage = "Hubo un error al editar la noticia. Revisa los datos introducidos.";

            if (Array.isArray(error)) {
                errorMessage = error.join(" | ");
            } else if (error?.response?.data?.errors) {
                errorMessage = error.response.data.errors.join(" | ");
            } else if (error?.response?.data?.message) {
                errorMessage = error.response.data.message;
            } else if (error instanceof Error) {
                errorMessage = error.message;
            } else if (typeof error === "string") {
                errorMessage = error;
            }

            return {
                success: false,
                error: errorMessage,
            };
        }
    }

    const [state, formAction, isPending] = useActionState(editPostAction, null);

    return (
        <NewsForm
            post={post}
            actionState={[state, formAction, isPending]}
            onCancel={() => navigate(`/news/${post.newId}`)}
        />
    );
}