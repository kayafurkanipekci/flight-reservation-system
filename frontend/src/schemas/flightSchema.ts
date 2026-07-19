import { z } from 'zod';

export const flightSchema = z.object({
  flightNumber: z.string().min(1, 'Flight number is required'),
  airplaneId: z.coerce.number().int().positive(),
  departureAirportId: z.coerce.number().int().positive(),
  arrivalAirportId: z.coerce.number().int().positive(),
  departureTime: z.string().min(1, 'Departure time is required'),
  arrivalTime: z.string().min(1, 'Arrival time is required'),
  status: z.string().min(1, 'Status is required'),
})
.refine((data) => data.departureAirportId !== data.arrivalAirportId, {
  message: 'Departure and arrival airports cannot be the same',
  path: ['arrivalAirportId'],
})
.refine((data) => {
  const dep = new Date(data.departureTime).getTime();
  const arr = new Date(data.arrivalTime).getTime();
  return dep < arr;
}, {
  message: 'Departure time must be before arrival time',
  path: ['arrivalTime'],
});

export type FlightFormData = z.infer<typeof flightSchema>;

