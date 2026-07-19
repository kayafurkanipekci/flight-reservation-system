import { useEffect, useMemo, useState } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { useAuth } from '../context/AuthContext';
import { apiClient } from '../api/client';
import { reservationSchema, type ReservationFormData } from '../schemas/reservationSchema';

type FlightStatus = string;

export interface Flight {
  id: number;
  flightNumber: string;
  departureTime: string;
  arrivalTime: string;
  status: FlightStatus;
  airplaneModel: string;
  airplaneTailNumber: string;
  departureAirportCode: string;
  departureAirportCity: string;
  arrivalAirportCode: string;
  arrivalAirportCity: string;
}

export interface TicketResponse {
  id: number;
  flightNumber: string;
  seatNumber: string;
  segmentOrder: number;
  departureAirportCode: string;
  arrivalAirportCode: string;
}

export interface ReservationResponse {
  id: number;
  passengerEmail: string;
  status: string;
  tickets: TicketResponse[];
}

export default function ReservationsPage() {
  const { role } = useAuth();
  const isAdmin = role === 'ADMIN';

  const [flights, setFlights] = useState<Flight[]>([]);
  const [flightsLoading, setFlightsLoading] = useState(true);
  const [flightsError, setFlightsError] = useState('');

  const [reservations, setReservations] = useState<ReservationResponse[]>([]);
  const [reservationsLoading, setReservationsLoading] = useState(true);
  const [reservationsError, setReservationsError] = useState('');

  const [isSubmitting, setIsSubmitting] = useState(false);
  const [submitError, setSubmitError] = useState('');

  const {
    register,
    handleSubmit,
    formState: { errors },
    reset,
  } = useForm<ReservationFormData>({
    resolver: zodResolver(reservationSchema),
    defaultValues: {
      flightId: undefined as unknown as number,
      seatNumber: '',
    },
  });

  const flightOptions = useMemo(() => {
    return flights.map((f) => {
      const label = `${f.flightNumber} (${f.departureAirportCode} → ${f.arrivalAirportCode})`;
      return { value: f.id, label };
    });
  }, [flights]);

  useEffect(() => {
    const fetchFlights = async () => {
      try {
        setFlightsLoading(true);
        setFlightsError('');
        const res = await apiClient.get('/flights');
        setFlights(res.data);
      } catch (err) {
        setFlightsError('Failed to load flights');
        console.error(err);
      } finally {
        setFlightsLoading(false);
      }
    };

    const fetchReservations = async () => {
      try {
        setReservationsLoading(true);
        setReservationsError('');
        const res = await apiClient.get(isAdmin ? '/reservations' : '/reservations/my');
        setReservations(res.data);
      } catch (err) {
        setReservationsError('Failed to load reservations');
        console.error(err);
      } finally {
        setReservationsLoading(false);
      }
    };

    fetchFlights();
    fetchReservations();
  }, [isAdmin]);

  const refreshReservations = async () => {
    try {
      setReservationsLoading(true);
      setReservationsError('');
      const res = await apiClient.get(isAdmin ? '/reservations' : '/reservations/my');
      setReservations(res.data);
    } catch (err) {
      setReservationsError('Failed to load reservations');
      console.error(err);
    } finally {
      setReservationsLoading(false);
    }
  };

  const onSubmit = async (data: ReservationFormData) => {
    setSubmitError('');
    setIsSubmitting(true);
    try {
      await apiClient.post('/reservations', {
        tickets: [
          {
            flightId: data.flightId,
            seatNumber: data.seatNumber,
          },
        ],
      });
      reset();
      await refreshReservations();
    } catch (err: any) {
      const message = err?.response?.data?.message || 'Failed to create reservation';
      setSubmitError(message);
      console.error(err);
    } finally {
      setIsSubmitting(false);
    }
  };

  const cancelReservation = async (id: number) => {
    if (!window.confirm('Cancel this reservation?')) return;
    try {
      await apiClient.delete(`/reservations/${id}`);
      await refreshReservations();
    } catch (err: any) {
      const message = err?.response?.data?.message || 'Failed to cancel reservation';
      setReservationsError(message);
      console.error(err);
    }
  };

  const isBusy = flightsLoading || reservationsLoading;

  if (isBusy) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-brand-400 mx-auto mb-4"></div>
          <p className="text-gray-600">Loading reservations...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50 p-6">
      <div className="max-w-6xl mx-auto">
        <div className="flex justify-between items-center mb-6">
          <div>
            <h1 className="text-3xl font-bold text-gray-900">Reservations</h1>
            <p className="text-gray-600 mt-2">
              {isAdmin
                ? 'Admin view: manage all reservations'
                : 'Passenger view: create and view your reservations'}
            </p>
          </div>
        </div>

        {(flightsError || reservationsError || submitError) && (
          <div className="space-y-3 mb-6">
            {flightsError && (
              <div className="rounded-md bg-red-50 p-4 text-sm text-red-700 border border-red-200">
                {flightsError}
              </div>
            )}
            {reservationsError && (
              <div className="rounded-md bg-red-50 p-4 text-sm text-red-700 border border-red-200">
                {reservationsError}
              </div>
            )}
            {submitError && (
              <div className="rounded-md bg-red-50 p-4 text-sm text-red-700 border border-red-200">
                {submitError}
              </div>
            )}
          </div>
        )}

        {/* Create reservation */}
        <div className="bg-white rounded-lg shadow overflow-hidden mb-6">
          <div className="px-6 py-4 border-b border-gray-200">
            <h2 className="text-xl font-bold text-gray-900">Create reservation</h2>
            <p className="text-gray-600 mt-1 text-sm">Pick a flight and choose your seat number.</p>
          </div>

          <form
            onSubmit={handleSubmit(onSubmit)}
            className="p-6 grid grid-cols-1 md:grid-cols-3 gap-4 items-end"
          >
            <div className="md:col-span-2">
              <label className="block text-sm font-medium text-gray-700 mb-1">Flight</label>
              <select
                {...register('flightId', { valueAsNumber: true })}
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-brand-400"
                defaultValue=""
              >
                <option value="" disabled>
                  Select a flight
                </option>
                {flightOptions.map((opt) => (
                  <option key={opt.value} value={opt.value}>
                    {opt.label}
                  </option>
                ))}
              </select>
              {errors.flightId && <p className="text-red-500 text-sm mt-1">{errors.flightId.message}</p>}
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Seat number</label>
              <input
                {...register('seatNumber')}
                placeholder="e.g., 12A"
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-brand-400"
              />
              {errors.seatNumber && (
                <p className="text-red-500 text-sm mt-1">{errors.seatNumber.message}</p>
              )}
            </div>

            <div className="md:col-span-3 flex gap-3">
              <button
                type="submit"
                disabled={isSubmitting || flights.length === 0}
                className="flex-1 px-4 py-2 bg-brand-400 text-white rounded-md hover:bg-brand-300 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
              >
                {isSubmitting ? 'Creating...' : 'Create reservation'}
              </button>
              <button
                type="button"
                onClick={() => {
                  setSubmitError('');
                  reset();
                }}
                disabled={isSubmitting}
                className="flex-1 px-4 py-2 border border-gray-300 rounded-md text-gray-700 hover:bg-gray-50 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
              >
                Reset
              </button>
            </div>
          </form>
        </div>

        {/* Reservations list */}
        <div className="bg-white rounded-lg shadow overflow-hidden">
          <div className="px-6 py-4 border-b border-gray-200 flex justify-between items-center">
            <h2 className="text-xl font-bold text-gray-900">{isAdmin ? 'All reservations' : 'My reservations'}</h2>
            <div className="text-sm text-gray-600">{reservations.length} total</div>
          </div>

          <div className="w-full overflow-x-auto">
            <table className="w-full">
              <thead className="bg-gray-100 border-b border-gray-200">
                <tr>
                  {isAdmin && (
                    <th className="px-6 py-3 text-left text-sm font-semibold text-gray-900">Passenger</th>
                  )}
                  <th className="px-6 py-3 text-left text-sm font-semibold text-gray-900">Status</th>
                  <th className="px-6 py-3 text-left text-sm font-semibold text-gray-900">Tickets</th>
                  <th className="px-6 py-3 text-left text-sm font-semibold text-gray-900">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-200">
                {reservations.map((r) => {

                  return (
                    <tr key={r.id} className="hover:bg-gray-50 transition-colors align-top">
                      {isAdmin && (
                        <td className="px-6 py-4 text-sm text-gray-900">{r.passengerEmail}</td>
                      )}
                      <td className="px-6 py-4 text-sm">
                        <span
                          className={
                            r.status === 'CANCELED'
                              ? 'inline-flex items-center rounded-full bg-red-50 px-3 py-1 text-sm font-medium text-red-700'
                              : 'inline-flex items-center rounded-full bg-brand-50 px-3 py-1 text-sm font-medium text-brand-700'
                          }
                        >
                          {r.status}
                        </span>
                      </td>
                      <td className="px-6 py-4 text-sm text-gray-700">
                        {r.tickets.length === 0 ? (
                          <span className="text-gray-500">No tickets</span>
                        ) : (
                          <div className="space-y-1">
                            {r.tickets.map((t) => (
                              <div key={t.id}>
                                <span className="font-medium text-gray-900">{t.flightNumber}</span>
                                <span className="text-gray-500"> ({t.departureAirportCode} → {t.arrivalAirportCode})</span>
                                <div className="text-gray-600 text-xs">Seat: {t.seatNumber} • Segment: {t.segmentOrder}</div>
                              </div>
                            ))}
                          </div>
                        )}
                      </td>
                      <td className="px-6 py-4 text-sm">
                        <button
                          onClick={() => cancelReservation(r.id)}
                          className="text-red-500 hover:text-red-700 font-medium text-sm"
                          disabled={r.status === 'CANCELED'}
                        >
                          Cancel
                        </button>
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>

          {reservations.length === 0 && (
            <div className="text-center py-12">
              <p className="text-gray-600">No reservations found</p>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

