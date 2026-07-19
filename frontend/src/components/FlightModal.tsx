import { useEffect, useMemo, useState } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { flightSchema, type FlightFormData } from '../schemas/flightSchema';
import { apiClient } from '../api/client';

export interface AirportLite {
  id: number;
  iataCode: string;
  city: string;
  name?: string;
  country?: string;
}

export interface AirplaneLite {
  id: number;
  model: string;
  tailNumber: string;
  capacity?: number;
  airline?: string;
}

export interface FlightLike {
  id: number;
  flightNumber: string;
  departureTime: string | null;
  arrivalTime: string | null;
  status: string;
  airplaneId?: number;
  departureAirportId?: number;
  arrivalAirportId?: number;
}

interface FlightModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSubmit: (data: FlightFormData, flightId?: number) => Promise<void>;
  initialData?: FlightLike;
  isLoading?: boolean;
}

const FLIGHT_STATUSES = ['SCHEDULED', 'DELAYED', 'CANCELLED', 'COMPLETED'] as const;

function toDateTimeLocalValue(value: unknown): string {
  // input[type=datetime-local] expects "YYYY-MM-DDTHH:mm"
  if (!value) return '';
  const d = new Date(value as any);
  if (Number.isNaN(d.getTime())) return '';
  const pad = (n: number) => String(n).padStart(2, '0');
  const yyyy = d.getFullYear();
  const mm = pad(d.getMonth() + 1);
  const dd = pad(d.getDate());
  const hh = pad(d.getHours());
  const min = pad(d.getMinutes());
  return `${yyyy}-${mm}-${dd}T${hh}:${min}`;
}

