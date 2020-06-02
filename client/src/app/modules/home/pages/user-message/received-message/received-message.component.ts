import {Component, Input} from '@angular/core';

@Component({
  selector: 'app-received-message',
  templateUrl: './received-message.component.html',
  styleUrls: ['./received-message.component.css']
})
export class ReceivedMessageComponent {

  @Input()
  public message: string;
  @Input()
  public date: string;

  constructor() {
  }

}

