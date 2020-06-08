export class City {
  name: string;
  latitude: string;
  longitude: string;
}

export class AuctionBaseField {
  id: number;
  price: string;
  category: string;
  city: City;
  condition: string;
  title: string;
  createdDate: string;
  viewers: number;
  photoUrl: string;
}
