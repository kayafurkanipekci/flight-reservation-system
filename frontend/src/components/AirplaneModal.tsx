 import { useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { airplaneSchema, type AirplaneFormData } from '../schemas/airplaneSchema';

export interface Airplane {
  id: number;
  model: string;
  tailNumber: string;
  capacity: number;
  airline: string;
}

interface AirplaneModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSubmit: (data: AirplaneFormData) => Promise<void>;
  initialData?: Airplane;
  isLoading?: boolean;
}

export default function AirplaneModal({
  isOpen,
  onClose,
  onSubmit,
  initialData,
  isLoading = false,
}: AirplaneModalProps) {
  const {
    register,
    handleSubmit,
    formState: { errors },
    reset,
  } = useForm<any>({
    resolver: zodResolver(airplaneSchema),
    defaultValues: initialData || {
      model: '',
      tailNumber: '',
      capacity: 1,
      airline: '',
    },
  });

  useEffect(() => {
    if (initialData) {
      reset({
        model: initialData.model,
        tailNumber: initialData.tailNumber,
        capacity: initialData.capacity,
        airline: initialData.airline,
      });
    } else {
      reset({
        model: '',
        tailNumber: '',
        capacity: 1,
        airline: '',
      });
    }
  }, [initialData, reset]);

  const handleFormSubmit = async (data: AirplaneFormData) => {
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
        <div className="px-6 py-4 border-b border-gray-200 flex justify-between items-center">
          <h2 className="text-xl font-bold text-gray-900">
            {initialData ? 'Edit Airplane' : 'Create Airplane'}
          </h2>
          <button
            onClick={handleClose}
            className="text-gray-500 hover:text-gray-700 text-2xl leading-none"
          >
            ×
          </button>
        </div>

        <form onSubmit={handleSubmit(handleFormSubmit)} className="p-6 space-y-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Model
            </label>
            <input
              {...register('model')}
              placeholder="e.g., Airbus A320"
              className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-brand-400"
            />
            {errors.model && (
              <p className="text-red-500 text-sm mt-1">{String((errors.model as any).message ?? '')}</p>
            )}
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Tail number
            </label>
            <input
              {...register('tailNumber')}
              placeholder="e.g., N123AB"
              className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-brand-400"
            />
            {errors.tailNumber && (
              <p className="text-red-500 text-sm mt-1">{String((errors.tailNumber as any).message ?? '')}</p>
            )}
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Capacity
            </label>
            <input
              {...register('capacity')}
              type="number"
              min={1}
              className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-brand-400"
            />
            {errors.capacity && (
              <p className="text-red-500 text-sm mt-1">{String((errors.capacity as any).message ?? '')}</p>
            )}
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Airline
            </label>
            <input
              {...register('airline')}
              placeholder="e.g., Turkish Airlines"
              className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-brand-400"
            />
            {errors.airline && (
              <p className="text-red-500 text-sm mt-1">{String((errors.airline as any).message ?? '')}</p>
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

