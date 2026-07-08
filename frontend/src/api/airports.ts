export interface Airport {
  id: number;
  name: string;
  iataCode: string;
  city: string;
  country: string;
}

export async function getAirports(): Promise<Airport[]> {
  const response = await fetch("http://localhost:8081/api/airports");
  if (!response.ok) {
    throw new Error("Havalimanları alınamadı");
  }
  return response.json();
}