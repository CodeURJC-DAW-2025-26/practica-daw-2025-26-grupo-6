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
        const tag = formData.get("eventTag") as string;
        const requiresRegistration = formData.get("requiresRegistration") === "true";
        const registerLink = formData.get("link") as string;
        const eventDate = formData.get("eventDate") as string;
        const maxParticipantsRaw = (formData.get("maxParticipants") as string | null)?.trim() ?? "";
        const maxParticipants = maxParticipantsRaw === "" ? null : Number(maxParticipantsRaw);
        const imageFile = formData.get("imageField") as File | null;

        try {
            const event = await createEvent(name, description, tag, requiresRegistration, registerLink, eventDate, maxParticipants);

            if (imageFile) {
                await uploadEventImage(event.eventId, imageFile);
            }

            navigate(`/new/events`);
            return { success: true, error: null };
        } catch (error) {
            console.error(error);
            return {
                success: false,
                error: "No se ha podido guardar el evento",
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
