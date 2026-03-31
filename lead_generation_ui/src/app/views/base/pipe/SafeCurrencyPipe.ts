import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
    name: 'safeCurrency',
    standalone: true 
})
export class SafeCurrencyPipe implements PipeTransform {
    transform(value: any, ...args: any[]): any {
        return isNaN(value) ? 0 : value;
    }
}