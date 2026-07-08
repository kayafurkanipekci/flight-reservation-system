import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import AirportsPage from "./pages/AirportsPage";

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Navigate to="/airports" replace />} />
        <Route path="/airports" element={<AirportsPage />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;