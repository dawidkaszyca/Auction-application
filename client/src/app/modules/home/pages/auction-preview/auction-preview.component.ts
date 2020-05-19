import { Component, OnInit } from '@angular/core';
import {ActivatedRoute, Route, Router} from '@angular/router';

@Component({
  selector: 'app-auction-preview',
  templateUrl: './auction-preview.component.html',
  styleUrls: ['./auction-preview.component.scss']
})
export class AuctionPreviewComponent implements OnInit {
  auctionId: number;
  category: string;
  constructor(private router: ActivatedRoute) { }

  ngOnInit(): void {
    this.auctionId = this.router.snapshot.queryParams.id;
    this.category = this.router.snapshot.queryParams.category;
  }

}
