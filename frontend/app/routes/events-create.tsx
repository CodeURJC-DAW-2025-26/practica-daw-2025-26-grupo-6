import { useNavigate } from "react-router";
import { useActionState } from "react";
import type { Route } from "./+types/events-create";
import EventsForm from "../components/events-form";
import { createEvent, uploadEventImage } from "~/services/events-service";
import { requireLoggedUser } from "~/services/route-guards";

export async function clientLoader(_: Route.ClientLoaderArgs) {
    await requireLoggedUser();
    return null;
}

export default function EventSave({ }: Route.ComponentProps) {
    const navigate = useNavigate();

    async function saveEvent(
        prevState: {
            success: boolean;
            error: string | null;
        } | null,
        formData: FormData,
    ) {
        const name = formData.get("eventName") as string;
        const description = formData.get("eventDescription") as string;
        let tag = formData.get("eventTag") as string;
        const requiresRegistration = formData.get("requiresRegistration") === "true";
        const registerLink = formData.get("link") as string;
        const eventDate = formData.get("eventDate") as string;
        const maxParticipantsRaw = (formData.get("maxParticipants") as string | null)?.trim() ?? "";
        const maxParticipants = maxParticipantsRaw === "" ? null : Number(maxParticipantsRaw);
        const imageFile = formData.get("imageField") as File | null;


        // if the tag is empty or only whitespace, set it to a default value
        if (tag.trim() === "") {
            tag = "General";
        }

        try {
            const event = await createEvent(name, description, tag, requiresRegistration, registerLink, eventDate, maxParticipants);


            if (imageFile && imageFile.size > 0) {
                await uploadEventImage(event.eventId, imageFile);
            }

            navigate(`/new/events`);
            return { success: true, error: null };
        } catch (error: any) {
            console.error("Error de validación capturado:", error);


            let errorMessage = "No se ha podido guardar el evento. Revisa los datos introducidos.";

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

    const [state, formAction, isPending] = useActionState(saveEvent, null);

    return (
        <EventsForm
            actionState={[state, formAction, isPending]}
            onCancel={() => navigate("/new/events")}
        />
    );
}