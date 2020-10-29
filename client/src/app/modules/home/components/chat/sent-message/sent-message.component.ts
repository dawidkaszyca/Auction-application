import {Component, Input} from '@angular/core';

@Component({
  selector: 'app-sent-message',
  templateUrl: './sent-message.component.html',
  styleUrls: ['./sent-message.component.css']
})
export class SentMessageComponent {
  @Input()
  public message: string;
  @Input()
  public date: string;
}
