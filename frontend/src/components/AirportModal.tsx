import { useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { airportSchema, type AirportFormData } from '../schemas/airportSchema';
import type { Airport } from '../pages/AirportsPage';

interface AirportModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSubmit: (data: AirportFormData) => Promise<void>;
  initialData?: Airport;
  isLoading?: boolean;
}

export default function AirportModal({
  isOpen,
  onClose,
  onSubmit,
  initialData,
  isLoading = false,
}: AirportModalProps) {
  const {
    register,
    handleSubmit,
    formState: { errors },
    reset,
  } = useForm<AirportFormData>({
    resolver: zodResolver(airportSchema),
    defaultValues: initialData || {
      name: '',
      iataCode: '',
      city: '',
      country: '',
    },
  });

  useEffect(() => {
    if (initialData) {
      reset({
        name: initialData.name,
        iataCode: initialData.iataCode,
        city: initialData.city,
        country: initialData.country,
      });
    } else {
      reset({
        name: '',
        iataCode: '',
        city: '',
        country: '',
      });
    }
  }, [initialData, reset]);

  const handleFormSubmit = async (data: AirportFormData) => {
    await onSubmit(data);
    reset();
  };

  const handleClose = () => {
    reset();
    onClose();
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 bg-white/60 backdrop-blur-md flex items-center justify-center z-50">
      <div className="bg-white rounded-lg shadow-lg w-full max-w-md mx-4">
        {/* Header */}
        <div className="px-6 py-4 border-b border-gray-200 flex justify-between items-center">
          <h2 className="text-xl font-bold text-gray-900">
            {initialData ? 'Edit Airport' : 'Create Airport'}
          </h2>
          <button
            onClick={handleClose}
            className="text-gray-500 hover:text-gray-700 text-2xl leading-none"
          >
            ×
          </button>
        </div>

        {/* Form */}
        <form onSubmit={handleSubmit(handleFormSubmit)} className="p-6 space-y-4">
          {/* Name */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Airport Name
            </label>
            <input
              {...register('name')}
              placeholder="e.g., Istanbul Airport"
              className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-brand-400"
            />
            {errors.name && (
              <p className="text-red-500 text-sm mt-1">{errors.name.message}</p>
            )}
          </div>

          {/* IATA Code */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              IATA Code
            </label>
            <input
              {...register('iataCode')}
              placeholder="e.g., IST"
              maxLength={3}
              className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-brand-400 uppercase"
            />
            {errors.iataCode && (
              <p className="text-red-500 text-sm mt-1">{errors.iataCode.message}</p>
            )}
          </div>

          {/* City */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              City
            </label>
            <input
              {...register('city')}
              placeholder="e.g., Istanbul"
              className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-brand-400"
            />
            {errors.city && (
              <p className="text-red-500 text-sm mt-1">{errors.city.message}</p>
            )}
          </div>

          {/* Country */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Country
            </label>
            <input
              {...register('country')}
              placeholder="e.g., Turkey"
              className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-brand-400"
            />
            {errors.country && (
              <p className="text-red-500 text-sm mt-1">{errors.country.message}</p>
            )}
          </div>

          {/* Buttons */}
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
              disabled={isLoading}
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