import { useEffect, useState } from "react";
import { getAirports, type Airport } from "../api/airports";

export default function AirportsPage() {
  const [airports, setAirports] = useState<Airport[]>([]);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    getAirports()
      .then(setAirports)
      .catch((err) => setError(err.message));
  }, []);

  return (
    <div className="p-8">
      <h1 className="text-2xl font-bold mb-4">Havalimanları</h1>

      {error && <p className="text-red-500">{error}</p>}

      <table className="w-full border-collapse">
        <thead>
          <tr className="border-b text-left">
            <th className="p-2">Ad</th>
            <th className="p-2">IATA</th>
            <th className="p-2">Şehir</th>
            <th className="p-2">Ülke</th>
          </tr>
        </thead>
        <tbody>
          {airports.map((airport) => (
            <tr key={airport.id} className="border-b">
              <td className="p-2">{airport.name}</td>
              <td className="p-2">{airport.iataCode}</td>
              <td className="p-2">{airport.city}</td>
              <td className="p-2">{airport.country}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}