import { useEffect, useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { apiClient } from '../api/client';
import AirplaneModal from '../components/AirplaneModal';
import type { AirplaneFormData } from '../schemas/airplaneSchema';

export interface Airplane {
  id: number;
  model: string;
  tailNumber: string;
  capacity: number;
  airline: string;
}

export default function AirplanesPage() {
  const [allAirplanes, setAllAirplanes] = useState<Airplane[]>([]);
  const [displayedAirplanes, setDisplayedAirplanes] = useState<Airplane[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState('');

  const { role } = useAuth();
  const isAdmin = role === 'ADMIN';

  const itemsPerPage = 20;
  const totalPages = Math.ceil(allAirplanes.length / itemsPerPage);
  const [currentPage, setCurrentPage] = useState(0);

  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingAirplane, setEditingAirplane] = useState<Airplane | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);

  useEffect(() => {
    fetchAirplanes();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const fetchAirplanes = async () => {
    try {
      setIsLoading(true);
      const response = await apiClient.get('/airplanes');
      setAllAirplanes(response.data);
      setDisplayedAirplanes(response.data.slice(0, itemsPerPage));
      setError('');
    } catch (err: any) {
      setError('Failed to load airplanes');
      console.error(err);
    } finally {
      setIsLoading(false);
    }
  };

  const handleLoadMore = () => {
    const nextPageNum = currentPage + 1;
    const startIdx = nextPageNum * itemsPerPage;
    const endIdx = (nextPageNum + 1) * itemsPerPage;
    const newItems = allAirplanes.slice(startIdx, endIdx);

    setDisplayedAirplanes((prev) => [...prev, ...newItems]);
    setCurrentPage(nextPageNum);
  };

  const handleDelete = async (id: number) => {
    if (!window.confirm('Are you sure?')) return;

    try {
      await apiClient.delete(`/airplanes/${id}`);
      const updated = allAirplanes.filter((a) => a.id !== id);
      setAllAirplanes(updated);
      setDisplayedAirplanes(updated.slice(0, displayedAirplanes.length - 1));
      setError('');
    } catch (err) {
      setError('Failed to delete airplane');
      console.error(err);
    }
  };

  const handleOpenCreateModal = () => {
    setEditingAirplane(null);
    setIsModalOpen(true);
  };

  const handleOpenEditModal = (airplane: Airplane) => {
    setEditingAirplane(airplane);
    setIsModalOpen(true);
  };

  const handleModalSubmit = async (data: AirplaneFormData) => {
    setIsSubmitting(true);
    try {
      if (editingAirplane) {
        await apiClient.put(`/airplanes/${editingAirplane.id}`, data);
        const updated = allAirplanes.map((a) =>
          a.id === editingAirplane.id ? { ...a, ...data } : a
        );
        setAllAirplanes(updated);
        setDisplayedAirplanes(
          displayedAirplanes.map((a) =>
            a.id === editingAirplane.id ? { ...a, ...data } : a
          )
        );
      } else {
        const response = await apiClient.post('/airplanes', data);
        setAllAirplanes([...allAirplanes, response.data]);
        setDisplayedAirplanes([...displayedAirplanes, response.data]);
      }

      setIsModalOpen(false);
      setError('');
    } catch (err) {
      setError('Failed to save airplane');
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
          <p className="text-gray-600">Loading airplanes...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50 p-6">
      <div className="max-w-6xl mx-auto">
        <div className="flex justify-between items-center mb-6">
          <div>
            <h1 className="text-3xl font-bold text-gray-900">Airplanes</h1>
            <p className="text-gray-600 mt-2">
              {allAirplanes.length} airplanes available, showing {displayedAirplanes.length}
            </p>
          </div>
          {isAdmin && (
            <button
              onClick={handleOpenCreateModal}
              className="bg-brand-400 text-white px-4 py-2 rounded-md hover:bg-brand-300 transition-colors"
            >
              + New Airplane
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
                  <th className="px-6 py-3 text-left text-sm font-semibold text-gray-900">Model</th>
                  <th className="px-6 py-3 text-left text-sm font-semibold text-gray-900">Tail Number</th>
                  <th className="px-6 py-3 text-left text-sm font-semibold text-gray-900">Capacity</th>
                  <th className="px-6 py-3 text-left text-sm font-semibold text-gray-900">Airline</th>
                  {isAdmin && (
                    <th className="px-6 py-3 text-left text-sm font-semibold text-gray-900">Actions</th>
                  )}
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-200">
                {displayedAirplanes.map((airplane) => (
                  <tr key={airplane.id} className="hover:bg-gray-50 transition-colors">
                    <td className="px-6 py-4 text-sm text-gray-900">{airplane.model}</td>
                    <td className="px-6 py-4 text-sm text-gray-600">{airplane.tailNumber}</td>
                    <td className="px-6 py-4 text-sm text-gray-600">{airplane.capacity}</td>
                    <td className="px-6 py-4 text-sm text-gray-600">{airplane.airline}</td>
                    {isAdmin && (
                      <td className="px-6 py-4 text-sm">
                        <div className="flex gap-6 items-center">
                          <button
                            onClick={() => handleOpenEditModal(airplane)}
                            className="text-brand-400 hover:text-brand-500 font-semibold text-sm transition-colors"
                          >
                            Edit
                          </button>
                          <button
                            onClick={() => handleDelete(airplane.id)}
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

          {displayedAirplanes.length === 0 && (
            <div className="text-center py-12">
              <p className="text-gray-600">No airplanes found</p>
            </div>
          )}
        </div>

        {totalPages > 1 && currentPage < totalPages - 1 && (
          <div className="flex justify-center mt-6">
            <button
              onClick={handleLoadMore}
              className="px-6 py-2 bg-brand-400 text-white rounded-md hover:bg-brand-300 transition-colors"
            >
              Load More ({displayedAirplanes.length} / {allAirplanes.length})
            </button>
          </div>
        )}
      </div>

      <AirplaneModal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        onSubmit={handleModalSubmit}
        initialData={editingAirplane || undefined}
        isLoading={isSubmitting}
      />
    </div>
  );
}

