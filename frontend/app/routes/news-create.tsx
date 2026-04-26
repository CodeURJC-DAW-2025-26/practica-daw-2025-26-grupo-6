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
    const tag = formData.get("newTag") as string;
    const imageFile = formData.get("imageField") as File | null;

    try {
      const newPost = await createNew(name, tag, description);

      if (imageFile) {
        await uploadNewImage(newPost.newId, imageFile);
      }

      navigate(`/new/news`);
      return { success: true, error: null };
    } catch (error) {
      console.error(error);
      return {
        success: false,
        error: "No se ha podido guardar la noticia",
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
