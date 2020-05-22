import { Component, OnInit } from '@angular/core';
import {AuctionService} from '../../../../shared/services/auction.service';

@Component({
  selector: 'app-categories',
  templateUrl: './categories.component.html',
  styleUrls: ['./categories.component.scss']
})
export class CategoriesComponent implements OnInit {

  constructor(private auctionService: AuctionService) { }

  ngOnInit(): void {

  }

  sendSelectedCategory(category: string) {
    this.auctionService.updateSelectedCategory(category);
  }

}
