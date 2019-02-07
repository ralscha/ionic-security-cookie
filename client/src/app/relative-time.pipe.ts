import {Pipe, PipeTransform} from '@angular/core';
import {distanceInWordsToNow} from 'date-fns';

@Pipe({
  name: 'relativeTime'
})
export class RelativeTimePipe implements PipeTransform {

  transform(value: any, args?: any): any {
    return distanceInWordsToNow(new Date(value), {addSuffix: true});
  }

}
