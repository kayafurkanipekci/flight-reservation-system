import { useEffect, useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { apiClient } from '../api/client';
import FlightModal, { type FlightLike } from '../components/FlightModal';
import type { FlightFormData } from '../schemas/flightSchema';

export interface Flight {
  id: number;
  flightNumber: string;
  departureTime: string;
  arrivalTime: string;
  status: string;
  airplaneModel: string;
  airplaneTailNumber: string;
  departureAirportCode: string;
  departureAirportCity: string;
  arrivalAirportCode: string;
  arrivalAirportCity: string;
  airplaneId: number;
  departureAirportId: number;
  arrivalAirportId: number;
  airline: string;
}

function formatDateTime(isoString: string | null): string {
  if (!isoString) return '-';
  try {
    const d = new Date(isoString);
    if (Number.isNaN(d.getTime())) return isoString;
    return d.toLocaleString(undefined, {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    });
  } catch {
    return isoString;
  }
}

export default function FlightsPage() {
  const [allFlights, setAllFlights] = useState<Flight[]>([]);
  const [displayedFlights, setDisplayedFlights] = useState<Flight[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState('');

  const { role } = useAuth();
  const isAdmin = role === 'ADMIN';

  const itemsPerPage = 20;
  const totalPages = Math.ceil(allFlights.length / itemsPerPage);
  const [currentPage, setCurrentPage] = useState(0);

  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingFlight, setEditingFlight] = useState<FlightLike | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);

  useEffect(() => {
    fetchFlights();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const fetchFlights = async () => {
    try {
      setIsLoading(true);
      const response = await apiClient.get('/flights');
      // Sort flights so newest or flight number order shows nicely
      const sorted = response.data.sort((a: Flight, b: Flight) => b.id - a.id);
      setAllFlights(sorted);
      setDisplayedFlights(sorted.slice(0, itemsPerPage));
      setError('');
    } catch (err: any) {
      setError('Failed to load flights');
      console.error(err);
    } finally {
      setIsLoading(false);
    }
  };

  const handleLoadMore = () => {
    const nextPageNum = currentPage + 1;
    const startIdx = nextPageNum * itemsPerPage;
    const endIdx = (nextPageNum + 1) * itemsPerPage;
    const newItems = allFlights.slice(startIdx, endIdx);

    setDisplayedFlights((prev) => [...prev, ...newItems]);
    setCurrentPage(nextPageNum);
  };

  const handleDelete = async (id: number) => {
    if (!window.confirm('Are you sure you want to delete this flight?')) return;

    try {
      await apiClient.delete(`/flights/${id}`);
      const updated = allFlights.filter((f) => f.id !== id);
      setAllFlights(updated);
      setDisplayedFlights(updated.slice(0, displayedFlights.length - 1));
      setError('');
    } catch (err) {
      setError('Failed to delete flight');
      console.error(err);
    }
  };

  const handleOpenCreateModal = () => {
    setEditingFlight(null);
    setIsModalOpen(true);
  };

  const handleOpenEditModal = (flight: Flight) => {
    setEditingFlight({
      id: flight.id,
      flightNumber: flight.flightNumber,
      departureTime: flight.departureTime,
      arrivalTime: flight.arrivalTime,
      status: flight.status,
      airplaneId: flight.airplaneId,
      departureAirportId: flight.departureAirportId,
      arrivalAirportId: flight.arrivalAirportId,
    });
    setIsModalOpen(true);
  };

  const handleModalSubmit = async (data: FlightFormData, flightId?: number) => {
    setIsSubmitting(true);
    try {
      if (flightId) {
        // Edit flight
        await apiClient.put(`/flights/${flightId}`, data);
        await fetchFlights(); // Reload list to map details nicely
      } else {
        // Create flight
        await apiClient.post('/flights', data);
        await fetchFlights();
      }

      setIsModalOpen(false);
    } catch (err: any) {
      const msg = err?.response?.data?.message || 'Failed to save flight';
      alert(msg);
      console.error(err);
    } finally {
      setIsSubmitting(false);
    }
  };

  const getStatusBadgeClass = (status: string) => {
    switch (status) {
      case 'SCHEDULED':
        return 'bg-blue-50 text-blue-700 border border-blue-200';
      case 'DELAYED':
        return 'bg-yellow-50 text-yellow-700 border border-yellow-200';
      case 'CANCELLED':
        return 'bg-red-50 text-red-700 border border-red-200';
      case 'COMPLETED':
        return 'bg-green-50 text-green-700 border border-green-200';
      default:
        return 'bg-gray-50 text-gray-700 border border-gray-200';
    }
  };

  const getStatusDotClass = (status: string) => {
    switch (status) {
      case 'SCHEDULED':
        return 'bg-blue-500';
      case 'DELAYED':
        return 'bg-yellow-500';
      case 'CANCELLED':
        return 'bg-red-500';
      case 'COMPLETED':
        return 'bg-green-500';
      default:
        return 'bg-gray-500';
    }
  };

  if (isLoading) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-brand-400 mx-auto mb-4"></div>
          <p className="text-gray-600">Loading flights...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50 p-6">
      <div className="max-w-6xl mx-auto">
        <div className="flex justify-between items-center mb-6">
          <div>
            <h1 className="text-3xl font-bold text-gray-900">Flights</h1>
            <p className="text-gray-600 mt-2">
              {allFlights.length} flights available, showing {displayedFlights.length}
            </p>
          </div>
          {isAdmin && (
            <button
              onClick={handleOpenCreateModal}
              className="bg-brand-400 text-white px-4 py-2 rounded-md hover:bg-brand-300 transition-colors cursor-pointer"
            >
              + New Flight
            </button>
          )}
        </div>

        {error && (
          <div className="mb-4 rounded-md bg-red-50 p-4 text-sm text-red-700 border border-red-200">
            {error}
          </div>
        )}

        <div className="bg-white rounded-lg shadow overflow-hidden">
          <div className="w-full overflow-x-auto">
            <table className="w-full">
              <thead className="bg-gray-100 border-b border-gray-200">
                <tr>
                  <th className="px-6 py-3 text-left text-sm font-semibold text-gray-900">Flight No</th>
                  <th className="px-6 py-3 text-left text-sm font-semibold text-gray-900">Route</th>
                  <th className="px-6 py-3 text-left text-sm font-semibold text-gray-900">Departure Time</th>
                  <th className="px-6 py-3 text-left text-sm font-semibold text-gray-900">Arrival Time</th>
                  <th className="px-6 py-3 text-left text-sm font-semibold text-gray-900">Airplane</th>
                  <th className="px-6 py-3 text-left text-sm font-semibold text-gray-900">Status</th>
                  {isAdmin && (
                    <th className="px-6 py-3 text-left text-sm font-semibold text-gray-900">Actions</th>
                  )}
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-200">
                {displayedFlights.map((flight) => (
                  <tr key={flight.id} className="hover:bg-gray-50 transition-colors">
                    <td className="px-6 py-4 text-sm font-bold text-gray-900">{flight.flightNumber}</td>
                    <td className="px-6 py-4 text-sm text-gray-600">
                      <span className="font-semibold text-brand-600">{flight.departureAirportCode}</span>
                      <span className="mx-2 text-gray-400">→</span>
                      <span className="font-semibold text-brand-600">{flight.arrivalAirportCode}</span>
                      <div className="text-xs text-gray-400 mt-0.5">
                        {flight.departureAirportCity} to {flight.arrivalAirportCity}
                      </div>
                    </td>
                    <td className="px-6 py-4 text-sm text-gray-600">{formatDateTime(flight.departureTime)}</td>
                    <td className="px-6 py-4 text-sm text-gray-600">{formatDateTime(flight.arrivalTime)}</td>
                    <td className="px-6 py-4 text-sm text-gray-600">
                      <span className="font-semibold text-gray-900">{flight.airline}</span>
                      <div className="text-xs text-gray-500 mt-0.5">{flight.airplaneModel} • Tail: {flight.airplaneTailNumber}</div>
                    </td>
                    <td className="px-6 py-4 text-sm whitespace-nowrap">
                      <span className={`inline-flex items-center gap-1.5 rounded-full px-2.5 py-0.5 text-xs font-semibold whitespace-nowrap ${getStatusBadgeClass(flight.status)}`}>
                        <span className={`h-1.5 w-1.5 rounded-full ${getStatusDotClass(flight.status)}`} />
                        {flight.status}
                      </span>
                    </td>
                    {isAdmin && (
                      <td className="px-6 py-4 text-sm">
                        <div className="flex gap-4 items-center">
                          <button
                            onClick={() => handleOpenEditModal(flight)}
                            className="text-brand-400 hover:text-brand-500 font-semibold text-sm transition-colors cursor-pointer"
                          >
                            Edit
                          </button>
                          <button
                            onClick={() => handleDelete(flight.id)}
                            className="text-red-500 hover:text-red-700 font-semibold text-sm cursor-pointer"
                          >
                            Delete
                          </button>
                        </div>
                      </td>
                    )}
                  </tr>
                ))}
              </tbody>
            </table>
          </div>

          {displayedFlights.length === 0 && (
            <div className="text-center py-12">
              <p className="text-gray-600">No flights found</p>
            </div>
          )}
        </div>

        {totalPages > 1 && currentPage < totalPages - 1 && (
          <div className="flex justify-center mt-6">
            <button
              onClick={handleLoadMore}
              className="px-6 py-2 bg-brand-400 text-white rounded-md hover:bg-brand-300 transition-colors cursor-pointer"
            >
              Load More ({displayedFlights.length} / {allFlights.length})
            </button>
          </div>
        )}
      </div>

      <FlightModal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        onSubmit={handleModalSubmit}
        initialData={editingFlight || undefined}
        isLoading={isSubmitting}
      />
    </div>
  );
}
