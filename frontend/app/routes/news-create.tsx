import { useNavigate } from "react-router";
import { useActionState } from "react";
import type { Route } from "./+types/news-create";
import NewsForm from "../components/news-form";
import { createNew, uploadNewImage } from "~/services/news-service";
import { requireLoggedUser } from "~/services/route-guards";

export async function clientLoader(_: Route.ClientLoaderArgs) {
    await requireLoggedUser();
    return null;
}

export default function NewSave({ }: Route.ComponentProps) {
    const navigate = useNavigate();

    async function savePost(
        prevState: {
            success: boolean;
            error: string | null;
        } | null,
        formData: FormData,
    ) {
        const name = formData.get("newName") as string;
        const description = formData.get("newDescription") as string;

        // Extract tag and assign a default value if left blank to bypass strict backend validation
        const rawTag = formData.get("newTag") as string;
        const tag = rawTag.trim() === "" ? "General" : rawTag.trim();

        const imageFile = formData.get("imageField") as File | null;

        try {
            const newPost = await createNew(name, tag, description);

            // Prevent ghost uploads if there is no actual file selected
            if (imageFile && imageFile.size > 0) {
                await uploadNewImage(newPost.newId, imageFile);
            }

            navigate(`/new/news`);
            return { success: true, error: null };
        } catch (error: any) {
            console.error("Validation error caught:", error);

            // Logic to extract the real error message from the backend
            let errorMessage = "No se ha podido guardar la noticia. Revisa los datos introducidos.";

            // If the service throws an array of errors directly
            if (Array.isArray(error)) {
                errorMessage = error.join(" | ");
            }
            // If it comes in Axios format from Spring Boot (@Valid)
            else if (error?.response?.data?.errors) {
                errorMessage = error.response.data.errors.join(" | ");
            }
            // If it's a single message from the backend
            else if (error?.response?.data?.message) {
                errorMessage = error.response.data.message;
            }
            // Fallbacks for standard JS errors
            else if (error instanceof Error) {
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

    const [state, formAction, isPending] = useActionState(savePost, null);

    return (
        <NewsForm
            actionState={[state, formAction, isPending]}
            onCancel={() => navigate("/new/news")}
        />
    );
}