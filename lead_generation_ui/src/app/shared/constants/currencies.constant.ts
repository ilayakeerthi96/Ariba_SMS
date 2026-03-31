// // src/app/shared/constants/currencies.constant.ts
// // Single source of truth for all world currencies used across the procurement system.

// export interface CurrencyOption {
//   code: string;
//   symbol: string;
//   locale: string;
//   name: string;
// }

// export const ALL_CURRENCIES: CurrencyOption[] = [
//   { code: 'INR', symbol: '₹',    locale: 'en-IN', name: 'Indian Rupee' },
//   { code: 'USD', symbol: '$',    locale: 'en-US', name: 'US Dollar' },
//   { code: 'EUR', symbol: '€',    locale: 'de-DE', name: 'Euro' },
//   { code: 'GBP', symbol: '£',    locale: 'en-GB', name: 'British Pound' },
//   { code: 'AED', symbol: 'د.إ',  locale: 'ar-AE', name: 'UAE Dirham' },
//   { code: 'SGD', symbol: 'S$',   locale: 'en-SG', name: 'Singapore Dollar' },
//   { code: 'JPY', symbol: '¥',    locale: 'ja-JP', name: 'Japanese Yen' },
//   { code: 'CNY', symbol: '¥',    locale: 'zh-CN', name: 'Chinese Yuan' },
//   { code: 'CAD', symbol: 'CA$',  locale: 'en-CA', name: 'Canadian Dollar' },
//   { code: 'AUD', symbol: 'A$',   locale: 'en-AU', name: 'Australian Dollar' },
//   { code: 'CHF', symbol: 'CHF',  locale: 'de-CH', name: 'Swiss Franc' },
//   { code: 'HKD', symbol: 'HK$',  locale: 'zh-HK', name: 'Hong Kong Dollar' },
//   { code: 'SAR', symbol: '﷼',    locale: 'ar-SA', name: 'Saudi Riyal' },
//   { code: 'QAR', symbol: '﷼',    locale: 'ar-QA', name: 'Qatari Riyal' },
//   { code: 'KWD', symbol: 'KD',   locale: 'ar-KW', name: 'Kuwaiti Dinar' },
//   { code: 'BHD', symbol: 'BD',   locale: 'ar-BH', name: 'Bahraini Dinar' },
//   { code: 'OMR', symbol: '﷼',    locale: 'ar-OM', name: 'Omani Rial' },
//   { code: 'MYR', symbol: 'RM',   locale: 'ms-MY', name: 'Malaysian Ringgit' },
//   { code: 'THB', symbol: '฿',    locale: 'th-TH', name: 'Thai Baht' },
//   { code: 'IDR', symbol: 'Rp',   locale: 'id-ID', name: 'Indonesian Rupiah' },
//   { code: 'PHP', symbol: '₱',    locale: 'en-PH', name: 'Philippine Peso' },
//   { code: 'VND', symbol: '₫',    locale: 'vi-VN', name: 'Vietnamese Dong' },
//   { code: 'KRW', symbol: '₩',    locale: 'ko-KR', name: 'South Korean Won' },
//   { code: 'NZD', symbol: 'NZ$',  locale: 'en-NZ', name: 'New Zealand Dollar' },
//   { code: 'ZAR', symbol: 'R',    locale: 'en-ZA', name: 'South African Rand' },
//   { code: 'TRY', symbol: '₺',    locale: 'tr-TR', name: 'Turkish Lira' },
//   { code: 'BRL', symbol: 'R$',   locale: 'pt-BR', name: 'Brazilian Real' },
//   { code: 'MXN', symbol: 'MX$',  locale: 'es-MX', name: 'Mexican Peso' },
//   { code: 'NGN', symbol: '₦',    locale: 'en-NG', name: 'Nigerian Naira' },
//   { code: 'EGP', symbol: 'E£',   locale: 'ar-EG', name: 'Egyptian Pound' },
//   { code: 'PKR', symbol: '₨',    locale: 'ur-PK', name: 'Pakistani Rupee' },
//   { code: 'BDT', symbol: '৳',    locale: 'bn-BD', name: 'Bangladeshi Taka' },
//   { code: 'LKR', symbol: '₨',    locale: 'si-LK', name: 'Sri Lankan Rupee' },
//   { code: 'NPR', symbol: '₨',    locale: 'ne-NP', name: 'Nepalese Rupee' },
//   { code: 'DKK', symbol: 'kr',   locale: 'da-DK', name: 'Danish Krone' },
//   { code: 'NOK', symbol: 'kr',   locale: 'nb-NO', name: 'Norwegian Krone' },
//   { code: 'SEK', symbol: 'kr',   locale: 'sv-SE', name: 'Swedish Krona' },
//   { code: 'PLN', symbol: 'zł',   locale: 'pl-PL', name: 'Polish Zloty' },
//   { code: 'CZK', symbol: 'Kč',   locale: 'cs-CZ', name: 'Czech Koruna' },
//   { code: 'HUF', symbol: 'Ft',   locale: 'hu-HU', name: 'Hungarian Forint' },
//   { code: 'RON', symbol: 'lei',  locale: 'ro-RO', name: 'Romanian Leu' },
//   { code: 'RUB', symbol: '₽',    locale: 'ru-RU', name: 'Russian Ruble' },
//   { code: 'UAH', symbol: '₴',    locale: 'uk-UA', name: 'Ukrainian Hryvnia' },
//   { code: 'ILS', symbol: '₪',    locale: 'he-IL', name: 'Israeli Shekel' },
//   { code: 'JOD', symbol: 'JD',   locale: 'ar-JO', name: 'Jordanian Dinar' },
//   { code: 'KES', symbol: 'KSh',  locale: 'en-KE', name: 'Kenyan Shilling' },
//   { code: 'GHS', symbol: 'GH₵',  locale: 'en-GH', name: 'Ghanaian Cedi' },
//   { code: 'CLP', symbol: 'CL$',  locale: 'es-CL', name: 'Chilean Peso' },
//   { code: 'COP', symbol: 'CO$',  locale: 'es-CO', name: 'Colombian Peso' },
//   { code: 'ARS', symbol: 'AR$',  locale: 'es-AR', name: 'Argentine Peso' },
//   { code: 'PEN', symbol: 'S/.',  locale: 'es-PE', name: 'Peruvian Sol' },
// ];

// /** Returns the CurrencyOption for a given ISO code, defaulting to INR. */
// export function getCurrencyOption(code: string | null | undefined): CurrencyOption {
//   if (!code) return ALL_CURRENCIES[0];
//   return ALL_CURRENCIES.find(c => c.code === code) ?? ALL_CURRENCIES[0];
// }

// /** Formats an amount with the correct symbol and locale for the given currency code. */
// export function formatWithCurrency(
//   amount: number | null | undefined,
//   code: string = 'INR'
// ): string {
//   const option = getCurrencyOption(code);
//   const val = Number(amount ?? 0);
//   const formatted = val.toLocaleString(option.locale, {
//     minimumFractionDigits: 2,
//     maximumFractionDigits: 2
//   });
//   return code === 'AED' ? `${formatted} ${option.symbol}` : `${option.symbol} ${formatted}`;
// }