import { Pipe, PipeTransform } from '@angular/core';
import { formatDistanceToNow } from 'date-fns';

@Pipe({ name: 'relativeTime' })
export class RelativeTimePipe implements PipeTransform {
  transform(value: string | number | Date): string {
    return formatDistanceToNow(new Date(value), { addSuffix: true });
  }
}
