import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'replace',
  standalone: true // ✅ Important for Angular 17 standalone components
})
export class ReplacePipe implements PipeTransform {
  transform(value: string, search: string, replacement: string): string {
    return value.replace(new RegExp(search, 'g'), replacement);
  }
}
