

export interface CityData {
  name: string;
  postalCode: string;
}

export interface StateData {
  name: string;
  cities: CityData[];
}

export interface CountryData {
  name: string;
  code: string;
  states: StateData[];
}

export const COUNTRY_STATE_CITY_DATA: CountryData[] = [
  {
    name: 'Afghanistan', code: 'AF',
    states: [
      { name: 'Kabul', cities: [{ name: 'Kabul', postalCode: '1001' }, { name: 'Kart-e-Seh', postalCode: '1003' }, { name: 'Khair Khana', postalCode: '1005' }] },
      { name: 'Herat', cities: [{ name: 'Herat', postalCode: '3001' }, { name: 'Injil', postalCode: '3002' }] },
      { name: 'Kandahar', cities: [{ name: 'Kandahar', postalCode: '3801' }, { name: 'Spin Boldak', postalCode: '3802' }] },
      { name: 'Balkh', cities: [{ name: 'Mazar-i-Sharif', postalCode: '2001' }, { name: 'Dehdadi', postalCode: '2002' }] },
      { name: 'Nangarhar', cities: [{ name: 'Jalalabad', postalCode: '2601' }, { name: 'Behsud', postalCode: '2602' }] },
      { name: 'Baghlan', cities: [{ name: 'Pul-e-Khumri', postalCode: '3601' }] },
      { name: 'Kunduz', cities: [{ name: 'Kunduz', postalCode: '3201' }] },
      { name: 'Ghazni', cities: [{ name: 'Ghazni', postalCode: '2401' }] },
      { name: 'Helmand', cities: [{ name: 'Lashkar Gah', postalCode: '3901' }] },
      { name: 'Paktia', cities: [{ name: 'Gardez', postalCode: '2801' }] }
    ]
  },
  {
    name: 'Albania', code: 'AL',
    states: [
      { name: 'Tirana', cities: [{ name: 'Tirana', postalCode: '1001' }, { name: 'Kamez', postalCode: '1031' }, { name: 'Vora', postalCode: '1032' }] },
      { name: 'Durrës', cities: [{ name: 'Durrës', postalCode: '2001' }, { name: 'Shijak', postalCode: '2002' }] },
      { name: 'Vlorë', cities: [{ name: 'Vlorë', postalCode: '9401' }, { name: 'Sarandë', postalCode: '9701' }] },
      { name: 'Shkodër', cities: [{ name: 'Shkodër', postalCode: '4001' }] },
      { name: 'Fier', cities: [{ name: 'Fier', postalCode: '9301' }] },
      { name: 'Korçë', cities: [{ name: 'Korçë', postalCode: '7001' }] },
      { name: 'Elbasan', cities: [{ name: 'Elbasan', postalCode: '3001' }] },
      { name: 'Berat', cities: [{ name: 'Berat', postalCode: '5001' }] }
    ]
  },
  {
    name: 'Algeria', code: 'DZ',
    states: [
      { name: 'Algiers', cities: [{ name: 'Algiers', postalCode: '16000' }, { name: 'Bab El Oued', postalCode: '16001' }, { name: 'Hydra', postalCode: '16035' }, { name: 'El Harrach', postalCode: '16040' }] },
      { name: 'Oran', cities: [{ name: 'Oran', postalCode: '31000' }, { name: 'Es Senia', postalCode: '31003' }] },
      { name: 'Constantine', cities: [{ name: 'Constantine', postalCode: '25000' }, { name: 'El Khroub', postalCode: '25003' }] },
      { name: 'Annaba', cities: [{ name: 'Annaba', postalCode: '23000' }] },
      { name: 'Blida', cities: [{ name: 'Blida', postalCode: '09000' }, { name: 'Boufarik', postalCode: '09005' }] },
      { name: 'Sétif', cities: [{ name: 'Sétif', postalCode: '19000' }] },
      { name: 'Batna', cities: [{ name: 'Batna', postalCode: '05000' }] },
      { name: 'Tlemcen', cities: [{ name: 'Tlemcen', postalCode: '13000' }] }
    ]
  },
  {
    name: 'Argentina', code: 'AR',
    states: [
      { name: 'Buenos Aires', cities: [{ name: 'Buenos Aires', postalCode: 'C1000' }, { name: 'La Plata', postalCode: 'B1900' }, { name: 'Mar del Plata', postalCode: 'B7600' }, { name: 'Quilmes', postalCode: 'B1878' }] },
      { name: 'Córdoba', cities: [{ name: 'Córdoba', postalCode: 'X5000' }, { name: 'Villa Carlos Paz', postalCode: 'X5152' }] },
      { name: 'Santa Fe', cities: [{ name: 'Rosario', postalCode: 'S2000' }, { name: 'Santa Fe', postalCode: 'S3000' }] },
      { name: 'Mendoza', cities: [{ name: 'Mendoza', postalCode: 'M5500' }, { name: 'San Rafael', postalCode: 'M5600' }] },
      { name: 'Tucumán', cities: [{ name: 'San Miguel de Tucumán', postalCode: 'T4000' }] },
      { name: 'Salta', cities: [{ name: 'Salta', postalCode: 'A4400' }] },
      { name: 'Misiones', cities: [{ name: 'Posadas', postalCode: 'N3300' }] },
      { name: 'Chaco', cities: [{ name: 'Resistencia', postalCode: 'H3500' }] }
    ]
  },
  {
    name: 'Armenia', code: 'AM',
    states: [
      { name: 'Yerevan', cities: [{ name: 'Yerevan', postalCode: '0001' }, { name: 'Nor Nork', postalCode: '0047' }, { name: 'Erebuni', postalCode: '0085' }] },
      { name: 'Ararat', cities: [{ name: 'Artashat', postalCode: '0901' }, { name: 'Ararat', postalCode: '0903' }] },
      { name: 'Kotayk', cities: [{ name: 'Hrazdan', postalCode: '2301' }, { name: 'Abovyan', postalCode: '2201' }] },
      { name: 'Lori', cities: [{ name: 'Vanadzor', postalCode: '2001' }] },
      { name: 'Syunik', cities: [{ name: 'Kapan', postalCode: '3301' }] }
    ]
  },
  {
    name: 'Australia', code: 'AU',
    states: [
      { name: 'New South Wales', cities: [{ name: 'Sydney', postalCode: '2000' }, { name: 'Newcastle', postalCode: '2300' }, { name: 'Wollongong', postalCode: '2500' }, { name: 'Parramatta', postalCode: '2150' }, { name: 'Penrith', postalCode: '2750' }] },
      { name: 'Victoria', cities: [{ name: 'Melbourne', postalCode: '3000' }, { name: 'Geelong', postalCode: '3220' }, { name: 'Ballarat', postalCode: '3350' }, { name: 'Bendigo', postalCode: '3550' }] },
      { name: 'Queensland', cities: [{ name: 'Brisbane', postalCode: '4000' }, { name: 'Gold Coast', postalCode: '4217' }, { name: 'Townsville', postalCode: '4810' }, { name: 'Cairns', postalCode: '4870' }] },
      { name: 'Western Australia', cities: [{ name: 'Perth', postalCode: '6000' }, { name: 'Fremantle', postalCode: '6160' }, { name: 'Bunbury', postalCode: '6230' }] },
      { name: 'South Australia', cities: [{ name: 'Adelaide', postalCode: '5000' }, { name: 'Mount Gambier', postalCode: '5290' }] },
      { name: 'Tasmania', cities: [{ name: 'Hobart', postalCode: '7000' }, { name: 'Launceston', postalCode: '7250' }] },
      { name: 'Australian Capital Territory', cities: [{ name: 'Canberra', postalCode: '2600' }, { name: 'Belconnen', postalCode: '2617' }] },
      { name: 'Northern Territory', cities: [{ name: 'Darwin', postalCode: '0800' }, { name: 'Alice Springs', postalCode: '0870' }] }
    ]
  },
  {
    name: 'Austria', code: 'AT',
    states: [
      { name: 'Vienna', cities: [{ name: 'Vienna', postalCode: '1010' }, { name: 'Favoriten', postalCode: '1100' }, { name: 'Donaustadt', postalCode: '1220' }] },
      { name: 'Lower Austria', cities: [{ name: 'St. Pölten', postalCode: '3100' }, { name: 'Wiener Neustadt', postalCode: '2700' }, { name: 'Krems', postalCode: '3500' }] },
      { name: 'Upper Austria', cities: [{ name: 'Linz', postalCode: '4020' }, { name: 'Wels', postalCode: '4600' }, { name: 'Steyr', postalCode: '4400' }] },
      { name: 'Styria', cities: [{ name: 'Graz', postalCode: '8010' }, { name: 'Leoben', postalCode: '8700' }] },
      { name: 'Tyrol', cities: [{ name: 'Innsbruck', postalCode: '6020' }, { name: 'Kufstein', postalCode: '6330' }] },
      { name: 'Salzburg', cities: [{ name: 'Salzburg', postalCode: '5020' }, { name: 'Hallein', postalCode: '5400' }] },
      { name: 'Carinthia', cities: [{ name: 'Klagenfurt', postalCode: '9020' }, { name: 'Villach', postalCode: '9500' }] },
      { name: 'Vorarlberg', cities: [{ name: 'Bregenz', postalCode: '6900' }, { name: 'Dornbirn', postalCode: '6850' }] },
      { name: 'Burgenland', cities: [{ name: 'Eisenstadt', postalCode: '7000' }] }
    ]
  },
  {
    name: 'Azerbaijan', code: 'AZ',
    states: [
      { name: 'Baku', cities: [{ name: 'Baku', postalCode: 'AZ1000' }, { name: 'Sabunchu', postalCode: 'AZ1100' }, { name: 'Surakhani', postalCode: 'AZ1200' }] },
      { name: 'Ganja', cities: [{ name: 'Ganja', postalCode: 'AZ2000' }] },
      { name: 'Sumgayit', cities: [{ name: 'Sumgayit', postalCode: 'AZ5000' }] },
      { name: 'Nakhchivan', cities: [{ name: 'Nakhchivan', postalCode: 'AZ7000' }] },
      { name: 'Mingachevir', cities: [{ name: 'Mingachevir', postalCode: 'AZ4500' }] }
    ]
  },
  {
    name: 'Bahrain', code: 'BH',
    states: [
      { name: 'Capital', cities: [{ name: 'Manama', postalCode: '317' }, { name: 'Juffair', postalCode: '319' }] },
      { name: 'Northern', cities: [{ name: 'Muharraq', postalCode: '200' }, { name: 'Hidd', postalCode: '212' }] },
      { name: 'Southern', cities: [{ name: 'Riffa', postalCode: '931' }, { name: 'Isa Town', postalCode: '806' }] },
      { name: 'Central', cities: [{ name: 'Manama Central', postalCode: '410' }] },
      { name: 'Muharraq', cities: [{ name: 'Muharraq City', postalCode: '201' }] }
    ]
  },
  {
    name: 'Bangladesh', code: 'BD',
    states: [
      { name: 'Dhaka', cities: [{ name: 'Dhaka', postalCode: '1000' }, { name: 'Narayanganj', postalCode: '1400' }, { name: 'Gazipur', postalCode: '1700' }, { name: 'Savar', postalCode: '1340' }] },
      { name: 'Chittagong', cities: [{ name: 'Chittagong', postalCode: '4000' }, { name: 'Cox\'s Bazar', postalCode: '4700' }, { name: 'Comilla', postalCode: '3500' }] },
      { name: 'Sylhet', cities: [{ name: 'Sylhet', postalCode: '3100' }, { name: 'Moulvibazar', postalCode: '3200' }] },
      { name: 'Khulna', cities: [{ name: 'Khulna', postalCode: '9000' }, { name: 'Jessore', postalCode: '7400' }] },
      { name: 'Rajshahi', cities: [{ name: 'Rajshahi', postalCode: '6000' }, { name: 'Bogra', postalCode: '5800' }] },
      { name: 'Barisal', cities: [{ name: 'Barisal', postalCode: '8200' }] },
      { name: 'Rangpur', cities: [{ name: 'Rangpur', postalCode: '5400' }] },
      { name: 'Mymensingh', cities: [{ name: 'Mymensingh', postalCode: '2200' }] }
    ]
  },
  {
    name: 'Belgium', code: 'BE',
    states: [
      { name: 'Brussels', cities: [{ name: 'Brussels', postalCode: '1000' }, { name: 'Anderlecht', postalCode: '1070' }, { name: 'Ixelles', postalCode: '1050' }, { name: 'Molenbeek', postalCode: '1080' }] },
      { name: 'Antwerp', cities: [{ name: 'Antwerp', postalCode: '2000' }, { name: 'Mechelen', postalCode: '2800' }, { name: 'Lier', postalCode: '2500' }] },
      { name: 'East Flanders', cities: [{ name: 'Ghent', postalCode: '9000' }, { name: 'Aalst', postalCode: '9300' }] },
      { name: 'West Flanders', cities: [{ name: 'Bruges', postalCode: '8000' }, { name: 'Kortrijk', postalCode: '8500' }] },
      { name: 'Flemish Brabant', cities: [{ name: 'Leuven', postalCode: '3000' }, { name: 'Vilvoorde', postalCode: '1800' }] },
      { name: 'Liège', cities: [{ name: 'Liège', postalCode: '4000' }, { name: 'Verviers', postalCode: '4800' }] },
      { name: 'Hainaut', cities: [{ name: 'Charleroi', postalCode: '6000' }, { name: 'Mons', postalCode: '7000' }] },
      { name: 'Namur', cities: [{ name: 'Namur', postalCode: '5000' }] },
      { name: 'Luxembourg', cities: [{ name: 'Arlon', postalCode: '6700' }] }
    ]
  },
  {
    name: 'Bolivia', code: 'BO',
    states: [
      { name: 'Santa Cruz', cities: [{ name: 'Santa Cruz de la Sierra', postalCode: 'SC-000' }, { name: 'Montero', postalCode: 'SC-001' }] },
      { name: 'La Paz', cities: [{ name: 'La Paz', postalCode: 'LP-000' }, { name: 'El Alto', postalCode: 'LP-001' }] },
      { name: 'Cochabamba', cities: [{ name: 'Cochabamba', postalCode: 'CB-000' }, { name: 'Sacaba', postalCode: 'CB-001' }] },
      { name: 'Oruro', cities: [{ name: 'Oruro', postalCode: 'OR-000' }] },
      { name: 'Potosí', cities: [{ name: 'Potosí', postalCode: 'PO-000' }] },
      { name: 'Tarija', cities: [{ name: 'Tarija', postalCode: 'TJ-000' }] }
    ]
  },
  {
    name: 'Brazil', code: 'BR',
    states: [
      { name: 'São Paulo', cities: [{ name: 'São Paulo', postalCode: '01310-100' }, { name: 'Campinas', postalCode: '13010-001' }, { name: 'Santos', postalCode: '11010-001' }, { name: 'Ribeirão Preto', postalCode: '14010-001' }] },
      { name: 'Rio de Janeiro', cities: [{ name: 'Rio de Janeiro', postalCode: '20040-020' }, { name: 'Niterói', postalCode: '24020-005' }, { name: 'Petrópolis', postalCode: '25610-060' }] },
      { name: 'Minas Gerais', cities: [{ name: 'Belo Horizonte', postalCode: '30110-001' }, { name: 'Uberlândia', postalCode: '38400-902' }, { name: 'Contagem', postalCode: '32010-020' }] },
      { name: 'Bahia', cities: [{ name: 'Salvador', postalCode: '40010-000' }, { name: 'Feira de Santana', postalCode: '44001-008' }] },
      { name: 'Paraná', cities: [{ name: 'Curitiba', postalCode: '80010-010' }, { name: 'Londrina', postalCode: '86010-000' }] },
      { name: 'Rio Grande do Sul', cities: [{ name: 'Porto Alegre', postalCode: '90010-000' }, { name: 'Caxias do Sul', postalCode: '95010-001' }] },
      { name: 'Pernambuco', cities: [{ name: 'Recife', postalCode: '50010-000' }, { name: 'Caruaru', postalCode: '55000-000' }] },
      { name: 'Ceará', cities: [{ name: 'Fortaleza', postalCode: '60010-000' }] },
      { name: 'Pará', cities: [{ name: 'Belém', postalCode: '66010-000' }] },
      { name: 'Amazonas', cities: [{ name: 'Manaus', postalCode: '69010-000' }] },
      { name: 'Goiás', cities: [{ name: 'Goiânia', postalCode: '74010-010' }] },
      { name: 'Distrito Federal', cities: [{ name: 'Brasília', postalCode: '70040-010' }] }
    ]
  },
  {
    name: 'Canada', code: 'CA',
    states: [
      { name: 'Ontario', cities: [{ name: 'Toronto', postalCode: 'M5H 2N2' }, { name: 'Ottawa', postalCode: 'K1P 1J1' }, { name: 'Mississauga', postalCode: 'L5B 3C1' }, { name: 'Hamilton', postalCode: 'L8N 1A1' }, { name: 'London', postalCode: 'N6A 1E1' }] },
      { name: 'Quebec', cities: [{ name: 'Montreal', postalCode: 'H3A 1A1' }, { name: 'Quebec City', postalCode: 'G1R 2J5' }, { name: 'Laval', postalCode: 'H7S 2G6' }] },
      { name: 'British Columbia', cities: [{ name: 'Vancouver', postalCode: 'V6B 1A1' }, { name: 'Victoria', postalCode: 'V8W 1A1' }, { name: 'Surrey', postalCode: 'V3T 1A1' }] },
      { name: 'Alberta', cities: [{ name: 'Calgary', postalCode: 'T2P 1J9' }, { name: 'Edmonton', postalCode: 'T5J 0N3' }, { name: 'Red Deer', postalCode: 'T4N 1G9' }] },
      { name: 'Manitoba', cities: [{ name: 'Winnipeg', postalCode: 'R3C 0V8' }, { name: 'Brandon', postalCode: 'R7A 0A1' }] },
      { name: 'Saskatchewan', cities: [{ name: 'Saskatoon', postalCode: 'S7K 0J5' }, { name: 'Regina', postalCode: 'S4P 0A1' }] },
      { name: 'Nova Scotia', cities: [{ name: 'Halifax', postalCode: 'B3J 1A1' }] },
      { name: 'New Brunswick', cities: [{ name: 'Moncton', postalCode: 'E1C 1A1' }, { name: 'Fredericton', postalCode: 'E3B 1A1' }] },
      { name: 'Newfoundland and Labrador', cities: [{ name: 'St. John\'s', postalCode: 'A1C 5H7' }] },
      { name: 'Prince Edward Island', cities: [{ name: 'Charlottetown', postalCode: 'C1A 1A1' }] }
    ]
  },
  {
    name: 'Chile', code: 'CL',
    states: [
      { name: 'Metropolitana de Santiago', cities: [{ name: 'Santiago', postalCode: '8320000' }, { name: 'Maipú', postalCode: '9250000' }, { name: 'Las Condes', postalCode: '7550000' }, { name: 'Providencia', postalCode: '7500000' }] },
      { name: 'Valparaíso', cities: [{ name: 'Valparaíso', postalCode: '2360000' }, { name: 'Viña del Mar', postalCode: '2520000' }] },
      { name: 'Biobío', cities: [{ name: 'Concepción', postalCode: '4030000' }, { name: 'Talcahuano', postalCode: '4030000' }] },
      { name: 'La Araucanía', cities: [{ name: 'Temuco', postalCode: '4780000' }] },
      { name: 'Los Lagos', cities: [{ name: 'Puerto Montt', postalCode: '5480000' }] }
    ]
  },
  {
    name: 'China', code: 'CN',
    states: [
      { name: 'Beijing', cities: [{ name: 'Beijing', postalCode: '100000' }, { name: 'Chaoyang', postalCode: '100020' }, { name: 'Haidian', postalCode: '100080' }, { name: 'Dongcheng', postalCode: '100010' }] },
      { name: 'Shanghai', cities: [{ name: 'Shanghai', postalCode: '200000' }, { name: 'Pudong', postalCode: '200120' }, { name: 'Jing\'an', postalCode: '200040' }, { name: 'Huangpu', postalCode: '200001' }] },
      { name: 'Guangdong', cities: [{ name: 'Guangzhou', postalCode: '510000' }, { name: 'Shenzhen', postalCode: '518000' }, { name: 'Dongguan', postalCode: '523000' }, { name: 'Foshan', postalCode: '528000' }] },
      { name: 'Zhejiang', cities: [{ name: 'Hangzhou', postalCode: '310000' }, { name: 'Ningbo', postalCode: '315000' }, { name: 'Wenzhou', postalCode: '325000' }] },
      { name: 'Jiangsu', cities: [{ name: 'Nanjing', postalCode: '210000' }, { name: 'Suzhou', postalCode: '215000' }, { name: 'Wuxi', postalCode: '214000' }] },
      { name: 'Sichuan', cities: [{ name: 'Chengdu', postalCode: '610000' }, { name: 'Mianyang', postalCode: '621000' }] },
      { name: 'Hubei', cities: [{ name: 'Wuhan', postalCode: '430000' }, { name: 'Yichang', postalCode: '443000' }] },
      { name: 'Hunan', cities: [{ name: 'Changsha', postalCode: '410000' }, { name: 'Zhuzhou', postalCode: '412000' }] },
      { name: 'Shandong', cities: [{ name: 'Jinan', postalCode: '250000' }, { name: 'Qingdao', postalCode: '266000' }, { name: 'Yantai', postalCode: '264000' }] },
      { name: 'Henan', cities: [{ name: 'Zhengzhou', postalCode: '450000' }, { name: 'Luoyang', postalCode: '471000' }] },
      { name: 'Fujian', cities: [{ name: 'Fuzhou', postalCode: '350000' }, { name: 'Xiamen', postalCode: '361000' }] },
      { name: 'Chongqing', cities: [{ name: 'Chongqing', postalCode: '400000' }, { name: 'Jiangbei', postalCode: '400021' }] }
    ]
  },
  {
    name: 'Colombia', code: 'CO',
    states: [
      { name: 'Cundinamarca', cities: [{ name: 'Bogotá', postalCode: '110111' }, { name: 'Soacha', postalCode: '251054' }, { name: 'Chía', postalCode: '250001' }] },
      { name: 'Antioquia', cities: [{ name: 'Medellín', postalCode: '050001' }, { name: 'Bello', postalCode: '051001' }, { name: 'Envigado', postalCode: '055422' }] },
      { name: 'Valle del Cauca', cities: [{ name: 'Cali', postalCode: '760001' }, { name: 'Buenaventura', postalCode: '764001' }] },
      { name: 'Atlántico', cities: [{ name: 'Barranquilla', postalCode: '080001' }, { name: 'Soledad', postalCode: '083001' }] },
      { name: 'Bolívar', cities: [{ name: 'Cartagena', postalCode: '130001' }] },
      { name: 'Santander', cities: [{ name: 'Bucaramanga', postalCode: '680001' }] },
      { name: 'Córdoba', cities: [{ name: 'Montería', postalCode: '230001' }] }
    ]
  },
  {
    name: 'Croatia', code: 'HR',
    states: [
      { name: 'Zagreb City', cities: [{ name: 'Zagreb', postalCode: '10000' }, { name: 'Sesvete', postalCode: '10360' }] },
      { name: 'Split-Dalmatia', cities: [{ name: 'Split', postalCode: '21000' }, { name: 'Kaštela', postalCode: '21212' }] },
      { name: 'Primorje-Gorski Kotar', cities: [{ name: 'Rijeka', postalCode: '51000' }] },
      { name: 'Osijek-Baranja', cities: [{ name: 'Osijek', postalCode: '31000' }] },
      { name: 'Zadar', cities: [{ name: 'Zadar', postalCode: '23000' }] },
      { name: 'Dubrovnik-Neretva', cities: [{ name: 'Dubrovnik', postalCode: '20000' }] }
    ]
  },
  {
    name: 'Czech Republic', code: 'CZ',
    states: [
      { name: 'Prague', cities: [{ name: 'Prague 1', postalCode: '110 00' }, { name: 'Prague 2', postalCode: '120 00' }, { name: 'Prague 6', postalCode: '160 00' }] },
      { name: 'South Bohemia', cities: [{ name: 'České Budějovice', postalCode: '370 01' }] },
      { name: 'South Moravia', cities: [{ name: 'Brno', postalCode: '601 00' }, { name: 'Znojmo', postalCode: '669 02' }] },
      { name: 'Moravia-Silesia', cities: [{ name: 'Ostrava', postalCode: '702 00' }, { name: 'Opava', postalCode: '746 01' }] },
      { name: 'Olomouc', cities: [{ name: 'Olomouc', postalCode: '772 00' }] },
      { name: 'Plzeň', cities: [{ name: 'Plzeň', postalCode: '301 00' }] }
    ]
  },
  {
    name: 'Denmark', code: 'DK',
    states: [
      { name: 'Capital Region', cities: [{ name: 'Copenhagen', postalCode: '1050' }, { name: 'Frederiksberg', postalCode: '2000' }, { name: 'Helsingør', postalCode: '3000' }] },
      { name: 'Central Denmark', cities: [{ name: 'Aarhus', postalCode: '8000' }, { name: 'Silkeborg', postalCode: '8600' }] },
      { name: 'Southern Denmark', cities: [{ name: 'Odense', postalCode: '5000' }, { name: 'Esbjerg', postalCode: '6700' }] },
      { name: 'North Denmark', cities: [{ name: 'Aalborg', postalCode: '9000' }] },
      { name: 'Region Zealand', cities: [{ name: 'Roskilde', postalCode: '4000' }, { name: 'Næstved', postalCode: '4700' }] }
    ]
  },
  {
    name: 'Egypt', code: 'EG',
    states: [
      { name: 'Cairo', cities: [{ name: 'Cairo', postalCode: '11511' }, { name: 'Heliopolis', postalCode: '11341' }, { name: 'Nasr City', postalCode: '11371' }, { name: 'Maadi', postalCode: '11431' }] },
      { name: 'Alexandria', cities: [{ name: 'Alexandria', postalCode: '21511' }, { name: 'Sidi Gaber', postalCode: '21523' }] },
      { name: 'Giza', cities: [{ name: 'Giza', postalCode: '12511' }, { name: '6th of October City', postalCode: '12566' }] },
      { name: 'Qalyubia', cities: [{ name: 'Shubra El Kheima', postalCode: '13711' }] },
      { name: 'Sharqia', cities: [{ name: 'Zagazig', postalCode: '44511' }] },
      { name: 'Dakahlia', cities: [{ name: 'Mansoura', postalCode: '35511' }] },
      { name: 'Gharbia', cities: [{ name: 'Tanta', postalCode: '31511' }] }
    ]
  },
  {
    name: 'Ethiopia', code: 'ET',
    states: [
      { name: 'Addis Ababa', cities: [{ name: 'Addis Ababa', postalCode: '1000' }, { name: 'Bole', postalCode: '1110' }, { name: 'Kirkos', postalCode: '1250' }] },
      { name: 'Oromia', cities: [{ name: 'Adama', postalCode: '2000' }, { name: 'Jimma', postalCode: '7000' }] },
      { name: 'Amhara', cities: [{ name: 'Bahir Dar', postalCode: '6000' }, { name: 'Gondar', postalCode: '6200' }] },
      { name: 'Tigray', cities: [{ name: 'Mekelle', postalCode: '7000' }] },
      { name: 'Dire Dawa', cities: [{ name: 'Dire Dawa', postalCode: '3000' }] }
    ]
  },
  {
    name: 'Finland', code: 'FI',
    states: [
      { name: 'Uusimaa', cities: [{ name: 'Helsinki', postalCode: '00100' }, { name: 'Espoo', postalCode: '02100' }, { name: 'Vantaa', postalCode: '01300' }] },
      { name: 'Pirkanmaa', cities: [{ name: 'Tampere', postalCode: '33100' }] },
      { name: 'Southwest Finland', cities: [{ name: 'Turku', postalCode: '20100' }] },
      { name: 'North Ostrobothnia', cities: [{ name: 'Oulu', postalCode: '90100' }] },
      { name: 'Central Finland', cities: [{ name: 'Jyväskylä', postalCode: '40100' }] },
      { name: 'Lapland', cities: [{ name: 'Rovaniemi', postalCode: '96100' }] }
    ]
  },
  {
    name: 'France', code: 'FR',
    states: [
      { name: 'Île-de-France', cities: [{ name: 'Paris', postalCode: '75001' }, { name: 'Boulogne-Billancourt', postalCode: '92100' }, { name: 'Saint-Denis', postalCode: '93200' }, { name: 'Versailles', postalCode: '78000' }] },
      { name: 'Auvergne-Rhône-Alpes', cities: [{ name: 'Lyon', postalCode: '69001' }, { name: 'Grenoble', postalCode: '38000' }, { name: 'Saint-Étienne', postalCode: '42000' }] },
      { name: 'Provence-Alpes-Côte d\'Azur', cities: [{ name: 'Marseille', postalCode: '13001' }, { name: 'Nice', postalCode: '06000' }, { name: 'Toulon', postalCode: '83000' }] },
      { name: 'Nouvelle-Aquitaine', cities: [{ name: 'Bordeaux', postalCode: '33000' }, { name: 'Limoges', postalCode: '87000' }] },
      { name: 'Occitanie', cities: [{ name: 'Toulouse', postalCode: '31000' }, { name: 'Montpellier', postalCode: '34000' }] },
      { name: 'Hauts-de-France', cities: [{ name: 'Lille', postalCode: '59000' }, { name: 'Amiens', postalCode: '80000' }] },
      { name: 'Grand Est', cities: [{ name: 'Strasbourg', postalCode: '67000' }, { name: 'Reims', postalCode: '51100' }] },
      { name: 'Pays de la Loire', cities: [{ name: 'Nantes', postalCode: '44000' }, { name: 'Le Mans', postalCode: '72000' }] },
      { name: 'Bretagne', cities: [{ name: 'Rennes', postalCode: '35000' }, { name: 'Brest', postalCode: '29200' }] },
      { name: 'Normandie', cities: [{ name: 'Rouen', postalCode: '76000' }, { name: 'Caen', postalCode: '14000' }] }
    ]
  },
  {
    name: 'Germany', code: 'DE',
    states: [
      { name: 'Berlin', cities: [{ name: 'Berlin', postalCode: '10115' }, { name: 'Mitte', postalCode: '10117' }, { name: 'Charlottenburg', postalCode: '10623' }, { name: 'Kreuzberg', postalCode: '10961' }] },
      { name: 'Bavaria', cities: [{ name: 'Munich', postalCode: '80331' }, { name: 'Nuremberg', postalCode: '90402' }, { name: 'Augsburg', postalCode: '86150' }, { name: 'Regensburg', postalCode: '93047' }] },
      { name: 'North Rhine-Westphalia', cities: [{ name: 'Cologne', postalCode: '50667' }, { name: 'Düsseldorf', postalCode: '40210' }, { name: 'Dortmund', postalCode: '44135' }, { name: 'Essen', postalCode: '45127' }] },
      { name: 'Baden-Württemberg', cities: [{ name: 'Stuttgart', postalCode: '70173' }, { name: 'Karlsruhe', postalCode: '76133' }, { name: 'Freiburg', postalCode: '79098' }, { name: 'Mannheim', postalCode: '68159' }] },
      { name: 'Hamburg', cities: [{ name: 'Hamburg', postalCode: '20095' }, { name: 'Altona', postalCode: '22765' }] },
      { name: 'Hesse', cities: [{ name: 'Frankfurt', postalCode: '60311' }, { name: 'Wiesbaden', postalCode: '65183' }, { name: 'Kassel', postalCode: '34117' }] },
      { name: 'Saxony', cities: [{ name: 'Dresden', postalCode: '01067' }, { name: 'Leipzig', postalCode: '04109' }, { name: 'Chemnitz', postalCode: '09111' }] },
      { name: 'Lower Saxony', cities: [{ name: 'Hanover', postalCode: '30159' }, { name: 'Braunschweig', postalCode: '38100' }, { name: 'Osnabrück', postalCode: '49074' }] },
      { name: 'Brandenburg', cities: [{ name: 'Potsdam', postalCode: '14467' }, { name: 'Cottbus', postalCode: '03046' }] },
      { name: 'Rhineland-Palatinate', cities: [{ name: 'Mainz', postalCode: '55116' }, { name: 'Ludwigshafen', postalCode: '67059' }] }
    ]
  },
  {
    name: 'Ghana', code: 'GH',
    states: [
      { name: 'Greater Accra', cities: [{ name: 'Accra', postalCode: 'GA-001' }, { name: 'Tema', postalCode: 'GA-002' }, { name: 'Madina', postalCode: 'GA-003' }] },
      { name: 'Ashanti', cities: [{ name: 'Kumasi', postalCode: 'AK-001' }, { name: 'Obuasi', postalCode: 'AK-002' }] },
      { name: 'Western', cities: [{ name: 'Takoradi', postalCode: 'WR-001' }, { name: 'Tarkwa', postalCode: 'WR-002' }] },
      { name: 'Central', cities: [{ name: 'Cape Coast', postalCode: 'CR-001' }] },
      { name: 'Northern', cities: [{ name: 'Tamale', postalCode: 'NR-001' }] },
      { name: 'Volta', cities: [{ name: 'Ho', postalCode: 'VR-001' }] },
      { name: 'Eastern', cities: [{ name: 'Koforidua', postalCode: 'ER-001' }] }
    ]
  },
  {
    name: 'Greece', code: 'GR',
    states: [
      { name: 'Attica', cities: [{ name: 'Athens', postalCode: '10431' }, { name: 'Piraeus', postalCode: '18531' }, { name: 'Peristeri', postalCode: '12131' }, { name: 'Kallithea', postalCode: '17671' }] },
      { name: 'Central Macedonia', cities: [{ name: 'Thessaloniki', postalCode: '54621' }, { name: 'Serres', postalCode: '62122' }] },
      { name: 'Crete', cities: [{ name: 'Heraklion', postalCode: '71201' }, { name: 'Chania', postalCode: '73100' }] },
      { name: 'Western Greece', cities: [{ name: 'Patras', postalCode: '26221' }] },
      { name: 'Thessaly', cities: [{ name: 'Larissa', postalCode: '41222' }, { name: 'Volos', postalCode: '38221' }] }
    ]
  },
  {
    name: 'Hungary', code: 'HU',
    states: [
      { name: 'Budapest', cities: [{ name: 'Budapest I', postalCode: '1011' }, { name: 'Budapest II', postalCode: '1021' }, { name: 'Budapest VII', postalCode: '1071' }, { name: 'Budapest XI', postalCode: '1111' }] },
      { name: 'Pest', cities: [{ name: 'Érd', postalCode: '2030' }, { name: 'Gödöllő', postalCode: '2100' }] },
      { name: 'Hajdú-Bihar', cities: [{ name: 'Debrecen', postalCode: '4024' }] },
      { name: 'Borsod-Abaúj-Zemplén', cities: [{ name: 'Miskolc', postalCode: '3525' }] },
      { name: 'Csongrád-Csanád', cities: [{ name: 'Szeged', postalCode: '6720' }] },
      { name: 'Győr-Moson-Sopron', cities: [{ name: 'Győr', postalCode: '9021' }] }
    ]
  },
  {
    name: 'India', code: 'IN',
    states: [
      { name: 'Andhra Pradesh', cities: [{ name: 'Visakhapatnam', postalCode: '530001' }, { name: 'Vijayawada', postalCode: '520001' }, { name: 'Guntur', postalCode: '522001' }, { name: 'Tirupati', postalCode: '517501' }, { name: 'Rajahmundry', postalCode: '533101' }, { name: 'Kakinada', postalCode: '533001' }, { name: 'Kurnool', postalCode: '518001' }, { name: 'Nellore', postalCode: '524001' }, { name: 'Anantapur', postalCode: '515001' }, { name: 'Kadapa', postalCode: '516001' }] },
      { name: 'Arunachal Pradesh', cities: [{ name: 'Itanagar', postalCode: '791111' }, { name: 'Naharlagun', postalCode: '791110' }, { name: 'Pasighat', postalCode: '791102' }, { name: 'Tezpur', postalCode: '784001' }] },
      { name: 'Assam', cities: [{ name: 'Guwahati', postalCode: '781001' }, { name: 'Dibrugarh', postalCode: '786001' }, { name: 'Silchar', postalCode: '788001' }, { name: 'Jorhat', postalCode: '785001' }, { name: 'Nagaon', postalCode: '782001' }, { name: 'Tinsukia', postalCode: '786125' }] },
      { name: 'Bihar', cities: [{ name: 'Patna', postalCode: '800001' }, { name: 'Gaya', postalCode: '823001' }, { name: 'Bhagalpur', postalCode: '812001' }, { name: 'Muzaffarpur', postalCode: '842001' }, { name: 'Purnia', postalCode: '854301' }, { name: 'Darbhanga', postalCode: '846004' }, { name: 'Arrah', postalCode: '802301' }] },
      { name: 'Chhattisgarh', cities: [{ name: 'Raipur', postalCode: '492001' }, { name: 'Bhilai', postalCode: '490001' }, { name: 'Bilaspur', postalCode: '495001' }, { name: 'Korba', postalCode: '495677' }, { name: 'Durg', postalCode: '491001' }, { name: 'Rajnandgaon', postalCode: '491441' }] },
      { name: 'Delhi', cities: [{ name: 'New Delhi', postalCode: '110001' }, { name: 'Connaught Place', postalCode: '110001' }, { name: 'Karol Bagh', postalCode: '110005' }, { name: 'Lajpat Nagar', postalCode: '110024' }, { name: 'Rohini', postalCode: '110085' }, { name: 'Dwarka', postalCode: '110075' }, { name: 'Saket', postalCode: '110017' }, { name: 'Pitampura', postalCode: '110034' }, { name: 'Janakpuri', postalCode: '110058' }, { name: 'Nehru Place', postalCode: '110019' }] },
      { name: 'Goa', cities: [{ name: 'Panaji', postalCode: '403001' }, { name: 'Margao', postalCode: '403601' }, { name: 'Vasco da Gama', postalCode: '403802' }, { name: 'Mapusa', postalCode: '403507' }, { name: 'Ponda', postalCode: '403401' }] },
      { name: 'Gujarat', cities: [{ name: 'Ahmedabad', postalCode: '380001' }, { name: 'Surat', postalCode: '395001' }, { name: 'Vadodara', postalCode: '390001' }, { name: 'Rajkot', postalCode: '360001' }, { name: 'Bhavnagar', postalCode: '364001' }, { name: 'Jamnagar', postalCode: '361001' }, { name: 'Gandhinagar', postalCode: '382010' }, { name: 'Anand', postalCode: '388001' }, { name: 'Bharuch', postalCode: '392001' }, { name: 'Morbi', postalCode: '363641' }] },
      { name: 'Haryana', cities: [{ name: 'Faridabad', postalCode: '121001' }, { name: 'Gurgaon', postalCode: '122001' }, { name: 'Panipat', postalCode: '132103' }, { name: 'Ambala', postalCode: '134003' }, { name: 'Yamunanagar', postalCode: '135001' }, { name: 'Rohtak', postalCode: '124001' }, { name: 'Hisar', postalCode: '125001' }, { name: 'Karnal', postalCode: '132001' }, { name: 'Sonipat', postalCode: '131001' }, { name: 'Panchkula', postalCode: '134112' }] },
      { name: 'Himachal Pradesh', cities: [{ name: 'Shimla', postalCode: '171001' }, { name: 'Dharamsala', postalCode: '176215' }, { name: 'Mandi', postalCode: '175001' }, { name: 'Solan', postalCode: '173211' }, { name: 'Kullu', postalCode: '175101' }, { name: 'Baddi', postalCode: '173205' }] },
      { name: 'Jharkhand', cities: [{ name: 'Ranchi', postalCode: '834001' }, { name: 'Jamshedpur', postalCode: '831001' }, { name: 'Dhanbad', postalCode: '826001' }, { name: 'Bokaro', postalCode: '827001' }, { name: 'Deoghar', postalCode: '814112' }, { name: 'Hazaribagh', postalCode: '825301' }] },
      { name: 'Karnataka', cities: [{ name: 'Bengaluru', postalCode: '560001' }, { name: 'Mysuru', postalCode: '570001' }, { name: 'Hubli', postalCode: '580020' }, { name: 'Mangaluru', postalCode: '575001' }, { name: 'Belagavi', postalCode: '590001' }, { name: 'Kalaburagi', postalCode: '585101' }, { name: 'Ballari', postalCode: '583101' }, { name: 'Davangere', postalCode: '577001' }, { name: 'Shivamogga', postalCode: '577201' }, { name: 'Tumakuru', postalCode: '572101' }, { name: 'Udupi', postalCode: '576101' }, { name: 'Hassan', postalCode: '573201' }, { name: 'Dharwad', postalCode: '580001' }] },
      { name: 'Kerala', cities: [{ name: 'Thiruvananthapuram', postalCode: '695001' }, { name: 'Kochi', postalCode: '682001' }, { name: 'Kozhikode', postalCode: '673001' }, { name: 'Thrissur', postalCode: '680001' }, { name: 'Kollam', postalCode: '691001' }, { name: 'Palakkad', postalCode: '678001' }, { name: 'Alappuzha', postalCode: '688001' }, { name: 'Malappuram', postalCode: '676505' }, { name: 'Kannur', postalCode: '670001' }, { name: 'Kottayam', postalCode: '686001' }] },
      { name: 'Madhya Pradesh', cities: [{ name: 'Bhopal', postalCode: '462001' }, { name: 'Indore', postalCode: '452001' }, { name: 'Gwalior', postalCode: '474001' }, { name: 'Jabalpur', postalCode: '482001' }, { name: 'Ujjain', postalCode: '456001' }, { name: 'Sagar', postalCode: '470001' }, { name: 'Rewa', postalCode: '486001' }, { name: 'Satna', postalCode: '485001' }, { name: 'Dewas', postalCode: '455001' }, { name: 'Ratlam', postalCode: '457001' }] },
      { name: 'Maharashtra', cities: [{ name: 'Mumbai', postalCode: '400001' }, { name: 'Pune', postalCode: '411001' }, { name: 'Nagpur', postalCode: '440001' }, { name: 'Thane', postalCode: '400601' }, { name: 'Nashik', postalCode: '422001' }, { name: 'Aurangabad', postalCode: '431001' }, { name: 'Solapur', postalCode: '413001' }, { name: 'Kolhapur', postalCode: '416001' }, { name: 'Amravati', postalCode: '444601' }, { name: 'Navi Mumbai', postalCode: '400703' }, { name: 'Vasai', postalCode: '401202' }, { name: 'Pimpri-Chinchwad', postalCode: '411017' }, { name: 'Sangli', postalCode: '416416' }] },
      { name: 'Manipur', cities: [{ name: 'Imphal', postalCode: '795001' }, { name: 'Thoubal', postalCode: '795138' }, { name: 'Bishnupur', postalCode: '795126' }] },
      { name: 'Meghalaya', cities: [{ name: 'Shillong', postalCode: '793001' }, { name: 'Tura', postalCode: '794001' }] },
      { name: 'Mizoram', cities: [{ name: 'Aizawl', postalCode: '796001' }, { name: 'Lunglei', postalCode: '796701' }] },
      { name: 'Nagaland', cities: [{ name: 'Kohima', postalCode: '797001' }, { name: 'Dimapur', postalCode: '797112' }] },
      { name: 'Odisha', cities: [{ name: 'Bhubaneswar', postalCode: '751001' }, { name: 'Cuttack', postalCode: '753001' }, { name: 'Rourkela', postalCode: '769001' }, { name: 'Sambalpur', postalCode: '768001' }, { name: 'Berhampur', postalCode: '760001' }, { name: 'Puri', postalCode: '752001' }] },
      { name: 'Punjab', cities: [{ name: 'Ludhiana', postalCode: '141001' }, { name: 'Amritsar', postalCode: '143001' }, { name: 'Jalandhar', postalCode: '144001' }, { name: 'Patiala', postalCode: '147001' }, { name: 'Bathinda', postalCode: '151001' }, { name: 'Mohali', postalCode: '160059' }, { name: 'Pathankot', postalCode: '145001' }] },
      { name: 'Rajasthan', cities: [{ name: 'Jaipur', postalCode: '302001' }, { name: 'Jodhpur', postalCode: '342001' }, { name: 'Udaipur', postalCode: '313001' }, { name: 'Kota', postalCode: '324001' }, { name: 'Bikaner', postalCode: '334001' }, { name: 'Ajmer', postalCode: '305001' }, { name: 'Alwar', postalCode: '301001' }, { name: 'Bhilwara', postalCode: '311001' }, { name: 'Sikar', postalCode: '332001' }] },
      { name: 'Sikkim', cities: [{ name: 'Gangtok', postalCode: '737101' }, { name: 'Namchi', postalCode: '737126' }] },
      { name: 'Tamil Nadu', cities: [{ name: 'Chennai', postalCode: '600001' }, { name: 'Coimbatore', postalCode: '641001' }, { name: 'Madurai', postalCode: '625001' }, { name: 'Tiruchirappalli', postalCode: '620001' }, { name: 'Salem', postalCode: '636001' }, { name: 'Tirunelveli', postalCode: '627001' }, { name: 'Erode', postalCode: '638001' }, { name: 'Vellore', postalCode: '632001' }, { name: 'Thanjavur', postalCode: '613001' }, { name: 'Tiruppur', postalCode: '641601' }, { name: 'Dindigul', postalCode: '624001' }, { name: 'Nagercoil', postalCode: '629001' }, { name: 'Kanchipuram', postalCode: '631501' }] },
      { name: 'Telangana', cities: [{ name: 'Hyderabad', postalCode: '500001' }, { name: 'Warangal', postalCode: '506001' }, { name: 'Nizamabad', postalCode: '503001' }, { name: 'Karimnagar', postalCode: '505001' }, { name: 'Ramagundam', postalCode: '505208' }, { name: 'Khammam', postalCode: '507001' }, { name: 'Secunderabad', postalCode: '500003' }, { name: 'Mahbubnagar', postalCode: '509001' }] },
      { name: 'Tripura', cities: [{ name: 'Agartala', postalCode: '799001' }, { name: 'Udaipur', postalCode: '799120' }] },
      { name: 'Uttar Pradesh', cities: [{ name: 'Lucknow', postalCode: '226001' }, { name: 'Kanpur', postalCode: '208001' }, { name: 'Agra', postalCode: '282001' }, { name: 'Varanasi', postalCode: '221001' }, { name: 'Meerut', postalCode: '250001' }, { name: 'Allahabad', postalCode: '211001' }, { name: 'Ghaziabad', postalCode: '201001' }, { name: 'Noida', postalCode: '201301' }, { name: 'Bareilly', postalCode: '243001' }, { name: 'Aligarh', postalCode: '202001' }, { name: 'Mathura', postalCode: '281001' }, { name: 'Moradabad', postalCode: '244001' }, { name: 'Gorakhpur', postalCode: '273001' }] },
      { name: 'Uttarakhand', cities: [{ name: 'Dehradun', postalCode: '248001' }, { name: 'Haridwar', postalCode: '249401' }, { name: 'Roorkee', postalCode: '247667' }, { name: 'Haldwani', postalCode: '263139' }, { name: 'Rishikesh', postalCode: '249201' }] },
      { name: 'West Bengal', cities: [{ name: 'Kolkata', postalCode: '700001' }, { name: 'Howrah', postalCode: '711101' }, { name: 'Durgapur', postalCode: '713201' }, { name: 'Asansol', postalCode: '713301' }, { name: 'Siliguri', postalCode: '734001' }, { name: 'Bardhaman', postalCode: '713101' }, { name: 'Kharagpur', postalCode: '721301' }] },
      { name: 'Jammu and Kashmir', cities: [{ name: 'Srinagar', postalCode: '190001' }, { name: 'Jammu', postalCode: '180001' }, { name: 'Anantnag', postalCode: '192101' }, { name: 'Baramulla', postalCode: '193101' }] },
      { name: 'Ladakh', cities: [{ name: 'Leh', postalCode: '194101' }, { name: 'Kargil', postalCode: '194103' }] },
      { name: 'Chandigarh', cities: [{ name: 'Chandigarh', postalCode: '160017' }, { name: 'Sector 17', postalCode: '160017' }, { name: 'Sector 22', postalCode: '160022' }] },
      { name: 'Dadra and Nagar Haveli and Daman and Diu', cities: [{ name: 'Daman', postalCode: '396210' }, { name: 'Silvassa', postalCode: '396230' }, { name: 'Diu', postalCode: '362520' }] },
      { name: 'Lakshadweep', cities: [{ name: 'Kavaratti', postalCode: '682555' }] },
      { name: 'Puducherry', cities: [{ name: 'Puducherry', postalCode: '605001' }, { name: 'Karaikal', postalCode: '609601' }] },
      { name: 'Andaman and Nicobar Islands', cities: [{ name: 'Port Blair', postalCode: '744101' }] }
    ]
  },
  {
    name: 'Indonesia', code: 'ID',
    states: [
      { name: 'Jakarta', cities: [{ name: 'Jakarta Pusat', postalCode: '10110' }, { name: 'Jakarta Selatan', postalCode: '12110' }, { name: 'Jakarta Barat', postalCode: '11220' }, { name: 'Jakarta Timur', postalCode: '13110' }, { name: 'Jakarta Utara', postalCode: '14110' }] },
      { name: 'West Java', cities: [{ name: 'Bandung', postalCode: '40111' }, { name: 'Bekasi', postalCode: '17112' }, { name: 'Bogor', postalCode: '16111' }, { name: 'Depok', postalCode: '16412' }] },
      { name: 'East Java', cities: [{ name: 'Surabaya', postalCode: '60111' }, { name: 'Malang', postalCode: '65111' }, { name: 'Madiun', postalCode: '63111' }] },
      { name: 'Central Java', cities: [{ name: 'Semarang', postalCode: '50111' }, { name: 'Solo', postalCode: '57111' }, { name: 'Yogyakarta', postalCode: '55111' }] },
      { name: 'North Sumatra', cities: [{ name: 'Medan', postalCode: '20111' }, { name: 'Binjai', postalCode: '20711' }] },
      { name: 'South Sulawesi', cities: [{ name: 'Makassar', postalCode: '90111' }, { name: 'Parepare', postalCode: '91111' }] },
      { name: 'Bali', cities: [{ name: 'Denpasar', postalCode: '80111' }, { name: 'Kuta', postalCode: '80361' }, { name: 'Ubud', postalCode: '80571' }] },
      { name: 'South Sumatra', cities: [{ name: 'Palembang', postalCode: '30111' }] },
      { name: 'Riau', cities: [{ name: 'Pekanbaru', postalCode: '28111' }] }
    ]
  },
  {
    name: 'Iran', code: 'IR',
    states: [
      { name: 'Tehran', cities: [{ name: 'Tehran', postalCode: '1111111111' }, { name: 'Karaj', postalCode: '3137111111' }, { name: 'Shemiranat', postalCode: '1917111111' }] },
      { name: 'Isfahan', cities: [{ name: 'Isfahan', postalCode: '8143111111' }, { name: 'Kashan', postalCode: '8715111111' }] },
      { name: 'Fars', cities: [{ name: 'Shiraz', postalCode: '7194111111' }, { name: 'Marvdasht', postalCode: '7376111111' }] },
      { name: 'East Azerbaijan', cities: [{ name: 'Tabriz', postalCode: '5166111111' }, { name: 'Maragheh', postalCode: '5518111111' }] },
      { name: 'Khorasan Razavi', cities: [{ name: 'Mashhad', postalCode: '9187111111' }] },
      { name: 'Khuzestan', cities: [{ name: 'Ahvaz', postalCode: '6135111111' }, { name: 'Abadan', postalCode: '6318111111' }] },
      { name: 'Gilan', cities: [{ name: 'Rasht', postalCode: '4145111111' }] },
      { name: 'Mazandaran', cities: [{ name: 'Sari', postalCode: '4815111111' }, { name: 'Babol', postalCode: '4713111111' }] }
    ]
  },
  {
    name: 'Iraq', code: 'IQ',
    states: [
      { name: 'Baghdad', cities: [{ name: 'Baghdad', postalCode: '10001' }, { name: 'Sadr City', postalCode: '10017' }, { name: 'Kadhimiya', postalCode: '10013' }] },
      { name: 'Basra', cities: [{ name: 'Basra', postalCode: '61001' }, { name: 'Zubayr', postalCode: '61006' }] },
      { name: 'Arbil', cities: [{ name: 'Erbil', postalCode: '44001' }] },
      { name: 'Sulaymaniyah', cities: [{ name: 'Sulaymaniyah', postalCode: '46001' }] },
      { name: 'Nineveh', cities: [{ name: 'Mosul', postalCode: '41001' }] },
      { name: 'An Najaf', cities: [{ name: 'Najaf', postalCode: '54001' }] }
    ]
  },
  {
    name: 'Ireland', code: 'IE',
    states: [
      { name: 'Dublin', cities: [{ name: 'Dublin 1', postalCode: 'D01' }, { name: 'Dublin 2', postalCode: 'D02' }, { name: 'Dublin 4', postalCode: 'D04' }, { name: 'Dublin 6', postalCode: 'D06' }] },
      { name: 'Cork', cities: [{ name: 'Cork', postalCode: 'T12' }, { name: 'Cobh', postalCode: 'P24' }] },
      { name: 'Galway', cities: [{ name: 'Galway', postalCode: 'H91' }] },
      { name: 'Limerick', cities: [{ name: 'Limerick', postalCode: 'V94' }] },
      { name: 'Waterford', cities: [{ name: 'Waterford', postalCode: 'X91' }] },
      { name: 'Kerry', cities: [{ name: 'Tralee', postalCode: 'V92' }, { name: 'Killarney', postalCode: 'V93' }] }
    ]
  },
  {
    name: 'Israel', code: 'IL',
    states: [
      { name: 'Tel Aviv District', cities: [{ name: 'Tel Aviv', postalCode: '6100000' }, { name: 'Ramat Gan', postalCode: '5251300' }, { name: 'Bat Yam', postalCode: '5940000' }] },
      { name: 'Jerusalem District', cities: [{ name: 'Jerusalem', postalCode: '9100000' }] },
      { name: 'Haifa District', cities: [{ name: 'Haifa', postalCode: '3100000' }] },
      { name: 'Central District', cities: [{ name: 'Rishon LeZion', postalCode: '7520000' }, { name: 'Petah Tikva', postalCode: '4900000' }] },
      { name: 'Northern District', cities: [{ name: 'Nazareth', postalCode: '1600000' }, { name: 'Acre', postalCode: '2412200' }] },
      { name: 'Southern District', cities: [{ name: 'Beersheba', postalCode: '8410000' }, { name: 'Ashkelon', postalCode: '7838000' }] }
    ]
  },
  {
    name: 'Italy', code: 'IT',
    states: [
      { name: 'Lazio', cities: [{ name: 'Rome', postalCode: '00100' }, { name: 'Latina', postalCode: '04100' }, { name: 'Frosinone', postalCode: '03100' }] },
      { name: 'Lombardy', cities: [{ name: 'Milan', postalCode: '20121' }, { name: 'Brescia', postalCode: '25121' }, { name: 'Bergamo', postalCode: '24121' }, { name: 'Monza', postalCode: '20900' }] },
      { name: 'Campania', cities: [{ name: 'Naples', postalCode: '80121' }, { name: 'Salerno', postalCode: '84121' }] },
      { name: 'Sicily', cities: [{ name: 'Palermo', postalCode: '90121' }, { name: 'Catania', postalCode: '95121' }, { name: 'Messina', postalCode: '98121' }] },
      { name: 'Veneto', cities: [{ name: 'Venice', postalCode: '30121' }, { name: 'Verona', postalCode: '37121' }, { name: 'Padua', postalCode: '35121' }] },
      { name: 'Emilia-Romagna', cities: [{ name: 'Bologna', postalCode: '40121' }, { name: 'Parma', postalCode: '43121' }, { name: 'Modena', postalCode: '41121' }] },
      { name: 'Tuscany', cities: [{ name: 'Florence', postalCode: '50121' }, { name: 'Pisa', postalCode: '56121' }, { name: 'Siena', postalCode: '53100' }] },
      { name: 'Piedmont', cities: [{ name: 'Turin', postalCode: '10121' }, { name: 'Novara', postalCode: '28100' }] },
      { name: 'Apulia', cities: [{ name: 'Bari', postalCode: '70121' }, { name: 'Taranto', postalCode: '74121' }] },
      { name: 'Liguria', cities: [{ name: 'Genoa', postalCode: '16121' }] }
    ]
  },
  {
    name: 'Japan', code: 'JP',
    states: [
      { name: 'Tokyo', cities: [{ name: 'Shinjuku', postalCode: '160-0001' }, { name: 'Shibuya', postalCode: '150-0001' }, { name: 'Minato', postalCode: '105-0001' }, { name: 'Chiyoda', postalCode: '100-0001' }, { name: 'Sumida', postalCode: '130-0001' }] },
      { name: 'Osaka', cities: [{ name: 'Osaka', postalCode: '530-0001' }, { name: 'Sakai', postalCode: '590-0000' }] },
      { name: 'Kanagawa', cities: [{ name: 'Yokohama', postalCode: '220-0001' }, { name: 'Kawasaki', postalCode: '210-0001' }] },
      { name: 'Aichi', cities: [{ name: 'Nagoya', postalCode: '460-0001' }, { name: 'Toyota', postalCode: '471-0001' }] },
      { name: 'Hokkaido', cities: [{ name: 'Sapporo', postalCode: '060-0001' }, { name: 'Hakodate', postalCode: '040-0001' }] },
      { name: 'Fukuoka', cities: [{ name: 'Fukuoka', postalCode: '810-0001' }, { name: 'Kitakyushu', postalCode: '800-0001' }] },
      { name: 'Hyogo', cities: [{ name: 'Kobe', postalCode: '650-0001' }, { name: 'Himeji', postalCode: '670-0001' }] },
      { name: 'Kyoto', cities: [{ name: 'Kyoto', postalCode: '600-8001' }] },
      { name: 'Saitama', cities: [{ name: 'Saitama', postalCode: '330-0001' }, { name: 'Kawagoe', postalCode: '350-0001' }] },
      { name: 'Chiba', cities: [{ name: 'Chiba', postalCode: '260-0001' }, { name: 'Matsudo', postalCode: '271-0001' }] }
    ]
  },
  {
    name: 'Jordan', code: 'JO',
    states: [
      { name: 'Amman', cities: [{ name: 'Amman', postalCode: '11110' }, { name: 'Zarqa', postalCode: '13110' }, { name: 'Russeifa', postalCode: '13215' }] },
      { name: 'Irbid', cities: [{ name: 'Irbid', postalCode: '21110' }, { name: 'Ramtha', postalCode: '27110' }] },
      { name: 'Zarqa', cities: [{ name: 'Zarqa City', postalCode: '13110' }] },
      { name: 'Aqaba', cities: [{ name: 'Aqaba', postalCode: '77110' }] },
      { name: 'Balqa', cities: [{ name: 'Salt', postalCode: '19110' }] }
    ]
  },
  {
    name: 'Kazakhstan', code: 'KZ',
    states: [
      { name: 'Astana', cities: [{ name: 'Astana', postalCode: '010000' }, { name: 'Nur-Sultan', postalCode: '010000' }] },
      { name: 'Almaty City', cities: [{ name: 'Almaty', postalCode: '050000' }, { name: 'Medeu', postalCode: '050060' }] },
      { name: 'Karaganda', cities: [{ name: 'Karaganda', postalCode: '100000' }, { name: 'Temirtau', postalCode: '101400' }] },
      { name: 'East Kazakhstan', cities: [{ name: 'Ust-Kamenogorsk', postalCode: '070000' }, { name: 'Semey', postalCode: '071400' }] },
      { name: 'Shymkent', cities: [{ name: 'Shymkent', postalCode: '160000' }] }
    ]
  },
  {
    name: 'Kenya', code: 'KE',
    states: [
      { name: 'Nairobi', cities: [{ name: 'Nairobi', postalCode: '00100' }, { name: 'Westlands', postalCode: '00600' }, { name: 'Karen', postalCode: '00502' }, { name: 'Eastleigh', postalCode: '00610' }] },
      { name: 'Mombasa', cities: [{ name: 'Mombasa', postalCode: '80100' }, { name: 'Nyali', postalCode: '80118' }] },
      { name: 'Kisumu', cities: [{ name: 'Kisumu', postalCode: '40100' }] },
      { name: 'Nakuru', cities: [{ name: 'Nakuru', postalCode: '20100' }] },
      { name: 'Uasin Gishu', cities: [{ name: 'Eldoret', postalCode: '30100' }] },
      { name: 'Kiambu', cities: [{ name: 'Thika', postalCode: '01000' }] }
    ]
  },
  {
    name: 'Kuwait', code: 'KW',
    states: [
      { name: 'Capital', cities: [{ name: 'Kuwait City', postalCode: '13001' }, { name: 'Sharq', postalCode: '13007' }] },
      { name: 'Hawalli', cities: [{ name: 'Salmiya', postalCode: '22001' }, { name: 'Hawalli', postalCode: '32001' }] },
      { name: 'Al Farwaniyah', cities: [{ name: 'Farwaniya', postalCode: '81001' }, { name: 'Khaitan', postalCode: '82001' }] },
      { name: 'Al Ahmadi', cities: [{ name: 'Ahmadi', postalCode: '61001' }, { name: 'Fahaheel', postalCode: '64001' }] },
      { name: 'Al Jahra', cities: [{ name: 'Jahra', postalCode: '91001' }] },
      { name: 'Mubarak Al-Kabeer', cities: [{ name: 'Sabah Al-Salem', postalCode: '36001' }] }
    ]
  },
  {
    name: 'Malaysia', code: 'MY',
    states: [
      { name: 'Kuala Lumpur', cities: [{ name: 'Kuala Lumpur', postalCode: '50000' }, { name: 'Chow Kit', postalCode: '50350' }, { name: 'Bukit Bintang', postalCode: '55100' }, { name: 'Bangsar', postalCode: '59000' }] },
      { name: 'Selangor', cities: [{ name: 'Shah Alam', postalCode: '40000' }, { name: 'Subang Jaya', postalCode: '47500' }, { name: 'Petaling Jaya', postalCode: '46000' }, { name: 'Klang', postalCode: '41000' }] },
      { name: 'Johor', cities: [{ name: 'Johor Bahru', postalCode: '80000' }, { name: 'Batu Pahat', postalCode: '83000' }, { name: 'Muar', postalCode: '84000' }] },
      { name: 'Penang', cities: [{ name: 'George Town', postalCode: '10000' }, { name: 'Butterworth', postalCode: '12000' }] },
      { name: 'Perak', cities: [{ name: 'Ipoh', postalCode: '30000' }, { name: 'Taiping', postalCode: '34000' }] },
      { name: 'Sabah', cities: [{ name: 'Kota Kinabalu', postalCode: '88000' }, { name: 'Sandakan', postalCode: '90000' }] },
      { name: 'Sarawak', cities: [{ name: 'Kuching', postalCode: '93000' }, { name: 'Miri', postalCode: '98000' }] },
      { name: 'Putrajaya', cities: [{ name: 'Putrajaya', postalCode: '62000' }] }
    ]
  },
  {
    name: 'Mexico', code: 'MX',
    states: [
      { name: 'Ciudad de México', cities: [{ name: 'Mexico City', postalCode: '06600' }, { name: 'Coyoacán', postalCode: '04100' }, { name: 'Tlalpan', postalCode: '14000' }, { name: 'Iztapalapa', postalCode: '09820' }] },
      { name: 'Jalisco', cities: [{ name: 'Guadalajara', postalCode: '44100' }, { name: 'Zapopan', postalCode: '45100' }, { name: 'Tlaquepaque', postalCode: '45500' }] },
      { name: 'Nuevo León', cities: [{ name: 'Monterrey', postalCode: '64000' }, { name: 'San Nicolás', postalCode: '66450' }] },
      { name: 'Puebla', cities: [{ name: 'Puebla', postalCode: '72000' }, { name: 'Tehuacán', postalCode: '75700' }] },
      { name: 'Estado de México', cities: [{ name: 'Ecatepec', postalCode: '55000' }, { name: 'Naucalpan', postalCode: '53000' }, { name: 'Toluca', postalCode: '50000' }] },
      { name: 'Guanajuato', cities: [{ name: 'León', postalCode: '37000' }, { name: 'Irapuato', postalCode: '36500' }] },
      { name: 'Veracruz', cities: [{ name: 'Veracruz', postalCode: '91700' }, { name: 'Xalapa', postalCode: '91000' }] }
    ]
  },
  {
    name: 'Morocco', code: 'MA',
    states: [
      { name: 'Casablanca-Settat', cities: [{ name: 'Casablanca', postalCode: '20000' }, { name: 'Mohammedia', postalCode: '28810' }, { name: 'Settat', postalCode: '26000' }] },
      { name: 'Rabat-Salé-Kénitra', cities: [{ name: 'Rabat', postalCode: '10000' }, { name: 'Salé', postalCode: '11000' }, { name: 'Kénitra', postalCode: '14000' }] },
      { name: 'Fès-Meknès', cities: [{ name: 'Fès', postalCode: '30000' }, { name: 'Meknès', postalCode: '50000' }] },
      { name: 'Marrakech-Safi', cities: [{ name: 'Marrakech', postalCode: '40000' }, { name: 'Safi', postalCode: '46000' }] },
      { name: 'Oriental', cities: [{ name: 'Oujda', postalCode: '60000' }] },
      { name: 'Tanger-Tetouan-Al Hoceima', cities: [{ name: 'Tangier', postalCode: '90000' }, { name: 'Tétouan', postalCode: '93000' }] },
      { name: 'Souss-Massa', cities: [{ name: 'Agadir', postalCode: '80000' }] }
    ]
  },
  {
    name: 'Myanmar', code: 'MM',
    states: [
      { name: 'Yangon', cities: [{ name: 'Yangon', postalCode: '11111' }, { name: 'Hlaing', postalCode: '11121' }, { name: 'South Okkalapa', postalCode: '11151' }] },
      { name: 'Mandalay', cities: [{ name: 'Mandalay', postalCode: '05031' }, { name: 'Pyin Oo Lwin', postalCode: '05052' }] },
      { name: 'Naypyidaw Union Territory', cities: [{ name: 'Naypyidaw', postalCode: '15011' }] },
      { name: 'Sagaing', cities: [{ name: 'Sagaing', postalCode: '04011' }] },
      { name: 'Bago', cities: [{ name: 'Bago', postalCode: '12011' }] }
    ]
  },
  {
    name: 'Nepal', code: 'NP',
    states: [
      { name: 'Bagmati', cities: [{ name: 'Kathmandu', postalCode: '44600' }, { name: 'Lalitpur', postalCode: '44700' }, { name: 'Bhaktapur', postalCode: '44800' }, { name: 'Kirtipur', postalCode: '44618' }] },
      { name: 'Gandaki', cities: [{ name: 'Pokhara', postalCode: '33700' }] },
      { name: 'Lumbini', cities: [{ name: 'Butwal', postalCode: '32907' }, { name: 'Bhairahawa', postalCode: '32900' }] },
      { name: 'Madhesh', cities: [{ name: 'Janakpur', postalCode: '45600' }, { name: 'Birgunj', postalCode: '44300' }] },
      { name: 'Koshi', cities: [{ name: 'Biratnagar', postalCode: '56613' }] }
    ]
  },
  {
    name: 'Netherlands', code: 'NL',
    states: [
      { name: 'Noord-Holland', cities: [{ name: 'Amsterdam', postalCode: '1011 AB' }, { name: 'Haarlem', postalCode: '2011 AB' }, { name: 'Almere', postalCode: '1311 AA' }] },
      { name: 'Zuid-Holland', cities: [{ name: 'Rotterdam', postalCode: '3011 AA' }, { name: 'The Hague', postalCode: '2500 AA' }, { name: 'Leiden', postalCode: '2300 AA' }] },
      { name: 'Noord-Brabant', cities: [{ name: 'Eindhoven', postalCode: '5600 AA' }, { name: 'Tilburg', postalCode: '5000 AA' }, { name: 'Breda', postalCode: '4800 AA' }] },
      { name: 'Gelderland', cities: [{ name: 'Nijmegen', postalCode: '6500 AA' }, { name: 'Arnhem', postalCode: '6800 AA' }] },
      { name: 'Utrecht', cities: [{ name: 'Utrecht', postalCode: '3500 AA' }] },
      { name: 'Overijssel', cities: [{ name: 'Enschede', postalCode: '7500 AA' }, { name: 'Zwolle', postalCode: '8000 AA' }] },
      { name: 'Groningen', cities: [{ name: 'Groningen', postalCode: '9700 AA' }] },
      { name: 'Friesland', cities: [{ name: 'Leeuwarden', postalCode: '8900 AA' }] }
    ]
  },
  {
    name: 'New Zealand', code: 'NZ',
    states: [
      { name: 'Auckland', cities: [{ name: 'Auckland', postalCode: '1010' }, { name: 'Manukau', postalCode: '2104' }, { name: 'North Shore', postalCode: '0620' }, { name: 'Henderson', postalCode: '0610' }] },
      { name: 'Wellington', cities: [{ name: 'Wellington', postalCode: '6011' }, { name: 'Lower Hutt', postalCode: '5010' }, { name: 'Porirua', postalCode: '5022' }] },
      { name: 'Canterbury', cities: [{ name: 'Christchurch', postalCode: '8011' }, { name: 'Ashburton', postalCode: '7700' }] },
      { name: 'Waikato', cities: [{ name: 'Hamilton', postalCode: '3204' }, { name: 'Tauranga', postalCode: '3110' }] },
      { name: 'Otago', cities: [{ name: 'Dunedin', postalCode: '9016' }, { name: 'Queenstown', postalCode: '9300' }] }
    ]
  },
  {
    name: 'Nigeria', code: 'NG',
    states: [
      { name: 'Lagos', cities: [{ name: 'Lagos Island', postalCode: '101001' }, { name: 'Ikeja', postalCode: '100271' }, { name: 'Victoria Island', postalCode: '106104' }, { name: 'Lekki', postalCode: '105102' }] },
      { name: 'FCT', cities: [{ name: 'Abuja', postalCode: '900001' }, { name: 'Garki', postalCode: '900211' }] },
      { name: 'Kano', cities: [{ name: 'Kano', postalCode: '700001' }] },
      { name: 'Rivers', cities: [{ name: 'Port Harcourt', postalCode: '500001' }] },
      { name: 'Oyo', cities: [{ name: 'Ibadan', postalCode: '200001' }] },
      { name: 'Anambra', cities: [{ name: 'Onitsha', postalCode: '420001' }, { name: 'Awka', postalCode: '422001' }] },
      { name: 'Delta', cities: [{ name: 'Warri', postalCode: '332001' }, { name: 'Asaba', postalCode: '320001' }] },
      { name: 'Edo', cities: [{ name: 'Benin City', postalCode: '300001' }] }
    ]
  },
  {
    name: 'Norway', code: 'NO',
    states: [
      { name: 'Oslo', cities: [{ name: 'Oslo', postalCode: '0010' }, { name: 'Grünerløkka', postalCode: '0550' }, { name: 'Majorstuen', postalCode: '0305' }] },
      { name: 'Rogaland', cities: [{ name: 'Stavanger', postalCode: '4005' }, { name: 'Sandnes', postalCode: '4306' }] },
      { name: 'Vestland', cities: [{ name: 'Bergen', postalCode: '5003' }, { name: 'Ålesund', postalCode: '6002' }] },
      { name: 'Innlandet', cities: [{ name: 'Hamar', postalCode: '2317' }] },
      { name: 'Trøndelag', cities: [{ name: 'Trondheim', postalCode: '7010' }] },
      { name: 'Troms og Finnmark', cities: [{ name: 'Tromsø', postalCode: '9005' }] }
    ]
  },
  {
    name: 'Oman', code: 'OM',
    states: [
      { name: 'Muscat', cities: [{ name: 'Muscat', postalCode: '100' }, { name: 'Muttrah', postalCode: '114' }, { name: 'Ruwi', postalCode: '112' }, { name: 'Al Khuwair', postalCode: '133' }] },
      { name: 'Dhofar', cities: [{ name: 'Salalah', postalCode: '211' }] },
      { name: 'North Al Batinah', cities: [{ name: 'Sohar', postalCode: '311' }] },
      { name: 'South Ash Sharqiyah', cities: [{ name: 'Sur', postalCode: '411' }] },
      { name: 'Al Buraimi', cities: [{ name: 'Buraimi', postalCode: '512' }] }
    ]
  },
  {
    name: 'Pakistan', code: 'PK',
    states: [
      { name: 'Punjab', cities: [{ name: 'Lahore', postalCode: '54000' }, { name: 'Faisalabad', postalCode: '38000' }, { name: 'Rawalpindi', postalCode: '46000' }, { name: 'Gujranwala', postalCode: '52250' }, { name: 'Multan', postalCode: '60000' }, { name: 'Sialkot', postalCode: '51310' }] },
      { name: 'Sindh', cities: [{ name: 'Karachi', postalCode: '74200' }, { name: 'Hyderabad', postalCode: '71000' }, { name: 'Sukkur', postalCode: '65200' }] },
      { name: 'Khyber Pakhtunkhwa', cities: [{ name: 'Peshawar', postalCode: '25000' }, { name: 'Mardan', postalCode: '23200' }] },
      { name: 'Islamabad Capital Territory', cities: [{ name: 'Islamabad', postalCode: '44000' }, { name: 'I-8', postalCode: '44060' }] },
      { name: 'Balochistan', cities: [{ name: 'Quetta', postalCode: '87300' }] },
      { name: 'Azad Kashmir', cities: [{ name: 'Muzaffarabad', postalCode: '13100' }] }
    ]
  },
  {
    name: 'Philippines', code: 'PH',
    states: [
      { name: 'Metro Manila', cities: [{ name: 'Manila', postalCode: '1000' }, { name: 'Quezon City', postalCode: '1100' }, { name: 'Makati', postalCode: '1200' }, { name: 'Taguig', postalCode: '1630' }, { name: 'Pasig', postalCode: '1600' }] },
      { name: 'Cebu', cities: [{ name: 'Cebu City', postalCode: '6000' }, { name: 'Lapu-Lapu', postalCode: '6015' }, { name: 'Mandaue', postalCode: '6014' }] },
      { name: 'Davao del Sur', cities: [{ name: 'Davao City', postalCode: '8000' }] },
      { name: 'Laguna', cities: [{ name: 'San Pedro', postalCode: '4023' }, { name: 'Santa Rosa', postalCode: '4026' }] },
      { name: 'Cavite', cities: [{ name: 'Bacoor', postalCode: '4102' }, { name: 'Imus', postalCode: '4103' }] },
      { name: 'Pampanga', cities: [{ name: 'Angeles City', postalCode: '2009' }, { name: 'San Fernando', postalCode: '2000' }] }
    ]
  },
  {
    name: 'Poland', code: 'PL',
    states: [
      { name: 'Masovian', cities: [{ name: 'Warsaw', postalCode: '00-001' }, { name: 'Radom', postalCode: '26-600' }, { name: 'Płock', postalCode: '09-400' }] },
      { name: 'Lesser Poland', cities: [{ name: 'Kraków', postalCode: '30-001' }, { name: 'Tarnów', postalCode: '33-100' }] },
      { name: 'Silesian', cities: [{ name: 'Katowice', postalCode: '40-001' }, { name: 'Gliwice', postalCode: '44-100' }, { name: 'Częstochowa', postalCode: '42-200' }] },
      { name: 'Greater Poland', cities: [{ name: 'Poznań', postalCode: '60-001' }, { name: 'Kalisz', postalCode: '62-800' }] },
      { name: 'Lower Silesian', cities: [{ name: 'Wrocław', postalCode: '50-001' }, { name: 'Legnica', postalCode: '59-220' }] },
      { name: 'Łódź', cities: [{ name: 'Łódź', postalCode: '90-001' }] },
      { name: 'Pomeranian', cities: [{ name: 'Gdańsk', postalCode: '80-001' }, { name: 'Gdynia', postalCode: '81-001' }] }
    ]
  },
  {
    name: 'Portugal', code: 'PT',
    states: [
      { name: 'Lisbon', cities: [{ name: 'Lisbon', postalCode: '1000-001' }, { name: 'Sintra', postalCode: '2710-567' }, { name: 'Cascais', postalCode: '2750-310' }] },
      { name: 'Norte', cities: [{ name: 'Porto', postalCode: '4000-001' }, { name: 'Braga', postalCode: '4700-001' }, { name: 'Vila Nova de Gaia', postalCode: '4400-001' }] },
      { name: 'Centro', cities: [{ name: 'Coimbra', postalCode: '3000-001' }, { name: 'Aveiro', postalCode: '3800-001' }] },
      { name: 'Algarve', cities: [{ name: 'Faro', postalCode: '8000-001' }, { name: 'Portimão', postalCode: '8500-001' }] },
      { name: 'Alentejo', cities: [{ name: 'Évora', postalCode: '7000-001' }] }
    ]
  },
  {
    name: 'Qatar', code: 'QA',
    states: [
      { name: 'Ad Dawhah', cities: [{ name: 'Doha', postalCode: 'PO Box 1' }, { name: 'West Bay', postalCode: 'PO Box 100' }, { name: 'Al Sadd', postalCode: 'PO Box 200' }] },
      { name: 'Al Rayyan', cities: [{ name: 'Al Rayyan', postalCode: 'QR-001' }, { name: 'Al Wakra', postalCode: 'QR-002' }] },
      { name: 'Al Wakrah', cities: [{ name: 'Al Wakrah City', postalCode: 'QW-001' }] },
      { name: 'Al Khor', cities: [{ name: 'Al Khor', postalCode: 'QK-001' }] },
      { name: 'Umm Salal', cities: [{ name: 'Umm Salal', postalCode: 'QU-001' }] }
    ]
  },
  {
    name: 'Romania', code: 'RO',
    states: [
      { name: 'Bucharest', cities: [{ name: 'Bucharest Sector 1', postalCode: '010101' }, { name: 'Bucharest Sector 2', postalCode: '020101' }, { name: 'Bucharest Sector 3', postalCode: '030101' }] },
      { name: 'Cluj', cities: [{ name: 'Cluj-Napoca', postalCode: '400001' }] },
      { name: 'Timiș', cities: [{ name: 'Timișoara', postalCode: '300001' }] },
      { name: 'Iași', cities: [{ name: 'Iași', postalCode: '700001' }] },
      { name: 'Brașov', cities: [{ name: 'Brașov', postalCode: '500001' }] },
      { name: 'Constanța', cities: [{ name: 'Constanța', postalCode: '900001' }] }
    ]
  },
  {
    name: 'Russia', code: 'RU',
    states: [
      { name: 'Moscow', cities: [{ name: 'Moscow', postalCode: '101000' }, { name: 'Zelenograd', postalCode: '124482' }, { name: 'Troitsk', postalCode: '142190' }] },
      { name: 'Saint Petersburg', cities: [{ name: 'Saint Petersburg', postalCode: '190000' }, { name: 'Peterhof', postalCode: '198516' }, { name: 'Pushkin', postalCode: '196600' }] },
      { name: 'Sverdlovsk Oblast', cities: [{ name: 'Yekaterinburg', postalCode: '620000' }] },
      { name: 'Tatarstan', cities: [{ name: 'Kazan', postalCode: '420000' }] },
      { name: 'Chelyabinsk Oblast', cities: [{ name: 'Chelyabinsk', postalCode: '454000' }] },
      { name: 'Samara Oblast', cities: [{ name: 'Samara', postalCode: '443000' }] },
      { name: 'Novosibirsk Oblast', cities: [{ name: 'Novosibirsk', postalCode: '630000' }] },
      { name: 'Rostov Oblast', cities: [{ name: 'Rostov-on-Don', postalCode: '344000' }] },
      { name: 'Krasnodar Krai', cities: [{ name: 'Krasnodar', postalCode: '350000' }, { name: 'Sochi', postalCode: '354000' }] },
      { name: 'Primorsky Krai', cities: [{ name: 'Vladivostok', postalCode: '690000' }] }
    ]
  },
  {
    name: 'Saudi Arabia', code: 'SA',
    states: [
      { name: 'Riyadh', cities: [{ name: 'Riyadh', postalCode: '11564' }, { name: 'Al Olaya', postalCode: '12212' }, { name: 'Al Malaz', postalCode: '11417' }, { name: 'Al Rawdah', postalCode: '14812' }] },
      { name: 'Mecca', cities: [{ name: 'Jeddah', postalCode: '21589' }, { name: 'Mecca', postalCode: '24231' }, { name: 'Taif', postalCode: '21944' }] },
      { name: 'Eastern Province', cities: [{ name: 'Dammam', postalCode: '31411' }, { name: 'Dhahran', postalCode: '31311' }, { name: 'Al-Khobar', postalCode: '31952' }, { name: 'Jubail', postalCode: '31951' }] },
      { name: 'Al Madinah', cities: [{ name: 'Medina', postalCode: '42311' }] },
      { name: 'Asir', cities: [{ name: 'Abha', postalCode: '62411' }] },
      { name: 'Tabuk', cities: [{ name: 'Tabuk', postalCode: '71411' }] },
      { name: 'Ha\'il', cities: [{ name: 'Hail', postalCode: '55411' }] }
    ]
  },
  {
    name: 'Singapore', code: 'SG',
    states: [
      { name: 'Central Region', cities: [{ name: 'Marina Bay', postalCode: '018956' }, { name: 'Orchard Road', postalCode: '238801' }, { name: 'Chinatown', postalCode: '058416' }, { name: 'Raffles Place', postalCode: '048616' }] },
      { name: 'East Region', cities: [{ name: 'Tampines', postalCode: '520001' }, { name: 'Bedok', postalCode: '460001' }, { name: 'Pasir Ris', postalCode: '510001' }] },
      { name: 'North Region', cities: [{ name: 'Woodlands', postalCode: '738099' }, { name: 'Yishun', postalCode: '760001' }] },
      { name: 'North-East Region', cities: [{ name: 'Sengkang', postalCode: '540001' }, { name: 'Hougang', postalCode: '530001' }] },
      { name: 'West Region', cities: [{ name: 'Jurong East', postalCode: '608701' }, { name: 'Bukit Timah', postalCode: '588172' }, { name: 'Clementi', postalCode: '120001' }] }
    ]
  },
  {
    name: 'South Africa', code: 'ZA',
    states: [
      { name: 'Gauteng', cities: [{ name: 'Johannesburg', postalCode: '2000' }, { name: 'Pretoria', postalCode: '0001' }, { name: 'Soweto', postalCode: '1804' }, { name: 'Sandton', postalCode: '2196' }] },
      { name: 'Western Cape', cities: [{ name: 'Cape Town', postalCode: '8001' }, { name: 'Stellenbosch', postalCode: '7600' }, { name: 'George', postalCode: '6529' }] },
      { name: 'KwaZulu-Natal', cities: [{ name: 'Durban', postalCode: '4001' }, { name: 'Pietermaritzburg', postalCode: '3201' }] },
      { name: 'Eastern Cape', cities: [{ name: 'Port Elizabeth', postalCode: '6001' }, { name: 'East London', postalCode: '5201' }] },
      { name: 'Free State', cities: [{ name: 'Bloemfontein', postalCode: '9301' }] },
      { name: 'Limpopo', cities: [{ name: 'Polokwane', postalCode: '0699' }] },
      { name: 'Mpumalanga', cities: [{ name: 'Nelspruit', postalCode: '1200' }] },
      { name: 'North West', cities: [{ name: 'Rustenburg', postalCode: '0299' }] }
    ]
  },
  {
    name: 'South Korea', code: 'KR',
    states: [
      { name: 'Seoul', cities: [{ name: 'Seoul', postalCode: '03000' }, { name: 'Gangnam', postalCode: '06000' }, { name: 'Mapo', postalCode: '04000' }, { name: 'Jongno', postalCode: '03001' }] },
      { name: 'Gyeonggi-do', cities: [{ name: 'Suwon', postalCode: '16000' }, { name: 'Seongnam', postalCode: '13500' }, { name: 'Goyang', postalCode: '10400' }] },
      { name: 'Busan', cities: [{ name: 'Busan', postalCode: '49000' }, { name: 'Haeundae', postalCode: '48094' }] },
      { name: 'Incheon', cities: [{ name: 'Incheon', postalCode: '22000' }] },
      { name: 'Daegu', cities: [{ name: 'Daegu', postalCode: '41000' }] },
      { name: 'Daejeon', cities: [{ name: 'Daejeon', postalCode: '34000' }] },
      { name: 'Gwangju', cities: [{ name: 'Gwangju', postalCode: '61000' }] }
    ]
  },
  {
    name: 'Spain', code: 'ES',
    states: [
      { name: 'Madrid', cities: [{ name: 'Madrid', postalCode: '28001' }, { name: 'Alcalá de Henares', postalCode: '28801' }, { name: 'Leganés', postalCode: '28914' }, { name: 'Getafe', postalCode: '28901' }] },
      { name: 'Catalonia', cities: [{ name: 'Barcelona', postalCode: '08001' }, { name: 'L\'Hospitalet', postalCode: '08901' }, { name: 'Tarragona', postalCode: '43001' }] },
      { name: 'Andalusia', cities: [{ name: 'Seville', postalCode: '41001' }, { name: 'Málaga', postalCode: '29001' }, { name: 'Córdoba', postalCode: '14001' }] },
      { name: 'Valencia', cities: [{ name: 'Valencia', postalCode: '46001' }, { name: 'Alicante', postalCode: '03001' }] },
      { name: 'Basque Country', cities: [{ name: 'Bilbao', postalCode: '48001' }, { name: 'San Sebastián', postalCode: '20001' }] },
      { name: 'Galicia', cities: [{ name: 'Vigo', postalCode: '36001' }, { name: 'A Coruña', postalCode: '15001' }] },
      { name: 'Castile and León', cities: [{ name: 'Valladolid', postalCode: '47001' }, { name: 'Salamanca', postalCode: '37001' }] }
    ]
  },
  {
    name: 'Sri Lanka', code: 'LK',
    states: [
      { name: 'Western', cities: [{ name: 'Colombo', postalCode: '00100' }, { name: 'Sri Jayawardenepura Kotte', postalCode: '10100' }, { name: 'Dehiwala', postalCode: '10350' }, { name: 'Negombo', postalCode: '11500' }] },
      { name: 'Central', cities: [{ name: 'Kandy', postalCode: '20000' }, { name: 'Nuwara Eliya', postalCode: '22200' }] },
      { name: 'Southern', cities: [{ name: 'Galle', postalCode: '80000' }, { name: 'Matara', postalCode: '81000' }] },
      { name: 'Northern', cities: [{ name: 'Jaffna', postalCode: '40000' }] },
      { name: 'Eastern', cities: [{ name: 'Trincomalee', postalCode: '31000' }, { name: 'Batticaloa', postalCode: '30000' }] }
    ]
  },
  {
    name: 'Sweden', code: 'SE',
    states: [
      { name: 'Stockholm', cities: [{ name: 'Stockholm', postalCode: '111 29' }, { name: 'Solna', postalCode: '171 22' }, { name: 'Nacka', postalCode: '131 37' }] },
      { name: 'Västra Götaland', cities: [{ name: 'Gothenburg', postalCode: '411 01' }, { name: 'Borås', postalCode: '503 01' }] },
      { name: 'Skåne', cities: [{ name: 'Malmö', postalCode: '211 20' }, { name: 'Helsingborg', postalCode: '252 25' }, { name: 'Lund', postalCode: '221 00' }] },
      { name: 'Östergötland', cities: [{ name: 'Linköping', postalCode: '582 17' }, { name: 'Norrköping', postalCode: '602 21' }] },
      { name: 'Uppsala', cities: [{ name: 'Uppsala', postalCode: '751 05' }] },
      { name: 'Norrbotten', cities: [{ name: 'Luleå', postalCode: '972 41' }] }
    ]
  },
  {
    name: 'Switzerland', code: 'CH',
    states: [
      { name: 'Zurich', cities: [{ name: 'Zurich', postalCode: '8001' }, { name: 'Winterthur', postalCode: '8400' }, { name: 'Uster', postalCode: '8610' }] },
      { name: 'Bern', cities: [{ name: 'Bern', postalCode: '3001' }, { name: 'Biel', postalCode: '2502' }] },
      { name: 'Geneva', cities: [{ name: 'Geneva', postalCode: '1201' }, { name: 'Carouge', postalCode: '1227' }] },
      { name: 'Basel-Stadt', cities: [{ name: 'Basel', postalCode: '4001' }] },
      { name: 'Vaud', cities: [{ name: 'Lausanne', postalCode: '1003' }, { name: 'Montreux', postalCode: '1820' }] },
      { name: 'Ticino', cities: [{ name: 'Lugano', postalCode: '6900' }, { name: 'Bellinzona', postalCode: '6500' }] },
      { name: 'Lucerne', cities: [{ name: 'Lucerne', postalCode: '6003' }] }
    ]
  },
  {
    name: 'Taiwan', code: 'TW',
    states: [
      { name: 'Taipei', cities: [{ name: 'Taipei', postalCode: '100' }, { name: 'Zhongzheng', postalCode: '100' }, { name: 'Da\'an', postalCode: '106' }] },
      { name: 'New Taipei', cities: [{ name: 'Banqiao', postalCode: '220' }, { name: 'Xindian', postalCode: '231' }] },
      { name: 'Taichung', cities: [{ name: 'Taichung', postalCode: '400' }, { name: 'Xitun', postalCode: '407' }] },
      { name: 'Tainan', cities: [{ name: 'Tainan', postalCode: '700' }] },
      { name: 'Kaohsiung', cities: [{ name: 'Kaohsiung', postalCode: '800' }] },
      { name: 'Taoyuan', cities: [{ name: 'Taoyuan', postalCode: '330' }, { name: 'Zhongli', postalCode: '320' }] }
    ]
  },
  {
    name: 'Tanzania', code: 'TZ',
    states: [
      { name: 'Dar es Salaam', cities: [{ name: 'Dar es Salaam', postalCode: '11101' }, { name: 'Kinondoni', postalCode: '14112' }, { name: 'Ilala', postalCode: '11101' }] },
      { name: 'Mwanza', cities: [{ name: 'Mwanza', postalCode: '33101' }] },
      { name: 'Arusha', cities: [{ name: 'Arusha', postalCode: '23101' }] },
      { name: 'Kilimanjaro', cities: [{ name: 'Moshi', postalCode: '25101' }] },
      { name: 'Dodoma', cities: [{ name: 'Dodoma', postalCode: '41101' }] },
      { name: 'Zanzibar West', cities: [{ name: 'Zanzibar City', postalCode: '71101' }] }
    ]
  },
  {
    name: 'Thailand', code: 'TH',
    states: [
      { name: 'Bangkok', cities: [{ name: 'Bangkok', postalCode: '10200' }, { name: 'Chatuchak', postalCode: '10900' }, { name: 'Sukhumvit', postalCode: '10110' }, { name: 'Silom', postalCode: '10500' }] },
      { name: 'Chiang Mai', cities: [{ name: 'Chiang Mai', postalCode: '50000' }, { name: 'Hang Dong', postalCode: '50230' }] },
      { name: 'Chon Buri', cities: [{ name: 'Pattaya', postalCode: '20150' }, { name: 'Chonburi', postalCode: '20000' }] },
      { name: 'Nonthaburi', cities: [{ name: 'Nonthaburi', postalCode: '11000' }] },
      { name: 'Phuket', cities: [{ name: 'Phuket City', postalCode: '83000' }, { name: 'Patong', postalCode: '83150' }] },
      { name: 'Khon Kaen', cities: [{ name: 'Khon Kaen', postalCode: '40000' }] },
      { name: 'Nakhon Ratchasima', cities: [{ name: 'Korat', postalCode: '30000' }] }
    ]
  },
  {
    name: 'Turkey', code: 'TR',
    states: [
      { name: 'İstanbul', cities: [{ name: 'Fatih', postalCode: '34134' }, { name: 'Beyoğlu', postalCode: '34430' }, { name: 'Kadıköy', postalCode: '34710' }, { name: 'Şişli', postalCode: '34387' }, { name: 'Üsküdar', postalCode: '34672' }] },
      { name: 'Ankara', cities: [{ name: 'Ankara', postalCode: '06100' }, { name: 'Çankaya', postalCode: '06550' }, { name: 'Keçiören', postalCode: '06380' }] },
      { name: 'İzmir', cities: [{ name: 'İzmir', postalCode: '35210' }, { name: 'Karşıyaka', postalCode: '35600' }, { name: 'Bornova', postalCode: '35040' }] },
      { name: 'Bursa', cities: [{ name: 'Bursa', postalCode: '16010' }, { name: 'Osmangazi', postalCode: '16080' }] },
      { name: 'Antalya', cities: [{ name: 'Antalya', postalCode: '07100' }, { name: 'Alanya', postalCode: '07400' }] },
      { name: 'Gaziantep', cities: [{ name: 'Gaziantep', postalCode: '27010' }] },
      { name: 'Konya', cities: [{ name: 'Konya', postalCode: '42030' }] },
      { name: 'Adana', cities: [{ name: 'Adana', postalCode: '01130' }, { name: 'Seyhan', postalCode: '01150' }] }
    ]
  },
  {
    name: 'Uganda', code: 'UG',
    states: [
      { name: 'Central', cities: [{ name: 'Kampala', postalCode: '256' }, { name: 'Entebbe', postalCode: '257' }, { name: 'Wakiso', postalCode: '258' }] },
      { name: 'Eastern', cities: [{ name: 'Jinja', postalCode: '259' }, { name: 'Mbale', postalCode: '260' }] },
      { name: 'Northern', cities: [{ name: 'Gulu', postalCode: '261' }, { name: 'Lira', postalCode: '262' }] },
      { name: 'Western', cities: [{ name: 'Mbarara', postalCode: '263' }, { name: 'Fort Portal', postalCode: '264' }] }
    ]
  },
  {
    name: 'Ukraine', code: 'UA',
    states: [
      { name: 'Kyiv City', cities: [{ name: 'Kyiv', postalCode: '01001' }, { name: 'Darnytsya', postalCode: '02099' }, { name: 'Desnyansky', postalCode: '02140' }] },
      { name: 'Kharkiv', cities: [{ name: 'Kharkiv', postalCode: '61001' }] },
      { name: 'Odessa', cities: [{ name: 'Odessa', postalCode: '65001' }] },
      { name: 'Dnipropetrovsk', cities: [{ name: 'Dnipro', postalCode: '49001' }, { name: 'Kryvyi Rih', postalCode: '50001' }] },
      { name: 'Lviv', cities: [{ name: 'Lviv', postalCode: '79001' }] },
      { name: 'Donetsk', cities: [{ name: 'Mariupol', postalCode: '87501' }] }
    ]
  },
  {
    name: 'United Arab Emirates', code: 'AE',
    states: [
      { name: 'Dubai', cities: [{ name: 'Dubai', postalCode: '00000' }, { name: 'Deira', postalCode: '00001' }, { name: 'Bur Dubai', postalCode: '00002' }, { name: 'Jumeirah', postalCode: '00003' }, { name: 'Business Bay', postalCode: '00004' }, { name: 'Marina', postalCode: '00005' }] },
      { name: 'Abu Dhabi', cities: [{ name: 'Abu Dhabi', postalCode: '00000' }, { name: 'Al Ain', postalCode: '00001' }, { name: 'Khalifa City', postalCode: '00002' }] },
      { name: 'Sharjah', cities: [{ name: 'Sharjah City', postalCode: '00000' }, { name: 'Al Qasimia', postalCode: '00001' }] },
      { name: 'Ajman', cities: [{ name: 'Ajman City', postalCode: '00000' }] },
      { name: 'Ras Al Khaimah', cities: [{ name: 'Ras Al Khaimah', postalCode: '00000' }] },
      { name: 'Fujairah', cities: [{ name: 'Fujairah City', postalCode: '00000' }] },
      { name: 'Umm Al Quwain', cities: [{ name: 'Umm Al Quwain', postalCode: '00000' }] }
    ]
  },
  {
    name: 'United Kingdom', code: 'GB',
    states: [
      { name: 'Greater London', cities: [{ name: 'City of London', postalCode: 'EC1A 1BB' }, { name: 'Westminster', postalCode: 'SW1A 1AA' }, { name: 'Canary Wharf', postalCode: 'E14 5AB' }, { name: 'Southwark', postalCode: 'SE1 7PB' }, { name: 'Hammersmith', postalCode: 'W6 9DP' }, { name: 'Islington', postalCode: 'N1 9GU' }] },
      { name: 'West Yorkshire', cities: [{ name: 'Leeds', postalCode: 'LS1 1BA' }, { name: 'Bradford', postalCode: 'BD1 1HX' }] },
      { name: 'Greater Manchester', cities: [{ name: 'Manchester', postalCode: 'M1 1AE' }, { name: 'Salford', postalCode: 'M5 4WT' }] },
      { name: 'West Midlands', cities: [{ name: 'Birmingham', postalCode: 'B1 1BB' }, { name: 'Coventry', postalCode: 'CV1 1EZ' }, { name: 'Wolverhampton', postalCode: 'WV1 1LH' }] },
      { name: 'South Yorkshire', cities: [{ name: 'Sheffield', postalCode: 'S1 1DA' }, { name: 'Rotherham', postalCode: 'S60 1BD' }] },
      { name: 'Merseyside', cities: [{ name: 'Liverpool', postalCode: 'L1 8JQ' }, { name: 'Birkenhead', postalCode: 'CH41 5BX' }] },
      { name: 'Scotland', cities: [{ name: 'Edinburgh', postalCode: 'EH1 1YZ' }, { name: 'Glasgow', postalCode: 'G1 1HN' }, { name: 'Aberdeen', postalCode: 'AB10 1FQ' }] },
      { name: 'Wales', cities: [{ name: 'Cardiff', postalCode: 'CF10 1EP' }, { name: 'Swansea', postalCode: 'SA1 3SN' }] },
      { name: 'Northern Ireland', cities: [{ name: 'Belfast', postalCode: 'BT1 1AH' }, { name: 'Londonderry', postalCode: 'BT48 6AT' }] },
      { name: 'Kent', cities: [{ name: 'Canterbury', postalCode: 'CT1 2EH' }, { name: 'Maidstone', postalCode: 'ME14 1LQ' }] },
      { name: 'Hampshire', cities: [{ name: 'Southampton', postalCode: 'SO14 0YG' }, { name: 'Portsmouth', postalCode: 'PO1 1AE' }] },
      { name: 'Bristol', cities: [{ name: 'Bristol', postalCode: 'BS1 1EH' }] },
      { name: 'East Sussex', cities: [{ name: 'Brighton', postalCode: 'BN1 1EL' }] },
      { name: 'Oxfordshire', cities: [{ name: 'Oxford', postalCode: 'OX1 1PT' }] },
      { name: 'Cambridgeshire', cities: [{ name: 'Cambridge', postalCode: 'CB2 1TN' }] }
    ]
  },
  {
    name: 'United States', code: 'US',
    states: [
      { name: 'Alabama', cities: [{ name: 'Birmingham', postalCode: '35203' }, { name: 'Montgomery', postalCode: '36104' }, { name: 'Huntsville', postalCode: '35801' }] },
      { name: 'Alaska', cities: [{ name: 'Anchorage', postalCode: '99501' }, { name: 'Fairbanks', postalCode: '99701' }] },
      { name: 'Arizona', cities: [{ name: 'Phoenix', postalCode: '85001' }, { name: 'Tucson', postalCode: '85701' }, { name: 'Mesa', postalCode: '85201' }, { name: 'Scottsdale', postalCode: '85251' }] },
      { name: 'Arkansas', cities: [{ name: 'Little Rock', postalCode: '72201' }, { name: 'Fort Smith', postalCode: '72901' }] },
      { name: 'California', cities: [{ name: 'Los Angeles', postalCode: '90001' }, { name: 'San Francisco', postalCode: '94102' }, { name: 'San Diego', postalCode: '92101' }, { name: 'San Jose', postalCode: '95101' }, { name: 'Sacramento', postalCode: '95814' }, { name: 'Oakland', postalCode: '94601' }, { name: 'Fresno', postalCode: '93721' }] },
      { name: 'Colorado', cities: [{ name: 'Denver', postalCode: '80202' }, { name: 'Colorado Springs', postalCode: '80903' }, { name: 'Aurora', postalCode: '80010' }] },
      { name: 'Connecticut', cities: [{ name: 'Hartford', postalCode: '06101' }, { name: 'New Haven', postalCode: '06501' }, { name: 'Bridgeport', postalCode: '06601' }] },
      { name: 'Delaware', cities: [{ name: 'Wilmington', postalCode: '19801' }, { name: 'Dover', postalCode: '19901' }] },
      { name: 'Florida', cities: [{ name: 'Miami', postalCode: '33101' }, { name: 'Orlando', postalCode: '32801' }, { name: 'Tampa', postalCode: '33601' }, { name: 'Jacksonville', postalCode: '32201' }, { name: 'Fort Lauderdale', postalCode: '33301' }] },
      { name: 'Georgia', cities: [{ name: 'Atlanta', postalCode: '30301' }, { name: 'Savannah', postalCode: '31401' }, { name: 'Augusta', postalCode: '30901' }] },
      { name: 'Hawaii', cities: [{ name: 'Honolulu', postalCode: '96813' }, { name: 'Hilo', postalCode: '96720' }] },
      { name: 'Idaho', cities: [{ name: 'Boise', postalCode: '83701' }, { name: 'Nampa', postalCode: '83651' }] },
      { name: 'Illinois', cities: [{ name: 'Chicago', postalCode: '60601' }, { name: 'Aurora', postalCode: '60506' }, { name: 'Naperville', postalCode: '60540' }, { name: 'Rockford', postalCode: '61101' }] },
      { name: 'Indiana', cities: [{ name: 'Indianapolis', postalCode: '46201' }, { name: 'Fort Wayne', postalCode: '46801' }] },
      { name: 'Iowa', cities: [{ name: 'Des Moines', postalCode: '50301' }, { name: 'Cedar Rapids', postalCode: '52401' }] },
      { name: 'Kansas', cities: [{ name: 'Wichita', postalCode: '67201' }, { name: 'Overland Park', postalCode: '66201' }] },
      { name: 'Kentucky', cities: [{ name: 'Louisville', postalCode: '40201' }, { name: 'Lexington', postalCode: '40501' }] },
      { name: 'Louisiana', cities: [{ name: 'New Orleans', postalCode: '70112' }, { name: 'Baton Rouge', postalCode: '70801' }] },
      { name: 'Maine', cities: [{ name: 'Portland', postalCode: '04101' }, { name: 'Augusta', postalCode: '04330' }] },
      { name: 'Maryland', cities: [{ name: 'Baltimore', postalCode: '21201' }, { name: 'Rockville', postalCode: '20850' }] },
      { name: 'Massachusetts', cities: [{ name: 'Boston', postalCode: '02101' }, { name: 'Worcester', postalCode: '01601' }, { name: 'Cambridge', postalCode: '02139' }] },
      { name: 'Michigan', cities: [{ name: 'Detroit', postalCode: '48201' }, { name: 'Grand Rapids', postalCode: '49501' }, { name: 'Ann Arbor', postalCode: '48104' }] },
      { name: 'Minnesota', cities: [{ name: 'Minneapolis', postalCode: '55401' }, { name: 'Saint Paul', postalCode: '55101' }] },
      { name: 'Mississippi', cities: [{ name: 'Jackson', postalCode: '39201' }, { name: 'Gulfport', postalCode: '39501' }] },
      { name: 'Missouri', cities: [{ name: 'Kansas City', postalCode: '64101' }, { name: 'St. Louis', postalCode: '63101' }] },
      { name: 'Montana', cities: [{ name: 'Billings', postalCode: '59101' }, { name: 'Helena', postalCode: '59601' }] },
      { name: 'Nebraska', cities: [{ name: 'Omaha', postalCode: '68101' }, { name: 'Lincoln', postalCode: '68501' }] },
      { name: 'Nevada', cities: [{ name: 'Las Vegas', postalCode: '89101' }, { name: 'Reno', postalCode: '89501' }, { name: 'Henderson', postalCode: '89002' }] },
      { name: 'New Hampshire', cities: [{ name: 'Manchester', postalCode: '03101' }, { name: 'Concord', postalCode: '03301' }] },
      { name: 'New Jersey', cities: [{ name: 'Newark', postalCode: '07101' }, { name: 'Jersey City', postalCode: '07302' }, { name: 'Trenton', postalCode: '08601' }] },
      { name: 'New Mexico', cities: [{ name: 'Albuquerque', postalCode: '87101' }, { name: 'Santa Fe', postalCode: '87501' }] },
      { name: 'New York', cities: [{ name: 'New York City', postalCode: '10001' }, { name: 'Brooklyn', postalCode: '11201' }, { name: 'Queens', postalCode: '11101' }, { name: 'Buffalo', postalCode: '14201' }, { name: 'Rochester', postalCode: '14604' }] },
      { name: 'North Carolina', cities: [{ name: 'Charlotte', postalCode: '28201' }, { name: 'Raleigh', postalCode: '27601' }, { name: 'Greensboro', postalCode: '27401' }] },
      { name: 'North Dakota', cities: [{ name: 'Fargo', postalCode: '58102' }, { name: 'Bismarck', postalCode: '58501' }] },
      { name: 'Ohio', cities: [{ name: 'Columbus', postalCode: '43215' }, { name: 'Cleveland', postalCode: '44101' }, { name: 'Cincinnati', postalCode: '45201' }] },
      { name: 'Oklahoma', cities: [{ name: 'Oklahoma City', postalCode: '73101' }, { name: 'Tulsa', postalCode: '74101' }] },
      { name: 'Oregon', cities: [{ name: 'Portland', postalCode: '97201' }, { name: 'Salem', postalCode: '97301' }, { name: 'Eugene', postalCode: '97401' }] },
      { name: 'Pennsylvania', cities: [{ name: 'Philadelphia', postalCode: '19102' }, { name: 'Pittsburgh', postalCode: '15201' }, { name: 'Allentown', postalCode: '18101' }] },
      { name: 'Rhode Island', cities: [{ name: 'Providence', postalCode: '02901' }] },
      { name: 'South Carolina', cities: [{ name: 'Charleston', postalCode: '29401' }, { name: 'Columbia', postalCode: '29201' }] },
      { name: 'South Dakota', cities: [{ name: 'Sioux Falls', postalCode: '57104' }, { name: 'Rapid City', postalCode: '57701' }] },
      { name: 'Tennessee', cities: [{ name: 'Nashville', postalCode: '37201' }, { name: 'Memphis', postalCode: '38101' }, { name: 'Knoxville', postalCode: '37901' }] },
      { name: 'Texas', cities: [{ name: 'Houston', postalCode: '77001' }, { name: 'San Antonio', postalCode: '78201' }, { name: 'Dallas', postalCode: '75201' }, { name: 'Austin', postalCode: '78701' }, { name: 'Fort Worth', postalCode: '76101' }, { name: 'El Paso', postalCode: '79901' }] },
      { name: 'Utah', cities: [{ name: 'Salt Lake City', postalCode: '84101' }, { name: 'Provo', postalCode: '84601' }] },
      { name: 'Vermont', cities: [{ name: 'Burlington', postalCode: '05401' }, { name: 'Montpelier', postalCode: '05601' }] },
      { name: 'Virginia', cities: [{ name: 'Virginia Beach', postalCode: '23451' }, { name: 'Richmond', postalCode: '23218' }, { name: 'Arlington', postalCode: '22201' }] },
      { name: 'Washington', cities: [{ name: 'Seattle', postalCode: '98101' }, { name: 'Spokane', postalCode: '99201' }, { name: 'Tacoma', postalCode: '98401' }] },
      { name: 'Washington DC', cities: [{ name: 'Washington DC', postalCode: '20001' }, { name: 'Georgetown', postalCode: '20007' }] },
      { name: 'West Virginia', cities: [{ name: 'Charleston', postalCode: '25301' }, { name: 'Huntington', postalCode: '25701' }] },
      { name: 'Wisconsin', cities: [{ name: 'Milwaukee', postalCode: '53201' }, { name: 'Madison', postalCode: '53701' }] },
      { name: 'Wyoming', cities: [{ name: 'Cheyenne', postalCode: '82001' }, { name: 'Casper', postalCode: '82601' }] }
    ]
  },
  {
    name: 'Uzbekistan', code: 'UZ',
    states: [
      { name: 'Tashkent City', cities: [{ name: 'Tashkent', postalCode: '100000' }, { name: 'Yunusabad', postalCode: '100093' }] },
      { name: 'Samarqand', cities: [{ name: 'Samarkand', postalCode: '140100' }] },
      { name: 'Fergana', cities: [{ name: 'Fergana', postalCode: '150100' }, { name: 'Andijan', postalCode: '170100' }] },
      { name: 'Namangan', cities: [{ name: 'Namangan', postalCode: '160100' }] },
      { name: 'Bukhara', cities: [{ name: 'Bukhara', postalCode: '200100' }] }
    ]
  },
  {
    name: 'Venezuela', code: 'VE',
    states: [
      { name: 'Distrito Capital', cities: [{ name: 'Caracas', postalCode: '1010' }, { name: 'Chacao', postalCode: '1060' }, { name: 'Baruta', postalCode: '1080' }] },
      { name: 'Miranda', cities: [{ name: 'Los Teques', postalCode: '1201' }, { name: 'Guarenas', postalCode: '1220' }] },
      { name: 'Zulia', cities: [{ name: 'Maracaibo', postalCode: '4001' }] },
      { name: 'Carabobo', cities: [{ name: 'Valencia', postalCode: '2001' }] },
      { name: 'Aragua', cities: [{ name: 'Maracay', postalCode: '2101' }] }
    ]
  },
  {
    name: 'Vietnam', code: 'VN',
    states: [
      { name: 'Hà Nội', cities: [{ name: 'Hoàn Kiếm', postalCode: '100000' }, { name: 'Đống Đa', postalCode: '116000' }, { name: 'Cầu Giấy', postalCode: '122000' }, { name: 'Thanh Xuân', postalCode: '120000' }] },
      { name: 'Thành phố Hồ Chí Minh', cities: [{ name: 'District 1', postalCode: '700000' }, { name: 'District 3', postalCode: '720000' }, { name: 'Bình Thạnh', postalCode: '717000' }, { name: 'Tân Bình', postalCode: '725000' }] },
      { name: 'Đà Nẵng', cities: [{ name: 'Hải Châu', postalCode: '550000' }, { name: 'Thanh Khê', postalCode: '551000' }] },
      { name: 'Hải Phòng', cities: [{ name: 'Hồng Bàng', postalCode: '180000' }] },
      { name: 'Khánh Hòa', cities: [{ name: 'Nha Trang', postalCode: '650000' }] },
      { name: 'Lâm Đồng', cities: [{ name: 'Đà Lạt', postalCode: '670000' }] }
    ]
  },
  {
    name: 'Yemen', code: 'YE',
    states: [
      { name: 'Sanaa', cities: [{ name: 'Sanaa', postalCode: '00967' }] },
      { name: 'Aden', cities: [{ name: 'Aden', postalCode: '00967' }] },
      { name: 'Taiz', cities: [{ name: 'Taiz', postalCode: '00967' }] },
      { name: 'Hadramawt', cities: [{ name: 'Mukalla', postalCode: '00967' }] },
      { name: 'Ibb', cities: [{ name: 'Ibb', postalCode: '00967' }] }
    ]
  },
  {
    name: 'Zambia', code: 'ZM',
    states: [
      { name: 'Lusaka', cities: [{ name: 'Lusaka', postalCode: '10101' }, { name: 'Chilenje', postalCode: '10102' }] },
      { name: 'Copperbelt', cities: [{ name: 'Kitwe', postalCode: '20101' }, { name: 'Ndola', postalCode: '20201' }] },
      { name: 'Southern', cities: [{ name: 'Livingstone', postalCode: '30101' }] },
      { name: 'Northern', cities: [{ name: 'Kasama', postalCode: '40101' }] }
    ]
  },
  {
    name: 'Zimbabwe', code: 'ZW',
    states: [
      { name: 'Harare', cities: [{ name: 'Harare', postalCode: '00263' }, { name: 'Chitungwiza', postalCode: '00264' }] },
      { name: 'Bulawayo', cities: [{ name: 'Bulawayo', postalCode: '00263' }] },
      { name: 'Mashonaland East', cities: [{ name: 'Marondera', postalCode: '00263' }] },
      { name: 'Manicaland', cities: [{ name: 'Mutare', postalCode: '00263' }] }
    ]
  }
];

