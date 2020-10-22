import {Message} from './message';

export class Conversation {
  id: number;
  name: string;
  avatar: string;
  partnerId: number;
  unViewed = 0;
  yourMessages: Message[];
  partnerMessages: Message[];
}
