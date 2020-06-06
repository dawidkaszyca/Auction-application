import {Injectable, OnInit} from '@angular/core';

@Injectable({
  providedIn: 'root'
})
@Injectable()
export class NavigationService {
  show: boolean;

  constructor() {
    this.show = true;
  }

}
