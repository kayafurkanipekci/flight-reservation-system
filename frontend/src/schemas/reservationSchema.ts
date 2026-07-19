import { z } from 'zod';

export const reservationSchema = z.object({
  flightId: z
    .number()
    .int()
    .positive(),
  seatNumber: z.string().min(1, 'Seat number is required'),
});

export type ReservationFormData = z.infer<typeof reservationSchema>;

