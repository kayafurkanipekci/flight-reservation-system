import { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { apiClient } from '../api/client';
import AirportModal from '../components/AirportModal';
import type { AirportFormData } from '../schemas/airportSchema';

export interface Airport {
  id: number;
  name: string;
  iataCode: string;
  city: string;
  country: string;
}

export default function AirportsPage() {
  const [allAirports, setAllAirports] = useState<Airport[]>([]);
  const [displayedAirports, setDisplayedAirports] = useState<Airport[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState('');
  const [currentPage, setCurrentPage] = useState(0);
  const { role } = useAuth();
  const isAdmin = role === 'ADMIN';

  const itemsPerPage = 20;
  const totalPages = Math.ceil(allAirports.length / itemsPerPage);

  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingAirport, setEditingAirport] = useState<Airport | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);

  useEffect(() => {
    fetchAirports();
  }, []);

  const fetchAirports = async () => {
    try {
      setIsLoading(true);
      const response = await apiClient.get('/airports');
      setAllAirports(response.data);
      setDisplayedAirports(response.data.slice(0, itemsPerPage));
      setError('');
    } catch (err: any) {
      setError('Failed to load airports');
      console.error(err);
    } finally {
      setIsLoading(false);
    }
  };

  const handleLoadMore = () => {
    const nextPageNum = currentPage + 1;
    const startIdx = nextPageNum * itemsPerPage;
    const endIdx = (nextPageNum + 1) * itemsPerPage;
    const newItems = allAirports.slice(startIdx, endIdx);
    
    setDisplayedAirports(prev => [...prev, ...newItems]);
    setCurrentPage(nextPageNum);
  };

  const handleDelete = async (id: number) => {
    if (!window.confirm('Are you sure?')) return;
    
    try {
      await apiClient.delete(`/airports/${id}`);
      const updated = allAirports.filter(a => a.id !== id);
      setAllAirports(updated);
      setDisplayedAirports(updated.slice(0, displayedAirports.length - 1));
      setError('');
    } catch (err) {
      setError('Failed to delete airport');
      console.error(err);
    }
  };

const handleOpenCreateModal = () => {
  setEditingAirport(null);
  setIsModalOpen(true);
};

const handleOpenEditModal = (airport: Airport) => {
  setEditingAirport(airport);
  setIsModalOpen(true);
};

const handleModalSubmit = async (data: AirportFormData) => {
  setIsSubmitting(true);
  try {
    if (editingAirport) {
      await apiClient.put(`/airports/${editingAirport.id}`, data);
      const updated = allAirports.map(a =>
        a.id === editingAirport.id ? { ...a, ...data } : a
      );
      setAllAirports(updated);
      setDisplayedAirports(displayedAirports.map(a =>
        a.id === editingAirport.id ? { ...a, ...data } : a
      ));
    } else {
      const response = await apiClient.post('/airports', data);
      setAllAirports([...allAirports, response.data]);
      setDisplayedAirports([...displayedAirports, response.data]);
    }
    setIsModalOpen(false);
    setError('');
  } catch (err) {
    setError('Failed to save airport');
    console.error(err);
  } finally {
    setIsSubmitting(false);
  }
};

  if (isLoading) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-brand-400 mx-auto mb-4"></div>
          <p className="text-gray-600">Loading airports...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50 p-6">
      <div className="max-w-6xl mx-auto">
        <div className="flex justify-between items-center mb-6">
          <div>
            <h1 className="text-3xl font-bold text-gray-900">Airports</h1>
            <p className="text-gray-600 mt-2">{allAirports.length} airports available, showing {displayedAirports.length}</p>
          </div>
          {isAdmin && (
            <button 
            onClick={handleOpenCreateModal}
            className="bg-brand-400 text-white px-4 py-2 rounded-md hover:bg-brand-300 transition-colors">
              + New Airport
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
                  <th className="px-6 py-3 text-left text-sm font-semibold text-gray-900">Name</th>
                  <th className="px-6 py-3 text-left text-sm font-semibold text-gray-900">IATA Code</th>
                  <th className="px-6 py-3 text-left text-sm font-semibold text-gray-900">City</th>
                  <th className="px-6 py-3 text-left text-sm font-semibold text-gray-900">Country</th>
                  {isAdmin && (
                    <th className="px-6 py-3 text-left text-sm font-semibold text-gray-900">Actions</th>
                  )}
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-200">
                {displayedAirports.map((airport) => (
                  <tr key={airport.id} className="hover:bg-gray-50 transition-colors">
                    <td className="px-6 py-4 text-sm text-gray-900">{airport.name}</td>
                    <td className="px-6 py-4 text-sm">
                      <span className="inline-flex items-center rounded-full bg-brand-50 px-3 py-1 text-sm font-medium text-brand-700">
                        {airport.iataCode}
                      </span>
                    </td>
                    <td className="px-6 py-4 text-sm text-gray-600">{airport.city}</td>
                    <td className="px-6 py-4 text-sm text-gray-600">{airport.country}</td>
                    {isAdmin && (
                      <td className="px-6 py-4 text-sm">
                        <div className="flex gap-6 items-center">
                          <button
                            onClick={() => handleOpenEditModal(airport)}
                            className="text-blue-500 hover:text-blue-700 font-medium text-sm"
                          >
                            Edit
                          </button>
                          <button
                            onClick={() => handleDelete(airport.id)}
                            className="text-red-500 hover:text-red-700 font-medium text-sm"
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

          {displayedAirports.length === 0 && (
            <div className="text-center py-12">
              <p className="text-gray-600">No airports found</p>
            </div>
          )}
        </div>

        {totalPages > 1 && currentPage < totalPages - 1 && (
          <div className="flex justify-center mt-6">
            <button
              onClick={handleLoadMore}
              className="px-6 py-2 bg-brand-400 text-white rounded-md hover:bg-brand-300 transition-colors"
            >
              Load More ({displayedAirports.length} / {allAirports.length})
            </button>
          </div>
        )}
      </div>
      <AirportModal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        onSubmit={handleModalSubmit}
        initialData={editingAirport || undefined}
        isLoading={isSubmitting}
      />
    </div>
  );  
}