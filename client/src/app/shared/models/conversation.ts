import {Message} from './message';

export class Conversation {
  id: number;
  name: string;
  avatar: string;
  partnerId: number;
  yourMessages: Message[];
  partnerMessages: Message[];
}
