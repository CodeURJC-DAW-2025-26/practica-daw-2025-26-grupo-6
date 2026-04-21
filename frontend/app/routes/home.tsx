import { Outlet } from "react-router";
import Header from "../components/header";
import Footer from "../components/footer";

export default function Home() {
  return (
    <><Header />
      <div>
        < Outlet />
      </div>
      <Footer />
    </>
  );
}
