import { useNavigate } from "react-router";
import { useActionState } from "react";
import type { Route } from "./+types/news-edit";
import NewsForm from "../components/news-form";
import {
    deleteNewImage,
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
        const tag = formData.get("newTag") as string;
        const description = formData.get("newDescription") as string;
        const imageFile = formData.get("imageField") as File | null;

        try {
            await updateNew(post.newId, name, tag, description);

            if (imageFile && imageFile.size > 0 && post.newImage) {
                await replaceNewImage(post.newImage.id, imageFile);
            } else if (imageFile && imageFile.size > 0 && !post.newImage) {
                await uploadNewImage(post.newId, imageFile);
            }

            navigate(`/new/news`);
            return { success: true, error: null };
        } catch (error) {
            console.error(error);
            return {
                success: false,
                error: "Hubo un error al editar la noticia",
            };
        }
    }

    const [state, formAction, isPending] = useActionState(editPostAction, null);

    return (
        <NewsForm
            post={post}
            actionState={[state, formAction, isPending]}
            onCancel={() => navigate(`/new/news/${post.newId}`)}
        />
    );
}
