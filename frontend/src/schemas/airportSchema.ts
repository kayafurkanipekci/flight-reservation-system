import { z } from 'zod';

export const airportSchema = z.object({
  name: z.string().min(1, 'Airport name is required'),
  iataCode: z.string()
    .length(3, 'IATA code must be exactly 3 characters')
    .toUpperCase(),
  city: z.string().min(1, 'City is required'),
  country: z.string().min(1, 'Country is required'),
});

export type AirportFormData = z.infer<typeof airportSchema>;