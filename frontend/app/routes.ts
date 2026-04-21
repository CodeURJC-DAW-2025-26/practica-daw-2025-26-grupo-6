import { type RouteConfig, index, layout, route } from "@react-router/dev/routes";

export default [
    layout("routes/home.tsx", [
        route("/new", "routes/index.tsx"),
        route("/new/news", "routes/news.tsx"),
        route("/new/news/:id", "routes/news-detail.tsx"),
        route("/new/login", "routes/login.tsx")
    ]),
] satisfies RouteConfig;