export default function FlightModal({
  isOpen,
  onClose,
  onSubmit,
  initialData,
  isLoading = false,
}: FlightModalProps) {
  const {
    register,
    handleSubmit,
    formState: { errors },
    reset,
    setValue,
  } = useForm<FlightFormData>({
    resolver: zodResolver(flightSchema),
    defaultValues: initialData
      ? {
          flightNumber: initialData.flightNumber ?? '',
          airplaneId:
            initialData.airplaneId !== undefined ? Number(initialData.airplaneId) : 1,
          departureAirportId:
            initialData.departureAirportId !== undefined
              ? Number(initialData.departureAirportId)
              : 1,
          arrivalAirportId:
            initialData.arrivalAirportId !== undefined
              ? Number(initialData.arrivalAirportId)
              : 1,
          departureTime: toDateTimeLocalValue(initialData.departureTime ?? ''),
          arrivalTime: toDateTimeLocalValue(initialData.arrivalTime ?? ''),
          status: initialData.status ?? 'SCHEDULED',
        }
      : {
          flightNumber: '',
          airplaneId: 1,
          departureAirportId: 1,
          arrivalAirportId: 1,
          departureTime: '',
          arrivalTime: '',
          status: 'SCHEDULED',
        },
  });

  const [airports, setAirports] = useState<AirportLite[]>([]);
  const [airplanes, setAirplanes] = useState<AirplaneLite[]>([]);
  const [loadingLists, setLoadingLists] = useState(false);
  const [listError, setListError] = useState('');

  const [selectedAirline, setSelectedAirline] = useState<string>('');

  const uniqueAirlines = useMemo(() => {
    const set = new Set(airplanes.map((a) => a.airline).filter(Boolean));
    return Array.from(set) as string[];
  }, [airplanes]);

  const filteredAirplanes = useMemo(() => {
    if (!selectedAirline) return airplanes;
    return airplanes.filter((a) => a.airline === selectedAirline);
  }, [airplanes, selectedAirline]);

  const statusOptions = useMemo(() => FLIGHT_STATUSES.map((s) => s), []);

  useEffect(() => {
    if (!isOpen) return;

    const loadLists = async () => {
      try {
        setLoadingLists(true);
        setListError('');
        const [airportsRes, airplanesRes] = await Promise.all([
          apiClient.get('/airports'),
          apiClient.get('/airplanes'),
        ]);
        setAirports(airportsRes.data);
        setAirplanes(airplanesRes.data);
      } catch (e) {
        setListError('Failed to load airports/airplanes');
        console.error(e);
      } finally {
        setLoadingLists(false);
      }
    };

    loadLists();
  }, [isOpen]);

  useEffect(() => {
    if (!isOpen) return;

    if (initialData) {
      reset({
        flightNumber: initialData.flightNumber ?? '',
        airplaneId:
          initialData.airplaneId !== undefined ? Number(initialData.airplaneId) : 1,
        departureAirportId:
          initialData.departureAirportId !== undefined ? Number(initialData.departureAirportId) : 1,
        arrivalAirportId:
          initialData.arrivalAirportId !== undefined ? Number(initialData.arrivalAirportId) : 1,
        departureTime: toDateTimeLocalValue(initialData.departureTime ?? ''),
        arrivalTime: toDateTimeLocalValue(initialData.arrivalTime ?? ''),
        status: initialData.status ?? 'SCHEDULED',
      });
    } else {
      reset({
        flightNumber: '',
        airplaneId: airplanes[0]?.id ?? 1,
        departureAirportId: airports[0]?.id ?? 1,
        arrivalAirportId: airports[0]?.id ?? 1,
        departureTime: '',
        arrivalTime: '',
        status: 'SCHEDULED',
      });
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [initialData, reset, isOpen]);

  // Synchronize selectedAirline based on current airplane selection
  useEffect(() => {
    if (!isOpen || airplanes.length === 0) return;

    if (initialData?.airplaneId) {
      const plane = airplanes.find((a) => a.id === Number(initialData.airplaneId));
      if (plane?.airline) {
        setSelectedAirline(plane.airline);
      }
    } else {
      if (uniqueAirlines.length > 0 && !selectedAirline) {
        setSelectedAirline(uniqueAirlines[0]);
      }
    }
  }, [initialData, airplanes, isOpen, uniqueAirlines, selectedAirline]);

  // Ensure defaults update after list loads (for create case)
  useEffect(() => {
    if (!isOpen || initialData) return;
    if (airports.length === 0 || airplanes.length === 0) return;

    setValue('airplaneId', airplanes[0].id);
    setValue('departureAirportId', airports[0].id);
    setValue('arrivalAirportId', airports[0].id);
  }, [airports, airplanes, isOpen, initialData, setValue]);

  const handleAirlineChange = (airline: string) => {
    setSelectedAirline(airline);
    const firstPlane = airplanes.find((a) => a.airline === airline);
    if (firstPlane) {
      setValue('airplaneId', firstPlane.id);
    }
  };

  const handleFormSubmit = async (data: FlightFormData) => {
    await onSubmit(data, initialData?.id);
    reset();
  };

  const handleClose = () => {
    reset();
    onClose();
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 bg-white/60 backdrop-blur-md flex items-center justify-center z-50">
      <div className="bg-white rounded-lg shadow-lg w-full max-w-lg mx-4">
        <div className="px-6 py-4 border-b border-gray-200 flex justify-between items-center">
          <h2 className="text-xl font-bold text-gray-900">
            {initialData ? 'Edit Flight' : 'Create Flight'}
          </h2>
          <button
            onClick={handleClose}
            className="text-gray-500 hover:text-gray-700 text-2xl leading-none"
            type="button"
          >
            ×
          </button>
        </div>

        <div className="px-6 pt-4 pb-2">
          {listError && (
            <div className="mb-3 rounded-md bg-red-50 p-3 text-sm text-red-700 border border-red-200">
               {listError}
            </div>
          )}
          {loadingLists && (
            <div className="mb-3 text-sm text-gray-600">Loading airports/airplanes...</div>
          )}
        </div>

        <form onSubmit={handleSubmit(handleFormSubmit)} className="p-6 space-y-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Flight Number</label>
            <input
              {...register('flightNumber')}
              placeholder="e.g., TK101"
              className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-brand-400"
            />
            {errors.flightNumber && (
              <p className="text-red-500 text-sm mt-1">{errors.flightNumber.message}</p>
            )}
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Airline Company</label>
              <select
                value={selectedAirline}
                onChange={(e) => handleAirlineChange(e.target.value)}
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-brand-400"
              >
                {uniqueAirlines.map((airline) => (
                  <option key={airline} value={airline}>
                    {airline}
                  </option>
                ))}
              </select>
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Airplane</label>
              <select
                {...register('airplaneId')}
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-brand-400"
              >
                {filteredAirplanes.map((a) => (
                  <option key={a.id} value={a.id}>
                    {a.model} ({a.tailNumber})
                  </option>
                ))}
              </select>
              {errors.airplaneId && (
                <p className="text-red-500 text-sm mt-1">{String(errors.airplaneId.message ?? '')}</p>
              )}
            </div>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Departure Airport
              </label>
              <select
                {...register('departureAirportId')}
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-brand-400"
              >
                {airports.map((a) => (
                  <option key={a.id} value={a.id}>
                    {a.iataCode} - {a.city}
                  </option>
                ))}
              </select>
              {errors.departureAirportId && (
                <p className="text-red-500 text-sm mt-1">
                  {String(errors.departureAirportId.message ?? '')}
                </p>
              )}
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Arrival Airport
              </label>
              <select
                {...register('arrivalAirportId')}
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-brand-400"
              >
                {airports.map((a) => (
                  <option key={a.id} value={a.id}>
                    {a.iataCode} - {a.city}
                  </option>
                ))}
              </select>
              {errors.arrivalAirportId && (
                <p className="text-red-500 text-sm mt-1">
                  {String(errors.arrivalAirportId.message ?? '')}
                </p>
              )}
            </div>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Departure Time
              </label>
              <input
                {...register('departureTime')}
                type="datetime-local"
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-brand-400"
              />
              {errors.departureTime && (
                <p className="text-red-500 text-sm mt-1">{errors.departureTime.message}</p>
              )}
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Arrival Time</label>
              <input
                {...register('arrivalTime')}
                type="datetime-local"
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-brand-400"
              />
              {errors.arrivalTime && (
                <p className="text-red-500 text-sm mt-1">{errors.arrivalTime.message}</p>
              )}
            </div>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Status</label>
            <select
              {...register('status')}
              className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-brand-400"
            >
              {statusOptions.map((s) => (
                <option key={s} value={s}>
                  {s}
                </option>
              ))}
            </select>
            {errors.status && (
              <p className="text-red-500 text-sm mt-1">{errors.status.message}</p>
            )}
          </div>

          <div className="flex gap-3 pt-4">
            <button
              type="button"
              onClick={handleClose}
              className="flex-1 px-4 py-2 border border-gray-300 rounded-md text-gray-700 hover:bg-gray-50 transition-colors"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={isLoading || loadingLists}
              className="flex-1 px-4 py-2 bg-brand-400 text-white rounded-md hover:bg-brand-300 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {isLoading ? 'Saving...' : 'Save'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
