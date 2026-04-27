import { type RouteConfig, index, layout, route } from "@react-router/dev/routes";

export default [
    layout("routes/home.tsx", [
        route("/", "routes/index.tsx"),
        route("/error", "routes/error.tsx"),
        route("/login", "routes/login.tsx"),
        route("/register", "routes/register.tsx"),
        route("/news", "routes/news.tsx"),
        route("/news/:id", "routes/news-detail.tsx"),
        route("/news-create", "routes/news-create.tsx"),
        route("/news-edit/:id", "routes/news-edit.tsx"),
        route("/games", "routes/games.tsx"),
        route("/games/:id", "routes/games-detail.tsx"),
        route("/games-create", "routes/games-create.tsx"),
        route("/games-edit/:id", "routes/games-edit.tsx"),
        route("/events", "routes/events.tsx"),
        route("/events/:id", "routes/events-detail.tsx"),
        route("/events-create", "routes/events-create.tsx"),
        route("/events-edit/:id", "routes/events-edit.tsx"),
        route("/admin", "routes/admin.tsx"),
        route("/users/:id", "routes/profile.tsx"),
    ]),
] satisfies RouteConfig;
