import { type RouteConfig, index, layout, route } from "@react-router/dev/routes";

export default [
  layout("routes/home.tsx", [
    index("routes/index.tsx"),
    route("/new/news", "routes/news.tsx"),
    route("/new/news/:id", "routes/news-detail.tsx"),
    route("/new/news-create", "routes/news-create.tsx"),
    route("/new/news-edit/:id", "routes/news-edit.tsx"),
  ]),
] satisfies RouteConfig;