// ============================================================
// HELPER FUNCTIONS
// ============================================================

/** Get all country names (sorted) */
export function getAllCountryNames(): string[] {
  return COUNTRY_STATE_CITY_DATA.map(c => c.name);
}

/** Get states for a given country name */
export function getStatesForCountry(countryName: string): string[] {
  if (!countryName?.trim()) return [];
  const found = COUNTRY_STATE_CITY_DATA.find(
    c => c.name.trim().toLowerCase() === countryName.trim().toLowerCase()
  );
  return found ? found.states.map(s => s.name) : [];
}

/** Get cities for a given country + state */
export function getCitiesForState(countryName: string, stateName: string): CityData[] {
  if (!countryName?.trim() || !stateName?.trim()) return [];
  const country = COUNTRY_STATE_CITY_DATA.find(
    c => c.name.trim().toLowerCase() === countryName.trim().toLowerCase()
  );
  if (!country) return [];
  const state = country.states.find(
    s => s.name.trim().toLowerCase() === stateName.trim().toLowerCase()
  );
  return state ? state.cities : [];
}

/** Get city names only (for dropdown) */
export function getCityNamesForState(countryName: string, stateName: string): string[] {
  return getCitiesForState(countryName, stateName).map(c => c.name);
}

/** Get postal code for a specific city */
export function getPostalCodeForCity(countryName: string, stateName: string, cityName: string): string {
  if (!cityName?.trim()) return '';
  const cities = getCitiesForState(countryName, stateName);
  const city = cities.find(
    c => c.name.trim().toLowerCase() === cityName.trim().toLowerCase()
  );
  return city ? city.postalCode : '';
}