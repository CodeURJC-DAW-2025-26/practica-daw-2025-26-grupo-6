import { type RouteConfig, index, layout, route } from "@react-router/dev/routes";

export default [
    layout("routes/home.tsx", [
        route("/new", "routes/index.tsx"),
        route("/new/error", "routes/error.tsx"),
        route("/new/login", "routes/login.tsx"),
        route("/new/register", "routes/register.tsx"),
        route("/new/news", "routes/news.tsx"),
        route("/new/news/:id", "routes/news-detail.tsx"),
        route("/new/news-create", "routes/news-create.tsx"),
        route("/new/news-edit/:id", "routes/news-edit.tsx"),
        route("/new/games", "routes/games.tsx"),
        route("/new/games/:id", "routes/games-detail.tsx"),
        route("/new/games-create", "routes/games-create.tsx"),
        route("/new/games-edit/:id", "routes/games-edit.tsx"),
        route("/new/events", "routes/events.tsx"),
        route("/new/events/:id", "routes/events-detail.tsx"),
        route("/new/events-create", "routes/events-create.tsx"),
        route("/new/events-edit/:id", "routes/events-edit.tsx"),
        route("/new/admin", "routes/admin.tsx"),
    ]),
] satisfies RouteConfig;
