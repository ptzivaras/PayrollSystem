import { Link } from "react-router-dom";

export default function NotFound() {
  return (
    <section style={{ padding: 24 }}>
      <h2>Page not found</h2>
      <p>The page you’re looking for doesn’t exist.</p>
      <p>
        <Link to="/">Go back home</Link>
      </p>
    </section>
  );
}
