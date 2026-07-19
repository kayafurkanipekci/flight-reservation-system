import { z } from 'zod';

export const airplaneSchema = z.object({
  model: z.string().min(1, 'Model is required'),
  tailNumber: z.string().min(1, 'Tail number is required'),
  capacity: z.coerce.number().int().min(1, 'Capacity must be greater than 0'),
  airline: z.string().min(1, 'Airline is required'),
});

export type AirplaneFormData = z.infer<typeof airplaneSchema>;

