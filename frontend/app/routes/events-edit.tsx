import { useNavigate } from "react-router";
import { useActionState } from "react";
import type { Route } from "./+types/events-edit";
import EventsForm from "../components/events-form";
import {
    deleteEventImage,
    getEvent,
    replaceEventImage,
    updateEvent,
    uploadEventImage,
} from "~/services/events-service";

export async function clientLoader({ params }: Route.ClientLoaderArgs) {
    const event = await getEvent(Number(params.id!));
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
        const tag = formData.get("eventTag") as string;
        const requiresRegistration = formData.get("requiresRegistration") === "true";
        const registerLink = formData.get("link") as string;
        const eventDate = formData.get("eventDate") as string;
        const maxParticipantsRaw = (formData.get("maxParticipants") as string | null)?.trim() ?? "";
        const maxParticipants = maxParticipantsRaw === "" ? null : Number(maxParticipantsRaw);
        const imageFile = formData.get("imageField") as File | null;

        try {
            await updateEvent(event.eventId, name, description, tag, requiresRegistration, registerLink, eventDate, maxParticipants);

            if (imageFile && imageFile.size > 0 && event.eventImage) {
                await replaceEventImage(event.eventImage.id, imageFile);
            } else if (imageFile && imageFile.size > 0 && !event.eventImage) {
                await uploadEventImage(event.eventId, imageFile);
            }

            navigate(`/new/events`);
            return { success: true, error: null };
        } catch (error) {
            console.error(error);
            return {
                success: false,
                error: "Hubo un error al editar el evento",
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
