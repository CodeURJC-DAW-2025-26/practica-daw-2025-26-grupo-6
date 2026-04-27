import { useNavigate } from "react-router";
import { useActionState } from "react";
import type { Route } from "./+types/events-edit";
import EventsForm from "../components/events-form";
import {
    getEvent,
    replaceEventImage,
    updateEvent,
    uploadEventImage,
} from "~/services/events-service";
import { ensureOwnerOrAdmin, requireLoggedUser } from "~/services/route-guards";

export async function clientLoader({ params }: Route.ClientLoaderArgs) {
    const eventId = Number(params.id);

    const [event, user] = await Promise.all([
        getEvent(eventId),
        requireLoggedUser(),
    ]);

    ensureOwnerOrAdmin(user, event.eventCreator?.userId);

    return { event };
}

export default function EventEdit({ loaderData }: Route.ComponentProps) {
    const { event } = loaderData;
    const navigate = useNavigate();

    async function editEventAction(
        prevState: { success: boolean; error: string | null } | null,
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


        // If the tag is empty or only whitespace, set it to a default value
        if (tag.trim() === "") {
            tag = "General";
        }

        try {
            await updateEvent(event.eventId, name, description, tag, requiresRegistration, registerLink, eventDate, maxParticipants);

            // Handle image updates preventing empty file uploads
            if (imageFile && imageFile.size > 0 && event.eventImage) {
                await replaceEventImage(event.eventImage.id, imageFile);
            } else if (imageFile && imageFile.size > 0 && !event.eventImage) {
                await uploadEventImage(event.eventId, imageFile);
            }

            navigate(`/new/events`);
            return { success: true, error: null };
        } catch (error: any) {
            console.error("Validation error caught:", error);

            // Logic to extract the real error message from the backend
            let errorMessage = "Hubo un error al editar el evento. Revisa los datos introducidos.";

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

    const [state, formAction, isPending] = useActionState(editEventAction, null);

    return (
        <EventsForm
            event={event}
            actionState={[state, formAction, isPending]}
            onCancel={() => navigate(`/new/events/${event.eventId}`)}
        />
    );
}