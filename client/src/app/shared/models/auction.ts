import {AuctionBaseField} from './auction-base-field';
import {AuctionDetails} from './auction-details';

export class Auction extends AuctionBaseField {
  auctionDetails: AuctionDetails[];
  description: string;
  email: string;
  phone: string;
}
